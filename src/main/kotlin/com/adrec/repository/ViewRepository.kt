package com.adrec.repository

import com.adrec.db.tables.ViewsTable
import com.adrec.model.View
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import java.time.Instant

class ViewRepository {

    suspend fun findByAdId(adId: Int): View? = newSuspendedTransaction(Dispatchers.IO) {
        ViewsTable
            .selectAll().where { ViewsTable.adId eq adId }
            .singleOrNull()
            ?.toView()
    }

    suspend fun markSeen(adId: Int) = newSuspendedTransaction(Dispatchers.IO) {
        val existing = ViewsTable.selectAll().where { ViewsTable.adId eq adId }.singleOrNull()
        if (existing == null) {
            ViewsTable.insert {
                it[ViewsTable.adId] = adId
                it[seen] = true
                it[lastSeenAt] = Instant.now()
            }
        } else {
            ViewsTable.update({ ViewsTable.adId eq adId }) {
                it[seen] = true
                it[lastSeenAt] = Instant.now()
            }
        }
    }

    suspend fun rate(adId: Int, rating: Int) = newSuspendedTransaction(Dispatchers.IO) {
        require(rating in 1..5) { "Rating must be between 1 and 5" }
        val existing = ViewsTable.selectAll().where { ViewsTable.adId eq adId }.singleOrNull()
        if (existing == null) {
            ViewsTable.insert {
                it[ViewsTable.adId] = adId
                it[seen] = true
                it[ViewsTable.rating] = rating.toShort()
                it[lastSeenAt] = Instant.now()
            }
        } else {
            ViewsTable.update({ ViewsTable.adId eq adId }) {
                it[ViewsTable.rating] = rating.toShort()
            }
        }
    }

    suspend fun unratedSeenAdIds(): List<Int> = newSuspendedTransaction(Dispatchers.IO) {
        ViewsTable
            .selectAll().where { (ViewsTable.seen eq true) and (ViewsTable.rating.isNull()) }
            .map { it[ViewsTable.adId] }
    }

    private fun ResultRow.toView() = View(
        id = this[ViewsTable.id],
        adId = this[ViewsTable.adId],
        seen = this[ViewsTable.seen],
        rating = this[ViewsTable.rating],
        lastSeenAt = this[ViewsTable.lastSeenAt],
        createdAt = this[ViewsTable.createdAt],
    )
}
