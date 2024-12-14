package com.example.foodshare

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.foodshare.data.Meal
import com.example.foodshare.data.MealRepository
import com.example.foodshare.ui.theme.FoodShareTheme
import kotlinx.coroutines.launch

class AddMealsActivity : ComponentActivity() {

    private val mealRepository = MealRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodShareTheme {
                AddMealsScreen(mealRepository = mealRepository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMealsScreen(mealRepository: MealRepository) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // State variables for meal details
    var foodName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var calories by remember { mutableStateOf("") }
    var protein by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("")}

    // State to show toast messages
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Handle toast messages
    toastMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            toastMessage = null // Reset toast message
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Meal", style = MaterialTheme.typography.headlineLarge) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Input fields for meal details
                OutlinedTextField(
                    value = foodName,
                    onValueChange = { foodName = it },
                    label = { Text("Food Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = calories,
                    onValueChange = { calories = it },
                    label = { Text("Calories") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = protein,
                    onValueChange = { protein = it },
                    label = { Text("Protein (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price (dkk)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

                // Add Meal button
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val newMeal = Meal(
                                foodName = foodName,
                                description = description,
                                calories = calories,
                                protein = protein,
                                price = price,
                                address = address
                            )
                            val success = mealRepository.addMeal(newMeal)
                            toastMessage = if (success) {
                                // Reset fields
                                foodName = ""
                                description = ""
                                calories = ""
                                protein = ""
                                price = ""
                                address = ""
                                "Meal added successfully!"
                            } else {
                                "Failed to add meal. Try again."
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Meal")
                }
            }
        }
    )
}
