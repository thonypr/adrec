package com.adrec.bot.handlers

import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.bots.AbsSender

fun handleStart(sender: AbsSender, chatId: Long) {
    sender.execute(
        SendMessage.builder()
            .chatId(chatId)
            .text(
                """
                👋 Welcome to *AdRec* — your personal Ad Discovery Engine!
                
                Commands:
                /new — generate a fresh playlist of unseen ads
                /mix — generate a mixed playlist (unseen + top-rated)
                /rate — rate ads from your last session
                """.trimIndent(),
            )
            .parseMode("Markdown")
            .build(),
    )
}
