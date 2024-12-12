package com.example.foodshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodshare.data.Meal
import com.example.foodshare.data.MealRepository
import com.example.foodshare.ui.theme.FoodShareTheme

class BuyMealsActivity : ComponentActivity() {

    private val mealRepository = MealRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FoodShareTheme {
                val navController = rememberNavController()
                BuyMealsNavHost(navController, mealRepository)
            }
        }
    }
}

@Composable
fun BuyMealsNavHost(
    navController: NavHostController,
    mealRepository: MealRepository
) {
    NavHost(
        navController = navController,
        startDestination = "meal_list"
    ) {
        composable("meal_list") {
            BuyMealsScreen(
                mealRepository = mealRepository,
                onMealClick = { meal ->
                    // Use meal.id safely
                    if (meal.id.isNotEmpty()) {
                        navController.navigate("meal_details/${meal.id}")
                    }
                }
            )
        }
        composable("meal_details/{mealId}") { backStackEntry ->
            val mealId = backStackEntry.arguments?.getString("mealId") ?: ""
            MealDetailsScreen(mealId = mealId, mealRepository = mealRepository, navController = navController)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyMealsScreen(
    mealRepository: MealRepository,
    onMealClick: (Meal) -> Unit
) {
    var meals by remember { mutableStateOf<List<Meal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        meals = mealRepository.getAllMeals()
        isLoading = false
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Buy Meals") }) },
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
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    items(meals) { meal ->
                        MealCard(meal = meal, onClick = { onMealClick(meal) })
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(mealId: String, mealRepository: MealRepository, navController: NavHostController) {
    var meal by remember { mutableStateOf<Meal?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(mealId) {
        if (mealId.isNotEmpty()) {
            meal = mealRepository.getMealById(mealId)
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
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
                meal?.let {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                            .padding(16.dp)
                    ) {
                        Text("Name: ${it.foodName}", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Description: ${it.description}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Calories: ${it.calories}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Protein: ${it.protein}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Price: dkk${it.price}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Address: ${it.address}")
                    }
                } ?: Text("Meal not found.")
            }
        }
    )
}

@Composable
fun MealCard(meal: Meal, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = meal.foodName, style = MaterialTheme.typography.titleMedium)
            Text(text = "Price: dkk${meal.price}")
        }
    }
}
