package com.tinkofftest.filmbrowser.filmbrowserapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.tinkofftest.filmbrowser.R
import com.tinkofftest.filmbrowser.databinding.FragmentNoConnectionBinding
import com.tinkofftest.filmbrowser.databinding.FragmentTopFilmsBinding
import com.tinkofftest.filmbrowser.filmbrowserapp.adapters.FilmsAdapter
import com.tinkofftest.filmbrowser.filmbrowserapp.ui.FilmsActivity
import com.tinkofftest.filmbrowser.filmbrowserapp.ui.FilmsViewModel

class NoConnectionFragment : Fragment(R.layout.fragment_no_connection) {
    lateinit var binding: FragmentNoConnectionBinding
    lateinit var viewModel: FilmsViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNoConnectionBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as FilmsActivity).viewModel
        setUpRepeatButton()
    }

    private fun setUpRepeatButton() {
        viewModel.resetLiveData()
        binding.repeatButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

}