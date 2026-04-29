package com.adrec.service

import com.adrec.db.tables.SessionAdsTable
import com.adrec.db.tables.SessionsTable
import com.adrec.model.Ad
import com.adrec.model.Session
import com.adrec.repository.AdRepository
import com.adrec.repository.ViewRepository
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.Instant
import java.time.LocalDate

class SessionService(
    private val adRepo: AdRepository,
    private val viewRepo: ViewRepository,
    private val youTubeService: YouTubeService,
) {
    private val log = LoggerFactory.getLogger(SessionService::class.java)

    suspend fun generateNew(): String {
        val pool = adRepo.unseenAds(limit = 50).shuffled()
        val count = (15..25).random().coerceAtMost(pool.size)
        val ads = pool.take(count)

        if (ads.isEmpty()) {
            return "No unseen ads available. Add more ads to the database first!"
        }

        return createPlaylistAndSave(ads, "NEW")
    }

    suspend fun generateMix(): String {
        val totalCount = (15..25).random()
        val unseenCount = (totalCount * 0.7).toInt()
        val seenCount = totalCount - unseenCount

        val unseen = adRepo.unseenAds(limit = unseenCount * 2)
            .shuffled()
            .take(unseenCount)

        val seenAds = adRepo.seenAds()
            .map { ad ->
                val view = viewRepo.findByAdId(ad.id)
                val ratingScore = (view?.rating?.toInt() ?: 3) * 10.0
                val recencyPenalty = view?.lastSeenAt?.let {
                    val hoursSince = Duration.between(it, Instant.now()).toHours()
                    if (hoursSince < 48) -20.0 else 0.0
                } ?: 0.0
                Pair(ad, ratingScore + recencyPenalty)
            }
            .sortedByDescending { it.second }
            .take(seenCount)
            .map { it.first }

        val ads = (unseen + seenAds).shuffled()

        if (ads.isEmpty()) {
            return "No ads available. Add some ads to the database first!"
        }

        return createPlaylistAndSave(ads, "MIX")
    }

    private suspend fun createPlaylistAndSave(ads: List<Ad>, mode: String): String {
        val title = "AdRec $mode – ${LocalDate.now()}"
        val (playlistId, playlistUrl) = youTubeService.createPlaylist(
            title = title,
            videoIds = ads.map { it.youtubeVideoId },
        )

        val sessionId = newSuspendedTransaction(Dispatchers.IO) {
            val id = SessionsTable.insert {
                it[SessionsTable.playlistId] = playlistId
                it[SessionsTable.playlistUrl] = playlistUrl
                it[SessionsTable.mode] = mode
            }[SessionsTable.id]

            ads.forEachIndexed { index, ad ->
                SessionAdsTable.insert {
                    it[SessionAdsTable.sessionId] = id
                    it[SessionAdsTable.adId] = ad.id
                    it[SessionAdsTable.position] = index
                }
            }
            id
        }

        // Mark ads as seen
        ads.forEach { ad ->
            viewRepo.markSeen(ad.id)
        }

        log.info("Created session $sessionId with ${ads.size} ads, playlist: $playlistUrl")
        return playlistUrl
    }
}
