import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.ZoneOffset

val client = HttpClient(CIO)
private val json = Json { isLenient = true }
const val BASE_URL = "https://tonapi.io/v2"
const val CHART_URL = "$BASE_URL/rates/chart?token=ton&currency=eur"
const val RATES_URL = "$BASE_URL/rates?tokens=ton&currencies=eur"


suspend fun getRates(): Currency? {
    return json.decodeFromString<Rates>(client.get(RATES_URL).bodyAsText()).rates["TON"].also(::println)
}

suspend fun getChart(points: Int, startDate: LocalDateTime, endDate: LocalDateTime = LocalDateTime.now()): Chart {
    return json.decodeFromString<Chart>(client.get(CHART_URL) {
        url {
            parameters.append("start_date", startDate.toEpochSecond(ZoneOffset.UTC).toString())
            parameters.append("end_date", endDate.toEpochSecond(ZoneOffset.UTC).toString())
            parameters.append("points", points.toString())
        }
    }.bodyAsText()).also(::println)
}

suspend fun get24HChart(): Chart = getChart(200,LocalDateTime.now().minusDays(1))
suspend fun get7dChart(): Chart = getChart(200,LocalDateTime.now().minusWeeks(1))
suspend fun get1mChart(): Chart = getChart(300, LocalDateTime.now().minusMonths(1))