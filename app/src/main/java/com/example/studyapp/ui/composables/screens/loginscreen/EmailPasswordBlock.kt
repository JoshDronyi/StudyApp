package com.example.studyapp.ui.composables.screens.loginscreen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studyapp.ui.composables.sharedcomposables.OTFBuilder
import com.example.studyapp.util.*
import com.example.studyapp.util.Events.LoginScreenEvents
import com.example.studyapp.util.State.ScreenState.LoginScreenState

@Composable
fun EmailPasswordBlock(
    loginScreenState: LoginScreenState,
    onClick: (event: LoginScreenEvents) -> Unit
) {
    val emailValue by remember { mutableStateOf(loginScreenState.email) }
    val passwordValue by remember { mutableStateOf(loginScreenState.password) }

    val tag = "EMAIL_PASSWORD_BLOCK"
    Column(
        modifier = Modifier.fillMaxWidth(.80f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OTFBuilder(
            label = "Email",
            inValidInput = loginScreenState.validEmail,
            modifier = Modifier.fillMaxWidth()
        ) { value ->
            loginScreenState.email = value
        }

        OTFBuilder(
            label = "Password",
            inValidInput = loginScreenState.validPassword,
            modifier = Modifier.fillMaxWidth()
        ) { value ->
            loginScreenState.password = value
        }

        Spacer(modifier = Modifier.heightIn(min = 30.dp, max = 60.dp))

        Button(
            onClick = {
                when {
                    !loginScreenState.email.validateEmail() -> {
                        with(loginScreenState) {
                            this.copy(
                                error = StudyAppError.newBlankInstance().apply {
                                    message = "Invalid email entered [$emailValue]"
                                    data = null
                                    errorType = ErrorType.VALIDATION
                                    shouldShow = true
                                },
                                validEmail = false
                            )
                        }

                        onClick.invoke(
                            LoginScreenEvents.onValidationError(
                                loginScreenState.error,
                                emailValue
                            )
                        )

                    }
                    !loginScreenState.password.validatePassword() -> {
                        with(loginScreenState) {
                            this.copy(
                                error = StudyAppError.newBlankInstance().apply {
                                    message =
                                        "Password must be at least 6 characters and contain at least 1 digit. " +
                                                "\n password:[$passwordValue]"
                                    data = null
                                    errorType = ErrorType.VALIDATION
                                    shouldShow = true
                                },
                                validPassword = false
                            )
                        }
                        onClick.invoke(
                            LoginScreenEvents.onValidationError(
                                loginScreenState.error,
                                passwordValue
                            )
                        )
                    }
                    else -> {
                        if (loginScreenState.validEmail
                            && loginScreenState.validPassword
                        ) {
                            Log.e(
                                tag,
                                "Sign in button clicked with valid email:$emailValue and password:$passwordValue"
                            )
                            onClick.invoke(
                                LoginScreenEvents.onEmailLoginAttempt(
                                    loginScreenState.email,
                                    loginScreenState.password
                                )
                            )
                        } else {
                            Log.e(TAG, "EmailPasswordBlock: Invalid email or password.")
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(.6f)
        ) {
            Text(text = "SIGN-IN")
        }

        Spacer(modifier = Modifier.heightIn(min = 30.dp, max = 60.dp))

        Text(
            text = "Don't have an account? Sign up here!!",
            modifier = Modifier.clickable {
                onClick.invoke(
                    LoginScreenEvents
                        .onToggleOption(
                            Toggleable.VERIFICATION, VerificationOptions.SIGN_UP
                        )
                )
            },
            textAlign = TextAlign.Center
        )
    }
}
