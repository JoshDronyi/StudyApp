package com.example.studyapp.util


const val DATABASE_NAME = "question.db"
const val WK1 = "Week1"
const val WK2 = "Week2"
const val WK3 = "Week3"
const val WK4 = "Week4"
const val WK5 = "Week5"
const val WK6 = "Week6"
const val LINE_SIZE = 48
const val EMAIL_REGEX = ".+\\@.+\\..+" //[A-Z0-9._%+-]+@[A-Z0-9.-]+\.[A-Z]{2,3}
const val MIN_PW_CHARS: Int = 6
const val MIN_SPACER_HEIGHT = 8
const val MAX_SPACER_HEIGHT = 16

enum class QuestionStatus {
    NOT_ANSWERED,
    CORRECT_ANSWER,
    WRONG_ANSWER
}

enum class ButtonOptions {
    MENU, BACK, SETTINGS
}
enum class NavTarget {
    PREV, HOME, LIST, QUESTION, SETTINGS, NEW_QUESTION, MENU
}

enum class DrawerOptions {
    HOME, SCOREBOARD, PROFILE
}

enum class VerificationOptions {
    SIGN_IN, SIGN_UP, ERROR
}
enum class SignInOptions{
    EMAIL_PASSWORD, GOOGLE
}

enum class Toggleable {
    VERIFICATION, DATEPICKER
}

enum class ResultType {
    USER, QUESTION
}

enum class QuestionDTOAttributes(var value: String) {
    Topic("topicid"),
    Answer1("answer1"),
    Answer2("answer2"),
    Answer3("answer3"),
    CorrectAnswer("correctanswer"),
    QuestionText("questiontext"),
}

enum class UserDTOAttributes(var value: String) {
    ID("uid"),
    Alias("alias"),
    First("firstName"),
    Last("lastName"),
    Role("role"),
    Email("email"),
    Phone("phoneNumber"),
    ProfilePic("photoUrl"),
    BatchStartDate("batchStartDate")
}
