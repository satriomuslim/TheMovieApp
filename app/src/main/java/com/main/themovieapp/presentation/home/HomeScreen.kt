package com.main.themovieapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.main.themovieapp.presentation.components.GenreChip
import com.main.themovieapp.presentation.components.GlobalErrorScreen
import com.main.themovieapp.presentation.components.MovieCard
import com.main.themovieapp.presentation.components.SearchMovieCard
import com.main.themovieapp.presentation.components.TrendingCarousel
import com.main.themovieapp.presentation.theme.DarkBackground
import com.main.themovieapp.presentation.theme.NeonPurple
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    onMovieClick: (Int) -> Unit
) {
    val viewModel: HomeViewModel = koinViewModel()
    val genreState by viewModel.genreState.collectAsState()
    val trendingState by viewModel.trendingState.collectAsState()
    val selectedGenreId by viewModel.selectedGenreId.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults = viewModel.searchMoviesFlow.collectAsLazyPagingItems()

    val movies = viewModel.moviesFlow.collectAsLazyPagingItems()

    val isPagingError = movies.loadState.refresh is LoadState.Error
    val isTrendingError = trendingState is TrendingUiState.Error
    val isGenreError = genreState is HomeUiState.Error
    val isSearchError = searchResults.loadState.refresh is LoadState.Error

    if (isPagingError || isTrendingError || isGenreError || isSearchError) {
        GlobalErrorScreen(
            onRetry = {
                viewModel.retryInitialFetch()
                movies.refresh()
                if (searchQuery.isNotEmpty()) searchResults.refresh()
            }
        )
        return
    }

    Column(modifier = Modifier.fillMaxSize().background(DarkBackground)) {

        SearchMovieCard(
            query = searchQuery,
            onQueryChange = { viewModel.updateSearchQuery(it) }
        )

        if (searchQuery.isNotEmpty()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        text = "Search Results",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                items(searchResults.itemCount) { index ->
                    searchResults[index]?.let { movie ->
                        MovieCard(movie = movie, onClick = { onMovieClick(movie.id) })
                    }
                }

                if (searchResults.loadState.refresh is LoadState.Loading || searchResults.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NeonPurple)
                        }
                    }
                }
            }

        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(
                            text = "Trending This Week",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        TrendingCarousel(trendingState = trendingState, onMovieClick = onMovieClick)
                    }
                }

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Column(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)) {
                        Text(
                            text = "Movies By Genre",
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        if (genreState is HomeUiState.Success) {
                            val genres = (genreState as HomeUiState.Success).genres
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(genres) { genre ->
                                    val isSelected = selectedGenreId == genre.id
                                    GenreChip(
                                        name = genre.name,
                                        isSelected = isSelected,
                                        onClick = { viewModel.onGenreSelected(genre.id) }
                                    )
                                }
                            }
                        }
                    }
                }

                items(movies.itemCount) { index ->
                    val movie = movies[index]
                    movie?.let {
                        MovieCard(movie = it, onClick = { onMovieClick(it.id) })
                    }
                }

                if (movies.loadState.append is LoadState.Loading) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NeonPurple)
                        }
                    }
                }
            }
        }
    }
}