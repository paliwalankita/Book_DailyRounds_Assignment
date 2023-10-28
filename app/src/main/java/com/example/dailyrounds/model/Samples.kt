package com.example.dailyrounds.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Samples(
    val id: String,
    val image: String,
    val popularity: Int?,
    val publishedChapterDate: Int,
    val score: Double?,
    val title: String?
): Parcelable