package com.heremanikandan.scriptifyevents.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heremanikandan.scriptifyevents.db.dto.AttendanceWithParticipantAndUser
import com.heremanikandan.scriptifyevents.db.model.Attendance
import com.heremanikandan.scriptifyevents.db.model.AttendanceMode
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAttendance(attendance: Attendance): Long

    @Update
    suspend fun updateAttendance(attendance: Attendance)

    @Delete
    suspend fun deleteAttendance(attendance: Attendance)

    @Query("DELETE FROM attendance WHERE eventId = :eventId AND participantId =:participantId")
    suspend fun deleteAttendanceByEventId(eventId: Long,participantId: Long)

    @Query("SELECT * FROM attendance WHERE eventId = :eventId AND participantId = :participantId LIMIT 1")
    fun getAttendanceByEventAndParticipant(eventId: Long, participantId: Long): Attendance?

    @Query("SELECT * FROM attendance WHERE eventId = :eventId")
    fun getAttendanceByEventId(eventId: Long): Flow<List<AttendanceWithParticipantAndUser>>

    @Query("SELECT * FROM attendance WHERE attendanceMadeBy = :mode")
    fun getAttendanceByMode(mode: AttendanceMode): Flow<List<AttendanceWithParticipantAndUser>>
    @Query("SELECT * FROM attendance WHERE eventId = :eventId AND userId = :userId")
    fun getAttendanceByEventAndUser(eventId: Long, userId: Long): Flow<List<AttendanceWithParticipantAndUser>>

    @Query("SELECT COUNT(id) FROM attendance WHERE eventId = :eventId")
    fun getAttendanceCountByEventId(eventId: Long) :Flow<Long>

//    @Transaction
//    @Query("SELECT * FROM participants WHERE id = :participantId")
//    suspend fun getParticipantWithAttendance(participantId: Long): List<ParticipantWithAttendance>
}

