package com.example.plantarmy.workers

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.plantarmy.data.model.facts.PlantFacts
import com.example.plantarmy.data.model.settings.AppPrefs
import com.example.plantarmy.notifications.NotificationHelper

class DailyFactWorker(
    private val context: Context,
    params: WorkerParameters
) : Worker(context, params) {

    override fun doWork(): Result {
        // Optional-Feature: nur wenn aktiviert
        if (!AppPrefs.isDailyFactsEnabled(context)) {
            return Result.success()
        }

        val fact = PlantFacts.randomFact()
        NotificationHelper.showDailyFactNotification(
            context = context,
            id = 9001,
            title = "Daily plant fact 🌿",
            text = fact
        )

        return Result.success()
    }
}