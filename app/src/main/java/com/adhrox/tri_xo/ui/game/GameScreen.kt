package com.adhrox.tri_xo.ui.game

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = hiltViewModel(),
    gameId: String,
    userId: String,
    owner: Boolean,
    navigateToHome: () -> Unit
){

}