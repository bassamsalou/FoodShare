package com.example.foodshare

import android.content.Intent
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
import com.example.foodshare.ui.theme.FoodShareTheme
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class SignInActivity : ComponentActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        setContent {
            FoodShareTheme {
                SignInScreen(
                    firebaseAuth = firebaseAuth,
                    onSignInSuccess = {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish() // Close this activity to prevent returning with the back button
                    },
                    onNavigateToSignUp = {
                        val intent = Intent(this, SignUpActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    firebaseAuth: FirebaseAuth,
    onSignInSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign In", style = MaterialTheme.typography.headlineLarge) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                if (isLoading) {
                    CircularProgressIndicator()
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
                        )

                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text("Password") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password)
                        )

                        Button(
                            onClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    isLoading = true
                                    coroutineScope.launch {
                                        firebaseAuth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { task ->
                                                isLoading = false
                                                if (task.isSuccessful) {
                                                    onSignInSuccess()
                                                } else {
                                                    Toast.makeText(
                                                        context,
                                                        task.exception?.message ?: "Sign-in failed",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                            }
                                    }
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Email and Password cannot be empty",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Sign In")
                        }

                        TextButton(onClick = onNavigateToSignUp) {
                            Text("Don't have an account? Sign Up")
                        }
                    }
                }
            }
        }
    )
}
