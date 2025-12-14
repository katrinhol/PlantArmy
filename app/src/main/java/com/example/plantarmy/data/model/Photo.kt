package com.example.plantarmy.data.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Speichert den Pfad zu einem Foto auf dem Handy.
 */

data class Photo(
    val id: String = UUID.randomUUID().toString(),

    // Der Dateipfad zum Bild (URI als String)
    val uri: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)