package com.example.foodshare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.foodshare.data.User
import com.example.foodshare.data.UserRepository
import com.example.foodshare.ui.theme.FoodShareTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileActivity : ComponentActivity() {

    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodShareTheme {
                ProfileScreen(userRepository = userRepository, onLogout = {
                    FirebaseAuth.getInstance().signOut() // Sign out the user
                    val intent = Intent(this, SignInActivity::class.java)
                    startActivity(intent)
                    finish() // Ensure this activity is removed from the backstack
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(userRepository: UserRepository, onLogout: () -> Unit) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // State variables for the user profile
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var isEditMode by remember { mutableStateOf(false) }

    // State for toast messages
    var toastMessage by remember { mutableStateOf<String?>(null) }

    // Handle toast messages
    toastMessage?.let { message ->
        LaunchedEffect(message) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            toastMessage = null // Reset the message
        }
    }

    // Load user profile when the screen is first displayed
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val user = userRepository.getUserProfile()
            if (user != null) {
                name = user.name
                age = user.age.toString()
            } else {
                toastMessage = "Failed to load user data"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile picture
                Image(
                    painter = painterResource(id = R.drawable.chefs),
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(bottom = 16.dp)
                )

                // Name field
                if (isEditMode) {
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray)
                            .padding(8.dp)
                    )
                } else {
                    Text(name, style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Age field
                if (isEditMode) {
                    BasicTextField(
                        value = age,
                        onValueChange = { age = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.LightGray)
                            .padding(8.dp),
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                } else {
                    Text("Age: $age", style = MaterialTheme.typography.bodyLarge)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Edit/Save button
                Button(onClick = {
                    if (isEditMode) {
                        // Save profile
                        coroutineScope.launch {
                            val success = userRepository.addOrUpdateUser(
                                User(
                                    userId = "",
                                    name = name,
                                    age = age.toIntOrNull() ?: 0
                                )
                            )
                            if (success) {
                                toastMessage = "Profile updated"
                                isEditMode = false
                            } else {
                                toastMessage = "Failed to update profile"
                            }
                        }
                    } else {
                        isEditMode = true
                    }
                }) {
                    Text(if (isEditMode) "Save" else "Edit")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout button
                Button(
                    onClick = { onLogout() }, // Trigger logout when button is clicked
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Logout")
                }
            }
        }
    )
}
