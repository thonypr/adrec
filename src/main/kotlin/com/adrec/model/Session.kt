package com.adrec.model

import java.time.Instant

data class Session(
    val id: Int,
    val playlistId: String,
    val playlistUrl: String,
    val mode: String,
    val createdAt: Instant,
    val ads: List<Ad> = emptyList(),
)
