package com.tinkofftest.filmbrowser.filmbrowserapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.tinkofftest.filmbrowser.R
import com.tinkofftest.filmbrowser.databinding.FragmentFavoritesFilmsBinding
import com.tinkofftest.filmbrowser.filmbrowserapp.adapters.FilmsAdapter
import com.tinkofftest.filmbrowser.filmbrowserapp.ui.FilmsActivity
import com.tinkofftest.filmbrowser.filmbrowserapp.ui.FilmsViewModel
import com.tinkofftest.filmbrowser.filmbrowserapp.util.Constants
import kotlinx.coroutines.launch

class FavoritesFilmsFragment : Fragment(R.layout.fragment_favorites_films) {

    lateinit var binding: FragmentFavoritesFilmsBinding
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
        binding = FragmentFavoritesFilmsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as FilmsActivity).viewModel
        setUpUi()

        filmsAdapter.setOnClickListener {lifecycleScope.launch {
                if (it.description == null) {
                    val description = viewModel.getFilm(it.filmId).await()?.description ?: return@launch
                    it.description = description
                }
                val bundle = Bundle().apply {
                    putSerializable("film", it)
                }
                findNavController().navigate(
                    R.id.action_favoritesFilmsFragment_to_filmDescriptionFragment,
                    bundle
                )
            }

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

        viewModel.getAllFilms().observe(viewLifecycleOwner, Observer { films ->
            filmsAdapter.differ.submitList(films)
        })
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
                viewModel.getTopFilms()
                isScrolling = false
            }
        }
    }

    private fun setupRecyclerView() {
        filmsAdapter = FilmsAdapter()
        binding.rvFavoritesFilms.apply{
            adapter = filmsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@FavoritesFilmsFragment.scrollListener)
        }

    }

    private fun setUpSearchButton() {
        viewModel.resetLiveData()
        binding.ivSearchIcon.setOnClickListener {
            findNavController().navigate(
                R.id.action_favoritesFilmsFragment_to_searchFilmFragment
            )
        }
    }

    private fun setTopFilmsButton() {
        viewModel.resetLiveData()
        binding.btTop.setOnClickListener {
            findNavController().navigate(
                R.id.action_favoritesFilmsFragment_to_topFilmsListFragment
            )
        }
    }

    private fun setUpUi() {
        setupRecyclerView()
        setUpSearchButton()
        setTopFilmsButton()
    }
}