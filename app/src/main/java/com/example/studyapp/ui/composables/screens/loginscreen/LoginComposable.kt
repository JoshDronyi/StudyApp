package com.example.studyapp.ui.composables.screens.loginscreen

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.studyapp.util.StudyAppError
import com.example.studyapp.util.Toggleable
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
    val loginState = userViewModel.loginScreenState.observeAsState()
    val context = LocalContext.current

    LoginScreenContent(
        isSignUp = loginState.value?.isSignUp == true,
        error = loginState.value?.error,
        showPicker = loginState.value?.showDatePicker == true,
        onErrorClear = userViewModel::clearLoginError
    ) { vOptions, email, password ->
        Log.e(TAG, "LoginScreen: isSignUp is ${loginState.value?.isSignUp}")
        if (vOptions == VerificationOptions.NEW_USER && loginState.value?.isSignUp != true) {
            Log.e(TAG, "LoginScreen: Toggling sign up value from -> ${loginState.value?.isSignUp}")
            userViewModel.toggleItems(Toggleable.SIGNUP)
        } else {
            Log.e(TAG, "LoginScreen: Attempting login")
            //called when error dialog OK button is clicked.
            userViewModel.onLoginAttempt(vOptions, email, password, context)
        }
    }
}

@DelicateCoroutinesApi
@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
fun LoginScreenContent(
    isSignUp: Boolean = false,
    error: StudyAppError? = null,
    showPicker: Boolean = false,
    onErrorClear: () -> Unit,
    onLoginAttempt: (VerificationOptions, String, String) -> Unit,
) {
    val defaultErrorShown = remember { mutableStateOf(false) }
    Log.e(TAG, "LoginScreenContent: Drawing login screen content")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        if (isSignUp) {
            SignUpBlock(
                inValidInput = false,
                showPicker
            )

        } else {
            MainTextCard(
                text = "Android Study App",
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth(.7f)
                    .heightIn(min = 100.dp, max = 200.dp)
            )

            EmailPasswordBlock { vOption, email, password ->
                onLoginAttempt(vOption, email, password)
            }
        }
    }

    with(error) {
        Log.e(TAG, "LoginScreenContent: error was $this")
        when (this?.errorType) {
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
                    shouldShow = error?.shouldShow == true
                ) { onErrorClear.invoke() }
            }
            ErrorType.NETWORK -> {
                Log.e(TAG, "LoginScreenContent: Network Error occurred.")
                ErrorDialog(
                    data = this,
                    title = LocalContext.current.getString(
                        R.string.errorMessage,
                        "Network"
                    ),
                    shouldShow = error?.shouldShow == true
                ) { onErrorClear.invoke() }
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


@ExperimentalAnimationApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
@Preview(
    showSystemUi = true, showBackground = true
)
fun PreviewLoginScreen() {
    LoginScreen()
}

