import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


@Serializable
data class Chart(
    val points: List<List<String>>
)

fun Chart.toDataset() = mapOf(
    "date" to points.map { it.first().toLong().toLocalDateTime().atZone(ZoneId.of("UTC")) }.toList(),
    "price(EUR)" to points.map { it.last().toDouble() }.toList()
)

fun Long.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(Instant.ofEpochSecond(this), ZoneId.of("UTC"))