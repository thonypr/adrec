package com.adrec.model

data class Ad(
    val id: Int,
    val title: String,
    val youtubeUrl: String,
    val youtubeVideoId: String,
    val brand: String?,
    val year: Int?,
    val tags: List<String> = emptyList(),
)
