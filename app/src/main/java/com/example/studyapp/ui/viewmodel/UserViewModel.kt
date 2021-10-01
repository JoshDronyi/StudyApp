package com.example.studyapp.ui.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.User
import com.example.studyapp.data.repo.UserRepository
import com.example.studyapp.util.Navigator
import com.example.studyapp.util.Screens
import com.example.studyapp.util.State.ApiState
import com.example.studyapp.util.State.ScreenState.LoginScreenState
import com.example.studyapp.util.StudyAppError
import com.example.studyapp.util.VerificationOptions
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {
    //empty constructor necessary for ViewModel()
    constructor() : this(UserRepository(FirebaseAuth.getInstance()))

    private val tag = "USER_VIEW_MODEL"


    private val _userLoginState: MutableLiveData<ApiState<Any>> = MutableLiveData()
    private val _isSignUp: MutableLiveData<Boolean> = MutableLiveData(false)
    private val _error: MutableLiveData<StudyAppError> =
        MutableLiveData(StudyAppError.newBlankInstance())

    val userLoginState: LiveData<ApiState<Any>> get() = _userLoginState
    val isSignUp: LiveData<Boolean> get() = _isSignUp
    val error: LiveData<StudyAppError> get() = _error

    /*private val _loginScreenState: MutableLiveData<LoginScreenState> =
        MutableLiveData(LoginScreenState(ApiState.Sleep))
    val loginState: LiveData<LoginScreenState>
        get() = _loginScreenState*/

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    private fun signInWithEmail(email: String, password: String) =
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(tag, "signInWithEmail: Calling Repo with result. State is loading.")
            _userLoginState.postValue(ApiState.Loading)
            repo.signInWithEmail(email, password).collect { state ->
                handleUserState(state)
            }
        }


    private fun signUpWithEmail(email: String, password: String): Flow<User?> {
        Log.e(tag, "signUpWithEmail: Calling Repo with result")
        return repo.createNewUserProfile(email, password)
    }

    private fun handleUserState(userState: ApiState<Any?>) {
        when (userState) {
            is ApiState.Loading, is ApiState.Sleep -> {
                Log.e(
                    tag,
                    "handleUserState: User changed but not valid yet. Loading or asleep: state= $userState."
                )
            }
            is ApiState.Success.UserApiSuccess -> {
                val user = userState.data as User
                if (!user.isDefault) {
                    Log.e(
                        tag,
                        "handleUserState: Got a valid user object. Username: ${user.name} email: ${user.email}"
                    )
                    if (Navigator.currentScreen.value != Screens.MainScreen) {
                        Navigator.navigateTo(Screens.MainScreen)
                    }
                } else {
                    Log.e(tag, "handleUserState: Default User retrieved. Ignoring.")
                }
            }
            is ApiState.Error -> {
                with(userState.data) {
                    Log.e(tag, "handleUserState: Error from studyapp: $this")
                    _error.postValue(this)
                }

            }
            is ApiState.Success.DefaultUserSuccess -> {
                Log.e(tag, "handleUserState: Default user found.")
            }
            else -> {
                Log.e(tag, "handleUserState: AuthResult might be null, authResult:$userState")
            }
        }
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    fun onLoginAttempt(
        verificationOption: VerificationOptions,
        email: String,
        password: String,
        context: Context

    ) {
        Log.e(
            tag,
            "exiting screen content. \n VerificationOption:$verificationOption \n Email:$email \n Password:$password"
        )

        when (verificationOption) {
            VerificationOptions.EmailPassword -> {
                Log.e(
                    tag,
                    "onLoginAttempt: in Verification option email/password."
                )
                viewModelScope.launch {
                    signInWithEmail(email, password)
                }

            }
            VerificationOptions.NewUser -> {
                signUpWithEmail(email, password)
            }
            VerificationOptions.Error -> {
                Log.e(
                    tag,
                    "onLoginAttempt: got an error, Email slot :[$email], Password Slot:[$password]"
                )
                Toast.makeText(context, "[Error:$email]", Toast.LENGTH_LONG).show()
            }
            VerificationOptions.PREVIOUS -> {
                Log.e(
                    tag, "onLoginAttempt: Tying to go back to email/password from Sign Up. \n" +
                            "HANDLE CASE TO RETURN TO LOGINSCREEN WITH LOGIN OPTION INSTEAD OF SIGN IN OPTION"
                )
            }
        }
    }

    fun clearLoginError() {
        _error.value = StudyAppError.newBlankInstance()
    }

/*fun checkLoginState(
    stateToCheck: ApiState<Any>,
    context: Context
) {
    when (stateToCheck) {
        is ApiState.Sleep -> {
            Log.e(
                tag,
                "checkLoginState: Invoking Login Sleep. $stateToCheck"
            )
            Toast
                .makeText(context, "Not now, State is sleeping.", Toast.LENGTH_SHORT)
                .show()
        }
        is ApiState.Success.UserApiSuccess -> {
            val user = stateToCheck.data as User
            if (!user.isDefault) {
                Log.e(
                    tag,
                    "checkLoginState: Invoking Login Success. $stateToCheck"
                )
                handleUserState(stateToCheck)
                Toast.makeText(context, "New User secured ${user.uid}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        is ApiState.Success.DefaultUserSuccess -> {
            Toast.makeText(context, " Got the default user back", Toast.LENGTH_SHORT).show()
            Log.e(
                tag,
                "checkLoginState: Default user captured. Still not ready"
            )
        }
        is ApiState.Loading -> {
            Toast.makeText(context, "Resource is currently loading", Toast.LENGTH_SHORT)
                .show()
            Log.e(
                tag,
                "checkLoginState: Invoking Loading screen. $stateToCheck"
            )
        }
        is ApiState.Error -> {
            Toast.makeText(
                context,
                "OOps there was an error!!! ${stateToCheck.data.message}\n ERROR TYPE: ${stateToCheck.data.errorType}",
                Toast.LENGTH_SHORT
            ).show()
            Log.e(
                tag,
                "checkLoginState: ERROR: ${stateToCheck.data.message}"
            )
        }
        else -> {
            Log.e(tag, "checkLoginState: Should never happen. Unspecified screen state.")
        }
    }
}
*/
}

