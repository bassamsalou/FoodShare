package com.example.foodshare.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MealRepository {

    private val db = FirebaseFirestore.getInstance()
    private val mealsCollection = db.collection("meals")
    private val auth = FirebaseAuth.getInstance()

    // Function to add a meal
    suspend fun addMeal(meal: Meal): Boolean {
        val userId = auth.currentUser?.uid ?: return false
        val mealWithUserId = meal.copy(userId = userId)
        return try {
            mealsCollection.add(mealWithUserId).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getMealsForCurrentUser(): List<Meal> {
        val userId = auth.currentUser?.uid ?: return emptyList()
        return try {
            val result = mealsCollection.whereEqualTo("userId", userId).get().await()
            result.toObjects(Meal::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getMealById(mealId: String): Meal? {
        return try {
            val document = mealsCollection.document(mealId).get().await()
            if (document.exists()) {
                document.toObject(Meal::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
