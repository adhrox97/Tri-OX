package com.adhrox.tri_xo.domain

import com.adhrox.tri_xo.data.dto.UserDto
import com.adhrox.tri_xo.domain.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseUser
import java.text.Bidi

interface AuthService {
    suspend fun registerUser(user: UserDto, password: String): Boolean
    suspend fun login(email: String, password: String): Boolean
    suspend fun loginAnonymously(): Boolean
    suspend fun loginWithGoogle(idToken: String): Boolean
    fun getCurrentUser():FirebaseUser?
    fun isUserLogged(): Boolean
    fun getGoogleClient(): GoogleSignInClient
}