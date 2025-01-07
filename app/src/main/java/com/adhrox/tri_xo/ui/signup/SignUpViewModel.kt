package com.adhrox.tri_xo.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adhrox.tri_xo.R
import com.adhrox.tri_xo.data.dto.UserDto
import com.adhrox.tri_xo.domain.AuthService
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.exceptions.UserAlreadyExistsException
import com.adhrox.tri_xo.domain.exceptions.UserHasNullEmail
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    private var _uiState = MutableStateFlow<AddNewUser>(AddNewUser())
    val uiState: StateFlow<AddNewUser> = _uiState

    fun onUserChange(user: String) {
        updateUserState(user)
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        updatePasswordState(password, _uiState.value.repeatPassword)
    }

    fun registerUser(
        userName: String,
        email: String,
        password: String,
        navigateToHome: () -> Unit
    ) {
        viewModelScope.launch {
            showLoading(true)
            try {
                val userDto = UserDto(userName = userName, email = email)
                val result = withContext(Dispatchers.IO) {
                    authService.registerUser(userDto, password)
                }

                if (result) navigateToHome()

            } catch (e: FirebaseAuthWeakPasswordException) {
                _uiState.update { it.copy(error = R.string.password_length_error) }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _uiState.update { it.copy(error = R.string.invalid_email_error) }
            } catch (e: UserAlreadyExistsException) {
                _uiState.update { it.copy(error = R.string.username_already_exists_error) }
            } catch (e: UserHasNullEmail) {
                _uiState.update { it.copy(error = R.string.problem_user_data_error) }
            } catch (e: FirebaseAuthUserCollisionException) {
                _uiState.update { it.copy(error = R.string.already_user_linked_email_error) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = R.string.unknown_error) }
            }
            showLoading(false)
        }
    }

    private fun showLoading(state: Boolean) {
        _uiState.update { it.copy(isLoading = state) }
    }

    fun resetErrorStatus() {
        _uiState.update { it.copy(error = null) }
    }

    fun onRepeatPasswordChange(repeatPassword: String) {
        updatePasswordState(_uiState.value.password, repeatPassword)
    }

    private fun updatePasswordState(password: String, repeatPassword: String) {
        _uiState.update {
            it.copy(
                password = password,
                repeatPassword = repeatPassword,
                isPasswordMatch = password == repeatPassword
            )
        }
    }

    private fun updateUserState(user: String) {
        _uiState.update {
            it.copy(
                user = user,
                isValidUserName = !(user.contains("\\") || user.contains("-") || user.contains(":") || user.contains(" "))
            )
        }
    }
}

data class AddNewUser(
    val user: String = "",
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = "",
    val isPasswordMatch: Boolean = false,
    val isValidUserName: Boolean = true,
    val isLoading: Boolean = false,
    val error: Int? = null
) {
    fun isValidUser() =
        user.isNotBlank() && email.isNotBlank() && password.isNotBlank() && isPasswordMatch && isValidUserName
}