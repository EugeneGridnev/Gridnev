package com.tinkofftest.filmbrowser.filmbrowserapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.tinkofftest.filmbrowser.R
import com.tinkofftest.filmbrowser.filmbrowserapp.db.FilmDatabase
import com.tinkofftest.filmbrowser.filmbrowserapp.repository.FilmsRepository

class FilmsActivity : AppCompatActivity() {

    lateinit var viewModel: FilmsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filmsRepository = FilmsRepository(FilmDatabase(this))
        val viewModelProviderFactory = FilmsViewModelProviderFactory(application, filmsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[FilmsViewModel::class.java]
        setContentView(R.layout.activity_films)
    }
}