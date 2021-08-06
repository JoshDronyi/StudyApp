package com.example.studyapp.ui.composables.screens.homescreen

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
import com.example.studyapp.ui.composables.sharedcomposables.MainTextCard
import com.example.studyapp.util.*

@Composable
fun MyApp(navigation: (String) -> Unit) {
    Surface(color = MaterialTheme.colors.background) {
        val CORNER_RADIUS = 15
        Column(
            modifier = Modifier
                .fillMaxSize(.999f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            MainTextCard(
                cornerRadius = CORNER_RADIUS,
                text = "Android Quiz",
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(.20f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                elevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth(.9f),
                shape = RoundedCornerShape(CORNER_RADIUS)
            ) {
                Column(
                    modifier = Modifier.heightIn(min = 350.dp, max = 600.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
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
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
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
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
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
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DefaultPreview() {
    MyApp {
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
            .width(120.dp)
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
            horizontalAlignment = Alignment.CenterHorizontally,
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






