package com.adrec.bot

import com.adrec.bot.handlers.handleMix
import com.adrec.bot.handlers.handleNew
import com.adrec.bot.handlers.handleRate
import com.adrec.bot.handlers.handleStart
import com.adrec.service.RatingService
import com.adrec.service.SessionService
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

fun startBot(
    token: String,
    sessionService: SessionService,
    ratingService: RatingService,
) {
    val log = LoggerFactory.getLogger("TelegramBot")
    val botsApi = TelegramBotsApi(DefaultBotSession::class.java)

    botsApi.registerBot(object : TelegramLongPollingBot(token) {
        override fun getBotUsername(): String = "AdRecBot"

        override fun onUpdateReceived(update: Update) {
            when {
                update.hasMessage() && update.message.hasText() -> {
                    val chatId = update.message.chatId
                    val text = update.message.text ?: return
                    val command = text.split(" ").first().removePrefix("/").lowercase()
                    when (command) {
                        "start", "help" -> handleStart(this, chatId)
                        "new" -> handleNew(this, chatId, sessionService)
                        "mix" -> handleMix(this, chatId, sessionService)
                        "rate" -> handleRate(this, chatId, ratingService)
                        else -> execute(
                            SendMessage.builder()
                                .chatId(chatId)
                                .text("Unknown command. Try /start")
                                .build(),
                        )
                    }
                }

                update.hasCallbackQuery() -> {
                    val query = update.callbackQuery
                    val data = query.data ?: return
                    val chatId = query.message?.chatId ?: return
                    val parts = data.split("_")
                    if (parts.size == 3 && parts[0] == "rate") {
                        val adId = parts[1].toIntOrNull() ?: return
                        val rating = parts[2].toIntOrNull() ?: return
                        runBlocking {
                            try {
                                ratingService.rateAd(adId, rating)
                                execute(AnswerCallbackQuery.builder().callbackQueryId(query.id).text("✅ Rated $rating/5!").build())
                                execute(SendMessage.builder().chatId(chatId).text("✅ Rated $rating⭐").build())
                            } catch (e: Exception) {
                                execute(AnswerCallbackQuery.builder().callbackQueryId(query.id).text("❌ ${e.message}").build())
                            }
                        }
                    }
                }
            }
        }
    })

    log.info("Telegram bot registered and polling for updates…")
}
