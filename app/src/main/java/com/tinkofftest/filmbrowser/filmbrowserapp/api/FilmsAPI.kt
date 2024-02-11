package com.tinkofftest.filmbrowser.filmbrowserapp.api

import com.tinkofftest.filmbrowser.filmbrowserapp.models.Film
import com.tinkofftest.filmbrowser.filmbrowserapp.models.FilmsResponse
import com.tinkofftest.filmbrowser.filmbrowserapp.util.Constants.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FilmsAPI {

    @GET("/api/v2.2/films/top")
    suspend fun getTopFilms(
        @Query("type")
        topType: String = "TOP_100_POPULAR_FILMS",
        @Query("page")
        pageNumber: Int,
        @Header("X-API-KEY")
        apiKey: String = API_KEY
    ): Response<FilmsResponse>

    @GET("/api/v2.2/films/{id}")
    suspend fun getFilm(
        @Path("id")
        filmId: Int,
        @Header("X-API-KEY")
        apiKey: String = API_KEY
    ): Response<Film>

    @GET("/api/v2.1/films/search-by-keyword")
    suspend fun searchForTopFilms(
        @Query("keyword")
        topType: String = "",
        @Query("page")
        pageNumber: Int,
        @Header("X-API-KEY")
        apiKey: String = API_KEY
    ): Response<FilmsResponse>
}