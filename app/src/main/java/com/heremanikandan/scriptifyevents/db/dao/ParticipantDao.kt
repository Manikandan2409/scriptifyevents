package com.heremanikandan.scriptifyevents.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heremanikandan.scriptifyevents.db.dto.DepartmentCount
import com.heremanikandan.scriptifyevents.db.model.Participant
import kotlinx.coroutines.flow.Flow

@Dao
interface ParticipantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: Participant) : Long
    @Update
    suspend fun updateParticipant(participant: Participant)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<Participant>)
    @Query("SELECT * FROM participants WHERE eventId = :eventId")
    fun getParticipantsByEventId(eventId: Long): Flow<List<Participant>>
    @Query("SELECT * FROM participants WHERE LOWER(rollNo) = LOWER(:rollNo) AND eventId= :eventId LIMIT 1")
    suspend fun getParticipantByRollNO(rollNo:String,eventId: Long): Participant?
    @Query("SELECT * FROM participants WHERE id = :participantId")
    suspend fun getParticipantById(participantId: Long): Participant?
    @Delete
    suspend fun deleteParticipant(participant: Participant)
    @Query("DELETE FROM participants WHERE eventId = :eventId")
    suspend fun deleteParticipantsByEventId(eventId: Long)

    @Query("DELETE FROM participants")
    suspend fun deleteAllParticipants()
    @Query("SELECT COUNT(id) FROM participants WHERE eventId= :eventId")
     fun getParticipantsCountByEventId(eventId: Long):Flow<Long>
    @Query("SELECT course, COUNT(id) as count FROM participants WHERE eventId = :eventId GROUP BY course")
    fun getDepartmentWiseParticipantCount(eventId: Long): Flow<List<DepartmentCount>>
    @Query("SELECT COUNT(DISTINCT course) FROM participants WHERE eventId = :eventId")
     fun getDistinctCourseCountByEventId(eventId: Long):Flow<Long>
}
