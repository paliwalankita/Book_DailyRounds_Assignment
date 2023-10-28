package com.example.dailyrounds.adapter

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dailyrounds.R
import com.example.dailyrounds.databinding.SampleListBinding
import com.example.dailyrounds.model.Samples
import com.example.dailyrounds.ui.book.DataListFragmentDirections
import com.example.dailyrounds.ui.book.SampleDataViewModel
import java.time.Instant
import java.time.ZoneId

class SampleAdapter(private val viewModel: SampleDataViewModel) : RecyclerView.Adapter<SampleAdapter.SampleViewHolder>() {

    private val favoriteBooks = mutableSetOf<String>()

    fun updateFavorites(favorites: Set<String>) {
        favoriteBooks.clear()
        favoriteBooks.addAll(favorites)
        notifyDataSetChanged()
    }

    inner class SampleViewHolder(val binding: SampleListBinding) : RecyclerView.ViewHolder(binding.root){

        init {
            binding.root.setOnClickListener {

                val data = differ.currentList[adapterPosition]

                val action = DataListFragmentDirections.actionDataListFragmentToDetailFragment(data)
                Navigation.findNavController(it).navigate(action)
            }

            binding.favoriteIv.setOnClickListener {
                val book = differ.currentList[adapterPosition]
                viewModel.toggleFavorites(book, viewModel.getCurrentUser())
            }
        }

        fun bind(sample: Samples) {
            binding.tvTitle.text = sample.title
            binding.tvScore.text = sample.score.toString()
            binding.tvYear.text = "Published in ${sample.publishedChapterDate?.let { getYearFromTimestamp(it.toLong()).toString()}}"

            Glide.with(itemView)
                .load(sample.image)
                .placeholder(R.drawable.ic_launcher_book_foreground)
                .error(R.drawable.ic_launcher_book_foreground)
                .centerCrop()
                .into(binding.ivSampleImage)

            if (viewModel.isFavoriteSelected(sample.id)) {
                binding.favoriteIv.setImageResource(R.drawable.ic_favorite_fill)
            } else {
                binding.favoriteIv.setImageResource(R.drawable.ic_favorite)
            }

        }
    }

    private val differCallBack = object: DiffUtil.ItemCallback<Samples>(){
        override fun areItemsTheSame(oldItem: Samples, newItem: Samples): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Samples, newItem: Samples): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SampleViewHolder {
        val binding = SampleListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SampleViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: SampleViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
    }

    private fun getYearFromTimestamp(timestamp: Long): Int {
        val instant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Instant.ofEpochSecond(timestamp)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val zoneId = ZoneId.systemDefault() // You can specify a different time zone if needed
        val year = instant.atZone(zoneId).year
        return year
    }
}