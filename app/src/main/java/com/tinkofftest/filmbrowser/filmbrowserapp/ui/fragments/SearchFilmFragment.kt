package com.tinkofftest.filmbrowser.filmbrowserapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tinkofftest.filmbrowser.R
import com.tinkofftest.filmbrowser.databinding.FragmentSearchFilmBinding
import com.tinkofftest.filmbrowser.filmbrowserapp.adapters.FilmsAdapter
import com.tinkofftest.filmbrowser.filmbrowserapp.ui.FilmsActivity
import com.tinkofftest.filmbrowser.filmbrowserapp.ui.FilmsViewModel
import com.tinkofftest.filmbrowser.filmbrowserapp.util.Constants
import com.tinkofftest.filmbrowser.filmbrowserapp.util.Resource
import com.tinkofftest.filmbrowser.filmbrowserapp.util.Constants.Companion.SEARCH_FILMS_TIME_DELAY
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class SearchFilmFragment : Fragment(R.layout.fragment_search_film) {
    lateinit var binding: FragmentSearchFilmBinding
    lateinit var viewModel: FilmsViewModel
    lateinit var filmsAdapter: FilmsAdapter

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchFilmBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as FilmsActivity).viewModel
        setupRecyclerView()

        filmsAdapter.setOnClickListener {lifecycleScope.launch {
            if (it.description == null) {
                val description = viewModel.getFilm(it.filmId).await()?.description ?: return@launch
                it.description = description
            }
            val bundle = Bundle().apply {
                putSerializable("film", it)
            }
            findNavController().navigate(
                R.id.action_searchFilmFragment_to_filmDescriptionFragment,
                bundle
            )}
        }

        filmsAdapter.setOnLongClickListener { film, position ->
            if (film.favorite == true) {
                viewModel.deleteFilm(film).invokeOnCompletion {
                    filmsAdapter.notifyItemChanged(position)
                }
                Snackbar.make(view, Constants.FILM_DELETED_MESSAGE, Snackbar.LENGTH_SHORT).show()
            } else {
                viewModel.saveFilm(film).invokeOnCompletion {
                    filmsAdapter.notifyItemChanged(position)
                }
                Snackbar.make(view, Constants.FILM_ADDED_MESSAGE, Snackbar.LENGTH_SHORT).show()
            }
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener {editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_FILMS_TIME_DELAY)
                editable?.let {
                    if (editable.toString().isNotEmpty()) {
                        viewModel.searchFilmByKeyword(editable.toString())
                    }
                }
            }
        }

        viewModel.searchFilmByKeyword.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.FilmsSuccess -> {
                    hideProgressBar()
                    response.data?.let {filmsResponse ->
                        filmsAdapter.differ.submitList(filmsResponse.films.toList())
                        val totalPages = filmsResponse.films.size / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.searchFilmByKeywordPage == totalPages
                        if (isLastPage) {
                            binding.paginationProgressBar.setPadding(0,0,0,0)
                        }

                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let {message ->
                        Toast.makeText(activity, Constants.NETWORK_ERROR, Toast.LENGTH_LONG).show()

                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
                is Resource.FilmDiscSuccess -> {
                    hideProgressBar()
                }
                is Resource.NetworkError -> {
                    hideProgressBar()
                    findNavController().navigate(
                        R.id.action_searchFilmFragment_to_noConnectionFragment
                    )
                }
                null -> { }
            }
        })
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.searchFilmByKeyword(binding.etSearch.text.toString())
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        filmsAdapter = FilmsAdapter()
        binding.rvSearchFilms.apply {
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchFilmFragment.scrollListener)
        }
    }
}