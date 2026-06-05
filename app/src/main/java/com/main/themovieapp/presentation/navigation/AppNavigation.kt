package com.main.themovieapp.presentation.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.main.themovieapp.presentation.detail.DetailScreen
import com.main.themovieapp.presentation.home.HomeScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    SharedTransitionLayout {
        NavHost(navController = navController, startDestination = "home") {

            composable("home") {
                HomeScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onMovieClick = { movieId, encodedPoster ->
                        navController.navigate("detail/$movieId?poster=$encodedPoster")
                    }
                )
            }

            composable(
                route = "detail/{movieId}?poster={poster}",
                arguments = listOf(
                    navArgument("movieId") { type = NavType.IntType },
                    navArgument("poster") { type = NavType.StringType; defaultValue = "" }
                )
            ) { backStackEntry ->
                val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable
                val posterRaw = backStackEntry.arguments?.getString("poster") ?: ""

                val decodedPoster = URLDecoder.decode(posterRaw, StandardCharsets.UTF_8.toString())

                DetailScreen(
                    movieId = movieId,
                    initialPosterPath = decodedPoster,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onBackClick = { navController.popBackStack() },
                    onSimilarMovieClick = { newId, newPoster ->
                        navController.navigate("detail/$newId?poster=$newPoster")
                    }
                )
            }
        }
    }
}