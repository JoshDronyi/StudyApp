package com.example.studyapp.ui.composables.screens.loginscreen

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.studyapp.R
import com.example.studyapp.ui.composables.screen_contracts.LoginContract
import com.example.studyapp.ui.composables.sharedcomposables.ErrorDialog
import com.example.studyapp.ui.composables.sharedcomposables.MainTextCard
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.*
import com.example.studyapp.util.Events.LoginScreenEvents
import com.example.studyapp.util.SideEffects.LoginScreenSideEffects
import com.example.studyapp.util.SideEffects.LoginScreenSideEffects.*
import com.example.studyapp.util.State.ScreenState.LoginScreenState
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
    navController: NavController,
    userViewModel: UserViewModel = viewModel()
) {
    Log.e(TAG, "LoginScreen: drawing Login Screen")
    val loginContract by userViewModel.loginScreenContract.collectAsState(initial = LoginContract())

    val sideEffect by rememberUpdatedState(newValue = loginContract.screenSideEffects)
    val theEvent by rememberUpdatedState(newValue = loginContract.screenEvent)

    LaunchedEffect(sideEffect, theEvent) {
        handleSideEffect(navController, sideEffect, userViewModel)
        handleLoginEvent(theEvent, userViewModel)
    }


    LoginScreenContent(
        navController,
        loginContract.screenState,
    ) { event ->
        Log.e(
            TAG,
            "LoginScreen: sign in method is ${loginContract.screenState.signInOption}"
        )
        userViewModel.setLoginEvent(event)
    }
}

@ExperimentalCoroutinesApi
@DelicateCoroutinesApi
@InternalCoroutinesApi
fun handleSideEffect(
    navController: NavController,
    sideEffect: LoginScreenSideEffects,
    userViewModel: UserViewModel
) {

    when (sideEffect) {
        is SetLoginType -> {
            when (sideEffect.signInMethod) {
                SignInOptions.EMAIL_PASSWORD -> userViewModel.changeLoginMethod(sideEffect.signInMethod)
                else -> {
                    Log.e(
                        TAG,
                        "LoginScreen: the signIn method given was ${sideEffect.signInMethod}",
                    )
                }
            }
        }
        is EmailLoginAttempt -> {
            //called when error dialog OK button is clicked.
            userViewModel.onLoginAttempt(
                SignInOptions.EMAIL_PASSWORD,
                sideEffect.email,
                sideEffect.password,
            )
        }
        is ClearError -> {
            userViewModel.setLoginError()
        }
        is ToggleItems -> {
            userViewModel.toggleItems(sideEffect.toggleable, sideEffect.verification)
        }
        is OnSignUpAttempt -> {
            userViewModel.onSignUpAttempt(
                sideEffect.newUser,
                sideEffect.passwordText,
                sideEffect.verifyPWText,
                sideEffect.context
            )
        }
        is Navigate -> {
            navController.navigate(sideEffect.target.route) {
                launchSingleTop = true
            }
        }
    }
}

@ExperimentalCoroutinesApi
@DelicateCoroutinesApi
@InternalCoroutinesApi
fun handleLoginEvent(event: LoginScreenEvents, userViewModel: UserViewModel) {
    when (event) {
        is LoginScreenEvents.OnLoginMethodSwitch -> {
            when (event.signInMethod) {
                SignInOptions.EMAIL_PASSWORD -> {
                    Log.e(
                        TAG,
                        "LoginScreen: setting the login method value from -> ${event.signInMethod}"
                    )

                    userViewModel.setSideEffect(SetLoginType(event.signInMethod))
                }
                else -> {
                    Log.e(
                        TAG,
                        "LoginScreen: Different type of onLoginMethodSwitch event was required. Current: ${event.signInMethod}"
                    )
                }
            }

        }
        is LoginScreenEvents.OnEmailLoginAttempt -> {
            Log.e(
                TAG,
                "LoginScreen: Changing to 'Email Login Attempt' screen side effect with email=${event.email}, password= ${event.password}."
            )
            userViewModel.setSideEffect(
                EmailLoginAttempt(
                    event.email,
                    event.password
                )
            )
        }
        is LoginScreenEvents.OnClearError -> {
            Log.e(TAG, "LoginScreen: Clearing previous error.")
            userViewModel.setSideEffect(
                ClearError
            )
        }
        is LoginScreenEvents.OnToggleOption -> {
            Log.e(TAG, "LoginScreen: toggling event toggleable ${event.toggleable}")
            userViewModel.setSideEffect(
                ToggleItems(
                    event.toggleable,
                    event.verification
                )
            )
        }
        is LoginScreenEvents.OnSignUpAttempt -> {
            userViewModel.setSideEffect(
                OnSignUpAttempt(
                    event.newUser, event.passwordText, event.verifyPWText, event.context
                )
            )
        }
        is LoginScreenEvents.OnComplete -> {
            Log.e(TAG, "handleLoginEvent: Navgating to Home Screen")
            userViewModel.setSideEffect(
                Navigate(Screens.HomeScreen)
            )
        }
    }
}

@DelicateCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
fun LoginScreenContent(
    navController: NavController,
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
                                navController = navController,
                                data = this,
                                title = LocalContext.current.getString(
                                    R.string.errorMessage,
                                    "Login"
                                ),
                                shouldShow = error.shouldShow
                            ) { onEventOccurred.invoke(LoginScreenEvents.OnClearError) }
                        }
                        ErrorType.NETWORK -> {
                            Log.e(TAG, "LoginScreenContent: Network Error occurred.")
                            ErrorDialog(
                                navController = navController,
                                data = this,
                                title = LocalContext.current.getString(
                                    R.string.errorMessage,
                                    "Network"
                                ),
                                shouldShow = error.shouldShow
                            ) { onEventOccurred.invoke(LoginScreenEvents.OnClearError) }
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

