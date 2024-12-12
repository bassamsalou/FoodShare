package com.example.foodshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.foodshare.data.Meal
import com.example.foodshare.data.MealRepository
import com.example.foodshare.ui.theme.FoodShareTheme
import kotlinx.coroutines.launch

class BuyMealsActivity : ComponentActivity() {

    private val mealRepository = MealRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FoodShareTheme {
                BuyMealsScreen(mealRepository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyMealsScreen(mealRepository: MealRepository) {
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        meals = mealRepository.getAllMeals() // Assumes a method to fetch all meals
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Buy Meals") })
        },
        content = { padding ->
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (meals.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text("No meals available.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        items(meals) { meal ->
                            MealCard(meal = meal) {
                                // Handle meal purchase click
                                onBuyMealClick(meal)
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun MealCard(meal: Meal, onBuyClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* Navigate to meal details if needed */ },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = meal.foodName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Description: ${meal.description}")
            Text(text = "Calories: ${meal.calories}")
            Text(text = "Protein: ${meal.protein}")
            Text(text = "Price: $${meal.price}")
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onBuyClick,
                modifier = Modifier.align(androidx.compose.ui.Alignment.End)
            ) {
                Text("Buy")
            }
        }
    }
}

fun onBuyMealClick(meal: Meal) {
    // Logic to handle meal purchase
    // For example, navigate to a confirmation screen or display a toast
}
