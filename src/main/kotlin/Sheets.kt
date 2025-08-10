import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.ServiceAccountCredentials
import java.io.ByteArrayInputStream

suspend fun updateSheet(spreadsheetId: String, sheetName: String, cell: String, value: String) {
    val credentialsJson = System.getenv("GOOGLE_CREDENTIALS") ?: return
    val credentials = ServiceAccountCredentials.fromStream(ByteArrayInputStream(credentialsJson.toByteArray()))
        .createScoped(listOf("https://www.googleapis.com/auth/spreadsheets"))
    val service = Sheets.Builder(
        NetHttpTransport(),
        GsonFactory.getDefaultInstance(),
        HttpCredentialsAdapter(credentials)
    )
        .setApplicationName("TonAlertBot")
        .build()

    val range = "$sheetName!$cell"
    val body = ValueRange().setValues(listOf(listOf(value)))
    service.spreadsheets().values().update(spreadsheetId, range, body)
        .setValueInputOption("RAW")
        .execute()
}
