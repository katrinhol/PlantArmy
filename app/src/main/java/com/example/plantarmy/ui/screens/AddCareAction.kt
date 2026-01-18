package com.example.plantarmy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.plantarmy.data.model.DuePlant
import com.example.plantarmy.ui.viewmodel.CareActionViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.plantarmy.data.model.CareType

@Composable
fun AddCareActionScreen(
    viewModel: CareActionViewModel = viewModel(),
    onBack: () -> Unit
) {
    LaunchedEffect(Unit) {
        viewModel.loadPlants()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }

            Column {
                Text(
                    text = "Care actions today",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Click plant to mark as done",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

        }

        Spacer(modifier = Modifier.height(16.dp))

        if (viewModel.duePlants.isEmpty()) {
            EmptyCareState()
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(viewModel.duePlants) { duePlant ->
                    DuePlantItem(
                        duePlant = duePlant,
                        onClick = {
                            viewModel.applyCareAction(duePlant)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyCareState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.InvertColors,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No care actions for today",
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DuePlantItem(
    duePlant: DuePlant,
    onClick: () -> Unit
) {
    val plant = duePlant.plant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            val imageModel = plant.photos.firstOrNull()?.uri ?: plant.imageUrl

            if (imageModel != null) {
                AsyncImage(
                    model = imageModel,
                    contentDescription = null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = plant.customName,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = when (duePlant.careType) {
                        CareType.WATER -> "Needs watering"
                        CareType.FERTILIZE -> "Needs fertilizing"
                    },
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.InvertColors,
                contentDescription = null,
                tint = when (duePlant.careType) {
                    CareType.WATER -> Color(0xFF2196F3)
                    CareType.FERTILIZE -> Color(0xFF4CAF50)
                }
            )
        }
    }
}



