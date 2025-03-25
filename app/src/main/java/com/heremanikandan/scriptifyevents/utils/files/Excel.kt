package com.heremanikandan.scriptifyevents.utils.files

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.heremanikandan.scriptifyevents.db.model.Participant
import org.apache.poi.ss.usermodel.WorkbookFactory

object Excel {
    fun readParticipantsFromExcel(context: Context, uri: Uri, eventId: Long): List<Participant>? {
        val participants = mutableListOf<Participant>()

        try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val workbook = WorkbookFactory.create(inputStream)
                val sheet = workbook.getSheetAt(0)

                val headerRow = sheet.getRow(0)
                if (headerRow == null ||
                    headerRow.getCell(0)?.stringCellValue != "Reg No" ||
                    headerRow.getCell(1)?.stringCellValue != "Name" ||
                    headerRow.getCell(2)?.stringCellValue != "Email" ||
                    headerRow.getCell(3)?.stringCellValue!="Course"
                ) {
                    Toast.makeText(context, "Header row mismatch.", Toast.LENGTH_LONG).show()
                    return null
                }

                val namePattern = Regex("^[a-zA-Z .]*$")
                val emailPattern = Regex("^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")

                for (rowIndex in 1..sheet.lastRowNum) {
                    val row = sheet.getRow(rowIndex) ?: continue
                    val rollNo = row.getCell(0)?.stringCellValue?.trim() ?: continue
                    val name = row.getCell(1)?.stringCellValue?.trim() ?: continue
                    val email = row.getCell(2)?.stringCellValue?.trim() ?: continue
                    val course = row.getCell(3)?.stringCellValue?.trim()?:continue

                    if (namePattern.matches(name) && emailPattern.matches(email)) {
                        participants.add(Participant(rollNo = rollNo, name = name, email = email, eventId = eventId, course = course))
                        //  Toast.makeText(context,rollNo,Toast.LENGTH_LONG).show()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error reading Excel file", Toast.LENGTH_LONG).show()
        }

        return participants
    }

}