package com.example.studyapp.ui.composables

import android.util.Log
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.example.studyapp.util.*

@Composable
fun MyApp(navigation: (String) -> Unit) {
    Surface(color = MaterialTheme.colors.background) {
        val constraintSet = ConstraintSet {
            //region create constraint refs
            val wk1 = createRefFor(WK1)
            val wk2 = createRefFor(WK2)
            val wk3 = createRefFor(WK3)
            val wk4 = createRefFor(WK4)
            val wk5 = createRefFor(WK5)
            val wk6 = createRefFor(WK6)
            val titleBackground = createRefFor(TITLE_BACKGROUND)
            //end region

            constrain(wk1) {
                top.linkTo(titleBackground.bottom)
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
                top.linkTo(wk1.top)
                start.linkTo(wk1.end)
                end.linkTo(parent.end)
                bottom.linkTo(wk1.bottom)
            }
            constrain(wk5) {
                top.linkTo(wk2.top)
                start.linkTo(wk2.end)
                end.linkTo(parent.end)
                bottom.linkTo(wk2.bottom)
            }
            constrain(wk6) {
                top.linkTo(wk3.top)
                start.linkTo(wk3.end)
                end.linkTo(parent.end)
                bottom.linkTo(wk3.bottom)
            }
            constrain(titleBackground) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(wk1.top)
            }
        }
        ConstraintLayout(
            constraintSet,
            modifier = Modifier
                .fillMaxSize(.99f)
                .height(IntrinsicSize.Max)
        ) {
            Box(
                modifier = Modifier
                    .layoutId(TITLE_BACKGROUND)
                    .fillMaxWidth(0.75f)
                    .height(200.dp)
                    .border(2.dp, Color.Black, RoundedCornerShape(15)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Android Quiz",
                    modifier = Modifier
                        .fillMaxWidth(.50f),
                    textAlign = TextAlign.Center,
                    style = TextStyle.Default
                )
            }


            WeekButton(
                weekNumber = WK1,
                modifier = Modifier.layoutId(WK1)
            ) { weekNumber -> navigation.invoke(weekNumber) }
            WeekButton(
                weekNumber = WK2,
                modifier = Modifier.layoutId(WK2)
            ) { weekNumber -> navigation.invoke(weekNumber) }
            WeekButton(
                weekNumber = WK3,
                modifier = Modifier.layoutId(WK3)
            ) { weekNumber -> navigation.invoke(weekNumber) }
            WeekButton(
                weekNumber = WK4,
                modifier = Modifier.layoutId(WK4)
            ) { weekNumber -> navigation.invoke(weekNumber) }
            WeekButton(
                weekNumber = WK5,
                modifier = Modifier.layoutId(WK5)
            ) { weekNumber -> navigation.invoke(weekNumber) }
            WeekButton(
                weekNumber = WK6,
                modifier = Modifier.layoutId(WK6)
            ) { weekNumber -> navigation.invoke(weekNumber) }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MyApp() {
        Log.e("JOSH", "Preview log. $it")
    }
}


@Composable
fun WeekButton(
    weekNumber: String,
    modifier: Modifier,
    clickReaction: (String) -> Unit
) {

    Button(
        onClick = {
            clickReaction.invoke(weekNumber)
        },
        modifier = modifier
            .fillMaxWidth(.35f)
            .fillMaxHeight(.08f),
    ) {
        Text(
            modifier = modifier
                .fillMaxSize()
                .padding(top = 10.dp),
            text = weekNumber,
            textAlign = TextAlign.Center,
            textDecoration = TextDecoration.Underline
        )
    }

}






