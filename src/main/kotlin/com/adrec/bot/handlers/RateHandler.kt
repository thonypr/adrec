package com.adrec.bot.handlers

import com.adrec.service.RatingService
import kotlinx.coroutines.runBlocking
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton
import org.telegram.telegrambots.meta.bots.AbsSender

fun handleRate(sender: AbsSender, chatId: Long, ratingService: RatingService) {
    runBlocking {
        try {
            val pending = ratingService.getPendingRatings()
            if (pending.isEmpty()) {
                sender.execute(SendMessage.builder().chatId(chatId).text("✅ No ads to rate right now. Watch a session first!").build())
                return@runBlocking
            }

            sender.execute(SendMessage.builder().chatId(chatId).text("📋 You have *${pending.size}* ad(s) to rate:").parseMode("Markdown").build())

            pending.take(5).forEach { ad ->
                val label = buildString {
                    append("*${escapeMarkdown(ad.title)}*")
                    if (ad.brand != null) append(" — ${escapeMarkdown(ad.brand)}")
                    if (ad.year != null) append(" (${ad.year})")
                    append("\n${ad.youtubeUrl}")
                }

                val buttons = listOf("1⭐", "2⭐", "3⭐", "4⭐", "5⭐").mapIndexed { i, btnLabel ->
                    InlineKeyboardButton.builder()
                        .text(btnLabel)
                        .callbackData("rate_${ad.id}_${i + 1}")
                        .build()
                }
                val keyboard = InlineKeyboardMarkup.builder().keyboardRow(buttons).build()

                sender.execute(
                    SendMessage.builder()
                        .chatId(chatId)
                        .text(label)
                        .parseMode("Markdown")
                        .replyMarkup(keyboard)
                        .build(),
                )
            }
        } catch (e: Exception) {
            sender.execute(SendMessage.builder().chatId(chatId).text("❌ Error loading ratings: ${e.message}").build())
        }
    }
}

private fun escapeMarkdown(text: String): String =
    text.replace("_", "\\_").replace("*", "\\*").replace("[", "\\[").replace("`", "\\`")
