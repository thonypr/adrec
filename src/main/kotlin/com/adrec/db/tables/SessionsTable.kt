package com.adrec.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object SessionsTable : Table("sessions") {
    val id = integer("id").autoIncrement()
    val playlistId = text("playlist_id")
    val playlistUrl = text("playlist_url")
    val mode = text("mode")
    val createdAt = timestamp("created_at").clientDefault { java.time.Instant.now() }

    override val primaryKey = PrimaryKey(id)
}
