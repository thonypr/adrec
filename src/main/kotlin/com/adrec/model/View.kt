package com.adrec.model

import java.time.Instant

data class View(
    val id: Int,
    val adId: Int,
    val seen: Boolean,
    val rating: Short?,
    val lastSeenAt: Instant?,
    val createdAt: Instant,
)
