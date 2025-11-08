package com.musicplayer.network

import kotlinx.serialization.Serializable

@Serializable
data class JamendoResponse(
    val results: List<JamendoTrack>
)

@Serializable
data class JamendoTrack(
    val id: Int,
    val name: String,
    val artist_name: String,
    val album_image: String?,
    val duration: Int,
    val audio: String
)

@Serializable
data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val imageUrl: String?,
    val durationInSeconds: Int,
    val audioUrl: String
)
