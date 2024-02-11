package com.tinkofftest.filmbrowser.filmbrowserapp.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tinkofftest.filmbrowser.filmbrowserapp.models.Film

@Dao
interface FilmDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(film: Film): Long

    @Query("SELECT * FROM items")
    fun getAllFilms(): LiveData<List<Film>>

    @Delete
    suspend fun deleteFilm(film: Film)

    @Query("SELECT * FROM items WHERE filmId in (:ids)")
    suspend fun getSavedFilms(ids: List<Int>): List<Film>
}