package com.example.dailyrounds.ui.book

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.dailyrounds.R
import com.example.dailyrounds.databinding.FragmentDataListBinding
import com.example.dailyrounds.databinding.FragmentDetailBinding
import com.example.dailyrounds.model.Samples
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar

class DetailFragment : Fragment() {
    lateinit var binding: FragmentDetailBinding
    private val viewModel: SampleDataViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = arguments
        val item : Samples? = args?.getParcelable("Sample")

        item?.let {

            Glide.with(requireContext())
                .load(it.image)
                .placeholder(R.drawable.ic_launcher_book_foreground)
                .error(R.drawable.ic_launcher_book_foreground)
                .centerCrop()
                .into(binding.bookImage)

            binding.bookTitle.text = it.title
            binding.bookScore.text = it.score.toString()
            binding.bookPopularity.text = it.popularity.toString()
            binding.bookPublished.text = "Published in ${viewModel.getYearValue(it.publishedChapterDate)}"
        }

        val isFavoriteSelected = viewModel.isFavoriteSelected(item?.id!!)

        val favBookIcon = if (isFavoriteSelected) {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_fill)
        } else {
            ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite)
        }
        binding.favoriteIv.setImageDrawable(favBookIcon)


        binding.favoriteIv.setOnClickListener {
            viewModel.toggleFavorites(item, viewModel.getCurrentUser())

            // Update the bookmark icon after toggling
            val newFavorite = viewModel.isFavoriteSelected(item.id)
            val newFavoriteIcon = if (newFavorite) {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_fill)
            } else {
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite)
            }
            binding.favoriteIv.setImageDrawable(newFavoriteIcon)
        }

    }

}