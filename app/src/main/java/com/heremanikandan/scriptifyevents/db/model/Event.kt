package com.heremanikandan.scriptifyevents.db.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "event")
data class Event(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val description: String,
    val createdBy :String,
    val dateTimeMillis: Long,
    val reminder: Boolean,
    val status: EventStatus,
)

enum class EventStatus {
    ONGOING,
    COMPLETED,
    WAITING,
    DISABLED
}
