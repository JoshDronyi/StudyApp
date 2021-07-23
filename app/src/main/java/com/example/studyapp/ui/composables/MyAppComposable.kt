package com.example.studyapp.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.navigation.NavController
import com.example.studyapp.ui.theme.StudyAppTheme
import com.example.studyapp.util.*

@Composable
fun MyApp(navigation: (Int) -> Unit) {
    val constraintSet = ConstraintSet {
        //region create constraint refs
        val wk1 = createRefFor(WK1)
        val wk2 = createRefFor(WK2)
        val wk3 = createRefFor(WK3)
        val wk4 = createRefFor(WK4)
        val wk5 = createRefFor(WK5)
        val wk6 = createRefFor(WK6)
        val titleBackground = createRefFor(TITLE_BACKGROUND)
        val buttonBackground = createRefFor(BUTTON_BACKGROUND)
        //end region

        constrain(wk1) {
            top.linkTo(parent.top)
            bottom.linkTo(wk2.top)
            start.linkTo(parent.start)
            end.linkTo(wk4.start)
        }
        constrain(wk2) {
            top.linkTo(wk1.bottom)
            start.linkTo(parent.start)
            end.linkTo(wk5.start)
            bottom.linkTo(wk3.top)
        }
        constrain(wk3) {
            top.linkTo(wk2.bottom)
            start.linkTo(parent.start)
            end.linkTo(wk6.start)
            bottom.linkTo(parent.bottom)
        }
        constrain(wk4) {
            top.linkTo(parent.top)
            start.linkTo(wk1.end)
            end.linkTo(parent.end)
            bottom.linkTo(wk5.top)
        }
        constrain(wk5) {
            top.linkTo(wk4.bottom)
            start.linkTo(wk2.end)
            end.linkTo(parent.end)
            bottom.linkTo(wk6.top)
        }
        constrain(wk6) {
            top.linkTo(wk5.bottom)
            start.linkTo(wk3.end)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
        constrain(titleBackground) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(buttonBackground.top)
        }
        constrain(buttonBackground) {
            top.linkTo(titleBackground.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(parent.bottom)
        }
    }
    ConstraintLayout(constraintSet) {
        // A surface container using the 'background' color from the theme
        Surface(
            color = MaterialTheme.colors.background, modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .padding(top = 60.dp)
        ) {

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .layoutId(TITLE_BACKGROUND)
                    .fillMaxWidth(0.75f)
                    .border(2.dp, Color.Black, RoundedCornerShape(15)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Android Quiz",
                    modifier = Modifier
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .layoutId(BUTTON_BACKGROUND)
            ) {
                WeekButton(
                    weekNumber = 1,
                    padding = 8.dp,
                    modifier = Modifier.layoutId(WK1)
                ) { weekNumber -> navigation.invoke(weekNumber) }
                WeekButton(
                    weekNumber = 2,
                    padding = 8.dp,
                    modifier = Modifier.layoutId(WK2)
                ) { weekNumber -> navigation.invoke(weekNumber) }
                WeekButton(
                    weekNumber = 3,
                    padding = 8.dp,
                    modifier = Modifier.layoutId(WK3)
                ) { weekNumber -> navigation.invoke(weekNumber) }
                WeekButton(
                    weekNumber = 4,
                    padding = 8.dp,
                    modifier = Modifier.layoutId(WK4)
                ) { weekNumber -> navigation.invoke(weekNumber) }
                WeekButton(
                    weekNumber = 5,
                    padding = 8.dp,
                    modifier = Modifier.layoutId(WK5)
                ) { weekNumber -> navigation.invoke(weekNumber) }
                WeekButton(
                    weekNumber = 6,
                    padding = 8.dp,
                    modifier = Modifier.layoutId(WK6)
                ) { weekNumber -> navigation.invoke(weekNumber) }
            }
        }
    }
}


@Composable
fun WeekButton(weekNumber: Int, padding: Dp, modifier: Modifier, clickReaction: (Int) -> Unit) {
    Button(
        onClick = {
            clickReaction.invoke(weekNumber)
        },
        modifier = modifier
            .width(150.dp)
            .padding(padding)
    ) {
        Text(
            text = "Week $weekNumber",
            modifier = modifier
                .fillMaxWidth()
                .padding(padding),
            textAlign = TextAlign.Center
        )
    }

}






