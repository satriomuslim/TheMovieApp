package com.main.themovieapp.data.remote.api

import com.main.themovieapp.data.remote.response.GenreResponse
import com.main.themovieapp.data.remote.response.MovieDto
import com.main.themovieapp.data.remote.response.MoviePagingResponse
import com.main.themovieapp.data.remote.response.ReviewPagingResponse
import com.main.themovieapp.data.remote.response.VideoResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TMDBApi {
    @GET("genre/movie/list")
    suspend fun getGenres() : GenreResponse

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int
    ): MoviePagingResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int)
    : MovieDto

    @GET("movie/{movie_id}/reviews")
    suspend fun getMovieReviews(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int
    ): ReviewPagingResponse

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: Int)
    : VideoResponse

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(
        @Path("movie_id") movieId: Int,
        @Query("page") page: Int
    ): MoviePagingResponse

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(): MoviePagingResponse

    @GET("search/movie")
    suspend fun searchMovies(
        @Query("query") query: String,
        @Query("page") page: Int
    ): MoviePagingResponse
}