package com.example.plantarmy.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.plantarmy.data.model.Plant
import com.example.plantarmy.ui.viewmodel.FavoritesViewModel

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = viewModel(),
    // NEU: Callback, wenn eine Pflanze angeklickt wird (gibt die ID zurück)
    onPlantClick: (String) -> Unit
) {
    // Daten neu laden, wenn der Screen angezeigt wird
    LaunchedEffect(Unit) {
        viewModel.loadPlants()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Meine Favoriten",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.plants.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Noch keine Pflanzen angelegt", color = Color.Gray)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(viewModel.plants) { plant ->
                    // NEU: Wir übergeben den Klick weiter
                    FavoritePlantItem(
                        plant = plant,
                        onClick = { onPlantClick(plant.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritePlantItem(
    plant: Plant,
    onClick: () -> Unit // NEU: Klick-Funktion
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // NEU: Macht die ganze Karte anklickbar
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Das Bild
            val imageUri = plant.photos.firstOrNull()?.uri

            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "Foto von ${plant.customName}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            } else {
                // Platzhalter
                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Favorite, contentDescription = null, tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 2. Text Informationen
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.customName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                if (plant.location.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = plant.location,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // 3. Kleiner Gieß-Indikator
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = Color(0xFF2196F3),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${plant.wateringIntervalDays} Tage",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}