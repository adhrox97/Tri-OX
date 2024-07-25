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
import com.adhrox.tri_xo.ui.login.LoginScreen
import com.adhrox.tri_xo.ui.signup.SignUpScreen
import com.adhrox.tri_xo.ui.splash.SplashScreen

@Composable
fun ContentWrapper(navigationController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navigationController, startDestination = Login.route) {

        composable(Splash.route) {
            SplashScreen(
                modifier = modifier,
                navigateToLogin = {
                    navigationController.navigate(
                        Login.route
                    )
                },
                navigateToHome = {
                    navigationController.navigate(
                        Home.route
                    )
                }
            )
        }

        composable(Login.route) {
            LoginScreen(
                modifier = modifier,
                navigateToSignUp = {
                    navigationController.navigate(
                        SignUp.route
                    )
                },
                navigateToHome = {
                    navigationController.navigate(
                        Home.route
                    ) {
                        popUpTo(Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(SignUp.route) {
            SignUpScreen(
                modifier = modifier,
                navigateToHome = {
                    navigationController.navigate(
                        Home.route
                    ) {
                        popUpTo(Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Home.route) {
            HomeScreen(
                modifier = modifier,
                navigateToGame = { gameId, userName, owner ->
                    navigationController.navigate(
                        Game.createRoute(gameId, userName, owner)
                    )
                }
            )
        }
        composable(
            Game.route,
            arguments = listOf(
                navArgument("gameId") { type = NavType.StringType },
                navArgument("userName") { type = NavType.StringType },
                navArgument("owner") { type = NavType.BoolType }
            )
        ) {
            GameScreen(
                modifier = modifier,
                gameId = it.arguments?.getString("gameId").orEmpty(),
                userName = it.arguments?.getString("userName").orEmpty(),
                owner = it.arguments?.getBoolean("owner") ?: false,
                navigateToHome = {
                    navigationController.popBackStack(
                        route = Home.route,
                        inclusive = false
                    )
                }
            )
        }
    }
}

sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object SignUp : Routes("signUp")
    data object Home : Routes("home")
    data object Game : Routes("game/{gameId}/{userName}/{owner}") {
        fun createRoute(gameId: String, userName: String, owner: Boolean): String {
            return "game/$gameId/$userName/$owner"
        }
    }
}