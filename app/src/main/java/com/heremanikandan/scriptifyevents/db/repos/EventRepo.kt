package com.heremanikandan.scriptifyevents.db.repos

import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.dao.ReminderDao
import com.heremanikandan.scriptifyevents.db.model.Event
import com.heremanikandan.scriptifyevents.db.model.Reminder

class EventRepo(private val eventDao: EventDao, private val reminderDao: ReminderDao) {

    suspend fun insertEventWithReminder(event: Event, reminderTimeMillis: Long?): Boolean {
        val eventId = eventDao.insertEvent(event)  // Insert event and get ID

        // If reminder is enabled, insert reminder
        if (reminderTimeMillis != null) {
            val reminder = Reminder(eventId = eventId, reminderTimeMillis = reminderTimeMillis)
            reminderDao.insertReminder(reminder)
        }
        return eventId > 0  // Return true if inserted successfully
    }
}
