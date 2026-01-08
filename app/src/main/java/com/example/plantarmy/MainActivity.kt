package com.example.plantarmy

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.plantarmy.ui.screens.CreatePlantScreen
import com.example.plantarmy.ui.screens.FavoritesScreen
import com.example.plantarmy.ui.screens.PlantArmyTheme
import com.example.plantarmy.ui.screens.SettingsScreen
import com.example.plantarmy.ui.screens.PlantRegisterScreen
import com.example.plantarmy.workers.ReminderScheduler
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import androidx.compose.material.icons.filled.List
import com.example.plantarmy.ui.screens.AllPlantsScreen

import com.example.plantarmy.ui.screens.PlantDetailsScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ReminderScheduler.start(applicationContext)

        enableEdgeToEdge()
        setContent {
            PlantArmyTheme {
                RequestNotificationPermissionIfNeeded()
                PlantArmyScreen()
            }
        }
    }
}

val PlantGreen = Color(0xFF8BC34A)
val PastelGreenBackground = Color(0xFFF1F8E9)

// --------------------------------- DEFINITION DER SCREENS --------------------------------- //
enum class AppScreen {
    HOME, FAVORITES, ALL_PLANTS, SETTINGS, PLANT_REGISTER, CREATE_PLANT, PLANT_DETAILS
}

// ------------------------------------- HAUPTANZEIGE --------------------------------------- //
@Composable
fun PlantArmyScreen() {

    // CURRENT STATE -> HOME (remember = lokale Variabel; mutableStatOf = beobachtbare Variable)
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    // Zwischenspeicher - TODO - Warum hier in Main?
    var editingPlantId by remember { mutableStateOf<String?>(null) }
    var selectedSpeciesId by remember { mutableStateOf<Int?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PastelGreenBackground,

        // UNTERE LEISTE
        bottomBar = {
            // Leiste nur anzeigen, wenn nicht in Register oder Create Plant Maske
            if (currentScreen != AppScreen.PLANT_REGISTER && currentScreen != AppScreen.CREATE_PLANT && currentScreen != AppScreen.PLANT_DETAILS) {

                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Settings", tint = if (currentScreen == AppScreen.SETTINGS) Color.Black else Color.Gray) },
                        selected = currentScreen == AppScreen.SETTINGS,
                        onClick = { currentScreen = AppScreen.SETTINGS }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = if (currentScreen == AppScreen.HOME) PlantGreen else Color.Gray) },
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { currentScreen = AppScreen.HOME }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Star, contentDescription = "Favorites", tint = if (currentScreen == AppScreen.FAVORITES) Color(0xFFFFD700) else Color.Gray) },
                        selected = currentScreen == AppScreen.FAVORITES,
                        onClick = { currentScreen = AppScreen.FAVORITES }
                    )

                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.List,
                                contentDescription = "All Plants",
                                tint = if (currentScreen == AppScreen.ALL_PLANTS) PlantGreen else Color.Gray
                            )
                        },
                        selected = currentScreen == AppScreen.ALL_PLANTS,
                        onClick = { currentScreen = AppScreen.ALL_PLANTS }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            //when wie switch
            when (currentScreen) {

                // State: HOME-SCREEN
                AppScreen.HOME -> HomeScreenContent(

                    // Pflanze anlegen - Button -> PLANT_REGISTER-SCREEN
                    onRegisterClick = { currentScreen = AppScreen.PLANT_REGISTER },

                    // Pflanze erstellen - Button -> CREATE_PLANT-SCREEN
                    onCreateClick = {
                        editingPlantId = null // Wichtig: ID löschen für NEUE Pflanze
                        currentScreen = AppScreen.CREATE_PLANT
                    }
                )

                // State: FAVORITES-SCREEN
                AppScreen.FAVORITES -> FavoritesScreen(

                    // Wenn eine Pflanze geklickt wird -> ID merken und zum Bearbeiten-Screen
                    onPlantClick = { plantId ->
                        editingPlantId = plantId
                        currentScreen = AppScreen.CREATE_PLANT
                    }
                )

                AppScreen.ALL_PLANTS -> AllPlantsScreen(
                    onPlantClick = { id ->
                        selectedSpeciesId = id
                        currentScreen = AppScreen.PLANT_DETAILS
                    }
                )

                AppScreen.PLANT_DETAILS -> {
                    val id = selectedSpeciesId
                    if (id != null) {
                        PlantDetailsScreen(
                            speciesId = id,
                            onBack = { currentScreen = AppScreen.ALL_PLANTS }
                        )
                    } else {
                        currentScreen = AppScreen.ALL_PLANTS
                    }
                }

                // State: SETTINGS-SCREEN
                AppScreen.SETTINGS -> SettingsScreen()

                // State: CREATE_PLANT-SCREEN
                AppScreen.CREATE_PLANT -> CreatePlantScreen(
                    // ID weitergeben (null - neu angelegt; ID - bearbeitet)
                    plantIdToEdit = editingPlantId,
                    onBack = {
                        // Zurück, woher gekommen -> Favorites oder Home)
                        currentScreen = if (editingPlantId != null) AppScreen.FAVORITES else AppScreen.HOME
                    },

                    onSaveSuccess = { currentScreen = AppScreen.FAVORITES }
                )

                // State: PLANT_REGISTER-SCREEN (Pflanze anlegen)
                AppScreen.PLANT_REGISTER -> PlantRegisterScreen(
                    // Zurück zu HOME
                    onBack = { currentScreen = AppScreen.HOME }
                )
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    onRegisterClick: () -> Unit,
    onCreateClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.plant_army_logo),
            contentDescription = "Plant Army Logo",
            modifier = Modifier.size(100.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Plant Army",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(60.dp))

        PlantMenuButton(text = "Pflanze anlegen", onClick = onRegisterClick)

        Spacer(modifier = Modifier.height(24.dp))

        PlantMenuButton(text = "Pflanze erstellen", onClick = onCreateClick)
    }
}

@Composable
fun PlantMenuButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.White
        ),
        border = BorderStroke(0.dp, Color.Transparent),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Surface(
                shape = CircleShape,
                color = PlantGreen.copy(alpha = 0.3f),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = PlantGreen,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlantArmyPreview() {
    PlantArmyTheme {
        PlantArmyScreen()
    }
}

@OptIn(com.google.accompanist.permissions.ExperimentalPermissionsApi::class)
@Composable
fun RequestNotificationPermissionIfNeeded() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val permissionState =
            rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)

        SideEffect {
            if (!permissionState.status.isGranted) {
                permissionState.launchPermissionRequest()
            }
        }
    }
}
