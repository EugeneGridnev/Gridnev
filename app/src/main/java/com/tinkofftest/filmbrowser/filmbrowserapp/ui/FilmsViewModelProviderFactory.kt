package com.tinkofftest.filmbrowser.filmbrowserapp.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tinkofftest.filmbrowser.filmbrowserapp.repository.FilmsRepository

class FilmsViewModelProviderFactory(
    val app: Application,
    val filmsRepository: FilmsRepository
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FilmsViewModel(app, filmsRepository) as T
    }
}