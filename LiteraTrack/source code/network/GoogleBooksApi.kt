package com.literatrack.network

import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path

interface GoogleBooksApi {
    @GET("volumes")
    suspend fun searchBooks(
        @Query("q") query: String
    ): NetworkVolumeResponse // Now returns the network-specific class

    // method to get a single book by ID
    @GET("volumes/{id}")
    suspend fun getBookById(
        @Path("id") id: String
    ): NetworkVolumeItem // Now returns the network-specific class
}