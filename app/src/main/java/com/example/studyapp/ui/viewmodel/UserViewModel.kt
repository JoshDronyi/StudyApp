package com.example.studyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.User
import com.example.studyapp.data.repo.UserRepository
import com.example.studyapp.util.State.ScreenState
import com.example.studyapp.util.State.UserApiState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {

    //empty constructor necessary for ViewModel()
    constructor() : this(UserRepository(FirebaseAuth.getInstance()))

    private val TAG = "USER_VIEW_MODEL"
    val userData: MutableStateFlow<UserApiState<List<Question>>> =
        MutableStateFlow(UserApiState.Sleep())


    init {
        observeRepo()
    }


    private val _loginState: MutableStateFlow<ScreenState.LoginScreenState> =
        MutableStateFlow(ScreenState.LoginScreenState(User.newBlankInstance()))
    val loginState: StateFlow<ScreenState.LoginScreenState>
        get() = _loginState

    private fun observeRepo() = viewModelScope.launch {
        Log.e(TAG, "observeRepo: observing current user from repo")
        repo.currentUser.collect { user ->
            Log.e(TAG, "observeRepo: received new user object. $user")
            _loginState.value.user = user
        }
    }


    fun signInWithEmail(email: String, password: String) {
        Log.e(TAG, "signInWithEmail: calling the repo")
        viewModelScope.launch {
            repo.signInWithEmail(email, password).collect {
                Log.e(TAG, "logged in user returned: $it")
                it?.let {
                    _loginState.value.user = it
                }
            }
        }
        Log.e(TAG, "signInWithEmail: leaving viewModel")
    }

    fun signUpWithEmail(email: String, password: String) {
        viewModelScope.launch {
            repo.createNewUserProfile(email, password).collect {
                Log.e(TAG, "Received new user result: $it")
                _loginState.value.user = it
            }
        }

    }


}