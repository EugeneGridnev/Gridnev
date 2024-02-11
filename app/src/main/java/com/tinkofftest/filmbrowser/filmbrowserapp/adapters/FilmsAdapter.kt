package com.tinkofftest.filmbrowser.filmbrowserapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tinkofftest.filmbrowser.databinding.ItemFilmLayoutBinding
import com.tinkofftest.filmbrowser.filmbrowserapp.models.Film

class FilmsAdapter : RecyclerView.Adapter<FilmsAdapter.FilmViewHolder>() {

    inner class FilmViewHolder(val binding: ItemFilmLayoutBinding)
        : RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Film>() {
        override fun areItemsTheSame(oldItem: Film, newItem: Film): Boolean {
            return oldItem.filmId == newItem.filmId
        }

        override fun areContentsTheSame(oldItem: Film, newItem: Film): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilmViewHolder {
        return FilmViewHolder(
            ItemFilmLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
                )
            )
    }

    override fun onBindViewHolder(holder: FilmViewHolder, position: Int) {
        val film = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(film.posterUrlPreview).into(holder.binding.ivFilmImage)
            holder.binding.tvFilmTitle.text = film.nameRu
            holder.binding.tvFilmGenre.text = "${film.genres?.firstOrNull()?.genre ?: ""} (${film.year})"
            if (film.favorite == true) {
                holder.binding.ivRating.visibility = View.VISIBLE
            } else {
                holder.binding.ivRating.visibility = View.GONE
            }

            setOnClickListener {
                onItemClickListener?.let { it(film)}
            }

            setOnLongClickListener {
                onItemLongClickListener?.let {
                    it(film, position)
                    true
                } ?: false
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    private var onItemClickListener: ((Film) -> Unit)? = null
    private var onItemLongClickListener: ((Film, Int) -> Unit)? = null

    fun setOnClickListener(listener: (Film) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnLongClickListener(listener: (Film, Int) -> Unit) {
        onItemLongClickListener = listener
    }
}