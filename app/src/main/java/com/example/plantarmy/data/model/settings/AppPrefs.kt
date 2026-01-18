package com.example.plantarmy.data.model.settings

import android.content.Context

object AppPrefs {
    private const val PREFS_NAME = "plant_army_prefs"
    private const val KEY_DAILY_FACTS = "daily_facts_enabled"

    fun isDailyFactsEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DAILY_FACTS, false)
    }

    fun setDailyFactsEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DAILY_FACTS, enabled)
            .apply()
    }
}