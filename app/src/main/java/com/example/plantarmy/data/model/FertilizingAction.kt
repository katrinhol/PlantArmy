package com.example.plantarmy.data.model

import java.time.LocalDateTime
import java.util.UUID

// AKTION: DÜNGEN

data class FertilizingAction(
    override val id: String = UUID.randomUUID().toString(),
    override val date: LocalDateTime = LocalDateTime.now(),
    override val note: String? = null,
    val fertilizerType: String = "Standard",

    //OPTIONAL: Menge
    val amountGrams: Double? = null
) : CareAction
// : CareAction verweist auf die Verwendung des Interface CareAction