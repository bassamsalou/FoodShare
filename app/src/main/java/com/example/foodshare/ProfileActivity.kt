package com.example.foodshare

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.foodshare.databinding.FragmentProfileBinding
import android.content.Intent
import android.app.Activity
import android.net.Uri
import android.widget.Toast
import com.example.foodshare.data.User
import com.example.foodshare.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private val userRepository = UserRepository()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)

        // Set up the profile picture click listener
        binding.profileImage.setOnClickListener {
            openImagePicker()
        }

        // Set up save button listener
        binding.saveBtn.setOnClickListener {
            saveProfile()
        }

        return binding.root
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            binding.profileImage.setImageURI(selectedImageUri) // Preview the selected image
        }
    }

    private fun uploadImageToFirebase() {
        val userId = auth.currentUser?.uid ?: return
        if (selectedImageUri != null) {
            val fileName = "profilePictures/$userId/${UUID.randomUUID()}.jpg"
            val storageRef = storage.reference.child(fileName)

            storageRef.putFile(selectedImageUri!!).addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { uri ->
                    updateUserProfilePicture(uri.toString())
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Image upload failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserProfilePicture(imageUrl: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val success = userRepository.addOrUpdateUser(
                User(
                    id = "",
                    userId = auth.currentUser?.uid ?: "",
                    name = binding.etFirstName.text.toString(),
                    age = binding.etAge.text.toString().toIntOrNull() ?: 0,
                    profilePicture = imageUrl
                )
            )
            requireActivity().runOnUiThread {
                if (success) {
                    Toast.makeText(requireContext(), "Profile updated with picture", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveProfile() {
        // Upload the image when the user clicks "Save"
        uploadImageToFirebase()
    }
}
