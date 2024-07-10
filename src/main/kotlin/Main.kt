import dev.inmo.krontab.doOnce
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.media.sendMediaGroup
import dev.inmo.tgbotapi.extensions.behaviour_builder.telegramBotWithBehaviourAndLongPolling
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.media.TelegramMediaPhoto
import dev.inmo.tgbotapi.types.toChatId
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.util.color.Color.Companion.BLUE
import java.awt.image.BufferedImage
import java.io.File
import java.time.ZonedDateTime
import javax.imageio.ImageIO

lateinit var bot:TelegramBot
suspend fun main() {
    val b = telegramBotWithBehaviourAndLongPolling(System.getenv("TOKEN"), CoroutineScope(Dispatchers.IO),
        defaultExceptionsHandler = {it -> it.printStackTrace() }) {
        setMyCommands(
            BotCommand("price", "get ton price")
        )
        onCommand("price") {
            sendAnswer(it.chat.id)
        }
    }
    bot = b.first

    doOnce("0 0 * * *") {
        sendAnswer(System.getenv("CHAT").toLong().toChatId())
    }
}

suspend fun sendAnswer(chatId: IdChatIdentifier) {
    val photos = getChartImages().map {
        val file = File.createTempFile("alertBot", "")
        ImageIO.write(it, "png", file)
        TelegramMediaPhoto(InputFile.fromFile(file))
    }.toMutableList()
    photos[0] = photos.first().let { TelegramMediaPhoto(it.file, getMessage()) }
    bot.sendMediaGroup(chatId, photos)
}

suspend fun getChartImages(): List<BufferedImage> {
    val ts = column<ZonedDateTime>("date")
    val price = column<Double>("price(EUR)")
    return listOf(
        plot(get24HChart().toDataset()) {
            layout {
                title = "ton price(EUR) 24 hours"
            }
            line {
                x(ts)
                y(price)
                color = BLUE
            }
        }.toBufferedImage(),
        plot(get7dChart().toDataset()) {
            layout {
                title = "ton price(EUR) 7 days"
            }
            line {
                x(ts)
                y(price)
                color = BLUE
            }
        }.toBufferedImage(),
        plot(get30dChart().toDataset()) {
            layout {
                title = "ton price(EUR) 30 days"
            }
            line {
                x(ts)
                y(price)
                color = BLUE
            }
        }.toBufferedImage()

    )
}

suspend fun getMessage(): String = getRates()?.let {
    """
        price: ${it.prices["EUR"].toString().substring(0, 5)}
        diff_24h: ${it.diff24h["EUR"]}
        diff_7d: ${it.diff7d["EUR"]}
        diff_30d: ${it.diff30d["EUR"]}
    """.trimIndent()
} ?: ""

