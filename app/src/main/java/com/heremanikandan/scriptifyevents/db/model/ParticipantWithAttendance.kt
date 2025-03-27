package com.heremanikandan.scriptifyevents.db.model

import androidx.room.Embedded
import androidx.room.Relation

data class ParticipantWithAttendance(
    @Embedded val participant: Participant,
    @Relation(
        parentColumn = "id",
        entityColumn = "participantId"
    )
    val attendanceList: List<Attendance>
)

