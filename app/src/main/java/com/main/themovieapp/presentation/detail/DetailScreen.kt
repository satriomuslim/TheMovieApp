package com.main.themovieapp.presentation.detail

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DetailScreen(
    movieId: Int,
    initialPosterPath: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackClick: () -> Unit,
    onSimilarMovieClick: (Int, String) -> Unit
) {
    val viewModel: DetailViewModel = koinViewModel(parameters = { parametersOf(movieId) })

    val uiState by viewModel.uiState.collectAsState()
    val reviews = viewModel.reviewsFlow.collectAsLazyPagingItems()
    val similarMovies = viewModel.similarMoviesFlow.collectAsLazyPagingItems()

    if (uiState.error != null && !uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(DarkBackground), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { viewModel.fetchMovieDetails() },
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
        item {
            with(sharedTransitionScope) {
                Box(modifier = Modifier.fillMaxWidth().height(450.dp)) {
                    val targetPoster = uiState.movie?.posterPath ?: initialPosterPath
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w780$targetPoster",
                        contentDescription = "Hero Image",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .sharedElement(
                                rememberSharedContentState(key = "image_$movieId"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
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
        }

        if (uiState.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = NeonPurple)
                }
            }
        } else {
            uiState.movie?.let { movie ->
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

            if (uiState.cast.isNotEmpty()) {
                item {
                    Text(
                        text = "Cast",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(uiState.cast.size) { index ->
                            val actor = uiState.cast[index]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(80.dp)
                            ) {
                                if (actor.profilePath != null) {
                                    AsyncImage(
                                        model = "https://image.tmdb.org/t/p/w200${actor.profilePath}",
                                        contentDescription = actor.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(72.dp).clip(CircleShape).background(Color.DarkGray)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.size(72.dp).clip(CircleShape).background(Color(0xFF2B2B36)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = actor.name.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = actor.name, color = Color.White, style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = actor.character, color = Color.Gray, style = MaterialTheme.typography.labelSmall,
                                    textAlign = TextAlign.Center, maxLines = 2, overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
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

                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(similarMovies.itemCount) { index ->
                        val movie = similarMovies[index]
                        movie?.let {
                            Box(modifier = Modifier.width(130.dp)) {
                                MovieCard(
                                    movie = it,
                                    sharedTransitionScope = sharedTransitionScope,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    onClick = {
                                        val poster = it.posterPath ?: ""
                                        val encodedPoster = URLEncoder.encode(poster, StandardCharsets.UTF_8.toString())
                                        onSimilarMovieClick(it.id, encodedPoster)
                                    }
                                )
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
}