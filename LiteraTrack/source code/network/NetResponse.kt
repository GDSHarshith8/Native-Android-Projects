package com.literatrack.network

import com.google.gson.annotations.SerializedName

/**
 * Top-level response from Google Books API
 */
data class NetworkVolumeResponse(
    val items: List<NetworkVolumeItem>?
)

/**
 * Each item (book) in the Google Books API response
 */
data class NetworkVolumeItem(
    val kind: String?,
    val id: String,
    val volumeInfo: NetworkVolumeInfo?
)

/**
 * Contains detailed book info (title, authors, etc.)
 */
data class NetworkVolumeInfo(
    val id: String?,
    val title: String,
    val authors: List<String>,
    val description: String?,
    val pageCount: Int?,
    val averageRating: Double?,
    val imageLinks: NetworkImageLinks?
)

/**
 * Contains book thumbnail URLs
 */
data class NetworkImageLinks(
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("large") val large: String?
)