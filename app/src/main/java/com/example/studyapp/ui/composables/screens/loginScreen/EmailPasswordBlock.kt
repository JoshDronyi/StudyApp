package com.example.studyapp.ui.composables.screens.loginScreen

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
import com.example.studyapp.util.VerificationOptions
import com.example.studyapp.util.validateEmail
import com.example.studyapp.util.validatePassword

@Composable
fun EmailPasswordBlock(onClick: (VerificationOptions, email: String, password: String) -> Unit) {
    var emailValue by remember { mutableStateOf("") }
    var passwordValue by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }
    var validEmail by remember { mutableStateOf(true) }
    var validPW by remember { mutableStateOf(true) }

    val TAG = "EMAIL_PASSWORD_BLOCK"
    Column(
        modifier = Modifier.fillMaxWidth(.80f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        OTFBuilder(
            value = emailValue,
            label = "Email",
            inValidInput = validEmail,
            modifier = Modifier.fillMaxWidth()
        ) { value ->
            emailValue = value
        }

        OTFBuilder(
            value = passwordValue,
            label = "Password",
            inValidInput = validPW,
            modifier = Modifier.fillMaxWidth()
        ) { value ->
            passwordValue = value
        }

        Spacer(modifier = Modifier.heightIn(min = 30.dp, max = 60.dp))

        Button(
            onClick = {
                when {
                    !emailValue.validateEmail() -> {
                        errorText = "Invalid email entered [$emailValue]"
                        validEmail = false
                        onClick.invoke(VerificationOptions.Error, errorText, validEmail.toString())
                    }
                    passwordValue.validatePassword() -> {
                        errorText =
                            "Password must be at least 6 characters and contain at least 1 digit. " +
                                    "\n password:[$passwordValue]"
                        validPW = false
                        onClick.invoke(VerificationOptions.Error, errorText, validPW.toString())
                    }
                    else -> {
                        if (validEmail && validPW) {
                            onClick.invoke(
                                VerificationOptions.EmailPassword,
                                emailValue.trim(),
                                passwordValue.trim()
                            )
                            Log.e(
                                TAG,
                                "Sign in button clicked with valid email:$emailValue and password:$passwordValue"
                            )
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
                onClick.invoke(VerificationOptions.NewUser, emailValue, passwordValue)
            },
            textAlign = TextAlign.Center
        )
    }
}
