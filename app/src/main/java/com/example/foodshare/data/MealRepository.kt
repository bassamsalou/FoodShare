package com.example.foodshare.data

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MealRepository {

    private val db = FirebaseFirestore.getInstance()
    private val mealsCollection = db.collection("meals")
    private val auth = FirebaseAuth.getInstance()

    suspend fun addMeal(meal: Meal): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val documentRef = mealsCollection.document()
        val mealWithId = meal.copy(id = documentRef.id, userId = userId)
        return try {
            documentRef.set(mealWithId).await()
            Log.d("MealRepository", "Meal added: $mealWithId")
            true
        } catch (e: Exception) {
            Log.e("MealRepository", "Error adding meal", e)
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

    suspend fun getMealById(mealId: String): Meal? {
        return try {
            val document = FirebaseFirestore.getInstance()
                .collection("meals")
                .document(mealId)
                .get()
                .await()

            if (document.exists()) {
                val data = document.data ?: return null
                Meal(
                    id = document.id,
                    userId = data["userId"]?.toString() ?: "",
                    foodName = data["foodName"]?.toString() ?: "",
                    description = data["description"]?.toString() ?: "",
                    calories = data["calories"]?.toString() ?: "",
                    protein = data["protein"]?.toString() ?: "",
                    price = data["price"]?.toString() ?: "", // Convert price to String
                    address = data["address"]?.toString() ?: ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("MealRepository", "Error fetching meal by ID", e)
            null
        }
    }

    suspend fun getAllMeals(): List<Meal> {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
        return try {
            val result = FirebaseFirestore.getInstance()
                .collection("meals")
                .whereNotEqualTo("userId", currentUserId) // Exclude current user's meals
                .get()
                .await()

            // Manually map each document to a `Meal` object
            result.documents.mapNotNull { doc ->
                val data = doc.data ?: return@mapNotNull null
                Meal(
                    id = doc.id,
                    userId = data["userId"]?.toString() ?: "",
                    foodName = data["foodName"]?.toString() ?: "",
                    description = data["description"]?.toString() ?: "",
                    calories = data["calories"]?.toString() ?: "",
                    protein = data["protein"]?.toString() ?: "",
                    price = data["price"]?.toString() ?: "", // Convert price to String
                    address = data["address"]?.toString() ?: ""
                )
            }
        } catch (e: Exception) {
            Log.e("MealRepository", "Error fetching meals", e)
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