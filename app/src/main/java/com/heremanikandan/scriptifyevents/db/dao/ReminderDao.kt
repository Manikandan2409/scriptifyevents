package com.heremanikandan.scriptifyevents.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.heremanikandan.scriptifyevents.db.entities.Reminder

@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertReminder(reminder: Reminder): Long

    @Query("SELECT * FROM reminder WHERE eventId = :eventId")
    fun getRemindersForEvent(eventId: Int): List<Reminder>

    @Delete
    fun deleteReminder(reminder: Reminder)
}