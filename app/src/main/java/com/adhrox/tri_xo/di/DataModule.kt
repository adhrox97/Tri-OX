package com.adhrox.tri_xo.di

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import com.adhrox.tri_xo.data.network.FirebaseAuthService
import com.adhrox.tri_xo.data.network.RepositoryImpl
import com.adhrox.tri_xo.domain.AuthService
import com.adhrox.tri_xo.domain.Repository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    fun provideFirestore() = Firebase.firestore

    @Singleton
    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    fun provideRepository(firebaseFirestore: FirebaseFirestore, authServiceProvider: Provider<AuthService>): Repository {
        return RepositoryImpl(firebaseFirestore, authServiceProvider)
    }

    @Provides
    fun provideAuthService(firebaseAuth: FirebaseAuth, repository: Repository, @ApplicationContext context: Context): AuthService {
        return FirebaseAuthService(firebaseAuth, repository, context)
    }

    @Singleton
    @Provides
    fun provideWorkManager(@ApplicationContext appContext: Context): WorkManager =
        WorkManager.getInstance(appContext)
}