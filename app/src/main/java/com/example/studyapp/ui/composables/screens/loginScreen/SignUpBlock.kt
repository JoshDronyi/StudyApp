package com.example.studyapp.ui.composables.screens.loginScreen

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studyapp.ui.composables.sharedcomposables.OTFBuilder
import com.example.studyapp.util.*

@Composable
fun SignUpBlock(
    inValidInput: Boolean,
    onClick: (VerificationOptions, email: String, password: String) -> Unit
) {
    var usernameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var verifyPWText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var errorText by remember { mutableStateOf("") }

    var validEmail = true
    var validPassword = true
    var validVerification = true
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        //UserName
        OTFBuilder(value = usernameText, label = "Username", inValidInput = inValidInput) {
            usernameText = it
        }
        //Email
        OTFBuilder(value = emailText, label = "Email", inValidInput = validEmail) {
            emailText = it
        }
        //Password
        OTFBuilder(value = passwordText, label = "Password", inValidInput = validPassword) {
            passwordText = it
        }
        //Verify Password
        OTFBuilder(
            value = verifyPWText,
            label = "Verify Password",
            inValidInput = validVerification
        ) {
            verifyPWText = it
        }

        Spacer(modifier = Modifier.height(LINE_SIZE.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                onClick.invoke(VerificationOptions.PREVIOUS, "", "")
            }) {
                Text(text = "Back")
            }

            Spacer(modifier = Modifier.width(LINE_SIZE.dp))
            Button(onClick = {
                when {
                    emailText.validateEmail() -> {
                        errorText = "Please enter a valid email"
                        validEmail = false
                    }
                    passwordText.validatePassword() -> {
                        errorText =
                            "Passwords must have at least $MIN_PW_CHARS characters with at least one digit."
                        validPassword = false
                    }
                    verifyPWText.validatePassword() -> {
                        errorText =
                            "Verified passwords must have at least $MIN_PW_CHARS characters with at least one digit."
                        validVerification = false
                    }
                    passwordText != verifyPWText -> {
                        errorText = "Passwords do not match."
                        validVerification = false
                    }
                    else -> {
                        if (validEmail && validPassword && validVerification) {
                            onClick.invoke(VerificationOptions.NewUser, emailText, passwordText)
                        }
                    }
                }
            }) {
                Text(text = "Sign Up!")
            }
        }
    }

}