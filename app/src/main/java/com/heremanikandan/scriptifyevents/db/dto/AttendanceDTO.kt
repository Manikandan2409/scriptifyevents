package com.heremanikandan.scriptifyevents.db.dto

data class AttendanceDTO
    (val attendanceId:Long,
     val ScannedBy: String,
     val rollNo:String,
     val name :String,
     val email:String,
     val course:String,
     val dateTimeInMillis:Long)
