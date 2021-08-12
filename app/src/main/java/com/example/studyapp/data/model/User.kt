package com.example.studyapp.data.model

import android.net.Uri

data class User(
    val uid: String,
    val photoUrl: Uri?,
    val name: String?,
    val email: String?,
    val phoneNumber: String?,
    val isAnnonymous: Boolean
)