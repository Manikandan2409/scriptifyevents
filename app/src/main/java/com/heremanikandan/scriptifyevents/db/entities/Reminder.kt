package com.heremanikandan.scriptifyevents.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminder",
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["eventId"])]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val dateTimeMillis: Long
)

