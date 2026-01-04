package com.example.plantarmy.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.plantarmy.data.repository.PlantRepository
import com.example.plantarmy.notifications.NotificationHelper
import java.time.LocalDate

class PlantReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repo = PlantRepository(applicationContext)
        val allPlants = repo.getAllPlants()

        val today = LocalDate.now()

        allPlants
            .filter { it.remindersEnabled }
            .forEach { plant ->

                // 🌱 GIESSEN
                if (plant.isWateringDue()) {
                    NotificationHelper.showNotification(
                        context = applicationContext,
                        id = ("water_${plant.id}").hashCode(),
                        title = "Zeit zum Gießen: ${plant.customName}",
                        text = "Das Gießintervall ist abgelaufen."
                    )
                }

                // 🌿 DÜNGEN
                val nextFertilizingDate = plant.calculateNextFertilizingDate()
                if (!today.isBefore(nextFertilizingDate)) {
                    NotificationHelper.showNotification(
                        context = applicationContext,
                        id = ("fert_${plant.id}").hashCode(),
                        title = "Zeit zum Düngen: ${plant.customName}",
                        text = "Das Düngeintervall ist abgelaufen."
                    )
                }
            }

        return Result.success()
    }
}
