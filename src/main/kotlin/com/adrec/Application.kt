package com.adrec

import com.adrec.bot.startBot
import com.adrec.config.AppConfig
import com.adrec.db.DatabaseFactory
import com.adrec.repository.AdRepository
import com.adrec.repository.ViewRepository
import com.adrec.service.RatingService
import com.adrec.service.SessionService
import com.adrec.service.YouTubeService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import org.slf4j.LoggerFactory

fun main() {
    val log = LoggerFactory.getLogger("Application")

    log.info("Initializing database…")
    DatabaseFactory.init()

    log.info("Wiring services…")
    val adRepo = AdRepository()
    val viewRepo = ViewRepository()
    val youTubeService = YouTubeService(
        clientId = AppConfig.youtubeClientId,
        clientSecret = AppConfig.youtubeClientSecret,
        refreshToken = AppConfig.youtubeRefreshToken,
    )
    val sessionService = SessionService(adRepo, viewRepo, youTubeService)
    val ratingService = RatingService(adRepo, viewRepo)

    log.info("Starting Ktor server…")
    val server = embeddedServer(Netty, port = 8080) {
        module()
    }
    server.start(wait = false)

    log.info("Starting Telegram bot…")
    startBot(
        token = AppConfig.telegramBotToken,
        sessionService = sessionService,
        ratingService = ratingService,
    )
}

fun Application.module() {
    // Minimal Ktor module — health check endpoint only for MVP
}
