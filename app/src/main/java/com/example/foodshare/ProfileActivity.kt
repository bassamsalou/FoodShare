package com.example.foodshare

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.net.Uri
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.foodshare.databinding.FragmentProfileBinding
import com.example.foodshare.data.User
import com.example.foodshare.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.URL
import java.util.UUID

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val userRepository = UserRepository()
    private val auth = FirebaseAuth.getInstance()

    private var isEditMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Load user data when the fragment is created
        loadUserProfile()

        // Set up button click listener
        binding.editSaveButton.setOnClickListener {
            if (isEditMode) {
                saveProfile() // Save changes when in edit mode
            } else {
                enterEditMode() // Enter edit mode
            }
        }

        return binding.root
    }

    private fun loadUserProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            val user = userRepository.getUserProfile()
            requireActivity().runOnUiThread {
                if (user != null) {
                    // Display user info
                    binding.etFirstName.setText(user.name)
                    binding.etAge.setText(user.age.toString())

                    // Always set the placeholder image for the profile picture
                    binding.profileImage.setImageResource(R.drawable.chefs)

                    exitEditMode() // Start in view mode
                } else {
                    Toast.makeText(requireContext(), "Failed to load user data", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveProfile() {
        // Since the profile picture cannot be changed, just update other user info
        updateUserProfile()
    }

    private fun updateUserProfile() {
        CoroutineScope(Dispatchers.IO).launch {
            val success = userRepository.addOrUpdateUser(
                User(
                    id = "",  // This might not be used here, make sure it's required
                    userId = auth.currentUser?.uid ?: "",
                    name = binding.etFirstName.text.toString(),
                    age = binding.etAge.text.toString().toIntOrNull() ?: 0,
                )
            )
            requireActivity().runOnUiThread {
                if (success) {
                    Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
                    exitEditMode() // Return to view mode
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun enterEditMode() {
        isEditMode = true
        binding.editSaveButton.text = "Save"
        binding.etFirstName.isEnabled = true
        binding.etAge.isEnabled = true
    }

    private fun exitEditMode() {
        isEditMode = false
        binding.editSaveButton.text = "Edit"
        binding.etFirstName.isEnabled = false
        binding.etAge.isEnabled = false
    }
}
