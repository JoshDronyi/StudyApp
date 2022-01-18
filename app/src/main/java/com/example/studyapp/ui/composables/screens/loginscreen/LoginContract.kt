package com.example.studyapp.ui.composables.screens.loginscreen

import androidx.lifecycle.MutableLiveData
import com.example.studyapp.util.Events.LoginScreenEvents
import com.example.studyapp.util.SideEffects.LoginScreenSideEffects
import com.example.studyapp.util.State.ScreenState.LoginScreenState

data class LoginContract(
    val screenState: MutableLiveData<LoginScreenState> =
        MutableLiveData(LoginScreenState()),
    val screenEvent: MutableLiveData<LoginScreenEvents> =
        MutableLiveData(LoginScreenEvents()),
    val screenSideEffects: LoginScreenSideEffects = LoginScreenSideEffects()
)