package com.adrec.service

import com.adrec.model.Ad
import com.adrec.repository.AdRepository
import com.adrec.repository.ViewRepository

class RatingService(
    private val adRepo: AdRepository,
    private val viewRepo: ViewRepository,
) {
    suspend fun getPendingRatings(): List<Ad> {
        val unratedIds = viewRepo.unratedSeenAdIds()
        return unratedIds.mapNotNull { adRepo.findById(it) }
    }

    suspend fun rateAd(adId: Int, rating: Int) {
        require(rating in 1..5) { "Rating must be 1–5" }
        viewRepo.rate(adId, rating)
    }
}
