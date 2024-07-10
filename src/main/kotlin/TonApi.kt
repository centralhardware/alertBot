import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneOffset

val client = HttpClient(CIO)
const val BASE_URL = "https://tonapi.io/v2"
const val CHART_URL = "$BASE_URL/rates/chart?token=ton&currency=eur&points_count=200"
const val RATES_URL = "$BASE_URL/rates?tokens=ton&currencies=eur"

suspend fun getRates(): Currency? {
    return Json.decodeFromString<Rates>(client.get(RATES_URL).bodyAsText()).currencies["TON"].also(::println)
}

suspend fun getChart(startDate: LocalDateTime, endDate: LocalDateTime = LocalDateTime.now()): Chart {
    return Json { isLenient = true }.decodeFromString<Chart>(client.get(CHART_URL) {
        url {
            parameters.append("start_date", startDate.toEpochSecond(ZoneOffset.UTC).toString())
            parameters.append("end_date", endDate.toEpochSecond(ZoneOffset.UTC).toString())
        }
    }.bodyAsText()).also(::println)
}

suspend fun get24HChart(): Chart = getChart(LocalDateTime.now().minusDays(1))
suspend fun get7dChart(): Chart = getChart(LocalDateTime.now().minusDays(7))
suspend fun get30dChart(): Chart = getChart(LocalDateTime.now().minusDays(30))