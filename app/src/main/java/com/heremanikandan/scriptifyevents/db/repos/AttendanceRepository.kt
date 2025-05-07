package com.heremanikandan.scriptifyevents.db.repos

import android.util.Log
import com.heremanikandan.scriptifyevents.db.dao.AttendanceDao
import com.heremanikandan.scriptifyevents.db.dao.ParticipantDao
import com.heremanikandan.scriptifyevents.db.dao.UserDao
import com.heremanikandan.scriptifyevents.db.dto.AttendanceDTO
import com.heremanikandan.scriptifyevents.db.model.Attendance
import com.heremanikandan.scriptifyevents.db.model.AttendanceMode
import com.heremanikandan.scriptifyevents.utils.toAttendanceDTO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AttendanceRepository(private val attendanceDao: AttendanceDao,private val participantDao: ParticipantDao,private val userDao: UserDao) {
    val TAG ="ATTENDANCE REPO"
    suspend fun insertAttendance(attendance: Attendance): Boolean {
        val result = attendanceDao.insertAttendance(attendance)
        // If result == -1, insertion was ignored due to a duplicate
        Log.e(TAG, "insertAttendance: $result inserted with id" )
        return result != -1L
    }

    suspend fun updateAttendance(attendance: Attendance) {
        attendanceDao.updateAttendance(attendance)
    }

    suspend fun deleteAttendance(attendance: Attendance) {
        attendanceDao.deleteAttendance(attendance)
    }

    suspend fun deleteAttendanceByEventId(eventId: Long,participantId: Long) {
        attendanceDao.deleteAttendanceByEventId(eventId,participantId)
    }

    suspend fun getAttendanceByEventAndParticipant(eventId: Long, participantId: Long): Attendance? {
        return attendanceDao.getAttendanceByEventAndParticipant(eventId, participantId)
    }

    suspend fun getAttendanceByEventId(eventId: Long): Flow<List<AttendanceDTO>> {
        return attendanceDao.getAttendanceByEventId(eventId).map{ list ->
            list.map { it.toAttendanceDTO() }
        }
    }

    suspend fun getAttendanceByMode(mode: AttendanceMode): Flow<List<AttendanceDTO>> {
        return attendanceDao.getAttendanceByMode(mode).map {
            list-> list.map { it.toAttendanceDTO() }
        }
    }


    suspend fun getAttendanceByEventAndUser(eventId: Long, userId: Long): Flow<List<AttendanceDTO>> {
        return attendanceDao.getAttendanceByEventAndUser(eventId, userId).map {
            list-> list.map { it.toAttendanceDTO() }
        }
    }
}
