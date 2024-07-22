import dev.inmo.tgbotapi.types.toChatId

val token = System.getenv("TOKEN")
val chatId = System.getenv("CHAT").toLong().toChatId()