package com.example.studyapp.ui.composables.screens.weekquestionsscreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studyapp.data.model.Question
import com.example.studyapp.util.MAX_SPACER_HEIGHT
import com.example.studyapp.util.MIN_SPACER_HEIGHT
import com.example.studyapp.util.Navigator

@Composable
fun NewQuestionScreen(onQuestionAdded: (week: String, question: Question) -> Unit) {
    val context = LocalContext.current
    var topic = ""
    var week = ""
    var questionText = ""
    var correctAns = ""
    var ans1 = ""
    var ans2 = ""
    var ans3 = ""

    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.surface, RectangleShape)
            .border(2.dp, MaterialTheme.colors.onSurface, RectangleShape)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            //Week
            TextInput(
                category = "Week",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally),
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            ) { theWeek ->

                try {
                    val wk = theWeek.toInt()
                    if (wk > 0 || wk <= 8) {
                        week = "Week$theWeek"
                    } else {
                        Toast.makeText(context, "Week must be between 1 and 8.", Toast.LENGTH_LONG)
                            .show()
                    }
                } catch (ex: Exception) {
                    Log.e(
                        TAG,
                        "NewQuestionScreen: there was an exception. Please lace a non decimal number ${ex.printStackTrace()}"
                    )
                }

            }
            //Topic
            TextInput(
                category = "Topic",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) { theTopic ->
                topic = theTopic
            }
            TextInput(
                category = "Question Text",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) { theQuestion ->
                questionText = theQuestion
            }
            TextInput(
                category = "Correct Answer",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) { Correct ->
                correctAns = Correct
            }
        }

        Spacer(modifier = Modifier.height(MAX_SPACER_HEIGHT.dp))

        Text(
            text = "Wrong Answers:",
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(start = 8.dp)
        )

        Spacer(modifier = Modifier.height(MIN_SPACER_HEIGHT.dp))

        Column(
            Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            TextInput(
                category = "Wrong Answer 1",
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) { ans ->
                ans1 = ans
            }
            TextInput(
                category = "Wrong Answer 2",
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) { ans ->
                ans2 = ans
            }
            TextInput(
                category = "Wrong Answer 3",
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            ) { ans ->
                ans3 = ans
            }
        }

        Spacer(modifier = Modifier.height(MAX_SPACER_HEIGHT.dp))

        Row(Modifier.padding(8.dp)) {

            Button(onClick = { Navigator.navigateUp() }) {
                Text(text = "Cancel")
            }

            Spacer(modifier = Modifier.width(MAX_SPACER_HEIGHT.dp))

            Button(onClick = {
                val newQuestion = Question(
                    id = "",
                    questionText = questionText,
                    correctAnswer = correctAns,
                    answer1 = ans1,
                    answer2 = ans2,
                    answer3 = ans3,
                    topic = topic,
                    week = week
                )

                Toast.makeText(context, "New Question created.$newQuestion", Toast.LENGTH_LONG)
                    .show()

                onQuestionAdded.invoke(week, newQuestion)
            }) {
                Text(text = "Add Question.")
            }
        }
    }


}

@Composable
fun TextInput(
    category: String,
    modifier: Modifier = Modifier,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    onValueChange: (String) -> Unit
) {
    val textValue = remember { mutableStateOf("") }
    Text(text = "$category:")
    Spacer(modifier = Modifier.width(16.dp))
    OutlinedTextField(
        value = textValue.value,
        modifier = modifier,
        keyboardOptions = keyboardOptions,
        onValueChange = { newVal ->
            textValue.value = newVal
            onValueChange.invoke(textValue.value)
        })
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun NewQuestionPreview() {
    NewQuestionScreen { _, _ ->

    }
}