package com.example.plantarmy.data.model

import java.time.LocalDateTime
import java.util.UUID

// SPEICHERT URL ZU FOTO

data class Photo(
    val id: String = UUID.randomUUID().toString(),

    // DATEIPFAD zum Bild (URI als String)
    val uri: String,

    val createdAt: LocalDateTime = LocalDateTime.now()
)