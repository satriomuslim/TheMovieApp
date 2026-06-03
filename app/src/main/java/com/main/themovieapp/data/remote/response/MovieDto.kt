package com.main.themovieapp.data.remote.response

data class MovieDto(
    val id: Int,
    val title: String,
    val poster_path: String?,
    val backdrop_path: String?,
    val overview: String,
    val release_date: String?,
    val vote_average: Double
)