package com.example.studyapp.ui.composables.sharedcomposables

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.studyapp.util.ButtonOptions
import com.example.studyapp.util.Screens

@Composable
fun StudyTopAppBar(
    text: String,
    destination: Screens,
    state: ScaffoldState,
    onMenuClick: (ButtonOptions, isOpen: Boolean) -> Unit
) {
    TopAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 0.dp,
        modifier = Modifier.requiredHeight(60.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(Modifier.width(10.dp))
            when (destination.route) {
                Screens.LoginScreen.route, Screens.MainScreen.route -> {
                    Image(
                        imageVector = Icons.TwoTone.Menu,
                        contentDescription = "Toggle the drawer menu",
                        Modifier.clickable {
                            onMenuClick.invoke(
                                ButtonOptions.MENU,
                                state.drawerState.isOpen
                            )
                        },
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary)
                    )
                }
                else -> {
                    Image(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Toggle the drawer menu",
                        Modifier.clickable {
                            onMenuClick.invoke(
                                ButtonOptions.BACK,
                                state.drawerState.isOpen
                            )
                        },
                        colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary)
                    )
                }
            }

            Text(
                text = text,
                Modifier
                    .weight(0.6f),
                textAlign = TextAlign.Center
            )

            Image(
                imageVector = Icons.TwoTone.Settings,
                contentDescription = "Access the settings page.",
                Modifier.clickable {
                    onMenuClick.invoke(
                        ButtonOptions.SETTINGS,
                        state.drawerState.isOpen
                    )
                }
            )
        }
    }
}