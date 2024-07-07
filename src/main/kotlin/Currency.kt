import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Currency(
    val prices: Map<String, Double>,
    @SerialName("diff_24h")
    val diff24h: Map<String, String>,
    @SerialName("diff_7d")
    val diff7d: Map<String, String>,
    @SerialName("diff_30d")
    val diff30d: Map<String, String>
)
