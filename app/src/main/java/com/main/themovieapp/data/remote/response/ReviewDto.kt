package com.main.themovieapp.data.remote.response

data class ReviewDto(
    val id: String,
    val author: String,
    val content: String,
    val created_at: String,
    val author_details: AuthorDetailsDto
)