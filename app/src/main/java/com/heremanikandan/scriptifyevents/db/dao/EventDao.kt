package com.heremanikandan.scriptifyevents.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.heremanikandan.scriptifyevents.db.model.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvent(event: Event): Long

    @Query("SELECT * FROM event WHERE id = :id")
    fun getEventByIdFlow(id: Long):Flow<Event?>

    @Query("SELECT * FROM event WHERE id = :id")
    suspend fun getEventById(id: Long):Event?

    @Query("SELECT * FROM event WHERE createdBy =:createdBy ORDER BY id")
    fun getAllEventsByUserId(createdBy:String): Flow<List<Event>>

    @Query("SELECT * FROM event")
    fun getAllEvents(): Flow<List<Event>>

    @Update
    suspend fun updateEvent(event: Event)
    @Delete
    fun deleteEvent(event: Event)

    @Query("SELECT COUNT(*) FROM event WHERE id = :eventId")
    suspend fun isEventExists(eventId: Long): Int

    @Query("SELECT * FROM event WHERE name = :eventName ")
    fun getEventByName(eventName: String): Event?


}