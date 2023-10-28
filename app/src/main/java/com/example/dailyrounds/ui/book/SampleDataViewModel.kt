package com.example.dailyrounds.ui.book

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.dailyrounds.api.SampleJsonReader
import com.example.dailyrounds.model.Samples
import java.time.Instant
import java.time.ZoneId

class SampleDataViewModel(application: Application): AndroidViewModel(application) {

    private val SampleJsonData = SampleJsonReader(application.applicationContext)

    private val _sampleLiveData = MutableLiveData<List<Samples>>()
    val sampleLiveData: LiveData<List<Samples>> = _sampleLiveData

    private val _yearLiveData = MutableLiveData<List<Int>>()
    val yearLiveData: LiveData<List<Int>> = _yearLiveData

    private val _favoriteLiveData = MutableLiveData<Set<String>>()
    val favoriteLiveData: LiveData<Set<String>> = _favoriteLiveData

    private val favoriteBooks = mutableSetOf<String>()

    init {
        getFavoriteFromPreferences(getCurrentUser())
        favoriteBooks.addAll(getFavoriteFromPreferences(getCurrentUser()))
        val currentUser = getCurrentUser()
        getUserFavorites(currentUser)
    }

    fun getCurrentUser(): String {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("current_user", "") ?: ""
    }

    fun getData() {
        val sample: List<Samples> = SampleJsonData.readSampleData()
        val sortedData: List<Samples> = sample.sortedBy { getYearFromTimestamp(it.publishedChapterDate.toLong()).dec() }
        _sampleLiveData.value = sortedData
        _yearLiveData.value = sortedData.map{getYearFromTimestamp(it.publishedChapterDate.toLong())}.distinct().sorted()
    }

    private fun getUserFavorites(currentUser: String) {
        val favoriteSet = getFavoriteFromPreferences(currentUser)
        favoriteBooks.clear()
        favoriteBooks.addAll(favoriteSet)
        _favoriteLiveData.value = favoriteBooks
    }

    fun toggleFavorites(sample: Samples, currentUser: String) {
        val sampleId = sample.id
        if (sampleId in favoriteBooks) {
            favoriteBooks.remove(sampleId)
        } else {
            favoriteBooks.add(sampleId)
        }
        _favoriteLiveData.value = favoriteBooks

        saveFavoritesToPreferences(currentUser)
    }

    fun isFavoriteSelected(sampleId: String): Boolean {
        return sampleId in favoriteBooks
    }

    private fun saveFavoritesToPreferences(username: String) {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("favorites_$username", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("favorite_set", favoriteBooks)
        editor.apply()
    }

    private fun getFavoriteFromPreferences(username: String): Set<String> {
        val sharedPreferences = getApplication<Application>().getSharedPreferences("favorites_$username", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet("favorite_set", emptySet()) ?: emptySet()
    }

    fun getMinimumYear(): Int{
        return yearLiveData.value?.minOrNull()?: 0
    }

    fun getYearValue(s:Int): Int{
        return getYearFromTimestamp(s.toLong())
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