package com.example.plantarmy

import android.content.Intent
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
import com.example.plantarmy.ui.screens.PlantRegisterScreen
import com.example.plantarmy.ui.screens.SettingsScreen
import com.example.plantarmy.workers.ReminderScheduler
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

// Deep-link keys (aus NotificationIntent)
private const val EXTRA_OPEN_SCREEN = "open_screen"
private const val EXTRA_PLANT_ID = "plant_id"
private const val SCREEN_FAVORITES = "FAVORITES"

class MainActivity : ComponentActivity() {


    // --------------------------------- BENACHRICHTIGUNGEN --------------------------------- //

    // Diese States brauchen wir, damit onNewIntent() Compose updaten kann
    private val deepLinkScreen = mutableStateOf<AppScreen?>(null)
    private val deepLinkPlantId = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        ReminderScheduler.start(applicationContext)

        // Falls App über Notification gestartet wurde
        handleIntent(intent)

        enableEdgeToEdge()
        setContent {
            PlantArmyTheme {
                RequestNotificationPermissionIfNeeded()

                PlantArmyScreen(
                    // wenn DeepLink da ist -> starte in Favorites, sonst Home
                    initialScreen = deepLinkScreen.value ?: AppScreen.HOME,
                    highlightPlantId = deepLinkPlantId.value
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Wenn App schon offen ist und Notification erneut geklickt wird
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent == null) return

        val openScreen = intent.getStringExtra(EXTRA_OPEN_SCREEN)
        val plantId = intent.getStringExtra(EXTRA_PLANT_ID)

        if (openScreen == SCREEN_FAVORITES) {
            deepLinkScreen.value = AppScreen.FAVORITES
            deepLinkPlantId.value = plantId
        }
    }
}

val PlantGreen = Color(0xFF8BC34A)
val PastelGreenBackground = Color(0xFFF1F8E9)

// --------------------------------- DEFINITION DER SCREENS --------------------------------- //
enum class AppScreen {
    HOME, FAVORITES, SETTINGS, PLANT_REGISTER, CREATE_PLANT,
}

// ------------------------------------- HAUPTANZEIGE --------------------------------------- //
@Composable
fun PlantArmyScreen(
    initialScreen: AppScreen = AppScreen.HOME,
    highlightPlantId: String? = null
) {
    var currentScreen by remember { mutableStateOf(initialScreen) }
    var editingPlantId by remember { mutableStateOf<String?>(null) }

    // Wenn ein DeepLink später reinkommt (onNewIntent), soll UI reagieren:
    LaunchedEffect(initialScreen, highlightPlantId) {
        currentScreen = initialScreen
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = PastelGreenBackground,

        bottomBar = {
            if (currentScreen != AppScreen.PLANT_REGISTER && currentScreen != AppScreen.CREATE_PLANT) {
                NavigationBar(containerColor = Color.White) {
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = if (currentScreen == AppScreen.SETTINGS) Color.Black else Color.Gray
                            )
                        },
                        selected = currentScreen == AppScreen.SETTINGS,
                        onClick = { currentScreen = AppScreen.SETTINGS }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = "Home",
                                tint = if (currentScreen == AppScreen.HOME) PlantGreen else Color.Gray
                            )
                        },
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { currentScreen = AppScreen.HOME }
                    )
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Favorites",
                                tint = if (currentScreen == AppScreen.FAVORITES) Color(0xFFFFD700) else Color.Gray
                            )
                        },
                        selected = currentScreen == AppScreen.FAVORITES,
                        onClick = { currentScreen = AppScreen.FAVORITES }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreenContent(
                    onRegisterClick = { currentScreen = AppScreen.PLANT_REGISTER },
                    onCreateClick = {
                        editingPlantId = null
                        currentScreen = AppScreen.CREATE_PLANT
                    }
                )

                AppScreen.FAVORITES -> FavoritesScreen(
                    // TODO: FavoritesScreen sollte highlightPlantId annehmen und markieren
                    // z.B. FavoritesScreen(highlightPlantId = highlightPlantId, onPlantClick = { ... })
                    onPlantClick = { plantId ->
                        editingPlantId = plantId
                        currentScreen = AppScreen.CREATE_PLANT
                    }
                )

                AppScreen.SETTINGS -> SettingsScreen()

                AppScreen.CREATE_PLANT -> CreatePlantScreen(
                    plantIdToEdit = editingPlantId,
                    onBack = {
                        currentScreen = if (editingPlantId != null) AppScreen.FAVORITES else AppScreen.HOME
                    },
                    onSaveSuccess = { currentScreen = AppScreen.FAVORITES }
                )

                AppScreen.PLANT_REGISTER -> PlantRegisterScreen(
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
        colors = ButtonDefaults.buttonColors(
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
