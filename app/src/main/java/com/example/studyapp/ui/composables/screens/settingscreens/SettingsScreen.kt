package com.example.studyapp.ui.composables.screens.settingscreens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studyapp.R
import com.example.studyapp.ui.composables.sharedcomposables.ProfileTextField

@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .fillMaxSize()
            .background(color = Color.Black.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(20.dp))
            .fillMaxWidth(0.7f)
            .fillMaxHeight(0.4f)
    ) {
        val context = LocalContext.current
        ProfileTextField(value = "Profile", resourceId = R.string.settingsItem){
            Toast.makeText(context, "Deciding to change $it",Toast.LENGTH_SHORT).show()
        }
        Divider()
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    SettingsScreen()
}