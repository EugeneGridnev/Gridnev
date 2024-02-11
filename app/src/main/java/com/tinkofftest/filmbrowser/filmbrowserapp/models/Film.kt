package com.tinkofftest.filmbrowser.filmbrowserapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(
    tableName = "items"
)
data class Film(
    @PrimaryKey
    val filmId: Int,
    val countries: List<Country>,
    val genres: List<Genre>,
    val nameRu: String,
    val posterUrl: String,
    val posterUrlPreview: String,
    val year: Int,
    var favorite: Boolean?,
    var description: String?
) : Serializable