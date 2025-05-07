package com.heremanikandan.scriptifyevents.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heremanikandan.scriptifyevents.db.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: Event): Long

    @Query("SELECT * FROM event WHERE id = :id")
    suspend fun getEventById(id: Long): Event?

    @Query("SELECT * FROM event")
    fun getAllEvents(): Flow<List<Event>>

    @Delete
    fun deleteEvent(event: Event)

    @Query("SELECT COUNT(*) FROM event WHERE id = :eventId")
    suspend fun isEventExists(eventId: Long): Int

    @Query("SELECT * FROM event WHERE name = :eventName ")
    fun getEventByName(eventName: String): Event?

    @Query("UPDATE event SET spreadsheetId = :spreadsheetId WHERE id = :eventId")
    suspend fun updateSpreadsheetId(eventId: Long, spreadsheetId: String)


}