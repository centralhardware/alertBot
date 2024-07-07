import dev.inmo.tgbotapi.extensions.api.send.send
import dev.inmo.tgbotapi.extensions.api.send.sendTextMessage
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.types.toChatId
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.*
import java.util.*

suspend fun main() {
    telegramBotWithBehaviourAndLongPolling(System.getenv("TOKEN"), CoroutineScope(Dispatchers.IO)) {
        val timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                val price = runBlocking { getPrice() }
                runBlocking { send(System.getenv("CHAT").toLong().toChatId(), price.toString()) }
                println("send data $price")
            }
        }, 0L, 3600000L)
        onCommand("price") {
            sendTextMessage(it.chat, getPrice().toString())
        }
    }
}

val client = HttpClient(CIO)
const val TON_API_URL = "https://tonapi.io/v2/rates?tokens=ton&currencies=eur"
suspend fun getPrice() = Json.parseToJsonElement(
    client.get(TON_API_URL).body()
).jsonObject["rates"]!!.jsonObject["TON"]!!.jsonObject["prices"]!!.jsonObject["EUR"]!!.jsonPrimitive.float