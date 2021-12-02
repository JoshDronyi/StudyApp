package com.example.studyapp.data.remote

import com.google.firebase.auth.FirebaseAuth

class AuthDataSource {

    private val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    suspend fun signInWithEmailAndPassword(email: String, password: String) =
        auth.signInWithEmailAndPassword(email, password)

    suspend fun createUserWithEmailAndPassword(email: String, password: String) =
        auth.createUserWithEmailAndPassword(email, password)
}