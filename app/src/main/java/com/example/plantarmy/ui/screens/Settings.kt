package com.example.plantarmy.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SettingsScreen() {

    // Zustand für den Switch
    var notificationsEnabled by remember { mutableStateOf(true) }

    // ZUSTAND FÜR DAS DROPDOWN
    var expanded by remember { mutableStateOf(false) }
    var selectedTime by remember { mutableStateOf("09:00") }

    // Generiere die Liste: 00:00, 03:00 ... 21:00
    val timeOptions = remember {
        (0..23 step 3).map { hour ->
            "%02d:00".format(hour)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Einstellungen",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- Toggle für Benachrichtigungen ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Benachrichtigungen", fontWeight = FontWeight.SemiBold)
                Text(
                    "Erinnerungen für Gießen & Düngen",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Switch(
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // --- Auswahl der Uhrzeit (Schönes Dropdown) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Benachrichtigungszeit", fontWeight = FontWeight.SemiBold)

            // Box für das Dropdown-Menü
            Box {
                // Ein sichtbarer Rahmen um die Auswahl
                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { expanded = true } // Macht die ganze Box klickbar
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$selectedTime Uhr",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown öffnen"
                    )
                }

                // Das eigentliche Menü (klappt unter der Box auf)
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    timeOptions.forEach { time ->
                        DropdownMenuItem(
                            text = { Text(text = "$time Uhr") },
                            onClick = {
                                selectedTime = time
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}