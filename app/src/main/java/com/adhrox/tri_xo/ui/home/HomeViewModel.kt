package com.adhrox.tri_xo.ui.home

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {
    fun onCreateGame(navigateToGame: (String, String, Boolean) -> Unit) {

    }

    fun onJoinGame(gameId: String, navigateToGame: (String, String, Boolean) -> Unit) {

    }
}