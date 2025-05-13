package com.heremanikandan.scriptifyevents.utils.files

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.heremanikandan.scriptifyevents.db.dto.AttendanceDTO
import com.heremanikandan.scriptifyevents.db.model.Participant
import com.heremanikandan.scriptifyevents.utils.convertMillisToDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.ss.usermodel.WorkbookFactory

object Excel {
//    fun readParticipantsFromExcel(context: Context, uri: Uri, eventId: Long): List<Participant>? {
//        val participants = mutableListOf<Participant>()
//
//        try {
//            context.contentResolver.openInputStream(uri)?.use { inputStream ->
//                val workbook = WorkbookFactory.create(inputStream)
//                val sheet = workbook.getSheetAt(0)
//
//                val headerRow = sheet.getRow(0)
//                if (headerRow == null ||
//                    headerRow.getCell(0)?.stringCellValue != "Reg No" ||
//                    headerRow.getCell(1)?.stringCellValue != "Name" ||
//                    headerRow.getCell(2)?.stringCellValue != "Email" ||
//                    headerRow.getCell(3)?.stringCellValue!="Course"
//                ) {
//                    Toast.makeText(context, "Header row mismatch.", Toast.LENGTH_LONG).show()
//                    return null
//                }
//
//                val namePattern = Regex("^[a-zA-Z .]*$")
//                val emailPattern = Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
//
//                for (rowIndex in 1..sheet.lastRowNum) {
//                    val row = sheet.getRow(rowIndex) ?: continue
//                    val rollNo = row.getCell(0)?.stringCellValue?.trim() ?: continue
//                    val name = row.getCell(1)?.stringCellValue?.trim() ?: continue
//                    val email = row.getCell(2)?.stringCellValue?.trim() ?: continue
//                    val course = row.getCell(3)?.stringCellValue?.trim()?:continue
//
//                    if (namePattern.matches(name) && emailPattern.matches(email)) {
//                        participants.add(Participant(rollNo = rollNo, name = name, email = email, eventId = eventId, course = course))
//                        //  Toast.makeText(context,rollNo,Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Toast.makeText(context, "Error reading Excel file", Toast.LENGTH_LONG).show()
//        }
//
//        return participants
//    }


    suspend fun readParticipantsFromExcel(
        context: Context,
        uri: Uri,
        eventId: Long
    ): Result<List<Participant>> = withContext(Dispatchers.IO) {

        val participants = mutableListOf<Participant>()

        return@withContext try {
            Log.d("EXCEL PARSER"," uri ${uri.path} ${uri.host}")
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val workbook = WorkbookFactory.create(inputStream)
                val sheet = workbook.getSheetAt(0)

                val headerRow = sheet.getRow(0)
                if (headerRow == null ||
                    headerRow.getCell(0)?.stringCellValue?.trim() != "Reg No" ||
                    headerRow.getCell(1)?.stringCellValue?.trim() != "Name" ||
                    headerRow.getCell(2)?.stringCellValue?.trim() != "Email" ||
                    headerRow.getCell(3)?.stringCellValue?.trim() != "Course"
                ) {
                    return@withContext Result.failure(IllegalArgumentException("Header row mismatch"))
                }

                val namePattern = Regex("^[a-zA-Z .]*$")
                val emailPattern = Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")

                for (rowIndex in 1..sheet.lastRowNum) {
                    try {
                        val row = sheet.getRow(rowIndex) ?: continue
                        val rollNo = row.getCell(0)?.stringCellValue?.trim() ?: continue
                        val name = row.getCell(1)?.stringCellValue?.trim() ?: continue
                        val email = row.getCell(2)?.stringCellValue?.trim() ?: continue
                        val course = row.getCell(3)?.stringCellValue?.trim() ?: continue

                        if (namePattern.matches(name) && emailPattern.matches(email)) {
                            participants.add(
                                Participant(
                                    rollNo = rollNo,
                                    name = name,
                                    email = email,
                                    eventId = eventId,
                                    course = course
                                )
                            )
                        }
                    } catch (e: Exception) {
                        Log.w("ExcelParser", "Skipping malformed row at index $rowIndex: ${e.message}")
                    }
                }

                workbook.close()
            }

            Result.success(participants)

        } catch (e: Exception) {
            Log.e("ExcelParser", "Failed to read Excel file", e)
            Result.failure(e)
        }
    }





//    fun exportToExcel(context: android.content.Context,eventName:String,uri:Uri, attendanceList: List<AttendanceDTO>) {
//        val workbook = WorkbookFactory.create(true)
//        val sheet = workbook.createSheet("Attendance")
//
//        val headerRow = sheet.createRow(0)
//        headerRow.createCell(0).setCellValue("S.No")
//        headerRow.createCell(1).setCellValue("Roll No")
//        headerRow.createCell(2).setCellValue("name")
//        headerRow.createCell(3).setCellValue("email")
//        headerRow.createCell(4).setCellValue("course")
//        headerRow.createCell(5).setCellValue("Scanned By")
//        headerRow.createCell(6).setCellValue("Date Time")
//
//        attendanceList.forEachIndexed { index, attendance ->
//            val row = sheet.createRow(index + 1)
//            row.createCell(0).setCellValue((index+1).toString())
//            row.createCell(1).setCellValue(attendance.rollNo)
//            row.createCell(2).setCellValue(attendance.name)
//            row.createCell(3).setCellValue(attendance.email)
//            row.createCell(4).setCellValue(attendance.course)
//            row.createCell(5).setCellValue(attendance.ScannedBy)
//            val (date,time) = convertMillisToDateTime(attendance.dateTimeInMillis)
//            row.createCell(6).setCellValue("$date:$time")
//        }
//
//        val fileName = "Attendance_${eventName}.xlsx"
//        val file = File(context.getExternalFilesDir(null), fileName)
//
//        FileOutputStream(file).use { outputStream ->
//            workbook.write(outputStream)
//            outputStream.close()
//        }
//
//        Toast.makeText(context, "$fileName Exported", Toast.LENGTH_LONG).show()
//    }

    fun exportToExcel(
        context: Context,
        eventName: String,
        attendanceList: List<AttendanceDTO>,
        uri: Uri
    ) {
        try {
            val workbook = WorkbookFactory.create(true)
            val sheet = workbook.createSheet("Attendance")

            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("S.No")
            headerRow.createCell(1).setCellValue("Roll No")
            headerRow.createCell(2).setCellValue("Name")
            headerRow.createCell(3).setCellValue("Email")
            headerRow.createCell(4).setCellValue("Course")
            headerRow.createCell(5).setCellValue("Scanned By")
            headerRow.createCell(6).setCellValue("Date Time")

            attendanceList.forEachIndexed { index, attendance ->
                val row = sheet.createRow(index + 1)
                row.createCell(0).setCellValue((index + 1).toString())
                row.createCell(1).setCellValue(attendance.rollNo)
                row.createCell(2).setCellValue(attendance.name)
                row.createCell(3).setCellValue(attendance.email)
                row.createCell(4).setCellValue(attendance.course)
                row.createCell(5).setCellValue(attendance.ScannedBy)
                val (date, time) = convertMillisToDateTime(attendance.dateTimeInMillis)
                row.createCell(6).setCellValue("$date $time")
            }

            val outputStream = context.contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                workbook.write(outputStream)
                outputStream.close()
                workbook.close()
                Toast.makeText(context, "Excel exported successfully!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "Failed to open output stream", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Export failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }


}