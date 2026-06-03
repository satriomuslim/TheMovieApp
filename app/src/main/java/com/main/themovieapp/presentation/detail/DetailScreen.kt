package com.main.themovieapp.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.main.themovieapp.presentation.components.MovieCard
import com.main.themovieapp.presentation.components.ReviewCard
import com.main.themovieapp.presentation.components.YouTubeTrailerPlayer
import com.main.themovieapp.presentation.theme.DarkBackground
import com.main.themovieapp.presentation.theme.NeonPurple
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DetailScreen(
    movieId: Int,
    onBackClick: () -> Unit,
    onSimilarMovieClick: (Int) -> Unit
) {
    val viewModel: DetailViewModel = koinViewModel(parameters = { parametersOf(movieId) })

    val uiState by viewModel.uiState.collectAsState()
    val reviews = viewModel.reviewsFlow.collectAsLazyPagingItems()
    val similarMovies = viewModel.similarMoviesFlow.collectAsLazyPagingItems()

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NeonPurple)
        }
        return
    }

    if (uiState.error != null) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.fetchMovieDetailsAndTrailer() },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
                ) {
                    Text("Retry", color = Color.White)
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        uiState.movie?.let { movie ->
            item {
                Box(modifier = Modifier.fillMaxWidth().height(450.dp)) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w780${movie.posterPath}",
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, DarkBackground)
                                )
                            )
                    )

                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(top = 40.dp, start = 16.dp)
                            .align(Alignment.TopStart)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                    ) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = movie.title,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = movie.releaseDate, color = Color.LightGray, style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(text = "★ ${String.format("%.1f", movie.voteAverage)}", color = Color(0xFFFFD700), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = movie.overview, color = Color.LightGray, style = MaterialTheme.typography.bodyMedium, lineHeight = 22.sp)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        uiState.trailer?.let { trailer ->
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = "Official Trailer",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    YouTubeTrailerPlayer(
                        videoId = trailer.key,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        item {
            Text(
                text = "Similar Movies",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(similarMovies.itemCount) { index ->
                    val movie = similarMovies[index]
                    movie?.let {
                        Box(modifier = Modifier.width(130.dp)) {
                            MovieCard(movie = it, onClick = { onSimilarMovieClick(it.id) })
                        }
                    }
                }

                if (similarMovies.loadState.append is LoadState.Loading) {
                    item {
                        Box(
                            modifier = Modifier.width(50.dp).height(195.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = NeonPurple)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = "User Reviews",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }

        items(reviews.itemCount) { index ->
            val review = reviews[index]
            review?.let {
                ReviewCard(review = it)
            }
        }

        reviews.apply {
            when (loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = NeonPurple)
                        }
                    }
                }
                is LoadState.Error -> {
                    val e = loadState.append as LoadState.Error
                    item {
                        Text(
                            text = "Failed to load reviews: ${e.error.localizedMessage}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {}
            }
        }
    }
}