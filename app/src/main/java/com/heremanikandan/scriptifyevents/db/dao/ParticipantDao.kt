package com.heremanikandan.scriptifyevents.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heremanikandan.scriptifyevents.db.model.Participant
import kotlinx.coroutines.flow.Flow

@Dao
interface ParticipantDao {

    // Insert participant
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipant(participant: Participant)

    // Insert multiple participants
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParticipants(participants: List<Participant>)

    // Get all participants for a specific event
    @Query("SELECT * FROM participants WHERE eventId = :eventId")
    fun getParticipantsByEventId(eventId: Long): Flow<List<Participant>>

    // Get participant by ID
    @Query("SELECT * FROM participants WHERE id = :participantId LIMIT 1")
    suspend fun getParticipantById(participantId: Long): Participant?

    // Delete a participant
    @Delete
    suspend fun deleteParticipant(participant: Participant)

    // Delete all participants for a specific event
    @Query("DELETE FROM participants WHERE eventId = :eventId")
    suspend fun deleteParticipantsByEventId(eventId: Long)

    // Delete all participants
    @Query("DELETE FROM participants")
    suspend fun deleteAllParticipants()
}
