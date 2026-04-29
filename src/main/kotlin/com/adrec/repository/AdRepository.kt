package com.adrec.repository

import com.adrec.db.tables.AdTagsTable
import com.adrec.db.tables.AdsTable
import com.adrec.db.tables.TagsTable
import com.adrec.db.tables.ViewsTable
import com.adrec.model.Ad
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class AdRepository {

    suspend fun unseenAds(limit: Int): List<Ad> = newSuspendedTransaction(Dispatchers.IO) {
        val seenAdIds = ViewsTable
            .selectAll().where { ViewsTable.seen eq true }
            .map { it[ViewsTable.adId] }
            .toSet()

        AdsTable
            .selectAll()
            .let { if (seenAdIds.isNotEmpty()) it.andWhere { AdsTable.id notInList seenAdIds } else it }
            .limit(limit)
            .map { it.toAd() }
    }

    suspend fun seenAds(): List<Ad> = newSuspendedTransaction(Dispatchers.IO) {
        val seenAdIds = ViewsTable
            .selectAll().where { ViewsTable.seen eq true }
            .map { it[ViewsTable.adId] }
            .toSet()

        if (seenAdIds.isEmpty()) return@newSuspendedTransaction emptyList()

        AdsTable
            .selectAll().where { AdsTable.id inList seenAdIds }
            .map { it.toAd() }
    }

    suspend fun findById(id: Int): Ad? = newSuspendedTransaction(Dispatchers.IO) {
        AdsTable
            .selectAll().where { AdsTable.id eq id }
            .singleOrNull()
            ?.toAd()
    }

    suspend fun all(): List<Ad> = newSuspendedTransaction(Dispatchers.IO) {
        AdsTable.selectAll().map { it.toAd() }
    }

    private fun ResultRow.toAd(): Ad {
        val adId = this[AdsTable.id]
        val tags = AdTagsTable
            .join(TagsTable, JoinType.INNER, AdTagsTable.tagId, TagsTable.id)
            .selectAll().where { AdTagsTable.adId eq adId }
            .map { it[TagsTable.name] }

        return Ad(
            id = adId,
            title = this[AdsTable.title],
            youtubeUrl = this[AdsTable.youtubeUrl],
            youtubeVideoId = this[AdsTable.youtubeVideoId],
            brand = this[AdsTable.brand],
            year = this[AdsTable.year],
            tags = tags,
        )
    }
}
