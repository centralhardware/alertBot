import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rates(
    @SerialName("rates")
    val currencies: Map<String, Currency>
)
