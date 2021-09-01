package com.example.studyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.User
import com.example.studyapp.data.repo.UserRepository
import com.example.studyapp.util.State.ScreenState.LoginScreenState
import com.example.studyapp.util.State.UserApiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {

    //empty constructor necessary for ViewModel()
    constructor() : this(UserRepository(FirebaseAuth.getInstance()))

    private val TAG = "USER_VIEW_MODEL"


    private val _loginScreenState: MutableStateFlow<UserApiState<Any>> =
        MutableStateFlow(UserApiState.Sleep())

    val loginScreenState: StateFlow<UserApiState<Any>>
        get() = _loginScreenState

    fun observeRepo() = viewModelScope.launch(Dispatchers.IO) {
        Log.e(TAG, "observeRepo: observing current user from repo")
        repo.currentUser.collect { newUser ->
            Log.e(TAG, "logged in user returned: $newUser")
            Log.e(TAG, "observeRepo: Setting state of loginScreenState.")
            _loginScreenState.value = UserApiState.Success(newUser)
        }
    }

    fun signInWithEmail(email: String, password: String) = viewModelScope.launch(Dispatchers.IO) {
        Log.e(TAG, "signUpWithEmail: Calling Repo with result")
        repo.signInWithEmail(email, password)
            .onStart {
                Log.e(TAG, "signInWithEmail: starting sign in. setting value to loading.")
                _loginScreenState.tryEmit(UserApiState.Loading())
            }
            .onCompletion {
                Log.e(TAG, "signInWithEmail: Complete: $newUser")
                newUser?.let { user ->
                    _loginScreenState.tryEmit(UserApiState.Success(user))
                }
            }
            .collect { newUser ->
                Log.e(TAG, "signInWithEmail: Got a new user in the viewModel -> $newUser")
                newUser?.let { user ->
                    _loginScreenState.tryEmit(UserApiState.Success(user))
                }
            }
    }


    fun signUpWithEmail(email: String, password: String): Flow<User?> {
        Log.e(TAG, "signUpWithEmail: Calling Repo with result")
        return repo.createNewUserProfile(email, password)
    }
}

