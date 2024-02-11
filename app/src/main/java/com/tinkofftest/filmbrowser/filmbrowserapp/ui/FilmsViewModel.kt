package com.tinkofftest.filmbrowser.filmbrowserapp.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tinkofftest.filmbrowser.filmbrowserapp.FilmsApplication
import com.tinkofftest.filmbrowser.filmbrowserapp.models.Film
import com.tinkofftest.filmbrowser.filmbrowserapp.models.FilmsResponse
import com.tinkofftest.filmbrowser.filmbrowserapp.repository.FilmsRepository
import com.tinkofftest.filmbrowser.filmbrowserapp.util.Resource
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class FilmsViewModel(
    app: Application,
    val filmsRepository: FilmsRepository
) : AndroidViewModel (app) {

    val topFilms: MutableLiveData<Resource?> = MutableLiveData(null)
    var topFilmsPage = 1
    var topFilmsResponse: FilmsResponse? = null

    val searchFilmByKeyword: MutableLiveData<Resource?> = MutableLiveData()
    var searchFilmByKeywordPage = 1
    var searchFilmByKeywordResponse: FilmsResponse? = null

    var currentSearchQuery: String? = null


    fun getTopFilms() = viewModelScope.launch {
        safeTopFilmsCall()
    }

    fun searchFilmByKeyword(keyword: String) = viewModelScope.launch {
        safeSearchFilmByKeyword(keyword)
    }

    fun getFilm(filmId: Int) = viewModelScope.async {
        return@async safeGetFilmCall(filmId)
    }

    private suspend fun handleTopFilmsResponse(response: Response<FilmsResponse>) : Resource {
        if(response.isSuccessful) {
            response.body()?.let {resultResponse ->
                val filmsList = resultResponse.films.map {
                    it.filmId
                }
                val savedFilms = getSavedFilms(filmsList).associateBy { it.filmId }
                resultResponse.films.forEach {
                    it.favorite = savedFilms.containsKey(it.filmId)
                }
                topFilmsPage++
                if (topFilmsResponse == null){
                    topFilmsResponse = resultResponse
                } else {
                    val oldFilms = topFilmsResponse?.films
                    val newFilms = resultResponse.films
                    oldFilms?.addAll(newFilms)
                }
                return Resource.FilmsSuccess(topFilmsResponse!!)
            }
        }
        return Resource.Error(response.message())
    }

    private suspend fun handleSearchFilmByKeywordResponse(response: Response<FilmsResponse>) : Resource {
        if(response.isSuccessful) {
            response.body()?.let {resultResponse ->
                val filmsList = resultResponse.films.map {
                    it.filmId
                }
                val savedFilms = getSavedFilms(filmsList).associateBy { it.filmId }
                resultResponse.films.forEach {
                    it.favorite = savedFilms.containsKey(it.filmId)
                }
                searchFilmByKeywordPage++
                if (searchFilmByKeywordResponse == null){
                    searchFilmByKeywordResponse = resultResponse
                } else {
                    val oldFilms = searchFilmByKeywordResponse?.films
                    val newFilms = resultResponse.films
                    oldFilms?.addAll(newFilms)
                }
                return Resource.FilmsSuccess(searchFilmByKeywordResponse!!)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSearchFilmResponse(response: Response<Film>) : Film {
        if(response.isSuccessful) {
           return response.body()?: throw IOException()
        }
        throw IOException()
    }

    suspend fun getSavedFilms(ids: List<Int>) = filmsRepository.getSavedFilms(ids)

    fun getAllFilms() = filmsRepository.getAllFilms()

    fun saveFilm(film: Film) = viewModelScope.launch {
        topFilmsResponse?.films?.find {
            it.filmId == film.filmId
        } ?.let { it.favorite = true }
        film.favorite = true
        filmsRepository.upsert(film)
    }

    fun deleteFilm(film: Film) = viewModelScope.launch {
        topFilmsResponse?.films?.find {
            it.filmId == film.filmId
        } ?.let { it.favorite = false }
        film.favorite = false
        filmsRepository.deleteFilm(film)

    }

    private suspend fun safeGetFilmCall(filmId: Int): Film? {
        topFilms.postValue(Resource.Loading())
            return withInternetConnection() {
                val response = filmsRepository.getFilm(filmId)
                val film = handleSearchFilmResponse(response)
                return@withInternetConnection Resource.FilmDiscSuccess(film)
            }.let {
                topFilms.postValue(it)
                if (it is Resource.FilmDiscSuccess) {
                    it.data
                } else {
                    null
                }
            }
    }

    private suspend fun safeSearchFilmByKeyword(searchQuery: String) {
        searchFilmByKeyword.postValue(Resource.Loading())
            if (searchQuery != currentSearchQuery) {
                resetSearch()
            }
            currentSearchQuery = searchQuery
            withInternetConnection() {
                val response = filmsRepository.searchByKeyword(searchQuery, searchFilmByKeywordPage)
                return@withInternetConnection handleSearchFilmByKeywordResponse(response)
            }.let {
                searchFilmByKeyword.postValue(it)
            }

    }

    private suspend fun safeTopFilmsCall() {
        topFilms.postValue(Resource.Loading())
            withInternetConnection() {
                val response = filmsRepository.getTopFilms(topFilmsPage)
                return@withInternetConnection handleTopFilmsResponse(response)
            }.let {
                topFilms.postValue(it)
            }
    }

    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<FilmsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(TRANSPORT_WIFI) -> true
            capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }

    private inline fun withInternetConnection(withConnection:() -> Resource): Resource {
        return try {
            if (hasInternetConnection()) withConnection() else Resource.NetworkError()
        } catch (t: Throwable) {
            when (t) {
                is IOException -> Resource.NetworkError()
                else -> Resource.Error("Conversion Error")
            }
        }
    }

    fun resetLiveData() {
        topFilms.postValue(topFilmsResponse?.let {
            Resource.FilmsSuccess(it)
        })
        searchFilmByKeyword.postValue(null)
    }

    fun resetSearch() {
        searchFilmByKeywordResponse = null
        searchFilmByKeywordPage = 1
    }

}