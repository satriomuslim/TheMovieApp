package com.main.themovieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.main.themovieapp.data.remote.api.TMDBApi
import com.main.themovieapp.domain.model.Movie
import retrofit2.HttpException
import java.io.IOException

class SearchPagingSource(
    private val api: TMDBApi,
    private val query: String
) : PagingSource<Int, Movie>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Movie> {
        val position = params.key ?: 1
        return try {
            val response = api.searchMovies(query, position)
            val movies = response.results.map {
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

            LoadResult.Page(
                data = movies,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (movies.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Movie>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}