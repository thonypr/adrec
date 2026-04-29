package com.adrec.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object AdsTable : Table("ads") {
    val id = integer("id").autoIncrement()
    val title = text("title")
    val youtubeUrl = text("youtube_url")
    val youtubeVideoId = text("youtube_video_id")
    val brand = text("brand").nullable()
    val year = integer("year").nullable()
    val createdAt = timestamp("created_at").clientDefault { java.time.Instant.now() }

    override val primaryKey = PrimaryKey(id)
}
