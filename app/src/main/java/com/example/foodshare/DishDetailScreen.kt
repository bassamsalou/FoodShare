package com.example.foodshare

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.foodshare.data.Dishes
import com.example.foodshare.data.DishesRepository
import androidx.navigation.NavHostController
import com.example.foodshare.R

@Composable
fun DishDetailsScreen(
    dishId: String,
    dishesRepository: DishesRepository,
    navController: NavHostController
) {
    var dish by remember { mutableStateOf<Dishes?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(dishId) {
        isLoading = true
        errorMessage = null
        try {
            dish = dishesRepository.getDishDetails(dishId)
        } catch (e: Exception) {
            errorMessage = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(id = R.drawable.background2), // Use the same background image
            contentDescription = "Background Image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Content over the background
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        ) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                errorMessage != null -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                else -> {
                    dish?.let {
                        Column(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = rememberAsyncImagePainter(it.thumbnail),
                                contentDescription = it.name,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .padding(horizontal = 16.dp)
                            ) {
                                item {
                                    Text(
                                        text = it.name,
                                        style = MaterialTheme.typography.titleLarge.copy(color = Color.Black), // Black font color
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                    Text(
                                        text = "Category: ${it.category ?: "Unknown"}",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black) // Black font color
                                    )

                                    Text(
                                        text = "Area: ${it.area ?: "Unknown"}",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black) // Black font color
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Ingredients:",
                                        style = MaterialTheme.typography.titleMedium.copy(color = Color.Black), // Black font color
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }

                                items(it.ingredients.zip(it.measures)) { (ingredient, measure) ->
                                    Text(
                                        text = "$ingredient: $measure",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black) // Black font color
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "Instructions:",
                                        style = MaterialTheme.typography.titleMedium.copy(color = Color.Black), // Black font color
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )

                                    Text(
                                        text = it.instructions ?: "No instructions available",
                                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.Black) // Black font color
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            Button(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text("Back", color = Color.Black) // Button text color is black
                            }
                        }
                    } ?: run {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Dish not found!", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
