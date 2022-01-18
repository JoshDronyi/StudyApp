package com.example.studyapp.ui.composables.screens.loginscreen

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.R
import com.example.studyapp.ui.composables.sharedcomposables.ErrorDialog
import com.example.studyapp.ui.composables.sharedcomposables.MainTextCard
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.ErrorType
import com.example.studyapp.util.Events.LoginScreenEvents
import com.example.studyapp.util.SideEffects.LoginScreenSideEffects
import com.example.studyapp.util.SignInOptions
import com.example.studyapp.util.State.ScreenState.LoginScreenState
import com.example.studyapp.util.VerificationOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

const val TAG = "LoginScreen"

@DelicateCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
fun LoginScreen(
    userViewModel: UserViewModel = viewModel()
) {
    Log.e(TAG, "LoginScreen: drawing Login Screen")
    var loginContract by remember { mutableStateOf(LoginContract()) }
    val loginState by userViewModel.loginScreenState.observeAsState()
    loginContract.screenState.value = loginState

    when (val event = loginContract.screenSideEffects) {
        is LoginScreenSideEffects.SetLoginType -> {
            when (event.signInMethod) {
                SignInOptions.EMAIL_PASSWORD -> userViewModel.changeLoginMethod(event.signInMethod)
                else -> {
                    Log.e(
                        TAG,
                        "LoginScreen: the signIn method given was ${event.signInMethod}",
                    )
                }
            }
        }
        is LoginScreenSideEffects.EmailLoginAttempt -> {
            //called when error dialog OK button is clicked.
            userViewModel.onLoginAttempt(
                SignInOptions.EMAIL_PASSWORD,
                event.email,
                event.password,
            )
        }
        is LoginScreenSideEffects.ClearError -> {
            userViewModel.clearLoginError()
        }
        is LoginScreenSideEffects.ToggleItems -> {
            userViewModel.toggleItems(event.toggleable, event.verification)
        }
        is LoginScreenSideEffects.OnSignUpAttempt -> {
            userViewModel.onSignUpAttempt(
                event.newUser,
                event.passwordText,
                event.verifyPWText,
                event.context
            )
        }
    }


    LoginScreenContent(
        loginContract.screenState.value!!,
    ) { event ->
        Log.e(
            TAG,
            "LoginScreen: sign in method is ${loginContract.screenState.value?.signInOption}"
        )
        loginContract.screenEvent.value = event
        when (event) {
            is LoginScreenEvents.onLoginMethodSwitch -> {
                when (event.signInMethod) {
                    SignInOptions.EMAIL_PASSWORD -> {
                        Log.e(
                            TAG,
                            "LoginScreen: setting the login method value from -> ${loginState?.signInOption}"
                        )
                        loginContract = loginContract.copy(
                            screenSideEffects = LoginScreenSideEffects.SetLoginType(event.signInMethod)
                        )

                    }
                    else -> {
                        Log.e(
                            TAG,
                            "LoginScreen: Different type of onLoginMethodSwitch event was required. Current: ${event.signInMethod}"
                        )
                    }
                }

            }
            is LoginScreenEvents.onEmailLoginAttempt -> {
                Log.e(
                    TAG,
                    "LoginScreen: Changing to 'Email Login Attempt' screen side effect with email=${event.email}, password= ${event.password}."
                )
                loginContract = loginContract.copy(
                    screenSideEffects = LoginScreenSideEffects.EmailLoginAttempt(
                        event.email,
                        event.password
                    )
                )
            }
            is LoginScreenEvents.onClearError -> {
                Log.e(TAG, "LoginScreen: Clearing previous error.")
                loginContract = loginContract.copy(
                    screenSideEffects = LoginScreenSideEffects.ClearError
                )
            }
            is LoginScreenEvents.onToggleOption -> {
                Log.e(TAG, "LoginScreen: toggling event toggleable ${event.toggleable}")
                loginContract = loginContract.copy(
                    screenSideEffects = LoginScreenSideEffects.ToggleItems(
                        event.toggleable,
                        event.verification
                    )
                )
            }
            is LoginScreenEvents.onSignUpAttempt -> {
                loginContract = loginContract.copy(
                    screenSideEffects = LoginScreenSideEffects.OnSignUpAttempt(
                        event.newUser, event.passwordText, event.verifyPWText, event.context
                    )
                )
            }
            else -> {
                Log.e(TAG, "LoginScreen: event = $event")
            }
        }
    }
}

@DelicateCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
fun LoginScreenContent(
    loginScreenState: LoginScreenState,
    onEventOccurred: (event: LoginScreenEvents) -> Unit,
) {
    val defaultErrorShown = remember { mutableStateOf(false) }
    val error = loginScreenState.error

    Log.e(TAG, "LoginScreenContent: Drawing login screen content")

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        when (loginScreenState.loginOption) {
            VerificationOptions.SIGN_UP -> {
                SignUpBlock(
                    loginScreenState.showDatePicker
                ) { event ->
                    onEventOccurred.invoke(event)
                }
            }
            VerificationOptions.SIGN_IN -> {
                MainTextCard(
                    text = "Android Study App",
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier
                        .fillMaxWidth(.7f)
                        .heightIn(min = 100.dp, max = 200.dp)
                )

                EmailPasswordBlock(loginScreenState) { event: LoginScreenEvents ->
                    onEventOccurred.invoke(event)
                }
            }
            VerificationOptions.ERROR -> {
                with(error) {
                    Log.e(TAG, "LoginScreenContent: error was $this")
                    when (this.errorType) {
                        ErrorType.DEFAULT, ErrorType.TEST -> {
                            if (!defaultErrorShown.value) {
                                Log.e(
                                    TAG,
                                    "LoginScreenContent: unintended error. ErrorType: $errorType"
                                )
                                defaultErrorShown.value = true
                            } else {
                                Log.e(TAG, "LoginScreenContent: ")
                            }
                        }
                        ErrorType.LOGIN -> {
                            Log.e(TAG, "LoginScreenContent: Error with login data.")
                            ErrorDialog(
                                data = this,
                                title = LocalContext.current.getString(
                                    R.string.errorMessage,
                                    "Login"
                                ),
                                shouldShow = error.shouldShow
                            ) { onEventOccurred.invoke(LoginScreenEvents.onClearError) }
                        }
                        ErrorType.NETWORK -> {
                            Log.e(TAG, "LoginScreenContent: Network Error occurred.")
                            ErrorDialog(
                                data = this,
                                title = LocalContext.current.getString(
                                    R.string.errorMessage,
                                    "Network"
                                ),
                                shouldShow = error.shouldShow
                            ) { onEventOccurred.invoke(LoginScreenEvents.onClearError) }
                        }
                        else -> {
                            Log.e(
                                TAG,
                                "LoginScreenContent: Unrecognized error Type. Should never occur.",
                            )
                        }
                    }
                }
            }
        }
    }
}


@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
@Preview(
    showSystemUi = true, showBackground = true
)
fun PreviewLoginScreen() {
    //LoginScreen()
}

