package com.heremanikandan.scriptifyevents.db.model

import android.os.Parcel
import android.os.Parcelable
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
        )
    ],
    indices = [
        Index(value = ["eventId", "participantId"], unique = true), // Enforce unique combination
        Index(value = ["participantId"])
    ]
)
data class Attendance(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val eventId: Long, // References Event ID
    val userId: Long,  // User marking attendance
    val participantId: Long, // References Participant
    val attendantAtInMillis: Long, // Timestamp of attendance

    @ColumnInfo(name = "attendanceMadeBy")
    val attendanceMadeBy: AttendanceMode // Enum: SCANNED or ENTERED
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        parcel.readLong(),
        TODO("attendanceMadeBy")
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeLong(eventId)
        parcel.writeLong(userId)
        parcel.writeLong(participantId)
        parcel.writeLong(attendantAtInMillis)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Attendance> {
        override fun createFromParcel(parcel: Parcel): Attendance {
            return Attendance(parcel)
        }

        override fun newArray(size: Int): Array<Attendance?> {
            return arrayOfNulls(size)
        }
    }
}

// Enum for Attendance Mode
enum class AttendanceMode {
    SCANNED,
    ENTERED
}


