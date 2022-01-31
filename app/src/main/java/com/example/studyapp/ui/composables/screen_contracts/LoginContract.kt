package com.example.studyapp.ui.composables.screen_contracts

import com.example.studyapp.util.Events.LoginScreenEvents
import com.example.studyapp.util.SideEffects.LoginScreenSideEffects
import com.example.studyapp.util.State.ScreenState.LoginScreenState

data class LoginContract(
    var screenState: LoginScreenState =
        LoginScreenState(),
    var screenEvent: LoginScreenEvents =
        LoginScreenEvents(),
    var screenSideEffects: LoginScreenSideEffects = LoginScreenSideEffects()
)

