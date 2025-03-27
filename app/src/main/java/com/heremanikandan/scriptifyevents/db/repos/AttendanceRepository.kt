package com.heremanikandan.scriptifyevents.db.repos

import com.heremanikandan.scriptifyevents.db.dao.AttendanceDao
import com.heremanikandan.scriptifyevents.db.model.Attendance
import com.heremanikandan.scriptifyevents.db.model.AttendanceMode
import com.heremanikandan.scriptifyevents.db.model.ParticipantWithAttendance

class AttendanceRepository(private val attendanceDao: AttendanceDao) {
    suspend fun insertAttendance(attendance: Attendance): Boolean {
        val result = attendanceDao.insertAttendance(attendance)
        // If result == -1, insertion was ignored due to a duplicate
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

    suspend fun getAttendanceByEventId(eventId: Long): List<Attendance> {
        return attendanceDao.getAttendanceByEventId(eventId)
    }

    suspend fun getAttendanceByMode(mode: AttendanceMode): List<Attendance> {
        return attendanceDao.getAttendanceByMode(mode)
    }

    suspend fun getParticipantWithAttendance(participantId: Long): List<ParticipantWithAttendance> {
        return attendanceDao.getParticipantWithAttendance(participantId)
    }
    suspend fun getAttendanceByEventAndUser(eventId: Long, userId: Long): List<Attendance> {
        return attendanceDao.getAttendanceByEventAndUser(eventId, userId)
    }
}
