package com.example.foodshare

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.foodshare.data.User
import com.example.foodshare.data.UserRepository
import com.example.foodshare.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private val userRepository = UserRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        binding.button.setOnClickListener {
            val name = binding.nameEt.text.toString()
            val ageText = binding.ageEt.text.toString()
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (name.isNotEmpty() && ageText.isNotEmpty() && email.isNotEmpty() && pass.isNotEmpty()) {
                val age = ageText.toIntOrNull()

                if (age != null) {
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val user = User(
                                name = name,
                                age = age
                            )
                            CoroutineScope(Dispatchers.IO).launch {
                                val success = userRepository.addOrUpdateUser(user)
                                runOnUiThread {
                                    if (success) {
                                        val intent = Intent(this@SignUpActivity, SignInActivity::class.java)
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this@SignUpActivity, "Failed to save user info", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}