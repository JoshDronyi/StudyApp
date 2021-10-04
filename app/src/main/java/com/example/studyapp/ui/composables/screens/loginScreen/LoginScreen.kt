package com.example.studyapp.ui.composables.screens.loginScreen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studyapp.R
import com.example.studyapp.ui.composables.sharedcomposables.ErrorDialog
import com.example.studyapp.ui.composables.sharedcomposables.MainTextCard
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlin.reflect.KFunction4

const val TAG = "LoginScreen"

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
fun LoginScreen(
    context: Context,
    userViewModel: UserViewModel
) {
    Log.e(TAG, "LoginScreen: drawing Login Screen")
    val isSignUp by userViewModel.isSignUp.observeAsState()
    val error by userViewModel.error.observeAsState()

    LoginScreenContent(
        isSignUp = isSignUp,
        context = context,
        error = error,
        onErrorClear = userViewModel::clearLoginError
    ) { vOptions, email, password ->
        //called when error dialog OK button is clicked.
        userViewModel.onLoginAttempt(vOptions, email, password, context)
    }
}

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
fun LoginScreenContent(
    isSignUp: Boolean? = false,
    error: StudyAppError? = null,
    context: Context,
    onErrorClear: () -> Unit,
    onLoginAttempt: (VerificationOptions, String, String) -> Unit,
) {
    Log.e(TAG, "LoginScreenContent: Drawing login screen content")
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxSize()
    ) {
        MainTextCard(
            text = "Android Study App",
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(.7f)
                .fillMaxHeight(.2f)
        )
        isSignUp?.let {
            if (it) {
                SignUpBlock(inValidInput = false) { vOptions, email, password ->
                    onLoginAttempt(vOptions, email, password)
                }
            } else {
                EmailPasswordBlock { vOption, email, password ->
                    onLoginAttempt(vOption, email, password)
                }
            }
        }

        with(error) {
            Log.e(TAG, "LoginScreenContent: error was $this")
            when (this?.errorType) {
                ErrorType.DEFAULT, ErrorType.TEST -> {
                    Log.e(
                        TAG,
                        "LoginScreenContent: unintended error. ErrorType: $errorType"
                    )
                }
                ErrorType.LOGIN -> {
                    Log.e(TAG, "LoginScreenContent: Error with login data.")
                    ErrorDialog(
                        data = this,
                        title = context.getString(
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
                        title = context.getString(
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
}


