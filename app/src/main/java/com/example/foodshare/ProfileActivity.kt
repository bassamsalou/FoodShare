package com.example.foodshare

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
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
    var name by remember { mutableStateOf("Name") }
    var age by remember { mutableStateOf("0") }
    var address by remember { mutableStateOf("Address") }
    var phone by remember { mutableStateOf("Phone") }
    var email by remember { mutableStateOf("Email") }
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
                address = user.address
                phone = user.phone
                email = user.email
            } else {
                toastMessage = "Failed to load user data"
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", style = MaterialTheme.typography.headlineLarge) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState()) // Make the column scrollable
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // Background Image
                Image(
                    painter = painterResource(id = R.drawable.background2),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Foreground content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Profile picture centered
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profilePictureUri != null) {
                            Image(
                                painter = rememberImagePainter(profilePictureUri),
                                contentDescription = "Profile Picture",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else if (profilePictureUrl.isNotBlank()) {
                            // Load image from URL
                            Image(
                                painter = rememberImagePainter(profilePictureUrl),
                                contentDescription = "Profile Picture from URL",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            isEditMode = true
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name field
                    if (isEditMode) {
                        EditableField(label = "Name", value = name, onValueChange = { name = it })
                    } else {
                        ProfileInfoRow(label = "Name", value = name)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Age field
                    if (isEditMode) {
                        EditableField(label = "Age", value = age, onValueChange = { age = it })
                    } else {
                        ProfileInfoRow(label = "Age", value = age)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Address field
                    if (isEditMode) {
                        EditableField(label = "Address", value = address, onValueChange = { address = it })
                    } else {
                        ProfileInfoRow(label = "Address", value = address)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Phone field
                    if (isEditMode) {
                        EditableField(label = "Phone", value = phone, onValueChange = { phone = it })
                    } else {
                        ProfileInfoRow(label = "Phone", value = phone)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Email field
                    if (isEditMode) {
                        EditableField(label = "Email", value = email, onValueChange = { email = it })
                    } else {
                        ProfileInfoRow(label = "Email", value = email)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Save button
                    ElevatedButton(
                        onClick = {
                            if (isEditMode) {
                                coroutineScope.launch {
                                    val success = userRepository.addOrUpdateUser(
                                        User(
                                            userId = "",
                                            name = name,
                                            age = age.toIntOrNull() ?: 0,
                                            address = address,
                                            phone = phone,
                                            email = email,
                                            profilePictureUri = profilePictureUri
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
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text(if (isEditMode) "Save" else "Edit Profile")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Logout button with a red color
                    ElevatedButton(
                        onClick = { onLogout() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Logout", color = Color.White)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout button with a red color
                ElevatedButton(
                    onClick = { onLogout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text("Logout", color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Delete profile button in edit mode
                var showConfirmationDialog by remember { mutableStateOf(false) }

                if (isEditMode) {
                    ElevatedButton(
                        onClick = { showConfirmationDialog = true },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Text("Delete Profile", color = Color.White)
                    }

                    if (showConfirmationDialog) {
                        AlertDialog(
                            onDismissRequest = { showConfirmationDialog = false },
                            title = { Text("Confirm Delete") },
                            text = { Text("Are you sure you want to delete your profile?") },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showConfirmationDialog = false
                                        coroutineScope.launch {
                                            val isDeleted = userRepository.deleteUserProfile()
                                            if (isDeleted) {
                                                onLogout() // Log out user after successful deletion
                                            } else {
                                                toastMessage = "Failed to delete profile."
                                            }
                                        }
                                    }
                                ) {
                                    Text("Delete")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showConfirmationDialog = false }) {
                                    Text("Cancel")
                                }
                            }
                        )
                    }

                }

            }
        }
    )
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(0.3f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(0.7f),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun EditableField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}