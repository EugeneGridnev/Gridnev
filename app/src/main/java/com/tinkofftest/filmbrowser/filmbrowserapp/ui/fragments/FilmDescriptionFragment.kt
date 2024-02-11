package com.tinkofftest.filmbrowser.filmbrowserapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.tinkofftest.filmbrowser.R
import com.tinkofftest.filmbrowser.databinding.FragmentFilmDescriptionBinding
import com.tinkofftest.filmbrowser.databinding.FragmentTopFilmsBinding
import com.tinkofftest.filmbrowser.filmbrowserapp.ui.FilmsActivity
import com.tinkofftest.filmbrowser.filmbrowserapp.ui.FilmsViewModel

class FilmDescriptionFragment : Fragment(R.layout.fragment_film_description) {
    lateinit var binding: FragmentFilmDescriptionBinding
    lateinit var viewModel: FilmsViewModel
    val args: FilmDescriptionFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilmDescriptionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as FilmsActivity).viewModel
        viewModel.resetLiveData()
        val film = args.film

        Glide.with(this)
            .load(film.posterUrl)
            .into(binding.ivFilmImage)
        binding.tvFilmTitle.text = film.nameRu
        binding.tvFilmDescription.text = film.description
        binding.tvFilmGenre.text = film.genres.joinToString(", ") { it.genre }
        binding.tvFilmCountry.text = film.countries.joinToString(", ") { it.country }
        binding.tvFilmYear.text = film.year.toString()
    }
}