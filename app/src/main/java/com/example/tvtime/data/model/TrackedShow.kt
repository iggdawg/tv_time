package com.example.tvtime.data.model

import kotlinx.serialization.Serializable

@Serializable
data class TrackedShow(
    val show: Show,
    val addedAt: Long = System.currentTimeMillis(),
    val watchedEpisodeIds: Set<Int> = emptySet(),
) {
    fun isEpisodeWatched(episodeId: Int): Boolean = watchedEpisodeIds.contains(episodeId)
}
