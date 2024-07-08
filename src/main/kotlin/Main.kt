import dev.inmo.krontab.doOnce
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.media.sendPhoto
import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.toChatId
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.util.color.Color.Companion.BLUE
import java.awt.image.BufferedImage
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.imageio.ImageIO

suspend fun main() {
    telegramBotWithBehaviourAndLongPolling(System.getenv("TOKEN"), CoroutineScope(Dispatchers.IO),
        defaultExceptionsHandler = {it -> println(it) }) {
        setMyCommands(
            BotCommand("price", "get ton price")
        )
        onCommand("price") {
            val file = File.createTempFile("alertBOt", "")
            ImageIO.write(getChartImage(), "png", file)
            sendPhoto(it.chat, InputFile.fromFile(file), getMessage())
        }
        async {
            doOnce("0 0 * * *") {
                val message = runBlocking { getMessage() }
                runBlocking { send(System.getenv("CHAT").toLong().toChatId(), message) }
            }
        }
    }.second.join()
}

val client = HttpClient(CIO)
const val TON_API_BASE_URL = "https://tonapi.io/v2"

const val RATES_URL = "$TON_API_BASE_URL/rates?tokens=ton&currencies=eur"
suspend fun getRates(): Currency? {
    val res = Json.decodeFromString<Rates>(client.get(RATES_URL).bodyAsText()).currencies["TON"]
    println(res)
    return res;
}

const val CHART_URL = "$TON_API_BASE_URL/rates/chart?token=ton&currency=eur&points_count=200"
suspend fun getChart(): Chart {
    val res = Json.decodeFromString<Chart>(client.get(CHART_URL){
        url {
            parameters.append("start_date", LocalDateTime.now().minusDays(1).toEpochSecond(ZoneOffset.UTC).toString())
            parameters.append("end_date", LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toString())
        }
    }.bodyAsText())
    println(res)
    return res;
}

suspend fun getChartImage(): BufferedImage {
    val data = getChart().points.map {
        Pair(it[0], it[1])
    }.toList()
    val ts = column<Long>("x")
    val price = column<Double>("y")
    val dataset = mapOf(
        "x" to data.map { it.first.toLong() }.toList(),
        "y" to data.map { it.second }.toList()
    )
    return plot(dataset) {
        line {
            x(ts)
            y(price)
            color = BLUE
        }
    }.toBufferedImage()
}

suspend fun getMessage(): String = getRates()?.let {
    """
        price: ${it.prices["EUR"].toString().substring(0, 5)}
        diff_24h: ${it.diff24h["EUR"]}
        diff_7d: ${it.diff7d["EUR"]}
        diff_30d: ${it.diff30d["EUR"]}
    """.trimIndent()
} ?: ""

