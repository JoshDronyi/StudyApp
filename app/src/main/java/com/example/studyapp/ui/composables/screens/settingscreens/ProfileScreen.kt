package com.example.studyapp.ui.composables.screens.settingscreens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.studyapp.R
import com.example.studyapp.data.model.User
import com.example.studyapp.ui.composables.sharedcomposables.ProfileTextField
import com.example.studyapp.ui.viewmodel.UserViewModel
import kotlinx.coroutines.InternalCoroutinesApi

const val TAG = "Profile_Screen"

@InternalCoroutinesApi
@ExperimentalCoilApi
@Composable
fun ProfileScreen(userViewModel: UserViewModel = viewModel()) {
    val homeScreenState = userViewModel.homeScreenState.observeAsState()
    val currentUser = homeScreenState.value?.currentUser!!

    ProfileScreenContent(user = currentUser) { setting, settingValue ->
        when (setting) {
            ProfileSettings.FirstName -> {
                currentUser.firstName = settingValue
            }
            ProfileSettings.LastName -> {
                currentUser.lastName = settingValue
            }
            ProfileSettings.Email -> {
                currentUser.email = settingValue
            }
            ProfileSettings.PhoneNumber -> {
                currentUser.phoneNumber = settingValue
            }
            ProfileSettings.Role -> {
                currentUser.role = settingValue
            }
            ProfileSettings.BatchStartDate -> {
                currentUser.batchStartDate = settingValue
            }
            ProfileSettings.PhotoUrl -> {
                currentUser.photoUrl = settingValue
            }
        }
        Log.e(
            TAG,
            "ProfileScreen: Setting was ${setting.value}, the setting's value was $settingValue"
        )
    }
}

@ExperimentalCoilApi
@Composable
fun ProfileScreenContent(
    user: User,
    onSettingsChange: (setting: ProfileSettings, settingValue: String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        val imageCornerRadius = 25.dp
        Log.e(TAG, "ProfileScreenContent: URL was ${user.photoUrl}")

        UserImageDisplay(
            user = user,
            imageCornerRadius = imageCornerRadius,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.4f)
                .background(MaterialTheme.colors.primary.copy(alpha = 0.2f))
                .padding(30.dp),
            onSettingsChange = onSettingsChange
        )

        UserDetailDisplay(user = user, onSettingsChange)
    }
}

@Composable
fun UserImageDisplay(
    user: User,
    imageCornerRadius: Dp,
    modifier: Modifier = Modifier,
    onSettingsChange: (setting: ProfileSettings, settingValue: String) -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = rememberImagePainter(user.photoUrl),
            contentDescription = "The user's profile picture.",
            modifier = Modifier
                .fillMaxWidth(.8f)
                .clip(RoundedCornerShape(imageCornerRadius))
                .border(
                    width = 2.dp,
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(imageCornerRadius)
                )
                .shadow(
                    elevation = 4.dp,
                    shape = RoundedCornerShape(imageCornerRadius),
                    clip = true
                ),
            alignment = Alignment.Center
        )

        Button(onClick = {
            //TODO figure out how to get pictures from the user's device or other sources.
            Log.e(TAG, "UserImageDisplay: Gotta change the url of the user")
            //onSettingsChange.invoke(ProfileSettings.PhotoUrl, "")
        }) {
            Image(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = "Button to edit profile pic",
            )
        }
    }
}


@Composable
fun UserDetailDisplay(
    user: User,
    onSettingsChange: (setting: ProfileSettings, settingValue: String) -> Unit
) {
    user.firstName?.let {
        Divider()
        Log.e(TAG, "ProfileScreenContent: First Name was ${user.firstName}")
        ProfileTextField(
            resourceId = R.string.firstName,
            it
        ) { settingValue ->
            onSettingsChange.invoke(ProfileSettings.FirstName, settingValue)
        }
    }

    user.lastName?.let {
        Divider()
        Log.e(TAG, "ProfileScreenContent: Last Name was ${user.lastName}")
        ProfileTextField(
            resourceId = R.string.lastName,
            it
        ) { settingValue ->
            onSettingsChange.invoke(ProfileSettings.LastName, settingValue)
        }
    }

    user.email?.let {
        Divider()
        Log.e(TAG, "ProfileScreenContent: Email was ${user.email}")
        ProfileTextField(
            resourceId = R.string.profileEmail,
            it
        ) { settingValue ->
            onSettingsChange.invoke(ProfileSettings.Email, settingValue)
        }
    }

    user.phoneNumber?.let {
        Divider()
        Log.e(TAG, "ProfileScreenContent: Phone number was ${user.phoneNumber}")
        ProfileTextField(
            resourceId = R.string.profilePhoneNumber,
            it
        ) { settingValue ->
            onSettingsChange.invoke(ProfileSettings.PhoneNumber, settingValue)
        }
    }

    user.role?.let {
        Divider()
        Log.e(TAG, "ProfileScreenContent: User role was ${user.role}")
        ProfileTextField(
            resourceId = R.string.role,
            it
        ) { settingValue ->
            onSettingsChange.invoke(ProfileSettings.Role, settingValue)
        }
    }

    user.batchStartDate.let {
        Divider()
        Log.e(TAG, "ProfileScreenContent: Batch Start Date was ${user.batchStartDate}")
        ProfileTextField(
            resourceId = R.string.startDate,
            it
        ) { settingValue ->
            onSettingsChange.invoke(ProfileSettings.BatchStartDate, settingValue)
        }
    }
}

enum class ProfileSettings(val value: String) {
    FirstName("firstName"),
    LastName("lastName"),
    Email("email"),
    PhoneNumber("number"),
    Role("role"),
    BatchStartDate("batchStartDate"),
    PhotoUrl("photoUrl")

}


@InternalCoroutinesApi
@ExperimentalCoilApi
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}