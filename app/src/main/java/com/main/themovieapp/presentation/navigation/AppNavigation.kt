package com.main.themovieapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.main.themovieapp.presentation.detail.DetailScreen
import com.main.themovieapp.presentation.home.HomeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {

        composable("home") {
            HomeScreen(
                onMovieClick = { movieId ->
                    navController.navigate("detail/$movieId")
                }
            )
        }

        composable(
            route = "detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: return@composable

            DetailScreen(
                movieId = movieId,
                onBackClick = { navController.popBackStack() },
                onSimilarMovieClick = { newMovieId ->
                    navController.navigate("detail/$newMovieId")
                }
            )
        }
    }
}