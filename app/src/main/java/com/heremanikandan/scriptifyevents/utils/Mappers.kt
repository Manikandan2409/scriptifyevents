package com.heremanikandan.scriptifyevents.utils

import com.heremanikandan.scriptifyevents.db.dto.AttendanceDTO
import com.heremanikandan.scriptifyevents.db.dto.AttendanceWithParticipantAndUser


fun AttendanceWithParticipantAndUser.toAttendanceDTO(): AttendanceDTO {
    return AttendanceDTO(
        attendanceId = attendance.id,
        ScannedBy = user.name, // Assuming User has a name field
        rollNo = participant.rollNo,
        name = participant.name,
        email = participant.email,
        course = participant.course,
        dateTimeInMillis = attendance.attendantAtInMillis
    )
}
