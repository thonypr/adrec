package com.adrec.config

import io.github.cdimascio.dotenv.dotenv

object AppConfig {
    private val dotenv = runCatching {
        dotenv { ignoreIfMissing = true }
    }.getOrNull()

    private fun env(key: String): String =
        dotenv?.get(key) ?: System.getenv(key)
            ?: error("Missing required environment variable: $key")

    private fun envOrNull(key: String): String? =
        runCatching { dotenv?.get(key) ?: System.getenv(key) }.getOrNull()
            ?.takeIf { it.isNotBlank() }

    val dbUrl: String get() = env("DB_URL")
    val dbUser: String get() = env("DB_USER")
    val dbPassword: String get() = env("DB_PASSWORD")

    val telegramBotToken: String get() = env("TELEGRAM_BOT_TOKEN")

    val youtubeApiKey: String get() = env("YOUTUBE_API_KEY")
    val youtubeClientId: String get() = env("YOUTUBE_CLIENT_ID")
    val youtubeClientSecret: String get() = env("YOUTUBE_CLIENT_SECRET")
    val youtubeRefreshToken: String get() = env("YOUTUBE_OAUTH_REFRESH_TOKEN")
}
