package com.heremanikandan.scriptifyevents.db.dto

import androidx.room.Embedded
import androidx.room.Relation
import com.heremanikandan.scriptifyevents.db.model.Attendance
import com.heremanikandan.scriptifyevents.db.model.Participant
import com.heremanikandan.scriptifyevents.db.model.User
data class AttendanceWithParticipantAndUser(
    @Embedded val attendance: Attendance,

    @Relation(
        parentColumn = "participantId",
        entityColumn = "id"
    )
    val participant: Participant,

    @Relation(
        parentColumn = "userId",
        entityColumn = "uid"
    )
    val user: User
)
