# TON Currency Alert Bot

A Telegram bot that tracks TON cryptocurrency prices in EUR and provides price alerts and charts.

## Features

- Real-time TON to EUR price tracking
- Automatic hourly price updates to a specified Telegram chat
- Price history storage in ClickHouse database
- Interactive charts for different time periods:
  - 24 hours
  - 1 week
  - 1 month
- Responds to `/price` command with current price and price change information

## Requirements

- Java 22 or later
- Gradle
- Telegram Bot Token
- ClickHouse database
- Docker (optional, for containerized deployment)

## Environment Variables

The application requires the following environment variables:

- `CHAT`: Telegram chat ID to send automatic updates to
- `CLICKHOUSE_URL`: ClickHouse database connection URL
- Telegram Bot Token (set via TgBotApi configuration)

## Installation

### Local Development

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/currencyAlert.git
   cd currencyAlert
   ```

2. Build the project:
   ```
   ./gradlew build
   ```

3. Run the application:
   ```
   ./gradlew run
   ```

### Docker Deployment

1. Build the Docker image:
   ```
   docker build -t ton-alert-bot .
   ```

2. Run the container:
   ```
   docker run -e CHAT=your_chat_id -e CLICKHOUSE_URL=your_clickhouse_url ton-alert-bot
   ```

## Database Setup

The application requires a ClickHouse database with the following schema:

```sql
CREATE DATABASE IF NOT EXISTS ton;

CREATE TABLE IF NOT EXISTS ton.prices (
    date_time DateTime,
    price Float64
) ENGINE = MergeTree()
ORDER BY date_time;
```

## Usage

1. Start a chat with your bot on Telegram
2. Send the `/price` command to get the current TON price in EUR with charts
3. The bot will automatically send price updates every hour to the configured chat

## Dependencies

- [TgBotApi](https://github.com/InsanusMokrassar/TelegramBotAPI) - Telegram Bot API wrapper for Kotlin
- [Ktor](https://ktor.io/) - HTTP client for API requests
- [ClickHouse JDBC](https://github.com/ClickHouse/clickhouse-jdbc) - Database connectivity
- [Krontab](https://github.com/InsanusMokrassar/krontab) - Scheduling library
- [Kandy-lets-plot](https://github.com/Kotlin/kandy) - Data visualization library

## License

This project is licensed under the terms of the license included in the repository.