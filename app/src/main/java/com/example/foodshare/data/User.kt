package com.example.foodshare.data

import android.net.Uri

data class User(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val age: Int = 0,
    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val profilePictureUri: Uri? = null
)
