package com.main.themovieapp.presentation.detail

import com.main.themovieapp.domain.model.Movie
import com.main.themovieapp.domain.model.VideoTrailer

data class DetailUiState(
    val isLoading: Boolean = true,
    val movie: Movie? = null,
    val trailer: VideoTrailer? = null,
    val error: String? = null
)