package com.example.studyapp.data.repo

import android.util.Log
import com.example.studyapp.data.model.User
import com.example.studyapp.util.ErrorType
import com.example.studyapp.util.State.ApiState
import com.example.studyapp.util.StudyAppError
import com.example.studyapp.util.asUser
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth
) : RepositoryInterface {
    private val tag = "USER_REPO"

    @ExperimentalCoroutinesApi
    fun signInWithEmail(
        email: String,
        password: String
    ) = callbackFlow {
        Log.e(tag, "signInWithEmail: inside the flow. Calling auth.")
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                with(task) {
                    if (isSuccessful) {
                        result?.let {
                            Log.e(
                                tag,
                                "signInWithEmail: SUCCESS!! Auth Result is $it, user is ${it.user}"
                            )
                            //Return this next variable somehow.
                            trySend(
                                ApiState.Success.UserApiSuccess(it.user?.asUser())
                            )
                        }
                    } else {
                        with(exception) {
                            Log.e(tag, "Exception: $this.?localizedMessage")
                            val derivedMessage = this?.localizedMessage ?: "UnKnown Error"
                            //Return this next line somehow.
                            trySend(
                                ApiState.Error(
                                    StudyAppError(
                                        data = exception,
                                        message = derivedMessage,
                                        errorType = ErrorType.NETWORK,
                                        shouldShow = true
                                    )
                                )
                            )
                        }
                    }
                }
            }
        awaitClose {
            Log.e(tag, "signInWithEmail: closing callback flow")
        }
    }

    fun createNewUserProfile(email: String, password: String): Flow<User> = flow {
        Log.e(tag, "createNewUserProfile: Going with the flow")
        auth.createUserWithEmailAndPassword(email, password)
            .addOnFailureListener { exception ->
                Log.e(
                    tag,
                    "Unfortunately an error has been thrown. EXCEPTION: ${exception.localizedMessage}"
                )
                exception.printStackTrace()
            }
            .addOnCompleteListener { task ->
                Log.e(tag, "createNewUserProfile: Task complete")
                if (task.isSuccessful) {
                    Log.e(tag, "createNewUserProfile: Task Successful")
                    task.result?.let { result ->
                        result.user?.let {
                            //_currentUser.tryEmit(it.asUser())
                        }
                    }
                } else {
                    Log.e(tag, "createNewUserProfile: Task Failed")
                    Log.e(tag, "Issue creating user:${task.exception?.printStackTrace()}")
                }
            }
    }

    override fun onDestroy() {
        //Logic for removing any state carrying objects.
    }
}