package com.example.foodshare.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class DishesRepository {

    private val apiBaseUrl = "https://www.themealdb.com/api/json/v1/1/"

    suspend fun getDishDetails(dishId: String): Dishes? = withContext(Dispatchers.IO) {
        val url = URL("${apiBaseUrl}lookup.php?i=$dishId")
        val connection = url.openConnection() as HttpURLConnection

        return@withContext try {
            connection.connect()
            val responseStream = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(responseStream)
            val dishesArray = jsonResponse.optJSONArray("meals")

            dishesArray?.let {
                val dishObject = it.getJSONObject(0)
                val dishMap = dishObject.toMap()
                Dishes.fromApiResponse(dishMap)
            }
        } finally {
            connection.disconnect()
        }
    }

    suspend fun searchDishes(query: String): List<Dishes> = withContext(Dispatchers.IO) {
        val url = URL("${apiBaseUrl}search.php?s=$query")
        val connection = url.openConnection() as HttpURLConnection

        return@withContext try {
            connection.connect()
            val responseStream = connection.inputStream.bufferedReader().use { it.readText() }
            val jsonResponse = JSONObject(responseStream)
            val dishesArray = jsonResponse.optJSONArray("meals")

            dishesArray?.let {
                (0 until it.length()).map { index ->
                    val dishObject = it.getJSONObject(index)
                    val dishMap = dishObject.toMap()
                    Dishes.fromApiResponse(dishMap)
                }
            } ?: emptyList()
        } finally {
            connection.disconnect()
        }
    }
}

fun JSONObject.toMap(): Map<String, Any?> {
    return keys().asSequence().associateWith { key -> this.opt(key) }
}
