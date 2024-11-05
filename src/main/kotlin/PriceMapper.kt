import com.clickhouse.jdbc.ClickHouseDataSource
import java.sql.SQLException
import javax.sql.DataSource
import kotliquery.queryOf
import kotliquery.sessionOf

object PriceMapper {

    private val dataSource: DataSource =
        try {
            ClickHouseDataSource(System.getenv("CLICKHOUSE_URL"))
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }

    fun save(price: Double) =
        sessionOf(dataSource)
            .execute(
                queryOf(
                    """
        insert into ton.prices (date_time, price) 
        values (now(), :price)
    """,
                    mapOf("price" to price),
                )
            )
}
