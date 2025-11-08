package com.musicplayer.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import javax.inject.Inject

/**
 * Remote data source for fetching track data using Ktor.
 * The @Inject constructor allows this class to be injected by Hilt.
 */
class TrackRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient
) {

    private val BASE_URL = "https://api.jamendo.com/v3.0/tracks/?client_id=bdaeac94&format=json&limit=50"

    /**
     * Fetches the list of tracks from Jamendo API.
     * @param orderBy Sorting parameter: "name_asc" or "duration_asc"
     */
    suspend fun getTracks(orderBy: String = "name_asc"): List<Track> {
        return try {
            val url = "$BASE_URL&order=$orderBy"
            val response: JamendoResponse = httpClient.get(url).body()
            response.results.map { track ->
                Track(
                    id = track.id.toString(),
                    title = track.name,
                    artist = track.artist_name,
                    imageUrl = track.album_image,
                    durationInSeconds = track.duration,
                    audioUrl = track.audio
                )
            }
        } catch (e: Exception) {
            // Handle network or parsing errors gracefully
            emptyList()
        }
    }
}