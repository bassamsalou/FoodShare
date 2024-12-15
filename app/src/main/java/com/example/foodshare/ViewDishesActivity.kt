package com.example.foodshare.ui

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.foodshare.data.Dishes
import com.example.foodshare.data.DishesRepository
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodshare.DishDetailsScreen

@OptIn(DelicateCoroutinesApi::class)
@Composable
fun ViewDishesScreen(
    dishesRepository: DishesRepository,
    navController: NavHostController
) {
    var query by remember { mutableStateOf("") }
    var dishesList by remember { mutableStateOf<List<Dishes>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { text -> query = text },
                modifier = Modifier.weight(1f),
                label = { Text("Search Meals") }
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        errorMessage = null
                        try {
                            dishesList = dishesRepository.searchDishes(query)
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.size(50.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search Icon")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(dishesList) { dish ->
                    DishesListItem(dish = dish) {
                        navController.navigate("dish_details/${Uri.encode(dish.idDish)}")
                    }
                }
            }
        }
    }
}

@Composable
fun DishesListItem(dish: Dishes, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(dish.thumbnail),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .padding(end = 16.dp),
            contentScale = ContentScale.Crop
        )
        Column {
            Text(text = dish.name, style = MaterialTheme.typography.bodyLarge)
            Text(
                text = "${dish.category ?: "Unknown"} | ${dish.area ?: "Unknown"}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}




