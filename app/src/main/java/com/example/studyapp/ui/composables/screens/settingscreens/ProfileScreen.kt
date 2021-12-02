package com.example.studyapp.ui.composables.screens.settingscreens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.example.studyapp.R
import com.example.studyapp.data.model.User
import com.example.studyapp.ui.composables.sharedcomposables.ProfileTextField

const val TAG = "Profile_Screen"

@ExperimentalCoilApi
@Composable
fun ProfileScreen() {
    val context = LocalContext.current
    ProfileScreenContent(user = User.newBlankInstance()) { setting, settingValue ->
        Toast.makeText(
            context,
            "Setting was $setting, the setting's value was $settingValue",
            Toast.LENGTH_SHORT
        ).show()
    }
}

@ExperimentalCoilApi
@Composable
fun ProfileScreenContent(
    user: User,
    onSettingsChange: (setting: String, settingValue: String) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        val imageCornerRadius = 25.dp
        Log.e(TAG, "ProfileScreenContent: URL was ${user.photoUrl}")

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(.4f)
                .background(MaterialTheme.colors.primary.copy(alpha = 0.2f))
                .padding(30.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = rememberImagePainter(user.photoUrl),
                contentDescription = "The user's profile picture.",
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .clip(RoundedCornerShape(imageCornerRadius))
                    .border(
                        width = 4.dp,
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(imageCornerRadius)
                    )
                    .shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(imageCornerRadius),
                        clip = true
                    ),
                alignment = Alignment.Center
            )
        }

        Divider()
        Log.e(TAG, "ProfileScreenContent: Name was ${user.firstName}")
        user.firstName?.let {
            ProfileTextField(
                resourceId = R.string.profileName,
                it
            ) { settingValue ->
                onSettingsChange.invoke("name", settingValue)
            }
        }

        Divider()
        Log.e(TAG, "ProfileScreenContent: Email was ${user.email}")
        user.email?.let {
            ProfileTextField(
                resourceId = R.string.profileEmail,
                it
            ) { settingValue ->
                onSettingsChange.invoke("email", settingValue)
            }
        }

        Divider()
        Log.e(TAG, "ProfileScreenContent: Phone number was ${user.phoneNumber}")
        user.phoneNumber?.let {
            ProfileTextField(
                resourceId = R.string.profilePhoneNumber,
                it
            ) { settingValue ->
                onSettingsChange.invoke("phoneNumber", settingValue)
            }
        }
    }
}


@ExperimentalCoilApi
@Preview(showSystemUi = true, showBackground = true)
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}