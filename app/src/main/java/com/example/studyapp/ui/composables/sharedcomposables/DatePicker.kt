package com.example.studyapp.ui.composables.sharedcomposables

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.studyapp.util.getFormattedDate
import java.util.*

@Composable
fun DatePicker(modifier: Modifier, onDateChange: (date:String) -> Unit) {
    val now: Calendar = Calendar.getInstance()

    val mYear: Int = now.get(Calendar.YEAR)
    val mMonth: Int = now.get(Calendar.MONTH)
    val mDay: Int = now.get(Calendar.DAY_OF_MONTH)

    val date = remember { mutableStateOf("") }

    val picker = DatePickerDialog(
        LocalContext.current,
        { _, year, month, day ->
            val cal = Calendar.getInstance()
            cal.set(year, month, day)
            date.value = cal.time.getFormattedDate( "dd MMM, yyy")
            onDateChange.invoke(date.value)
        }, mYear, mMonth, mDay
    )

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        picker.show()
    }
}

@Composable
@Preview(
    showBackground = true,
    showSystemUi = true
)
fun DatePickerPreview() {
    DatePicker(Modifier.padding(8.dp)){

    }
}