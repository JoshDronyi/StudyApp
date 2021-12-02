package com.example.studyapp.ui.composables.sharedcomposables

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
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
fun ProfileTextField(resourceId: Int, value: String, onSettingsChange: (value: String) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = resourceId, value),
            modifier = Modifier
                .fillMaxWidth(.7f)
                .padding(8.dp),
            textAlign = TextAlign.Center,
            fontStyle = FontStyle.Normal,
            fontSize = 24.sp
        )
        Spacer(Modifier.width(10.dp))

        Image(
            imageVector = Icons.Filled.MoreVert, contentDescription = "Edit the resource",
            modifier = Modifier
                .fillMaxWidth(.2f)
                .clickable {
                    onSettingsChange.invoke(value)
                }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun Preview() {
    OTFBuilder(label = "Thingy", inValidInput = true) {}
}

@Composable
fun OTFBuilder(
    modifier: Modifier = Modifier,
    label: String,
    errorMessage: String = "",
    inValidInput: Boolean?,
    onValueChange: (String) -> Unit,
) {
    val textValue = remember { mutableStateOf("") }
    Column(
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        OutlinedTextField(
            value = textValue.value,
            onValueChange = { newString ->
                textValue.value = newString
                onValueChange.invoke(textValue.value)
            },
            label = {
                Text(text = label)
            })
        inValidInput?.let {
            val messageString = if (errorMessage == "" && !it) {
                ""
            } else {
                errorMessage
            }
            Text(
                text = messageString,
                textAlign = TextAlign.Start,
                color = Color.Red,
                modifier = Modifier.defaultMinSize(minHeight = 24.dp)
            )
        }
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

@ExperimentalCoilApi
@Composable
fun DrawerImage(
    imageUrl: Uri?,
    imageID: Int = 0,
    description: String
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(16.dp)
    ) {
        if (imageUrl == null) {
            Image(
                ImageVector.vectorResource(id = imageID),
                contentDescription = description,
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxWidth(.9f)
                    .heightIn(min = 150.dp, max = 200.dp)
            )
        } else {
            Image(
                painter = rememberImagePainter(data = imageUrl.path),
                contentDescription = description,
                modifier = Modifier
                    .clip(CircleShape)
                    .fillMaxWidth(.9f)
                    .heightIn(min = 150.dp, max = 200.dp)
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = description)
    }
}


@Composable
fun BaseAlertDialog(
    title: String = "Alert!",
    text: String,
    screen: Screens = Screens.MainScreen
) {
    val shouldShowDialog = remember { mutableStateOf(true) }
    if (shouldShowDialog.value) {
        AlertDialog(
            onDismissRequest = {
                shouldShowDialog.value = false
                Navigator.navigateTo(screen)
            },
            title = { Text(text = title) },
            text = { Text(text = text) },
            confirmButton = {
                Button(
                    onClick = {
                        shouldShowDialog.value = false
                        Navigator.navigateTo(screen)
                    }
                ) {
                    Text(text = "Confirm")
                }
            }
        )
    }
}

