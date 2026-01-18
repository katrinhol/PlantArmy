package com.example.plantarmy.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object DailyFactScheduler {

    fun start(context: Context) {
        val request = PeriodicWorkRequestBuilder<DailyFactWorker>(
            1, TimeUnit.DAYS
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "daily_fact_work",
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}