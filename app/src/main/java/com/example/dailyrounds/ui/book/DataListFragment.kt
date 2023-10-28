package com.example.dailyrounds.ui.book

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dailyrounds.adapter.SampleAdapter
import com.example.dailyrounds.databinding.FragmentDataListBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class DataListFragment : Fragment() {
    lateinit var binding: FragmentDataListBinding
    private val viewModel: SampleDataViewModel by activityViewModels()
    private lateinit var adapter: SampleAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDataListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpTabLayout()
        closeAppOnBackPress()
        setupRecyclerView()

        binding.logoutButton.setOnClickListener {
            val sharedPreferences =
                requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            sharedPreferences.edit().remove("current_user").apply()
            Snackbar.make(
                requireView(),"Successfully logged out!!", Snackbar.LENGTH_SHORT).show()
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        var previousTabPosition = 0
        adapter = SampleAdapter(viewModel)
        binding.recyclerView.adapter = adapter

        viewModel.sampleLiveData.observe(viewLifecycleOwner) { samples ->
            adapter.differ.submitList(null)
            adapter.differ.submitList(samples)
            binding.recyclerView.scrollToPosition(0)


            binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
                    if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
                        val year = getItemYear(firstVisibleItemPosition)
                        val currentTabPosition = getTabPositionForYear(year)
                        //selectTabForYear(year)

                        // Update the selected tab when scrolling up or down
                        if (dy > 0 && currentTabPosition != previousTabPosition) {
                            binding.tabLayout.getTabAt(currentTabPosition)?.select()
                            previousTabPosition = currentTabPosition
                        } else if (dy < 0 && currentTabPosition != previousTabPosition) {
                            binding.tabLayout.getTabAt(currentTabPosition)?.select()
                            previousTabPosition = currentTabPosition
                        }

                    }
                }
            })

        }

        viewModel.favoriteLiveData.observe(viewLifecycleOwner) { favorites ->
            adapter.updateFavorites(favorites)
        }

        viewModel.getData()

    }

    fun getItemYear(position: Int): Int{
        val dataItem = adapter.differ.currentList[position]
        val year = dataItem.publishedChapterDate
        // Assuming 'year' is a property of your data type
        return viewModel.getYearValue(year)
    }

    /*fun selectTabForYear(year: Int) {
        viewModel.yearLiveData.observe(viewLifecycleOwner) { years ->
            val tabPosition = years.indexOf(year)
            if (tabPosition != -1) {
                binding.tabLayout.getTabAt(tabPosition)?.select()
            }
        }
    }*/

    fun getTabPositionForYear(selectedYear: Int): Int {
        return viewModel.yearLiveData.value!!.indexOf(selectedYear)
    }

    private fun setUpTabLayout(){
        viewModel.yearLiveData.observe(viewLifecycleOwner){ years ->
            for(year in years){
                val tab = binding.tabLayout.newTab()
                tab.text = year.toString()
                binding.tabLayout.addTab(tab)
            }
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val selectedYear = tab.text.toString().toInt()
                //updateRecyclerViewForYear(selectedYear)
                scrollToYear(selectedYear)
//                val position = findPositionForYear(selectedYear)
//                binding.recyclerView.scrollToPosition(position)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // Handle unselected tabs if needed
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // Handle reselected tabs if needed
            }
        })

    }

    fun scrollToYear(selectedYear: Int) {
        val position = findPositionForYear(selectedYear)
        val offset = 0

        val scrollToPosition = position + 6;
        val layoutManager = binding.recyclerView.layoutManager as LinearLayoutManager

//        layoutManager.smoothScrollToPosition(binding.recyclerView, null, scrollToPosition)

        layoutManager.scrollToPositionWithOffset(position, 0)
    }

    private fun findPositionForYear(selectedYear: Int): Int {
        var position = 0
        for ((index, item) in viewModel.sampleLiveData.value!!.withIndex()) {
            if (viewModel.getYearValue(item.publishedChapterDate) == selectedYear) {
                position = index
                break
            }
        }
        return position
    }


    fun updateRecyclerViewForYear(selectedYear: Int) {
        viewModel.sampleLiveData.observe(viewLifecycleOwner){ it ->
            val filterdSample = it.filter { viewModel.getYearValue(it.publishedChapterDate) == selectedYear }
            adapter.differ.submitList(filterdSample)
        }

    }

    private fun closeAppOnBackPress() {
        binding.root.isFocusableInTouchMode = true
        binding.root.requestFocus()
        binding.root.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
                requireActivity().finish()
                true
            } else {
                false
            }
        }
    }
}