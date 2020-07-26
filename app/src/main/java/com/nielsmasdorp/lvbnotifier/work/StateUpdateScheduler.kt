package com.nielsmasdorp.lvbnotifier.work

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.nielsmasdorp.domain.settings.SettingsRepository
import com.nielsmasdorp.domain.settings.ShouldPoll
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class StateUpdateScheduler(
    private val shouldPoll: ShouldPoll,
    private val settings: SettingsRepository,
    private val workManager: WorkManager
) {

    fun schedule() {
        workManager.cancelAllWork()
        GlobalScope.launch(Dispatchers.IO) {
            val shouldPoll = shouldPoll.invoke()
            if (!shouldPoll) return@launch
            val pollFrequency = settings.getPollFrequencyMinutes()
            withContext(Dispatchers.Main) {
                val constraints: Constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
                val periodicWorkRequest = PeriodicWorkRequest.Builder(
                    StateUpdateWorker::class.java,
                    pollFrequency.toLong(),
                    TimeUnit.MINUTES
                ).setConstraints(constraints).build()
                workManager.enqueue(periodicWorkRequest)
            }
        }
    }
}