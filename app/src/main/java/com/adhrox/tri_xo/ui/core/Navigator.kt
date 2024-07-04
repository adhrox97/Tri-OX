package com.adhrox.tri_xo.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.adhrox.tri_xo.ui.core.Routes.*
import com.adhrox.tri_xo.ui.game.GameScreen
import com.adhrox.tri_xo.ui.home.HomeScreen

@Composable
fun ContentWrapper(navigationController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navigationController, startDestination = Home.route) {
        composable(Home.route) {
            HomeScreen(
                modifier = modifier,
                navigateToGame = { gameId, userId, owner ->
                    navigationController.navigate(
                        Game.createRoute(gameId, userId, owner)
                )
            })
        }
        composable(Game.route,
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType },
                navArgument("userId") { type = NavType.StringType },
                navArgument("owner") { type = NavType.BoolType }
            )
        ) {
            GameScreen(
                modifier = modifier,
                gameId = it.arguments?.getString("gameId").orEmpty(),
                userId = it.arguments?.getString("userId").orEmpty(),
                owner = it.arguments?.getBoolean("owner") ?: false,
                navigateToHome = { navigationController.popBackStack(route = Home.route, inclusive = false) }
            )
        }
    }
}

sealed class Routes(val route: String) {
    data object Home : Routes("home")
    data object Game : Routes("game/{gameId}/{userId}/{owner}") {
        fun createRoute(gameId: String, userId: String, owner: Boolean): String {
            return "game/$gameId/$userId/$owner"
        }
    }
}