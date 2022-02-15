package com.example.studyapp.util

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.studyapp.data.model.Question
import com.example.studyapp.data.model.StudentProgress
import com.example.studyapp.data.model.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.getValue
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

fun formatWeekString(week: String): String {
    Log.e("TESTE", QuestionStatus.WRONG_ANSWER.ordinal.toString())
    return "Week ${week.filter { it.isDigit() }}"
}

fun formatWeekStringToInt(week: String): Int {
    return week.filter { it.isDigit() }.toInt()
}

/**
 * Email is valid if :
 * - it matches the regex
 * - it contains no empty spaces
 */
fun String.validateEmail(): Boolean {
    return this.apply {
        this.trim()
            .trimIndent()
    }.matches(EMAIL_REGEX.toRegex())
}

/**
 * Password is valid if:
 * - it has at least  the minimun characters (6 characters)
 * - it contains at least one number
 */
fun String.validatePassword(): Boolean = this.let { theString: String ->
    Log.e("Utils", "validatePassword: theString: $theString")
    return when {
        theString.length < MIN_PW_CHARS -> {
            Log.e("Utils", "validatePassword: Not enough characters")
            false
        }
        theString.count {
            it.isDigit()
        } == 0 -> {
            Log.e("Utils", "validatePassword: Not enough numbers in the password.")
            false // will be false after testing is done
        }
        else -> true
    }
}


fun verifyText(
    context: Context,
    verificationOption: SignInOptions,
    email: String,
    password: String
): Boolean {
    return when (verificationOption) {
        SignInOptions.EMAIL_PASSWORD -> {
            Toast.makeText(
                context,
                "Email/Password chosen \nEmail:$email\nPassword:$password",
                Toast.LENGTH_SHORT
            ).show()
            true
        }
        else -> {
            false
        }
    }
}


fun List<Question>.generateStudentProgress(): StudentProgress {
    return StudentProgress(
        week = get(0).week.last().digitToInt(),
        totalQuestions = size,
        answeredQuestions = count { it.questionStatus != QuestionStatus.NOT_ANSWERED.ordinal.toString() },
        correctAnswers = count { it.questionStatus == QuestionStatus.CORRECT_ANSWER.ordinal.toString() }
    )
}

fun FirebaseUser.asUser(): User {
    val user = User(
        uid = this.uid,
        phoneNumber = this.phoneNumber,
        alias = this.displayName,
        email = this.email,
        photoUrl = this.photoUrl.toString(),
        isDefault = this.isAnonymous,
    )
    Log.e("asUserHelper", "Creating new User: $user")
    return user
}


fun Date.getFormattedDate(format: String): String {
    return SimpleDateFormat(format, Locale.US)
        .format(this).apply {
            logMe("getFormatedTime")
        }
}

fun Any.logMe(tag: String) {
    Log.e(tag, "logMe: $this")
}

fun returnSnapShotAsQuestionList(snapshot: DataSnapshot, week: String): List<Question> {
    val questions = mutableListOf<Question>()
    val TAG = "Utils:"
    snapshot.children.forEach { questionData ->
        Log.e(
            TAG,
            "returnSnapShotAsQuestionList: snapshot was Key:${questionData.key}, value: ${questionData.value}"
        )
        val question = Question().apply {
            this.id = questionData.key.toString()
            this.week = week
        }
        questionData.getValue<Map<String, String>>()?.let { data ->
            questions.add(
                getQuestionFromMappedData(question, data)
            )
        }

    }
    return questions
}

private fun getQuestionFromMappedData(
    question: Question,
    snapshot: Map<String, String>
): Question {
    val TAG = "UTILS"
    Log.e(TAG, "getQuestionFromMappedData: QuestionID was ${question.id}")
    snapshot.map {
        Log.e(TAG, "getQuestionFromMappedData: category was ${it.key}, ${it.value}")
        when (it.key.lowercase()) {
            QuestionDTOAttributes.Topic.value -> {
                question.topic = it.value
            }
            QuestionDTOAttributes.Answer1.value -> {
                question.answer1 = it.value
            }
            QuestionDTOAttributes.Answer2.value -> {
                question.answer2 = it.value
            }
            QuestionDTOAttributes.Answer3.value -> {
                question.answer3 = it.value
            }
            QuestionDTOAttributes.CorrectAnswer.value -> {
                question.correctAnswer = it.value
            }
            QuestionDTOAttributes.QuestionText.value -> {
                question.questionText = it.value
            }
        }
    }
    return question
}


fun returnSnapShotAsUser(snapshot: DataSnapshot): User {
    val TAG = "Utils"
    val user = User.newBlankInstance().apply {
        snapshot.children.map {
            Log.e(TAG, "returnSnapShotAsUser: KEY WAS ${it.key}, VALUE WAS ${it.value}")
            when (it.key?.lowercase()) {
                UserDTOAttributes.ID.value -> {
                    this.uid = it.value.toString()
                }
                UserDTOAttributes.First.value -> {
                    this.firstName = it.value.toString()
                }
                UserDTOAttributes.Last.value -> {
                    this.lastName = it.value.toString()
                }
                UserDTOAttributes.Alias.value -> {
                    this.alias = it.value.toString()
                }
                UserDTOAttributes.ProfilePic.value -> {
                    this.photoUrl = it.value.toString()
                }
                UserDTOAttributes.Role.value -> {
                    this.role = it.value.toString()
                }
                UserDTOAttributes.BatchStartDate.value -> {
                    this.batchStartDate = it.value.toString()
                }
                UserDTOAttributes.Email.value -> {
                    this.email = it.value.toString()
                }
                UserDTOAttributes.Phone.value -> {
                    this.phoneNumber = it.value.toString()
                }
            }
        }
    }
    Log.e(TAG, "returnSnapShotAsUser: user after refactor was $user")
    return user.apply { isDefault = false }
}

fun returnErrorAsStudyAppError(error: DatabaseError): StudyAppError {
    return StudyAppError.newBlankInstance().apply {
        this.errorType = ErrorType.NETWORK
        this.message = error.message
        this.data = error.toException()
        this.shouldShow = true
    }
}

