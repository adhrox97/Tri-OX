package com.adhrox.tri_xo.data.network.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.adhrox.tri_xo.domain.Repository
import com.adhrox.tri_xo.domain.model.GameStatus
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class CancelGameWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repository: Repository
): CoroutineWorker(appContext, workerParams){
    override suspend fun doWork(): Result {
        val gameId = inputData.getString(KEY_GAME_ID) ?: return Result.failure()

        return try {
            repository.updateGameStatus(gameId, GameStatus.Finished().toEnumValue())
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object{
        const val KEY_GAME_ID = "GAME_ID"
    }
}