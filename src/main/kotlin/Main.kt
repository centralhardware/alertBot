import dev.inmo.krontab.doOnce
import dev.inmo.kslog.common.KSLog
import dev.inmo.kslog.common.info
import dev.inmo.tgbotapi.AppConfig
import dev.inmo.tgbotapi.bot.TelegramBot
import dev.inmo.tgbotapi.extensions.api.bot.setMyCommands
import dev.inmo.tgbotapi.extensions.api.send.media.sendMediaGroup
import dev.inmo.tgbotapi.extensions.behaviour_builder.triggers_handling.onCommand
import dev.inmo.tgbotapi.longPolling
import dev.inmo.tgbotapi.requests.abstracts.InputFile
import dev.inmo.tgbotapi.types.BotCommand
import dev.inmo.tgbotapi.types.IdChatIdentifier
import dev.inmo.tgbotapi.types.media.TelegramMediaPhoto
import dev.inmo.tgbotapi.types.toChatId
import kotlinx.datetime.LocalDateTime
import org.jetbrains.kotlinx.dataframe.api.column
import org.jetbrains.kotlinx.kandy.dsl.plot
import org.jetbrains.kotlinx.kandy.ir.Plot
import org.jetbrains.kotlinx.kandy.letsplot.export.toBufferedImage
import org.jetbrains.kotlinx.kandy.letsplot.feature.layout
import org.jetbrains.kotlinx.kandy.letsplot.layers.line
import org.jetbrains.kotlinx.kandy.util.color.Color.Companion.BLUE
import java.io.File
import javax.imageio.ImageIO

lateinit var bot: TelegramBot
suspend fun main() {
    AppConfig.init("TonAlertBot")
    bot = longPolling {
        setMyCommands(
            BotCommand("price", "get ton price")
        )
        onCommand("price") {
            sendAnswer(it.chat.id)
        }
    }.first

    doOnce("0 0 * * *") {
        KSLog.info("send pricing")
        sendAnswer(System.getenv("CHAT").toLong().toChatId())
    }
}

suspend fun sendAnswer(chatId: IdChatIdentifier) {
    val photos = getChartImages().map { it.toBufferedImage() }.map {
        val file = File.createTempFile("alertBot", "")
        ImageIO.write(it, "png", file)
        TelegramMediaPhoto(InputFile.fromFile(file))
    }.toMutableList()
    val msg = getMessage()
    photos[0] = TelegramMediaPhoto(photos.first().file, msg)
    bot.sendMediaGroup(chatId, photos)
    KSLog.info(msg)
}

suspend fun getChartImages(): List<Plot> {
    return listOf(
        createPlot(get24HChart().toDataset(), "24 hour"),
        createPlot(get7dChart().toDataset(), "1 week"),
        createPlot(get1mChart().toDataset(), "1 month"),
    )
}

val ts = column<LocalDateTime>("date")
val price = column<Double>("price(EUR)")
fun createPlot(dataset: Map<String, List<Any>>, period: String) = plot(dataset) {
    layout {
        title = "ton price(EUR) $period"
    }
    line {
        x(ts)
        y(price)
        color = BLUE
    }

}

suspend fun getMessage(): String = getRates()?.let {
    """
        price: ${it.prices["EUR"].toString().substring(0, 5)}
        diff_24h: ${it.diff24h["EUR"]}
        diff_7d: ${it.diff7d["EUR"]}
        diff_30d: ${it.diff30d["EUR"]}
    """.trimIndent()
} ?: ""

