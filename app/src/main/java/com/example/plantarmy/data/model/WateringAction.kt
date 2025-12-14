package com.example.plantarmy.data.model

import java.time.LocalDateTime
import java.util.UUID

/**
 * Spezifische Aktion: Gießen
 */

data class WateringAction(
    override val id: String = UUID.randomUUID().toString(),
    override val date: LocalDateTime = LocalDateTime.now(),
    override val note: String? = null,
    val amountLiters: Double? = null // Optional: Menge in Litern
) : CareAction