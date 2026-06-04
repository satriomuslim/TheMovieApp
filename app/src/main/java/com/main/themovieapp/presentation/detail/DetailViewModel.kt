package com.main.themovieapp.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.main.themovieapp.domain.model.Movie
import com.main.themovieapp.domain.model.Review
import com.main.themovieapp.domain.repository.MovieRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    private val repository: MovieRepository,
    private val movieId: Int
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()
    val reviewsFlow: Flow<PagingData<Review>> = repository.getMovieReviews(movieId)
        .cachedIn(viewModelScope)
    val similarMoviesFlow: Flow<PagingData<Movie>> = repository.getSimilarMovies(movieId)
        .cachedIn(viewModelScope)

    init {
        fetchMovieDetails()
    }

    fun fetchMovieDetails() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val movieDeferred = async { repository.getMovieDetails(movieId) }
            val trailerDeferred = async { repository.getMovieTrailer(movieId) }
            val castDeferred = async { repository.getMovieCast(movieId) }

            val movieResult = movieDeferred.await()
            val trailerResult = trailerDeferred.await()
            val castResult = castDeferred.await()

            if (movieResult.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    movie = movieResult.getOrNull(),
                    trailer = trailerResult.getOrNull(),
                    cast = castResult.getOrDefault(emptyList()),
                    error = null
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = movieResult.exceptionOrNull()?.localizedMessage ?: "Gagal memuat data film"
                )
            }
        }
    }
}