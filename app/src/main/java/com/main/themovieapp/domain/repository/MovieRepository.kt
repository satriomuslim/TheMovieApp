package com.main.themovieapp.domain.repository

import androidx.paging.PagingData
import com.main.themovieapp.domain.model.*
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    suspend fun getGenres(): Result<List<Genre>>
    fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>>
    suspend fun getMovieDetails(movieId: Int): Result<Movie>
    fun getMovieReviews(movieId: Int): Flow<PagingData<Review>>
    fun getSimilarMovies(movieId: Int): Flow<PagingData<Movie>>
    suspend fun getMovieTrailer(movieId: Int): Result<VideoTrailer?>
    suspend fun getTrendingMovies(): Result<List<Movie>>
    fun searchMovies(query: String): Flow<PagingData<Movie>>
}