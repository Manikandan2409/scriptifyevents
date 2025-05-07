package com.heremanikandan.scriptifyevents.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "attendance",
    foreignKeys = [
        ForeignKey(
            entity = Participant::class,
            parentColumns = ["id"],
            childColumns = ["participantId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["uid"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
    ForeignKey(
        entity = Event::class,
        parentColumns = ["id"],
        childColumns = ["eventId"],
        onDelete = ForeignKey.CASCADE
    )
    ],
    indices = [
        Index(value = ["eventId", "participantId"], unique = true), // Enforce unique combination
        Index(value = ["participantId"]),
        Index(value = ["userId"])
    ]
)
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val eventId: Long, // References Event ID
    val userId: String,  // User marking attendance
    val participantId: Long, // References Participant
    val attendantAtInMillis: Long, // Timestamp of attendance

    @ColumnInfo(name = "attendanceMadeBy")
    val attendanceMadeBy: AttendanceMode // Enum: SCANNED or ENTERED
)



