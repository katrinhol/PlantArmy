package com.example.plantarmy.data.model

import java.time.LocalDateTime

/**
* Interface für alle Pflegeaktionen.
* Sealed Interface erlaubt, sicher zwischen Gießen und Düngen zu unterscheiden.
**/

sealed interface CareAction {
    val id: String
    val plantId: String
    val date: LocalDateTime
    val note: String?
}