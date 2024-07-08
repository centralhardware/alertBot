import kotlinx.serialization.Serializable

@Serializable
data class Chart(
    val points: List<List<Double>>
)
