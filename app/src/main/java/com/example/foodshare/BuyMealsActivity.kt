package com.example.foodshare

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodshare.data.Meal
import com.example.foodshare.data.MealRepository
import com.example.foodshare.ui.theme.FoodShareTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

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
        topBar = {
            TopAppBar(
                title = { Text("Buy Meals", style = MaterialTheme.typography.headlineLarge) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background2),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(androidx.compose.ui.Alignment.Center))
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 16.dp)
                    ) {
                        items(meals) { meal ->
                            MealCard(meal = meal, onClick = { onMealClick(meal) })
                        }
                    }
                }
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailsScreen(
    mealId: String,
    mealRepository: MealRepository,
    navController: NavHostController
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var meal by remember { mutableStateOf<Meal?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var userId by remember { mutableStateOf("") } // Add userId (e.g., fetched from FirebaseAuth)

    // Simulate fetching the current user's ID
    LaunchedEffect(Unit) {
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: "UnknownUser"
    }

    LaunchedEffect(mealId) {
        if (mealId.isNotEmpty()) {
            meal = mealRepository.getMealById(mealId)
        }
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Details", style = MaterialTheme.typography.titleMedium, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f))
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                // Background Image
                Image(
                    painter = painterResource(id = R.drawable.background2),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Foreground Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = androidx.compose.ui.Alignment.TopCenter
                ) {
                    if (isLoading) {
                        CircularProgressIndicator()
                    } else {
                        meal?.let {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight(),
                                    elevation = CardDefaults.cardElevation(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "Name: ${it.foodName}",
                                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Description: ${it.description}",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                                            color = Color.Gray
                                        )
                                        Text(
                                            text = "Calories: ${it.calories}",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Protein: ${it.protein}",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Price: ${it.price} kr",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                                            color = Color.Black
                                        )
                                        Text(
                                            text = "Address: ${it.address}",
                                            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp),
                                            color = Color.Black
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // "Buy" Button
                                Button(
                                    onClick = {
                                        coroutineScope.launch {
                                            val success = mealRepository.markMealAsBought(mealId, userId)
                                            if (success) {
                                                Toast.makeText(
                                                    context,
                                                    "Meal bought successfully!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                navController.popBackStack() // Navigate back after success
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Failed to buy the meal. Please try again.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                                ) {
                                    Text("Buy", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                }


                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        } ?: Text(
                            text = "Meal not found.",
                            color = Color.Black,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    )
}



@Composable
fun MealCard(meal: Meal, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White) // Set card background to white
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = meal.foodName,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 20.sp),
                color = Color.Black
            )
            Text(
                text = "Price: ${meal.price} kr",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = Color.Gray
            )
        }
    }
}
