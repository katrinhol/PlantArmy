package com.example.plantarmy.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.plantarmy.MainActivity
import com.example.plantarmy.R


/**
 * Helper-Klasse, die
 * Notification-Channel erstellt,
 * Benachrichtigungen anzeigt
 * Beim klick auf Notification App öffent, zu Favorites (PlantId)
 */

object NotificationHelper {

    private const val CHANNEL_ID = "plant_army_channel"

    fun createChannel(context: Context) {
        //ältere Android-Versionen ignorieren Channels -> kein Crash
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            //Channel erzeugen:
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Plant Army Notifications",
                NotificationManager.IMPORTANCE_HIGH //= Pop-up, Sound, Heads-up notification
            )

            //wenn Channel schon existiert -> passiert nichts
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun showNotification(
        context: Context,
        id: Int,
        title: String,
        text: String,
        plantId: String
    ) {
        createChannel(context)


        //Bei Klick -> Main Activity
        val intent = Intent(context, MainActivity::class.java).apply {
            //Deep-Linking zu Main
            putExtra("open_screen", "FAVORITES")
            putExtra("plant_id", plantId)
            //Single-Top -> nutzt vorhandene Activity, kein mehrfaches Öffnen
            //Clear-Top -> räumt alte screens auf
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            //update_current -> überschreibt alte Extras (ID)
            //Immutable -> intent unveränderlich
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        //Notification bauen:
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) //notification verschwindet danach automatisch
            .build()

        // verhindert Fehler, wenn Notifications deaktiviert sind
        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(id, notification)
        }
    }

    // C3 Daily Fact Notifications

    fun showDailyFactNotification(
        context: Context,
        id: Int,
        title: String,
        text: String
    ) {
        createChannel(context)

        val intent = Intent(context, MainActivity::class.java).apply {
            // Optional: Home öffnen
            putExtra("open_screen", "HOME")
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
            NotificationManagerCompat.from(context).notify(id, notification)
        }
    }

}
