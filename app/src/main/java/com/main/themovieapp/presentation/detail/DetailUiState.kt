package com.main.themovieapp.presentation.detail

import com.main.themovieapp.domain.model.Cast
import com.main.themovieapp.domain.model.Movie
import com.main.themovieapp.domain.model.VideoTrailer

data class DetailUiState(
    val isLoading: Boolean = true,
    val movie: Movie? = null,
    val trailer: VideoTrailer? = null,
    val cast: List<Cast> = emptyList(),
    val error: String? = null
)