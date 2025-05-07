package com.heremanikandan.scriptifyevents.utils.connections



import android.content.Context
import android.util.Log
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.Sheet
import com.google.api.services.sheets.v4.model.SheetProperties
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import com.heremanikandan.scriptifyevents.R

object SpreadSheet {
    private const val TAG = "SpreadsheetManager"

    fun createSheetService(context: Context, credential: GoogleAccountCredential): Sheets? {
        return try {
            val transport = AndroidHttp.newCompatibleTransport()
            val jsonFactory = JacksonFactory.getDefaultInstance()

            Sheets.Builder(transport, jsonFactory, credential)
                .setApplicationName(context.getString(R.string.app_name))
                .build()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }



    fun createSpreadsheet(
        context: Context,
        credential: GoogleAccountCredential,
        sheetTitle: String,
        tabTitles: List<String>
    ): String? {
        return try {
            val sheetsService = createSheetService(context,credential)!!

            val spreadsheet = Spreadsheet()
                .setProperties(SpreadsheetProperties().setTitle(sheetTitle))
                .setSheets(
                    tabTitles.map { tabName ->
                        Sheet().setProperties(SheetProperties().setTitle(tabName))
                    }
                )

            val result = sheetsService.spreadsheets().create(spreadsheet).execute()
            Log.d(TAG, "Created Spreadsheet: ${result.spreadsheetId}")
            result.spreadsheetId
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun writeValues(
        credential: GoogleAccountCredential,
        spreadsheetId: String,
        context: Context,
        range: String,
        values: List<List<Any>>
    ) {
        try {
            val sheetsService = createSheetService(context = context,credential)!!
            val body = ValueRange().setValues(values)

            sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute()

            Log.d(TAG, "Values written to spreadsheet.")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to write to spreadsheet", e)
        }
    }
}
