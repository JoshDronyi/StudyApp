package com.example.studyapp.ui.composables

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstrainedLayoutReference
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import com.example.studyapp.util.*

@Composable
fun MyApp(navigation: (String) -> Unit) {
    Surface(color = MaterialTheme.colors.background) {
        val CORNER_RADIUS = 15
        val constraintSet = ConstraintSet {
            //region create constraint refs
            val wk1 = createRefFor(WK1)
            val wk2 = createRefFor(WK2)
            val wk3 = createRefFor(WK3)
            val wk4 = createRefFor(WK4)
            val wk5 = createRefFor(WK5)
            val wk6 = createRefFor(WK6)
            val buttons = createRefFor("buttons")
            val titleBackground = createRefFor(TITLE_BACKGROUND)
            val guideline = createGuidelineFromTop(.5f)
            //end region
            constrain(titleBackground) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(guideline)
            }

            constrain(buttons) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(guideline)
                bottom.linkTo(parent.bottom)
            }

            /*     constrain(wk1) {
                     top.linkTo(guideline)
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
                 }*/


            //Chain references
            val chainedWeeks: Array<ConstrainedLayoutReference> = arrayOf(titleBackground, buttons)
            //vertical Chains
            createVerticalChain(chainStyle = ChainStyle.Spread, elements = chainedWeeks)
        }
        ConstraintLayout(
            constraintSet,
            modifier = Modifier
                .fillMaxSize(.999f)
        ) {
            Card(
                modifier = Modifier
                    .layoutId(TITLE_BACKGROUND)
                    .fillMaxWidth(0.80f)
                    .fillMaxHeight(.20f),
                elevation = 12.dp,
                shape = RoundedCornerShape(CORNER_RADIUS)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Android Quiz",
                        modifier = Modifier
                            .fillMaxWidth(.50f),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Column(
                modifier = Modifier
                    .layoutId("buttons")
                    .fillMaxWidth(.8f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeekButton(
                        weekNumber = WK1,
                        modifier = Modifier
                            .layoutId(WK1)
                    ) { weekNumber -> navigation.invoke(weekNumber) }
                    WeekButton(
                        weekNumber = WK2,
                        modifier = Modifier
                            .layoutId(WK2)
                    ) { weekNumber -> navigation.invoke(weekNumber) }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeekButton(
                        weekNumber = WK3,
                        modifier = Modifier
                            .layoutId(WK3)
                    ) { weekNumber -> navigation.invoke(weekNumber) }
                    WeekButton(
                        weekNumber = WK4,
                        modifier = Modifier
                            .layoutId(WK4)
                    ) { weekNumber -> navigation.invoke(weekNumber) }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeekButton(
                        weekNumber = WK5,
                        modifier = Modifier
                            .layoutId(WK5)
                    ) { weekNumber -> navigation.invoke(weekNumber) }
                    WeekButton(
                        weekNumber = WK6,
                        modifier = Modifier
                            .layoutId(WK6)
                    ) { weekNumber -> navigation.invoke(weekNumber) }
                }
            }
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

    Card(
        modifier = modifier
            .padding(16.dp)
            .width(140.dp)
            .height(60.dp)
            .border(2.dp, MaterialTheme.colors.primaryVariant, shape = RoundedCornerShape(20))
            .clickable {
                clickReaction.invoke(weekNumber)
            }
            .shadow(8.dp),
        shape = RoundedCornerShape(20),
        elevation = 8.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = modifier.width(IntrinsicSize.Max),
                text = weekNumber,
                textAlign = TextAlign.Center,
                textDecoration = TextDecoration.Underline,
                color = MaterialTheme.colors.onSurface
            )
        }
    }

}






