package com.tinkofftest.filmbrowser.filmbrowserapp.models

data class FilmsResponse(
    val films: MutableList<Film>,
    val pagesCount: Int
)