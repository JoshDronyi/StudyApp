package com.example.studyapp.data.repo

import android.util.Log
import com.example.studyapp.data.model.User
import com.example.studyapp.data.remote.AuthDataSource
import com.example.studyapp.data.remote.FirebaseDatabaseDataSource
import com.example.studyapp.util.ErrorType
import com.example.studyapp.util.ResultType
import com.example.studyapp.util.State.ApiState
import com.example.studyapp.util.StudyAppError
import com.example.studyapp.util.asUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import javax.inject.Inject
import javax.inject.Singleton

@InternalCoroutinesApi
@Singleton
@ExperimentalCoroutinesApi
class UserRepository
@Inject constructor(
    private val auth: AuthDataSource,
    override val firebaseDatabase: FirebaseDatabaseDataSource
) : RepositoryInterface {
    private val tag = "USER_REPO"


    fun signInWithEmail(
        email: String,
        password: String
    ) = callbackFlow {
        Log.e(tag, "signInWithEmail: inside the flow. Calling auth.")
        send(ApiState.Loading)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                with(task) {
                    if (isSuccessful) {
                        result.user?.let {
                            Log.e(
                                tag,
                                "signInWithEmail: SUCCESS!! Auth Result is back and successful, user is $it"
                            )
                            //Return this next variable somehow.
                            trySend(
                                ApiState.Success.UserApiSuccess(it.asUser())
                            )
                        }
                    } else {
                        with(exception) {
                            Log.e(tag, "Exception: ${this?.localizedMessage}")
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

    @DelicateCoroutinesApi
    fun createNewUserProfile(newUser: User, password: String) = callbackFlow {
        Log.e(tag, "createNewUserProfile: Going with the flow")
        send(ApiState.Loading)

        //First call to create the user
        newUser.email?.let { email ->
            auth.createUserWithEmailAndPassword(email, password)
                .addOnFailureListener { exception ->
                    trySend(handleFailure(exception))  //Creating user failed
                }
                .addOnCompleteListener { task ->
                    Log.e(tag, "createNewUserProfile: Task complete")
                    //New User has been created. so check the task to see if its a success
                    when (val handledFirstResult = handleCompletedTask(task)) {
                        is ApiState.Success.UserApiSuccess -> {
                            //User was successfully created so we need to add them to
                            // the user details table in the realtime database.
                            CoroutineScope(Dispatchers.IO).launch {
                                Log.e(
                                    tag,
                                    "createNewUserProfile: the data from the handled first result was ${handledFirstResult.data}" +
                                            "\n new user  was $newUser",
                                )
                                newUser.apply {
                                    uid = (handledFirstResult.data as User)
                                        .uid
                                }
                                //adds the newly created user to the realtime database
                                addUserDetailsToRealtimeDB(newUser).collect { theState ->
                                    when (theState) { //check to see if the user was added successfully.
                                        is ApiState.Success.UserApiSuccess -> {
                                            Log.e(
                                                tag,
                                                "createNewUserProfile: Successfully added User details to RealtimeDB \n $newUser"
                                            )
                                            //add new firebase user to admins if applicable
                                            if (newUser.role?.lowercase() == "admin") {
                                                Log.e(
                                                    tag,
                                                    "createNewUserProfile: Adding user as admin."
                                                )

                                                withContext(Dispatchers.Default) {
                                                    addUserToAdminList(newUser) //Task to add the user to the admin list in the realtime database.
                                                }.addOnCompleteListener { task ->
                                                    //Check to see if new admin was successfully added.
                                                    if (task.isSuccessful) {
                                                        trySend(
                                                            ApiState.Success.UserApiSuccess(
                                                                newUser
                                                            )
                                                        )
                                                    } else {
                                                        trySend(
                                                            ApiState.Error(
                                                                StudyAppError(
                                                                    task.exception,
                                                                    "Unable to add user as an admin.",
                                                                    ErrorType.NETWORK,
                                                                    true
                                                                )
                                                            )
                                                        )
                                                    }
                                                }

                                            } else {
                                                Log.e(
                                                    tag,
                                                    "createNewUserProfile: User is not an admin "
                                                )
                                                trySend(theState)
                                            }
                                        }
                                        else -> {
                                            Log.e(
                                                tag,
                                                "createNewUserProfile: User created successfully but wasnt added to the realtime database",
                                            )
                                            trySend(theState) // User created successfully but wasnt added to the realtime database
                                        }
                                    }
                                }
                            }
                        }
                        else -> {
                            trySend(handledFirstResult) //potential error with first call to add a user
                        }
                    }
                }
        }

        awaitClose {
            Log.e(tag, "createNewUserProfile: closing callback flow")
        }
    }

    private fun handleCompletedTask(task: Task<AuthResult>): ApiState<Any> {
        return if (task.isSuccessful) {
            Log.e(
                tag,
                "createNewUserProfile: Task Successful\n additional info:${task.result.additionalUserInfo}"
            )
            //Add the user the realtime database

            task.result.user?.let {
                Log.e(tag, "handleCompletedTask: it was $it")
                val thisUser = it.asUser()
                return if (thisUser.isDefault) {
                    ApiState.Error(
                        StudyAppError(
                            null,
                            "Blank user returned because of null result user value.",
                            ErrorType.NETWORK,
                            true
                        )
                    )
                } else {
                    Log.e(
                        tag,
                        "handleCompletedTask: got created user, adding to realtime database."
                    )
                    ApiState.Success.UserApiSuccess(thisUser)
                }
            } ?: ApiState.Error(
                StudyAppError(
                    null,
                    "Error occured while attempting to add user.",
                    ErrorType.NETWORK,
                    true
                )
            )
        } else {
            Log.e(tag, "createNewUserProfile: Task Failed")
            Log.e(tag, "Issue creating user:${task.exception?.printStackTrace()}")
            ApiState.Error(
                StudyAppError(
                    task.exception,
                    "Task was unsuccessful.",
                    ErrorType.NETWORK,
                    true
                )
            )
        }
    }


    private fun handleFailure(exception: Exception): ApiState.Error {
        Log.e(
            tag,
            "Unfortunately an error has been thrown. EXCEPTION: ${exception.localizedMessage}"
        )
        exception.printStackTrace()
        return ApiState.Error(
            StudyAppError(
                exception,
                exception.localizedMessage ?: "Unknown Network Error.",
                ErrorType.NETWORK,
                true
            )
        )

    }

    @ExperimentalCoroutinesApi
    fun addUserDetailsToRealtimeDB(user: User): Flow<ApiState<Any>> =
        firebaseDatabase.addNewUserToRealtimeDatabase(user, ResultType.USER)

    private fun addUserToAdminList(user: User) = firebaseDatabase.addUserToAdminList(user)

    fun updateUser(user: User) = firebaseDatabase.updateUser(user)


    override fun onDestroy() {
        //Logic for removing any state carrying objects.
    }
}