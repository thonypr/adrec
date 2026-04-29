package com.adrec.db.tables

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object ViewsTable : Table("views") {
    val id = integer("id").autoIncrement()
    val adId = integer("ad_id").references(AdsTable.id)
    val seen = bool("seen").default(false)
    val rating = short("rating").nullable()
    val lastSeenAt = timestamp("last_seen_at").nullable()
    val createdAt = timestamp("created_at").clientDefault { java.time.Instant.now() }

    override val primaryKey = PrimaryKey(id)
}
