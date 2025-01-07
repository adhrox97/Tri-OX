package com.adhrox.tri_xo.ui.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.adhrox.tri_xo.ui.core.Routes.*
import com.adhrox.tri_xo.ui.game.GameScreen
import com.adhrox.tri_xo.ui.home.HomeScreen
import com.adhrox.tri_xo.ui.home.HomeViewModel
import com.adhrox.tri_xo.ui.login.LoginScreen
import com.adhrox.tri_xo.ui.signup.SignUpScreen
import com.adhrox.tri_xo.ui.splash.SplashScreen
import com.adhrox.tri_xo.ui.userdetail.UserDetailScreen

@Composable
fun ContentWrapper(navigationController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navigationController, startDestination = Splash.route) {
        composable(Splash.route) {
            SplashScreen(
                modifier = modifier,
                navigateToLogin = {
                    navigationController.navigate(
                        Login.route
                    ){
                        popUpTo(Splash.route) { inclusive = true }
                    }
                },
                navigateToHome = {
                    navigationController.navigate(
                        UserMenu.route
                    ){
                        popUpTo(Splash.route) { inclusive = true }
                    }
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
                        UserMenu.route
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
                        UserMenu.route
                    ) {
                        popUpTo(Login.route) { inclusive = true }
                    }
                }
            )
        }
        navigation(
            startDestination = Home.route,
            route = UserMenu.route
        ){
            composable(Home.route) { entry ->
                val viewModel = entry.sharedViewModel<HomeViewModel>(navigationController,)
                //val state by viewModel.gameState.collectAsState()
                HomeScreen(
                    modifier = modifier,
                    homeViewModel = viewModel,
                    navigateToGame = { gameId, userName, owner ->
                        navigationController.navigate(
                            Game.createRoute(gameId, userName, owner)
                        )
                    },
                    navigateToDetail = {
                        navigationController.navigate(
                            UserDetail.route
                        )
                    }
                )
            }
            composable(UserDetail.route) { entry ->
                val viewModel = entry.sharedViewModel<HomeViewModel>(navigationController)
                val state by viewModel.gameState.collectAsState()
                UserDetailScreen(
                    modifier = modifier,
                    userInfo = state.user
                )
            }
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
    data object UserDetail: Routes("userDetail")
    data object UserMenu : Routes("userMenu")
    data object Game : Routes("game/{gameId}/{userName}/{owner}") {
        fun createRoute(gameId: String, userName: String, owner: Boolean): String {
            return "game/$gameId/$userName/$owner"
        }
    }
}

@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavHostController,
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
