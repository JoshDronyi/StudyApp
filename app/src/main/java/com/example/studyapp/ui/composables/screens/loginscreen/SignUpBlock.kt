package com.example.studyapp.ui.composables.screens.loginscreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.data.model.User
import com.example.studyapp.ui.composables.sharedcomposables.DatePicker
import com.example.studyapp.ui.composables.sharedcomposables.OTFBuilder
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.LINE_SIZE
import com.example.studyapp.util.Toggleable
import com.example.studyapp.util.VerificationOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
fun SignUpBlock(
    inValidInput: Boolean,
    showPicker: Boolean,
    userViewModel: UserViewModel = viewModel()
) {
    val context = LocalContext.current
    var usernameText by remember { mutableStateOf("") }
    var passwordText by remember { mutableStateOf("") }
    var verifyPWText by remember { mutableStateOf("") }
    var emailText by remember { mutableStateOf("") }
    var roleText by remember { mutableStateOf("") }
    val dateString = remember { mutableStateOf("") }

    val validEmail by userViewModel.validEmail.observeAsState()
    val validPassword by userViewModel.validPassword.observeAsState()
    val validVerification by userViewModel.validVerification.observeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.padding(8.dp)
    ) {

        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            //UserName
            OTFBuilder(
                label = "Username",
                inValidInput = inValidInput,
                modifier = Modifier.weight(0.5f)
            ) {
                usernameText = it
            }
            Spacer(Modifier.width(8.dp))
            //Email
            OTFBuilder(
                label = "Email",
                inValidInput = validEmail,
                modifier = Modifier.weight(0.5f)
            ) {
                emailText = it
            }
        }

        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            //Password
            OTFBuilder(
                label = "Password",
                inValidInput = validPassword,
                modifier = Modifier.weight(0.5f)
            ) {
                passwordText = it
            }

            Spacer(Modifier.width(8.dp))
            //Verify Password
            OTFBuilder(
                label = "Verify Password",
                inValidInput = validVerification,
                modifier = Modifier.weight(0.5f)
            ) {
                verifyPWText = it
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "User Details:")
            Spacer(Modifier.width(8.dp))

            //Role
            OTFBuilder(
                label = "User Role:",
                inValidInput = roleText.isBlank() || roleText.isEmpty(),
            ) {
                roleText = it
            }

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {

                if (showPicker) {
                    DatePicker(modifier = Modifier.fillMaxWidth(.5f)) { date ->
                        dateString.value = date
                        userViewModel.toggleItems(Toggleable.DATEPICKER)
                    }
                } else {
                    Text(text = "Batch Start Date:")
                    Spacer(Modifier.width(8.dp))
                    Text(text = dateString.value)
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { userViewModel.toggleItems(Toggleable.DATEPICKER) }) {
                        Image(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "Change the start date"
                        )
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = {
                userViewModel.onLoginAttempt(VerificationOptions.PREVIOUS, "", "", context)
            }) {
                Text(text = "Back")
            }

            Spacer(modifier = Modifier.width(LINE_SIZE.dp))

            Button(onClick = {
                val newUser = User.newBlankInstance().apply {
                    with(this) {
                        firstName = usernameText
                        role = roleText
                        email = emailText
                        isDefault = false
                        batchStartDate = dateString.value
                    }
                }
                userViewModel.onSignUpAttempt(newUser, passwordText, verifyPWText, context)
            }) {
                Text(text = "Sign Up!")
            }
        }
        Spacer(Modifier.height(8.dp))
    }

}

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SignUpPreview() {
    SignUpBlock(inValidInput = false, false)
}