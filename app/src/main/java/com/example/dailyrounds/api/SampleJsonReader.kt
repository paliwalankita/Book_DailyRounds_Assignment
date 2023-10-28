package com.example.dailyrounds.api

import android.content.Context
import com.example.dailyrounds.model.Samples
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader

class SampleJsonReader(val context: Context) {

        fun readSampleData(): List<Samples> {
            val inputStream = context.assets.open("Sample.json")
            val reader = InputStreamReader(inputStream)
            val listType = object : TypeToken<List<Samples>>() {}.type
            return Gson().fromJson(reader, listType)
        }
}