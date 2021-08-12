package com.example.studyapp.data.repo

import android.util.Log
import com.example.studyapp.data.model.User
import com.example.studyapp.util.asUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth
) : RepositoryInterface {
    private val TAG = "USER_REPO"

    private val _currentUser: MutableSharedFlow<User> = MutableSharedFlow()
    val currentUser: SharedFlow<User> get() = _currentUser


    fun signInWithEmail(email: String, password: String): Flow<User?> = flow {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let { authResult ->
                        authResult.user?.let {
                            _currentUser.tryEmit(it.asUser())
                        }
                    }
                } else {
                    Log.e(TAG, "Issue signing in user: ${task.exception?.printStackTrace()}")
                }
            }
    }

    fun createNewUserProfile(email: String, password: String): Flow<User> = flow {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let { result ->
                        result.user?.let {
                            _currentUser.tryEmit(it.asUser())
                        }

                    }
                } else {
                    Log.e(TAG, "Issue creating user:${task.exception?.printStackTrace()}")
                }
            }
    }

    override fun onDestroy() {
        //Logic for removing any state carrying objects.
    }
}