package com.tinkofftest.filmbrowser.filmbrowserapp.util

import com.tinkofftest.filmbrowser.filmbrowserapp.models.Film
import com.tinkofftest.filmbrowser.filmbrowserapp.models.FilmsResponse

sealed class Resource(
    //val data: T? = null,
    //val message: String? = null
) {
    class FilmsSuccess(val data: FilmsResponse) : Resource()
    class FilmDiscSuccess(val data: Film) : Resource()
    class Error(val message: String?) : Resource()
    class Loading : Resource()
    class NetworkError(): Resource()
}