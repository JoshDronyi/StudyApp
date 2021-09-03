package com.example.studyapp.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.User
import com.example.studyapp.data.repo.UserRepository
import com.example.studyapp.util.State.UserApiState
import com.example.studyapp.util.asUser
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {

    //empty constructor necessary for ViewModel()
    constructor() : this(UserRepository(FirebaseAuth.getInstance()))

    private val TAG = "USER_VIEW_MODEL"


    private val _loginScreenState: MutableLiveData<UserApiState<Any>> = MutableLiveData()

    val loginScreenState: LiveData<UserApiState<Any>>
        get() = _loginScreenState

    suspend fun signInWithEmail(email: String, password: String) = viewModelScope.launch {
        _loginScreenState.value = UserApiState.Loading()
        Log.e(TAG, "signUpWithEmail: Calling Repo with result. State is loading.")

        repo.signInWithEmail(email, password)
            .addOnFailureListener { exception ->
                Log.e(TAG, "Exception: ${exception.localizedMessage}")
                exception.printStackTrace()
            }.addOnSuccessListener { result ->
                Log.e(
                    TAG,
                    "signInWithEmail: SUCCESS!! Auth Result is $result, user is ${result.user}"
                )
                result.user?.let {
                    Log.e(TAG, "signInWithEmail: user was ${it.asUser()}")
                    _loginScreenState.value = UserApiState.Success(it.asUser())
                }
            }
    }


    fun signUpWithEmail(email: String, password: String): Flow<User?> {


        Log.e(TAG, "signUpWithEmail: Calling Repo with result")


        return repo.createNewUserProfile(email, password)
    }
}

