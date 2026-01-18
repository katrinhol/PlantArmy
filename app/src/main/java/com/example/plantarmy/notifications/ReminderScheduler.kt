package com.example.plantarmy.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.plantarmy.notifications.NotificationSettings
import java.util.concurrent.TimeUnit
import com.example.plantarmy.notifications.PlantReminderWorker

/** M5-5: Benachrichtigung, sobald Intervall abläuft
 * - Android garantiert Ausführung
 * - Auch nach App-Neustart oder Energiesparmodus
 * */

//Singleton:
object ReminderScheduler {

    fun start(context: Context) {

        val time = NotificationSettings.getNotificationTime(context)
        val (hour, minute) = time.split(":").map { it.toInt() }

        val now = java.time.LocalDateTime.now()
        var triggerTime = now
            .withHour(hour)
            .withMinute(minute)
            .withSecond(0)

        if (triggerTime.isBefore(now)) {
            triggerTime = triggerTime.plusDays(1)
        }

        val delay = java.time.Duration.between(now, triggerTime)

        val request = PeriodicWorkRequestBuilder<PlantReminderWorker>(
            1, TimeUnit.DAYS
        )
            .setInitialDelay(delay.toMillis(), TimeUnit.MILLISECONDS)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "plant_reminder_work",
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
