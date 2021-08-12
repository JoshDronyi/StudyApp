package com.example.studyapp.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.User
import com.example.studyapp.data.repo.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {

    constructor() : this(UserRepository(FirebaseAuth.getInstance()))

    init {
        observeRepo()
    }


    private val _currentUser: MutableLiveData<User> = MutableLiveData()
    val currentUser: LiveData<User> get() = _currentUser

    private fun observeRepo() {
        viewModelScope.launch {
            repo.currentUser.collect { user ->
                _currentUser.value = user
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {
        repo.signInWithEmail(email, password)
    }

    fun signUpWithEmail(email: String, password: String) {
        repo.createNewUserProfile(email, password)
    }


}