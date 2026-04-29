package com.adrec.db.tables

import org.jetbrains.exposed.sql.Table

object SessionAdsTable : Table("session_ads") {
    val sessionId = integer("session_id").references(SessionsTable.id)
    val adId = integer("ad_id").references(AdsTable.id)
    val position = integer("position")

    override val primaryKey = PrimaryKey(sessionId, adId)
}
