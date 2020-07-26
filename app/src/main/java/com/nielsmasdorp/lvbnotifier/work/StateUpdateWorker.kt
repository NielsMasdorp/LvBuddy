package com.nielsmasdorp.lvbnotifier.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.nielsmasdorp.domain.stock.CheckNewLVBState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class StateUpdateWorker(context: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(context, workerParameters), KoinComponent {

    private val checkNewLVBState: CheckNewLVBState by inject()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val job = async { checkNewLVBState.invoke() }
        job.await()
        Result.success()
    }
}