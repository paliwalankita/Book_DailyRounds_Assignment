package com.example.dailyrounds.api

import android.content.Context
import com.example.dailyrounds.model.Countries
import com.example.dailyrounds.model.Samples
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class CountryJsonReader(val context: Context) {

    fun readCountriesFromJson(): Map<String, Countries> {
        val inputStream = context.assets.open("Countries.json")
        val reader = InputStreamReader(inputStream)
        val listType = object : TypeToken<Map<String, Countries>>() {}.type
        return Gson().fromJson(reader, listType)
    }
}