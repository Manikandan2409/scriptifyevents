package com.heremanikandan.scriptifyevents.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heremanikandan.scriptifyevents.db.entities.Event

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: Event): Long

    @Query("SELECT * FROM event WHERE id = :id")
    fun getEventById(id: Int): Event?

    @Query("SELECT * FROM event")
    fun getAllEvents(): List<Event>

    @Delete
    fun deleteEvent(event: Event)

    @Query("SELECT COUNT(*) FROM event WHERE id = :eventId")
    suspend fun isEventExists(eventId: Int): Int

}