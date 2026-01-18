package com.example.plantarmy.data.model

import java.time.LocalDateTime
import java.util.UUID

// AKTION: GIESSEN

data class WateringAction(
    override val id: String = UUID.randomUUID().toString(),
    override val plantId: String,
    override val date: LocalDateTime = LocalDateTime.now(),
    override val note: String? = null,
    val amountLiters: Double? = null
) : CareAction
