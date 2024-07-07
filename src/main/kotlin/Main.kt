import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.types.toChatId
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.util.*

suspend fun main() {
    telegramBotWithBehaviourAndLongPolling(System.getenv("TOKEN"), CoroutineScope(Dispatchers.IO)) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val message = runBlocking { getMessage() }
                runBlocking { send(System.getenv("CHAT").toLong().toChatId(), message) }
                println("send data $message")
            }
        }, 0L, 3600000L)
        onCommand("price") {
            sendTextMessage(it.chat, getMessage())
        }
    }
}

val client = HttpClient(CIO)
const val TON_API_URL = "https://tonapi.io/v2/rates?tokens=ton&currencies=eur"
suspend fun getPrice(): Currency? {
    val res = Json.decodeFromString<Rates>(client.get(TON_API_URL).bodyAsText()).currencies["TON"]
    println(res)
    return res;
}

suspend fun getMessage(): String = getPrice()?.let {
    """
        price: ${it.prices["EUR"].toString().substring(0,5)}
        diff_24h: ${it.diff24h["EUR"]}
        diff_7d: ${it.diff7d["EUR"]}
        diff_30d: ${it.diff30d["EUR"]}
    """.trimIndent()
}?: ""