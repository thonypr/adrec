package com.adrec.bot.handlers

import com.adrec.service.SessionService
import kotlinx.coroutines.runBlocking
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.bots.AbsSender

fun handleMix(sender: AbsSender, chatId: Long, sessionService: SessionService) {
    sender.execute(SendMessage.builder().chatId(chatId).text("⏳ Generating your *MIX* playlist…").parseMode("Markdown").build())
    runBlocking {
        try {
            val url = sessionService.generateMix()
            sender.execute(SendMessage.builder().chatId(chatId).text("🎬 Here's your playlist:\n$url").build())
        } catch (e: Exception) {
            sender.execute(SendMessage.builder().chatId(chatId).text("❌ Failed to generate playlist: ${e.message}").build())
        }
    }
}
