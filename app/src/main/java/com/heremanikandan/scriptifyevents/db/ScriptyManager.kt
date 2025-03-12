package com.heremanikandan.scriptifyevents.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.heremanikandan.scriptifyevents.db.dao.EventDao
import com.heremanikandan.scriptifyevents.db.dao.ReminderDao
import com.heremanikandan.scriptifyevents.db.dao.UserDao
import com.heremanikandan.scriptifyevents.db.model.Event
import com.heremanikandan.scriptifyevents.db.model.Reminder
import com.heremanikandan.scriptifyevents.db.model.User

//@Database(entities = [User::class,Event::class,], version = 1)
@Database(entities = [User::class,Event::class, Reminder::class], version = 1, exportSchema = false)
abstract class ScriptifyBase : RoomDatabase() {
    abstract fun userDao(): UserDao // Define your DAO interface here
    abstract  fun EventDao():EventDao

    abstract  fun ReminderDao():ReminderDao
}

object ScriptyManager {
    private var INSTANCE: ScriptifyBase? = null

    fun getInstance(context: Context): ScriptifyBase {
        synchronized(this) {
            var instance = INSTANCE
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    ScriptifyBase::class.java,
                    "scriptifyEventsData"
                ).build()
                INSTANCE = instance
            }
            return instance
        }
    }
}