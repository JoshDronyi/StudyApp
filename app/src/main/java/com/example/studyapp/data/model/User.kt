package com.example.studyapp.data.model

import android.net.Uri

data class User(
    val uid: String,
    val photoUrl: Uri? = null,
    val name: String? = "Default User Name",
    val email: String?,
    val phoneNumber: String? = null,
    val isDefault: Boolean = true
) {
    companion object {
        fun newBlankInstance(): User {
            return User(
                uid = "",
                photoUrl = null,
                email = null
            )
        }
    }
}