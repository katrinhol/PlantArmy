package com.example.plantarmy.data.model

import java.time.LocalDateTime
import java.util.UUID

// Für Erinnerungen

data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val plantId: String,            // Zu welcher Pflanze gehört das?
    val scheduledAt: LocalDateTime, // Wann soll es klingeln?
    val message: String,            // Was soll da stehen?
    var isSent: Boolean = false     // Wurde sie schon abgeschickt?
)