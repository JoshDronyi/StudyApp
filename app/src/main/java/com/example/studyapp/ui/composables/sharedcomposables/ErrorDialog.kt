package com.example.studyapp.ui.composables.sharedcomposables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studyapp.util.Navigator
import com.example.studyapp.util.StudyAppError


@Composable
fun ErrorDialog(
    data: StudyAppError?,
    title: String,
    shouldShow: Boolean = false,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 16.dp),
    onErrorConfirm: () -> Unit
) {
    val shouldShowError = remember { mutableStateOf(shouldShow) }
    val borderCornerRadius = 16.dp

    if (shouldShowError.value) {
        AlertDialog(
            onDismissRequest = {
                shouldShowError.value = !shouldShow
                Navigator.navigateTo(Navigator.currentScreen.value)
            },
            text = { Text(text = data?.message ?: DEFAULT_ERROR_MESSAGE) },
            title = { Text(text = title) },
            shape = RoundedCornerShape(borderCornerRadius),
            backgroundColor = Color.Green.copy(alpha = 0.1f),
            contentColor = Color.White.copy(alpha = 0.7f),
            confirmButton = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onErrorConfirm,
                        modifier = Modifier
                            .clip(RoundedCornerShape(borderCornerRadius))
                            .background(MaterialTheme.colors.primary.copy(alpha = 0.2f))
                            .border(width = 1.dp, color = Color.DarkGray)
                    ) {
                        Text(text = "OK")
                    }
                }
            },
            modifier = modifier
                .border(
                    width = 2.dp,
                    Color.Cyan.copy(alpha = 0.7f),
                    RoundedCornerShape(borderCornerRadius)
                )
        )
    }
}

const val DEFAULT_ERROR_MESSAGE = "Unspecified error. No message"

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ErrorDialogPreview() {
}