package com.example.foodshare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodshare.data.MealRepository
import com.example.foodshare.data.UserRepository
import com.example.foodshare.ui.theme.FoodShareTheme
import androidx.compose.ui.Modifier


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mealRepository = MealRepository()
        val userRepository = UserRepository()

        setContent {
            FoodShareTheme {
                val navController = rememberNavController()
                MainScreen(navController, mealRepository, userRepository)
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    mealRepository: MealRepository,
    userRepository: UserRepository
) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "view_meals",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("view_meals") { ViewMealsScreen(mealRepository) }
            composable("add_meal") { AddMealsScreen(mealRepository) }
            composable("buy_meals") { BuyMealsScreen(mealRepository) }
            composable("profile") { ProfileScreen(userRepository) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "View Meals") },
            label = { Text("View Meals") },
            selected = false,
            onClick = { navController.navigate("view_meals") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Add, contentDescription = "Add Meal") },
            label = { Text("Add Meal") },
            selected = false,
            onClick = { navController.navigate("add_meal") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.ShoppingCart, contentDescription = "Buy Meal") },
            label = { Text("Buy Meal") },
            selected = false,
            onClick = { navController.navigate("buy_meals") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = false,
            onClick = { navController.navigate("profile") }
        )
    }
}

