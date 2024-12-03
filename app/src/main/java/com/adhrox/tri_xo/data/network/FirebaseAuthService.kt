package com.adhrox.tri_xo.data.network

import android.content.Context
import android.util.Log
import com.adhrox.tri_xo.R
import com.adhrox.tri_xo.data.dto.UserDto
import com.adhrox.tri_xo.domain.AuthService
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.exceptions.UserHasNullEmail
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthService @Inject constructor(private val auth: FirebaseAuth, private val repository: Repository, private val context: Context): AuthService {
    override suspend fun registerUser(user: UserDto, password: String): Boolean {
        return suspendCancellableCoroutine { cancellableContinuation ->
            user.email?.let {
                auth.createUserWithEmailAndPassword(user.email, password)
                    .addOnSuccessListener {userAuth ->
                        val userDto = user.copy(uid = userAuth.user!!.uid)
                        repository.checkAndCreateUser(userDto, cancellableContinuation)
                    }
                    .addOnFailureListener {
                        cancellableContinuation.resumeWithException(it)
                    }
            } ?: {
                cancellableContinuation.resumeWithException(UserHasNullEmail())
            }
        }
    }

    override suspend fun login(email: String, password: String): Boolean {
        return suspendCancellableCoroutine {cancellableContinuation ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    cancellableContinuation.resume(true)
                }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
        }
    }

    override suspend fun loginAnonymously(): Boolean {
        return suspendCancellableCoroutine {cancellableContinuation ->
            auth.signInAnonymously()
                .addOnSuccessListener {userAuth ->
                    userAuth.user?.let {user ->
                        val userUid = user.uid
                        val userName = "Anon-${userUid.subSequence(0,5)}"
                        val userDto = UserDto(uid = userUid, userName = userName)
                        repository.checkAndCreateUser(userDto, cancellableContinuation)
                    }
                }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
            }
    }

    override fun getGoogleClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    override suspend fun loginWithGoogle(idToken: String): Boolean {
        fun getEmailUser(email: String): String {
            val userName = "${email.substringBefore("@")}-G"
            return userName
        }
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        return suspendCancellableCoroutine { cancellableContinuation ->
            auth.signInWithCredential(credential)
                .addOnSuccessListener { result ->
                    result.user?.let { user ->
                        user.email?.takeIf { it.isNotBlank() }?.let { email ->
                            val userDto = UserDto(uid = user.uid, userName = getEmailUser(email), email = email)
                            repository.checkAndCreateUser(userDto, cancellableContinuation)
                        } ?: cancellableContinuation.cancel()
                    } ?: cancellableContinuation.cancel()
                }
                .addOnFailureListener {
                    cancellableContinuation.resumeWithException(it)
                }
            }
    }

    override fun isUserLogged(): Boolean {
        return getCurrentUser() != null
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }
}