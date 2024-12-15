package com.example.foodshare

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.foodshare.data.MealRepository
import com.example.foodshare.data.UserRepository
import com.example.foodshare.data.DishesRepository
import com.example.foodshare.DishDetailsScreen
import com.example.foodshare.ui.ViewDishesScreen
import com.example.foodshare.ui.theme.FoodShareTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mealRepository = MealRepository()
        val userRepository = UserRepository()
        val dishesRepository = DishesRepository()

        setContent {
            FoodShareTheme {
                val navController = rememberNavController()
                MainScreen(navController, mealRepository, userRepository, dishesRepository)
            }
        }
    }
}


@Composable
fun MainScreen(
    navController: NavHostController,
    mealRepository: MealRepository,
    userRepository: UserRepository,
    dishesRepository: DishesRepository
) {

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "view_meals",
            modifier = Modifier.padding(innerPadding)
        ) {
            // View meals screen
            composable("view_meals") {
                ViewDishesScreen(
                    dishesRepository = dishesRepository,
                    navController = navController
                )
            }

            // Dish details screen
            composable("dish_details/{dishId}") { backStackEntry ->
                val dishId = backStackEntry.arguments?.getString("dishId") ?: ""
                DishDetailsScreen(
                    dishId = dishId,
                    dishesRepository = dishesRepository,
                    navController = navController
                )
            }

            // Add meal screen
            composable("add_meal") {
                AddMealsScreen(mealRepository = mealRepository)
            }

            // Buy meals screen
            composable("buy_meals") {
                BuyMealsScreen(
                    mealRepository = mealRepository,
                    onMealClick = { meal ->
                        if (meal.id.isNotEmpty()) {
                            navController.navigate("meal_details/${meal.id}")
                        }
                    }
                )
            }

            // Profile screen
            composable("profile") {
                ProfileScreen(
                    userRepository = userRepository,
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(navController.context, SignInActivity::class.java)
                        navController.context.startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        // Navigation bar items
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "View Meals") },
            label = { Text("View Meals") },
            selected = currentRoute == "view_meals",
            onClick = {
                // Prevent navigating to the same screen again
                if (currentRoute != "view_meals") {
                    navController.navigate("view_meals")
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Add Meal") },
            label = { Text("Add Meal") },
            selected = currentRoute == "add_meal",
            onClick = {
                if (currentRoute != "add_meal") {
                    navController.navigate("add_meal")
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Buy Meal") },
            label = { Text("Buy Meal") },
            selected = currentRoute == "buy_meals",
            onClick = {
                if (currentRoute != "buy_meals") {
                    navController.navigate("buy_meals")
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = {
                if (currentRoute != "profile") {
                    navController.navigate("profile")
                }
            }
        )
    }
}
