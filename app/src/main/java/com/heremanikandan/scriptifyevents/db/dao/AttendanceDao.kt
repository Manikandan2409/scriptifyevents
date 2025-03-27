package com.heremanikandan.scriptifyevents.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.heremanikandan.scriptifyevents.db.model.Attendance
import com.heremanikandan.scriptifyevents.db.model.AttendanceMode
import com.heremanikandan.scriptifyevents.db.model.ParticipantWithAttendance

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
    suspend fun getAttendanceByEventAndParticipant(eventId: Long, participantId: Long): Attendance?

    @Query("SELECT * FROM attendance WHERE eventId = :eventId")
    suspend fun getAttendanceByEventId(eventId: Long): List<Attendance>

    @Query("SELECT * FROM attendance WHERE attendanceMadeBy = :mode")
    suspend fun getAttendanceByMode(mode: AttendanceMode): List<Attendance>
    @Query("SELECT * FROM attendance WHERE eventId = :eventId AND userId = :userId")
    suspend fun getAttendanceByEventAndUser(eventId: Long, userId: Long): List<Attendance>

    @Transaction
    @Query("SELECT * FROM participants WHERE id = :participantId")
    suspend fun getParticipantWithAttendance(participantId: Long): List<ParticipantWithAttendance>
}

