package com.example.foodshare.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MealRepository {

    private val db = FirebaseFirestore.getInstance()
    private val mealsCollection = db.collection("meals")
    private val auth = FirebaseAuth.getInstance()

    // Function to add a meal
    suspend fun addMeal(meal: Meal): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val documentRef = mealsCollection.document() // Generate unique document ID
        val mealWithId = meal.copy(id = documentRef.id, userId = userId) // Assign ID
        return try {
            documentRef.set(mealWithId).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    // Function to fetch meals for the current user
    suspend fun getMealsForCurrentUser(): List<Meal> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val result = mealsCollection.whereEqualTo("userId", userId).get().await()
            result.documents.mapNotNull { doc ->
                doc.toObject(Meal::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // Function to fetch a meal by its ID
    suspend fun getMealById(mealId: String): Meal? {
        return try {
            val document = mealsCollection.document(mealId).get().await()
            if (document.exists()) {
                document.toObject(Meal::class.java)?.copy(id = document.id)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // New function to fetch all meals
    // Function to fetch all meals
    suspend fun getAllMeals(): List<Meal> {
        val currentUserId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val result = mealsCollection
                .whereNotEqualTo("userId", currentUserId) // Exclude meals created by the current user
                .get()
                .await()

            result.documents.mapNotNull { doc ->
                val meal = doc.toObject(Meal::class.java)
                meal?.copy(id = doc.id) // Populate the ID field
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }


    suspend fun markMealAsBought(mealId: String, buyerId: String): Boolean {
        return try {
            // Reference to the specific meal document in Firestore
            val mealRef = mealsCollection.document(mealId)

            // Update the meal with the buyer's ID
            mealRef.update("boughtBy", buyerId).await()

            true // Success
        } catch (e: Exception) {
            e.printStackTrace()
            false // Failure
        }
    }
}