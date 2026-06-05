package com.main.themovieapp.presentation.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.main.themovieapp.presentation.home.TrendingUiState
import com.main.themovieapp.presentation.theme.NeonPurple
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.math.absoluteValue

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun TrendingCarousel(
    trendingState: TrendingUiState,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onMovieClick: (Int, String) -> Unit
) {
    when (trendingState) {
        is TrendingUiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = NeonPurple)
            }
        }
        is TrendingUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Failed to load trending movies", color = Color.Gray)
            }
        }
        is TrendingUiState.Success -> {
            val movies = trendingState.movies
            val pagerState = rememberPagerState(pageCount = { movies.size })

            with(sharedTransitionScope) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    pageSpacing = 16.dp,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                ) { page ->
                    val movie = movies[page]

                    val pageOffset = (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    val scaleFactor = 0.85f + (1f - 0.85f) * (1f - pageOffset.absoluteValue.coerceIn(0f, 1f))

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                scaleX = scaleFactor
                                scaleY = scaleFactor
                            }
                            .clip(RoundedCornerShape(16.dp))
                            .clickable {
                                val poster = movie.posterPath ?: ""
                                val encodedPoster = URLEncoder.encode(poster, StandardCharsets.UTF_8.toString())
                                onMovieClick(movie.id, encodedPoster)
                            }
                    ) {
                        AsyncImage(
                            model = "https://image.tmdb.org/t/p/w780${movie.backdropPath}",
                            contentDescription = movie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .sharedElement(
                                    rememberSharedContentState(key = "image_${movie.id}"),
                                    animatedVisibilityScope = animatedVisibilityScope
                                )
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f)),
                                        startY = 200f
                                    )
                                )
                        )
                        Text(
                            text = movie.title,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.align(
                                Alignment.BottomStart).padding(16.dp)
                        )
                    }
                }
            }
        }
    }
}