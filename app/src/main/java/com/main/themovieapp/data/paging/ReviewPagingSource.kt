package com.main.themovieapp.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.main.themovieapp.data.remote.api.TMDBApi
import com.main.themovieapp.domain.model.Review
import retrofit2.HttpException
import java.io.IOException

class ReviewPagingSource(
    private val api: TMDBApi,
    private val movieId: Int
) : PagingSource<Int, Review>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
        val position = params.key ?: 1
        return try {
            val response = api.getMovieReviews(movieId, position)
            val reviews = response.results.map {
                Review(
                    id = it.id,
                    author = it.author,
                    content = it.content,
                    rating = it.author_details.rating,
                    createdAt = it.created_at.take(10),
                    avatarPath = it.author_details.avatar_path
                )
            }

            LoadResult.Page(
                data = reviews,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (reviews.isEmpty()) null else position + 1
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}