package com.example.foodshare.data

data class Dishes(
    val idDish: String,
    val name: String,
    val category: String?,
    val area: String?,
    val instructions: String?,
    val thumbnail: String?,
    val tags: String?,
    val youtubeLink: String?,
    val ingredients: List<String>,
    val measures: List<String>,
    val source: String?
) {
    companion object {
        fun fromApiResponse(apiResponse: Map<String, Any?>): Dishes {
            val ingredients = (1..20).mapNotNull { index ->
                apiResponse["strIngredient$index"] as? String
            }.filter { it.isNotEmpty() }

            val measures = (1..20).mapNotNull { index ->
                apiResponse["strMeasure$index"] as? String
            }.filter { it.isNotEmpty() }

            return Dishes(
                idDish = apiResponse["idMeal"] as String,
                name = apiResponse["strMeal"] as String,
                category = apiResponse["strCategory"] as? String,
                area = apiResponse["strArea"] as? String,
                instructions = apiResponse["strInstructions"] as? String,
                thumbnail = apiResponse["strMealThumb"] as? String,
                tags = apiResponse["strTags"] as? String,
                youtubeLink = apiResponse["strYoutube"] as? String,
                ingredients = ingredients,
                measures = measures,
                source = apiResponse["strSource"] as? String
            )
        }
    }
}




data class ViewDishes(
    val idDish: String,
    val name: String,
    val category: String?,
    val area: String?
) {
    companion object {
        fun fromDishes(dishes: Dishes): ViewDishes {
            return ViewDishes(
                idDish = dishes.idDish,
                name = dishes.name,
                category = dishes.category,
                area = dishes.area
            )
        }
    }
}
