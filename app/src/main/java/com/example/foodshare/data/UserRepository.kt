package com.example.foodshare.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val auth = FirebaseAuth.getInstance()
    private val storage = FirebaseStorage.getInstance()

    // Add or update user profile
    suspend fun addOrUpdateUser(user: User): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val userWithId = user.copy(userId = userId)
        return try {
            usersCollection.document(userId).set(userWithId).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    // Fetch user profile
    suspend fun getUserProfile(): User? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val document = usersCollection.document(userId).get().await()
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Upload profile picture to Firebase Storage and return its URL
    suspend fun uploadProfilePicture(imageUri: Uri): String? {
        val userId = auth.currentUser?.uid ?: return null
        val fileName = "profilePictures/$userId.jpg"
        val storageRef = storage.reference.child(fileName)

        return try {
            storageRef.putFile(imageUri).await()
            storageRef.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Update profile picture URL in Firestore
    suspend fun updateProfilePictureUrl(url: String): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        return try {
            usersCollection.document(userId).update("profilePicture", url).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
