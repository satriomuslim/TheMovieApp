package com.main.themovieapp.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.main.themovieapp.data.paging.MoviePagingSource
import com.main.themovieapp.data.paging.ReviewPagingSource
import com.main.themovieapp.data.paging.SearchPagingSource
import com.main.themovieapp.data.paging.SimilarMoviePagingSource
import com.main.themovieapp.data.remote.api.TMDBApi
import com.main.themovieapp.domain.model.Genre
import com.main.themovieapp.domain.model.Movie
import com.main.themovieapp.domain.model.Review
import com.main.themovieapp.domain.model.VideoTrailer
import com.main.themovieapp.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class MovieRepositoryImpl(private val api: TMDBApi) : MovieRepository {

    override suspend fun getGenres(): Result<List<Genre>> {
        return try {
            val response = api.getGenres()
            Result.success(response.genres.map { Genre(it.id, it.name) })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMoviesByGenre(genreId: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false,
                prefetchDistance = 2
            ),
            pagingSourceFactory = { MoviePagingSource(api, genreId) }
        ).flow
    }

    override suspend fun getMovieDetails(movieId: Int): Result<Movie> {
        return try {
            val dto = api.getMovieDetails(movieId)
            val movie = Movie(
                id = dto.id,
                title = dto.title,
                posterPath = dto.poster_path,
                backdropPath = dto.backdrop_path,
                overview = dto.overview,
                releaseDate = dto.release_date ?: "-",
                voteAverage = dto.vote_average
            )
            Result.success(movie)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getMovieReviews(movieId: Int): Flow<PagingData<Review>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { ReviewPagingSource(api, movieId) }
        ).flow
    }

    override fun getSimilarMovies(movieId: Int): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { SimilarMoviePagingSource(api, movieId) }
        ).flow
    }

    override suspend fun getMovieTrailer(movieId: Int): Result<VideoTrailer?> {
        return try {
            val response = api.getMovieVideos(movieId)

            val trailerDto = response.results.firstOrNull {
                it.site.equals("YouTube", ignoreCase = true) &&
                        it.type.equals("Trailer", ignoreCase = true)
            } ?: response.results.firstOrNull {
                it.site.equals("YouTube", ignoreCase = true)
            }

            val trailer = trailerDto?.let { VideoTrailer(it.key, it.site, it.type) }
            Result.success(trailer)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTrendingMovies(): Result<List<Movie>> {
        return try {
            val response = api.getTrendingMovies()
            val movies = response.results.take(10).map {
                Movie(
                    id = it.id,
                    title = it.title,
                    posterPath = it.poster_path,
                    backdropPath = it.backdrop_path,
                    overview = it.overview,
                    releaseDate = it.release_date ?: "",
                    voteAverage = it.vote_average
                )
            }
            Result.success(movies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun searchMovies(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = { SearchPagingSource(api, query) }
        ).flow
    }
}