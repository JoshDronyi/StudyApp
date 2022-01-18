package com.example.studyapp.ui.composables.screens.loginscreen

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.studyapp.data.model.User
import com.example.studyapp.ui.composables.sharedcomposables.DatePicker
import com.example.studyapp.ui.composables.sharedcomposables.OTFBuilder
import com.example.studyapp.ui.composables.sharedcomposables.Title
import com.example.studyapp.ui.viewmodel.UserViewModel
import com.example.studyapp.util.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
fun SignUpBlock(
    showPicker: Boolean,
    userViewModel: UserViewModel = viewModel(),
    onEventOccurred: (Events.LoginScreenEvents) -> Unit
) {
    val context = LocalContext.current
    val newUser: User by remember { mutableStateOf(User.newBlankInstance()) }
    var passwordText by remember { mutableStateOf("") }
    var verifyPWText by remember { mutableStateOf("") }

    val validEmail by userViewModel.validEmail.observeAsState()
    val validPassword by userViewModel.validPassword.observeAsState()
    val validVerification by userViewModel.validVerification.observeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier
            .padding(8.dp)
            .verticalScroll(rememberScrollState())
    ) {

        var currentStep: SignUpSteps by remember { mutableStateOf(signUpSteps.first()) }

        Title(
            "Step ${signUpSteps.indexOf(currentStep) + 1} of ${signUpSteps.size}"
        )

        Crossfade(
            targetState = currentStep,
            animationSpec = tween(500, 0, LinearEasing)
        ) {
            when (it) {
                SignUpSteps.NAME_DETAILS -> {
                    SignUpBG(text = "Name Details:") {
                        NameDisplays(newUser)
                    }
                }
                SignUpSteps.ACCOUNT_DETAILS -> {
                    SignUpBG("Account Details:") {
                        UserAuthDisplays(
                            newUser,
                            passwordText,
                            verifyPWText
                        ) { password, verifyPW ->
                            passwordText = password
                            verifyPWText = verifyPW
                        }
                    }
                }
                SignUpSteps.CONSULTANT_DETAILS -> {
                    SignUpBG("Consultant Details:") {
                        AdditionalUserDetails(newUser, showPicker) { toggleable -> // will always be datepicker
                            when (toggleable) {
                                Toggleable.DATEPICKER -> {
                                    onEventOccurred.invoke(
                                        Events.LoginScreenEvents.onToggleOption(
                                            toggleable, null
                                        )
                                    )
                                }
                                Toggleable.VERIFICATION -> {
                                    onEventOccurred.invoke(
                                        Events.LoginScreenEvents.onToggleOption(
                                            toggleable,
                                            VerificationOptions.SIGN_IN
                                        )
                                    )
                                }
                            }

                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(16.dp))

        SignUpNavigation(
            currentStep = currentStep,
            onSignUpClicked = {
                newUser.isDefault = false
                onEventOccurred.invoke(
                    Events.LoginScreenEvents.onSignUpAttempt(
                        newUser, passwordText, verifyPWText, context
                    )
                )
            },
            onNextSelected = { sentStep ->
                when (sentStep) {
                    SignUpSteps.NAME_DETAILS -> {
                        currentStep = SignUpSteps.ACCOUNT_DETAILS
                    }
                    SignUpSteps.ACCOUNT_DETAILS -> {
                        currentStep = SignUpSteps.CONSULTANT_DETAILS
                    }
                    SignUpSteps.CONSULTANT_DETAILS -> {
                        Log.e(
                            TAG,
                            "SignUpBlock: Should never happen. Should go to the onSignUpClicked method",
                        )
                    }
                }
            }) { sentStep: SignUpSteps ->
            when (sentStep) {
                SignUpSteps.NAME_DETAILS -> {
                    onEventOccurred.invoke(
                        Events.LoginScreenEvents.onToggleOption(
                            Toggleable.VERIFICATION,
                            VerificationOptions.SIGN_IN
                        )
                    )
                }
                SignUpSteps.ACCOUNT_DETAILS -> {
                    currentStep = SignUpSteps.NAME_DETAILS
                }
                SignUpSteps.CONSULTANT_DETAILS -> {
                    currentStep = SignUpSteps.ACCOUNT_DETAILS
                }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}

val signUpSteps = listOf(
    SignUpSteps.NAME_DETAILS,
    SignUpSteps.ACCOUNT_DETAILS,
    SignUpSteps.CONSULTANT_DETAILS
)

@Composable
fun SignUpNavigation(
    currentStep: SignUpSteps,
    onSignUpClicked: () -> Unit,
    onNextSelected: (SignUpSteps) -> Unit,
    onCurrentStepChange: (SignUpSteps) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(onClick = {
            onCurrentStepChange.invoke(currentStep)
        }) {
            Text(text = "Back")
        }

        Spacer(modifier = Modifier.width(LINE_SIZE.dp))

        when (currentStep) {
            SignUpSteps.CONSULTANT_DETAILS -> {
                Button(onClick = {
                    onSignUpClicked.invoke()
                }) {
                    Text(text = "Sign Up!")
                }
            }
            else -> {
                Button(onClick = {
                    onNextSelected.invoke(currentStep)
                }) {
                    Text(text = "Next")
                }
            }
        }


    }
}

enum class SignUpSteps {
    NAME_DETAILS, ACCOUNT_DETAILS, CONSULTANT_DETAILS
}

@Composable
fun SignUpBG(text: String, modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val cornerRadius = 30.dp
    Card(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(Alignment.CenterVertically)
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(cornerRadius))
            .background(color = MaterialTheme.colors.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colors.onSurface,
                shape = RoundedCornerShape(cornerRadius)
            )
            .shadow(elevation = 16.dp, RoundedCornerShape(cornerRadius)),
        elevation = 16.dp,
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(16.dp))
            Title(text = text)
            content.invoke()
        }
    }
}


@Composable
fun AdditionalUserDetails(newUser: User, showPicker: Boolean, toggleItems: (Toggleable) -> Unit) {
    Spacer(modifier = Modifier.heightIn(min = 16.dp, max = 32.dp))
    //Role
    OTFBuilder(
        label = "User Role:",
        value = newUser.role ?: "",
        inValidInput = newUser.role.isNullOrEmpty() || newUser.role.isNullOrBlank(),
    ) {
        newUser.role = it
    }
    //Date
    DatePickerToggling(showPicker, newUser) { toggleable ->
        toggleItems.invoke(toggleable)
    }
    Spacer(modifier = Modifier.heightIn(min = 16.dp, max = 32.dp))


}

@Composable
fun DatePickerToggling(showPicker: Boolean, newUser: User, onToggleItem: (Toggleable) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Log.e(TAG, "DatePickerToggling: showPicker was $showPicker")
        if (showPicker) {
            DatePicker(modifier = Modifier.fillMaxWidth(.5f)) { date ->
                newUser.batchStartDate = date
                onToggleItem.invoke(Toggleable.DATEPICKER)
            }
        } else {
            Text(text = "Batch Start Date:")
            Spacer(Modifier.width(8.dp))
            Text(text = newUser.batchStartDate)
            Spacer(Modifier.width(8.dp))
            Button(onClick = { onToggleItem.invoke(Toggleable.DATEPICKER) }) {
                Image(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Change the start date"
                )
            }
        }
    }
}

@Composable
fun UserAuthDisplays(
    newUser: User,
    passwordText: String,
    verifyPWText: String,
    onPasswordChange: (password: String, verifyPW: String) -> Unit
) {

    //Email
    OTFBuilder(
        label = "Email",
        value = newUser.email ?: "",
        inValidInput = TextValidator.isValidEmail(newUser.email),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        newUser.email = it
    }

    //Password
    OTFBuilder(
        label = "Password",
        value = passwordText,
        inValidInput = TextValidator.isValidPassword(passwordText),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        onPasswordChange.invoke(it, verifyPWText)
    }
    //Verify Password
    OTFBuilder(
        label = "Verify Password",
        value = verifyPWText,
        inValidInput = TextValidator.verifyPassword(passwordText, verifyPWText),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        onPasswordChange.invoke(passwordText, it)
    }

}

@Composable
fun NameDisplays(newUser: User) {
    //First Name
    OTFBuilder(
        label = "First Name",
        value = newUser.firstName,
        inValidInput = TextValidator.isValidName(newUser.firstName),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        newUser.firstName = it
    }

    //Last Name
    OTFBuilder(
        label = "Last Name",
        value = newUser.lastName,
        inValidInput = TextValidator.isValidName(newUser.lastName),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        newUser.lastName = it
    }
    //Alias
    OTFBuilder(
        label = "Alias",
        value = newUser.alias ?: "",
        inValidInput = TextValidator.isValidAlias(newUser.alias),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        newUser.alias = it
    }
}

@DelicateCoroutinesApi
@ExperimentalCoroutinesApi
@InternalCoroutinesApi
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SignUpPreview() {
    //SignUpBlock(false){}
}