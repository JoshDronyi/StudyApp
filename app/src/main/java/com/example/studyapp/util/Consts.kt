package com.example.studyapp.util


const val DATABASE_NAME = "question.db"
const val WK1 = "week1"
const val WK2 = "week2"
const val WK3 = "week3"
const val WK4 = "week4"
const val WK5 = "week5"
const val WK6 = "week6"
const val LINE_SIZE = 48
const val EMAIL_REGEX = ".+\\@.+\\..+" //[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,3}
const val MIN_PW_CHARS:Int = 6

enum class ButtonOptions {
    MENU, BACK
}

enum class DrawerOptions {
    HOME, SCOREBOARD
}

enum class VerificationOptions {
    EmailPassword, NewUser, PREVIOUS, Error
}