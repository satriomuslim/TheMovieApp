package com.main.themovieapp.domain.model

data class Review(
    val id: String,
    val author: String,
    val content: String,
    val rating: Double?,
    val createdAt: String,
    val avatarPath: String?
)