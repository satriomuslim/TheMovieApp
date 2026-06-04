package com.main.themovieapp.data.remote.response

data class CastDto(
    val id: Int,
    val name: String,
    val character: String,
    val profile_path: String?
)