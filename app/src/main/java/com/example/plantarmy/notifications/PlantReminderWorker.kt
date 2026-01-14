package com.example.plantarmy.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.plantarmy.data.repository.PlantRepository
import java.time.LocalDate
import com.example.plantarmy.notifications.NotificationSettings


class PlantReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        if (!NotificationSettings.areNotificationsEnabled(applicationContext)) {
            return Result.success()
        }

        val repo = PlantRepository(applicationContext)
        val allPlants = repo.getAllPlants()

        val today = LocalDate.now()

        allPlants
            .filter { it.remindersEnabled }
            .forEach { plant ->

                /**
                 * GIESSEN
                 **/

                if (
                    plant.isWateringDue() &&
                    !repo.wasReminderSentToday(
                        type = "water",
                        plantId = plant.id,
                        today = today
                    )
                ) {
                    NotificationHelper.showNotification(
                        context = applicationContext,
                        id = ("water_${plant.id}").hashCode(),
                        title = "Zeit zum Gießen: ${plant.customName}",
                        text = "Das Gießintervall ist abgelaufen.",
                        plantId = plant.id
                    )

                    // merken: heute schon erinnert
                    repo.markReminderSentToday(
                        type = "water",
                        plantId = plant.id,
                        today = today
                    )
                }

                /**
                 * DÜNGEN
                 **/

                val fertilizingDue =
                    !today.isBefore(plant.calculateNextFertilizingDate())

                if (
                    fertilizingDue &&
                    !repo.wasReminderSentToday(
                        type = "fert",
                        plantId = plant.id,
                        today = today
                    )
                ) {
                    NotificationHelper.showNotification(
                        context = applicationContext,
                        id = ("fert_${plant.id}").hashCode(),
                        title = "Zeit zum Düngen: ${plant.customName}",
                        text = "Das Düngeintervall ist abgelaufen.",
                        plantId = plant.id
                    )

                    // merken: heute schon erinnert
                    repo.markReminderSentToday(
                        type = "fert",
                        plantId = plant.id,
                        today = today
                    )
                }
            }

        return Result.success()
    }
}