package com.example.plantarmy.notifications

import android.content.Context

/**
 * =====================================================
 * NotificationSettings
 * =====================================================
 * Zentrale Verwaltung aller Benachrichtigungseinstellungen
 * (Toggle + Uhrzeit)
 */
object NotificationSettings {

    private const val PREFS_NAME = "notification_settings"

    private const val KEY_ENABLED = "notifications_enabled"
    private const val KEY_TIME = "notification_time"


    /** M10-2: Aktivieren & Deaktivieren von Benachrichtigungen
     * - Benachrichtigungen standardmäßig: EIN
     * - Funktion prüft, ob Benachrichtigungen aktuell aktiviert sind
     * */
    fun areNotificationsEnabled(context: Context): Boolean {
        return context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE) //lokale Einstellungsdatei
            .getBoolean(KEY_ENABLED, true) // liest Wert aus - per Default: EIN
    }

    /** M10-3: Aktivieren & Deaktivieren von Benachrichtigungen
     * - speichert den Wert, den Nutzer auswählt (true/false)
     * */
    fun setNotificationsEnabled(context: Context, enabled: Boolean) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLED, enabled)
            .apply()
    }


    /** M11-3: Uhrzeit für Benachrichtigung auwöhlen
     * - Benachrichtigungen standardmäßig: 09:00
     * - Funktion prüft, welche Uhrzeit ausgewählt ist
     * */

    fun getNotificationTime(context: Context): String {
        return context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_TIME, "09:00") ?: "09:00"
    }

    /** M11-4: Uhrzeit für Benachrichtigung auwöhlen
     * - speichert dir Uhrzeit, den Nutzer auswählt
     * */

    fun setNotificationTime(context: Context, time: String) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_TIME, time)
            .apply()
    }
}
