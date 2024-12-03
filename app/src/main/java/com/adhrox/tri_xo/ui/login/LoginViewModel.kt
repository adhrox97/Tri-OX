package com.adhrox.tri_xo.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhrox.tri_xo.domain.AuthService
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.exceptions.UserAlreadyExistsException
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val auth: AuthService): ViewModel() {

    private val _uiState = MutableStateFlow<LoginState>(LoginState())
    val uiState: StateFlow<LoginState> = _uiState

    fun login(email: String, password: String, navigateToHome: () -> Unit){
        viewModelScope.launch {
            showLoading(true)
            try {
                val result = withContext(Dispatchers.IO){ auth.login(email, password) }

                if (result) navigateToHome() else Log.i("adhrox", "Error")

            } catch (e: Exception){
                _uiState.update { it.copy(error = "Datos incorrectos") }
            }
            showLoading(false)
        }
    }
    fun signUpAnonymously(navigateToHome: () -> Unit){
        viewModelScope.launch {
            showLoading(true)
            try {
                val result = withContext(Dispatchers.IO){ auth.loginAnonymously() }

                if (result){
                    navigateToHome()
                } else {
                    Log.i("adhrox", "Error null")
                }
            } catch (e: UserAlreadyExistsException) {
                navigateToHome()
            } catch (e: Exception) {
                Log.i("adhrox", "Error: ${e.message}")
            }
            showLoading(false)
        }
    }

    private fun showLoading(state: Boolean){
        _uiState.update { it.copy(isLoading = state) }
    }

    fun resetErrorStatus(){
        _uiState.update { it.copy(error = null) }
    }

    fun loginWithGoogle(idToken: String, navigateToHome: () -> Unit) {
        viewModelScope.launch {
            showLoading(true)
            try {
                val result = withContext(Dispatchers.IO) {
                    auth.loginWithGoogle(idToken)
                }
                if (result) navigateToHome()
            } catch (e: UserAlreadyExistsException) {
                navigateToHome()
            } catch (e: Exception) {
                Log.i("adhrox", "Error: ${e.message}")
            }
            showLoading(false)
        }
    }

    fun onGoogleLoginSelected(googleLauncherLogin: (GoogleSignInClient) -> Unit){
        val gsc = auth.getGoogleClient()
        googleLauncherLogin(gsc)
    }
}

data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null
)