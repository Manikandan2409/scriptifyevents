package com.heremanikandan.scriptifyevents.db.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "participants",
    indices = [
        Index(value = ["eventId"]),            // for WHERE eventId = ?
        Index(value = ["eventId", "course"]) // for GROUP BY queries
    ],
    foreignKeys = [
        ForeignKey(
            entity = Event::class,
            parentColumns = ["id"],
            childColumns = ["eventId"],
            onDelete = ForeignKey.CASCADE // Deletes participants if event is deleted
        )
    ]
)
data class Participant(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // Auto-increment ID
    val rollNo :String,
    val name: String,
    val email: String,
    val course: String,
    val eventId: Long // References Event ID
)
