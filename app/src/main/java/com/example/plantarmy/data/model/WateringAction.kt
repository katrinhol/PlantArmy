package com.example.plantarmy.data.model

import java.time.LocalDateTime
import java.util.UUID

// AKTION: GIESSEN

data class WateringAction(
    override val id: String = UUID.randomUUID().toString(),
    override val date: LocalDateTime = LocalDateTime.now(),
    override val note: String? = null,

    // OPTIONAL: Menge in Litern
    val amountLiters: Double? = null
) : CareAction
// : CareAction verweist auf die Verwendung des Interface CareAction