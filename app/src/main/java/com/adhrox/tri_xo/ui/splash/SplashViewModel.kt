package com.adhrox.tri_xo.ui.splash

import androidx.lifecycle.ViewModel
import com.adhrox.tri_xo.domain.AuthService
import com.adhrox.tri_xo.ui.splash.SplashDestination.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(private val authService: AuthService): ViewModel() {

    fun checkDestination(): SplashDestination{
        val isUserLogged = authService.isUserLogged()
        return if (isUserLogged) Home else Login
    }
}

sealed class SplashDestination{
    data object Login: SplashDestination()
    data object Home: SplashDestination()
}