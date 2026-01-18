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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.plantarmy.notifications.NotificationSettings
import com.example.plantarmy.notifications.PlantReminderWorker
import com.example.plantarmy.workers.ReminderScheduler


@Composable
fun SettingsScreen() {


    val context = LocalContext.current

    var notificationsEnabled by remember {
        mutableStateOf(NotificationSettings.areNotificationsEnabled(context))
    }

    /** M11-2: Uhrzeit für Benachrichtigung auwöhlen
     * - Beobachtung der ausgwählten Uhrzeit
     * */

    var selectedTime by remember {
        mutableStateOf(NotificationSettings.getNotificationTime(context))
    }

    var expanded by remember { mutableStateOf(false) }

    // Uhrzeiten für Dropdown (alle 3 Stunden)
    val timeOptions = remember {
        (0..23 step 3).map { hour -> "%02d:00".format(hour) }
    }

    /** -------------------------------------------------
     * UI
     * ------------------------------------------------- */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Settings",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Notifications", fontWeight = FontWeight.SemiBold)
                Text(
                    "Reminders for watering and fertilizing",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            /** M10-1: Aktivieren & Deaktivieren von Benachrichtigungen
             * - Toggle
             * */

            Switch(
                checked = notificationsEnabled,
                onCheckedChange = {
                    notificationsEnabled = it
                    NotificationSettings.setNotificationsEnabled(context, it)

                    ReminderScheduler.start(context)
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Text("Notification time", fontWeight = FontWeight.SemiBold)

            Box {
                Row(
                    modifier = Modifier
                        .border(
                            width = 1.dp,
                            color = Color.Gray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { expanded = true }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedTime,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Open dropdown"
                    )
                }

                /** M11-1: Uhrzeit für Benachrichtigung auwöhlen
                 * - Dropdown für die Auswahl der Uhrzeit
                 * - Next: FavoritesScreen
                 * */

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    timeOptions.forEach { time ->
                        DropdownMenuItem(
                            text = { Text(time) },
                            onClick = {
                                selectedTime = time
                                NotificationSettings.setNotificationTime(context, time)
                                ReminderScheduler.start(context)
                                expanded = false
                            }
                        )
                    }
                }

            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Button(
                onClick = {
                    WorkManager.getInstance(context)
                        .enqueue(OneTimeWorkRequestBuilder<PlantReminderWorker>().build())
                }
            ) {
                Text("🔔 Test notification")
            }
        }
    }
}
