package com.tinkofftest.filmbrowser.filmbrowserapp.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tinkofftest.filmbrowser.filmbrowserapp.models.Country
import com.tinkofftest.filmbrowser.filmbrowserapp.models.Genre

class Converters {

    @TypeConverter
    fun fromCountry(country: Country): String {
        return country.country
    }

    @TypeConverter
    fun toCountry(country: String): Country {
        return Country(country)
    }

    @TypeConverter
    fun fromGenre(genre: Genre): String {
        return genre.genre
    }

    @TypeConverter
    fun toGenre(genre: String): Genre {
        return Genre(genre)
    }

    @TypeConverter
    fun fromCountryList(value: List<Country>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toCountryList(value: String): List<Country> {
        return try {
            Gson().fromJson<List<Country>>(value)
        } catch (e: Exception) {
            arrayListOf()
        }
    }

    @TypeConverter
    fun fromGenreList(value: List<Genre>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toGenreList(value: String): List<Genre> {
        return try {
            Gson().fromJson<List<Genre>>(value)
        } catch (e: Exception) {
            arrayListOf()
        }
    }



}

inline fun <reified T> Gson.fromJson(json: String) = fromJson<T>(json, object : TypeToken<T>() {}.type)