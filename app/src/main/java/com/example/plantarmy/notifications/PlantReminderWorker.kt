package com.example.plantarmy.notifications

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.plantarmy.data.repository.PlantRepository
import java.time.LocalDate
import java.time.LocalTime

/**
 * M5-2: Benachrichtigung, sobald Intervall abläuft
 * - regelmäßige automatische Hintergrundprüfung
 */
class PlantReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        /**
         * -------------------------------------------------
         * M10-4: Aktivieren & Deaktivieren von Benachrichtigungen
         * -------------------------------------------------
         * - Prüft, ob Benachrichtigungen global aktiviert sind
         * - Falls deaktiviert → Worker beendet sich ohne Aktion
         */
        if (!NotificationSettings.areNotificationsEnabled(applicationContext)) {
            return Result.success()
        }

        /** M11-5: Uhrzeit für Benachrichtigung auswählen
         * - Gelesene Uhrzeit aus den Einstellungen (z. B. "09:00")
         * - Vergleich mit aktueller Uhrzeit
         * - Benachrichtigungen werden NUR ausgelöst,
         *   wenn die gewählte Uhrzeit erreicht oder überschritten ist
         * */

        val notificationTime = NotificationSettings.getNotificationTime(applicationContext)
        val (hour, minute) = notificationTime.split(":").map { it.toInt() }

        val selectedTimeInMinutes = hour * 60 + minute

        val now = LocalTime.now()
        val currentTimeInMinutes = now.hour * 60 + now.minute

        // Noch zu früh → heute noch keine Benachrichtigungen
        if (currentTimeInMinutes < selectedTimeInMinutes) {
            return Result.success()
        }

        /**
         * -------------------------------------------------
         * Laden der Pflanzen & aktuelles Datum
         * -------------------------------------------------
         */

        val repo = PlantRepository(applicationContext)
        val allPlants = repo.getAllPlants()
        val today = LocalDate.now()

        allPlants
            .filter { it.remindersEnabled }
            .forEach { plant ->

                /**
                 * =================================================
                 * GIESSEN
                 * =================================================
                 */

                /**
                 * M5-3: Benachrichtigung, sobald Intervall abläuft
                 * - Prüft, ob Gießintervall abgelaufen ist
                 * - Prüft, ob heute bereits erinnert wurde
                 * - Löst Benachrichtigung aus
                 */
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
                        title = "Time for watering: ${plant.customName}",
                        text = "The watering interval has expired.",
                        plantId = plant.id
                    )

                    // Merken: heute bereits erinnert
                    repo.markReminderSentToday(
                        type = "water",
                        plantId = plant.id,
                        today = today
                    )
                }

                /**
                 * =================================================
                 * DÜNGEN
                 * =================================================
                 */

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
                        title = "Time for fertilizing: ${plant.customName}",
                        text = "The fertilizing interval has expired.",
                        plantId = plant.id
                    )

                    // Merken: heute bereits erinnert
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
