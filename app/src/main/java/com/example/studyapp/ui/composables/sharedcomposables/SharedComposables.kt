package com.example.studyapp.ui.composables.sharedcomposables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.twotone.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.util.*

@Composable
fun QuestionCard(
    question: Question,
    backgroundColor: Color,
    onQuestionSelected: (Question) -> Unit
) {
    Card(
        elevation = 10.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            Modifier
                .clickable {
                    onQuestionSelected.invoke(question)
                }
                .fillMaxSize()
                .background(backgroundColor)
        ) {
            Text(
                text = "Question number ${question.questionNumber}",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ProgressBanner(currentWeek: String, progress: StudentProgress) {
    Surface(
        elevation = 16.dp,
        shape = RoundedCornerShape(35.dp),
        color = MaterialTheme.colors.surface
    ) {
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Text(
                text = formatWeekString(currentWeek),
                textAlign = TextAlign.Center,
                fontSize = 28.sp,
                modifier = Modifier
                    .padding(16.dp)
            )
            with(progress) {
                Text(
                    text = "${correctAnswers}/${totalQuestions}",
                    textAlign = TextAlign.Center,
                    fontSize = 28.sp,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }
        }

    }

}

@Composable
fun MainTextCard(text: String, shape: Shape, modifier: Modifier) {
    Card(
        modifier = modifier,
        elevation = 12.dp,
        shape = shape
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                modifier = Modifier
                    .fillMaxWidth(.80f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.subtitle1
            )
        }
    }
}

@Composable
fun StudyTopAppBar(
    text: String,
    destination: NavDestination?,
    onMenuClick: (ButtonOptions) -> Unit
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
            if (destination == null || destination.route == Screens.MainScreen.route) {
                Image(
                    imageVector = Icons.TwoTone.Menu,
                    contentDescription = "Toggle the drawer menu",
                    Modifier.clickable { onMenuClick.invoke(ButtonOptions.MENU) },
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary)
                )
            } else {
                Image(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Toggle the drawer menu",
                    Modifier.clickable { onMenuClick.invoke(ButtonOptions.BACK) },
                    colorFilter = ColorFilter.tint(color = MaterialTheme.colors.secondary)
                )
            }
            Text(
                text = text,
                Modifier
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun EmailPasswordBlock(onClick: (VerificationOptions, email: String, password: String) -> Unit) {
    var emailValue by remember { mutableStateOf("Email") }
    var passwordValue by remember { mutableStateOf("Password") }
    Column(
        modifier = Modifier.fillMaxWidth(.80f),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = emailValue,
            onValueChange = { value ->
                emailValue = value
            },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = passwordValue,
            onValueChange = { value ->
                passwordValue = value
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.heightIn(min = 30.dp, max = 60.dp))
        Button(
            onClick = {
                onClick.invoke(
                    VerificationOptions.EmailPassword,
                    emailValue,
                    passwordValue
                )
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


@Composable
fun DrawerItem(
    text: DrawerOptions = DrawerOptions.HOME,
    onClick: (DrawerOptions) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = text.name,
            Modifier
                .fillMaxWidth()
                .clickable { onClick.invoke(text) },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DrawerImage(imageID: Int, name: String = "Name of Account Owner", description: String) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        Image(
            ImageVector.vectorResource(id = imageID),
            contentDescription = description,
            modifier = Modifier
                .clip(CircleShape)
                .fillMaxWidth(.9f)
                .heightIn(min = 150.dp, max = 200.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = name)
    }
}

@Composable
fun LoginScreenContent(
    onLoginAttempt: (verificationOption: VerificationOptions, email: String, password: String) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        MainTextCard(
            text = "Android App",
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth(.7f)
                .fillMaxHeight(.2f)
        )
        EmailPasswordBlock { verificationOption, email, password ->
            onLoginAttempt.invoke(verificationOption, email, password)
        }
    }
}