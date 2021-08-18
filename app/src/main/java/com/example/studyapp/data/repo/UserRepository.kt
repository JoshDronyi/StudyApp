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
        Log.e(TAG, "signInWithEmail: inside the flow. Calling auth.")
        auth.signInWithEmailAndPassword(email, password)
            .addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "Unfortunately an error has been thrown. Exception: ${exception.localizedMessage}"
                )
                exception.printStackTrace()
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let { authResult ->
                        Log.e(TAG, "signInWithEmail: task result was not null. $authResult")
                        authResult.user?.let { user ->
                            Log.e(TAG, "signInWithEmail: successfully got firebase user. $user")
                            _currentUser.tryEmit(user.asUser())

                        }
                    }
                } else {
                    Log.e(TAG, "Issue signing in user: ${task.exception?.printStackTrace()}")
                }
            }
    }

    fun createNewUserProfile(email: String, password: String): Flow<User> = flow {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnFailureListener { exception ->
                Log.e(
                    TAG,
                    "Unfortunately an error has been thrown. EXCEPTION: ${exception.localizedMessage}"
                )
                exception.printStackTrace()
            }
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