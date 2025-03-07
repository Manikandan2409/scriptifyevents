package com.heremanikandan.scriptifyevents.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val description: String,
    val dateTimeMillis: String,
    val reminder: Boolean,
    val isCompleted: Boolean,
    val isOngoing: Boolean,
    val isWaiting: Boolean,
    val disabled: Boolean
)

