package com.heremanikandan.scriptifyevents.db.model
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "shared_with",
    foreignKeys = [ForeignKey(
        entity = Event::class,
        parentColumns = ["id"],
        childColumns = ["eventId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class SharedWith(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Long,        // Foreign key pointing to Event table
    val createdBy: Int,      // User who created the sharing entry
    val permittedTo: Int,    // User who has permission
    val status: Boolean      // True = Permitted, False = Not Permitted
)
