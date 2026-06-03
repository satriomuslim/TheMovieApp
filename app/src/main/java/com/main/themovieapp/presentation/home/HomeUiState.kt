package com.main.themovieapp.presentation.home

import com.main.themovieapp.domain.model.Genre
import com.main.themovieapp.domain.model.Movie

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(val genres: List<Genre>) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}

sealed class TrendingUiState {
    data object Loading : TrendingUiState()
    data class Success(val movies: List<Movie>) : TrendingUiState()
    data class Error(val message: String) : TrendingUiState()
}