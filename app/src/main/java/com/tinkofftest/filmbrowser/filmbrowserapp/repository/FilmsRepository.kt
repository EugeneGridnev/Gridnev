package com.tinkofftest.filmbrowser.filmbrowserapp.repository

import com.tinkofftest.filmbrowser.filmbrowserapp.api.RetrofitInstance
import com.tinkofftest.filmbrowser.filmbrowserapp.db.FilmDatabase
import com.tinkofftest.filmbrowser.filmbrowserapp.models.Film

class FilmsRepository (
    val db: FilmDatabase
) {
    suspend fun getTopFilms(pageNumber: Int) =
        RetrofitInstance.api.getTopFilms(pageNumber = pageNumber)

    suspend fun searchByKeyword(searchKeyWord: String, pageNumber: Int) =
        RetrofitInstance.api.searchForTopFilms(searchKeyWord, pageNumber)

    suspend fun upsert(film: Film) = db.getFilmDao().upsert(film)

    fun getAllFilms() = db.getFilmDao().getAllFilms()

    suspend fun getSavedFilms(ids: List<Int>) = db.getFilmDao().getSavedFilms(ids)

    suspend fun deleteFilm(film:Film) = db.getFilmDao().deleteFilm(film)

    suspend fun getFilm(filmId: Int) = RetrofitInstance.api.getFilm(filmId)
}