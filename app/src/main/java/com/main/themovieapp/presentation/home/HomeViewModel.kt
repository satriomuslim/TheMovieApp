package com.main.themovieapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.main.themovieapp.domain.model.Movie
import com.main.themovieapp.domain.repository.MovieRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    private val _genreState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val genreState: StateFlow<HomeUiState> = _genreState.asStateFlow()
    private val _selectedGenreId = MutableStateFlow(28)
    val selectedGenreId: StateFlow<Int> = _selectedGenreId.asStateFlow()
    private val _trendingState = MutableStateFlow<TrendingUiState>(TrendingUiState.Loading)
    val trendingState: StateFlow<TrendingUiState> = _trendingState.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        fetchGenres()
        fetchTrendingMovies()
    }

    private fun fetchGenres() {
        viewModelScope.launch {
            repository.getGenres()
                .onSuccess { genres ->
                    _genreState.value = HomeUiState.Success(genres)
                    if (genres.isNotEmpty()) {
                        _selectedGenreId.value = genres.first().id
                    }
                }
                .onFailure { exception ->
                    _genreState.value = HomeUiState.Error(exception.message ?: "Error")
                }
        }
    }

    private fun fetchTrendingMovies() {
        viewModelScope.launch {
            repository.getTrendingMovies()
                .onSuccess { movies ->
                    _trendingState.value = TrendingUiState.Success(movies)
                }
                .onFailure { exception ->
                    _trendingState.value = TrendingUiState.Error(exception.message ?: "Unknown Error")
                }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun onGenreSelected(genreId: Int) {
        _selectedGenreId.value = genreId
    }

    fun retryInitialFetch() {
        fetchGenres()
        fetchTrendingMovies()
    }

    val searchMoviesFlow: Flow<PagingData<Movie>> = _searchQuery
        .debounce(500)
        .filter { it.trim().isNotEmpty() }
        .flatMapLatest { query ->
            repository.searchMovies(query.trim())
        }
        .cachedIn(viewModelScope)

    val moviesFlow: Flow<PagingData<Movie>> = _selectedGenreId.flatMapLatest { genreId ->
        repository.getMoviesByGenre(genreId)
    }.cachedIn(viewModelScope)
}