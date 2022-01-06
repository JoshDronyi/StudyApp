package com.example.studyapp.ui.viewmodel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studyapp.data.model.User
import com.example.studyapp.data.remote.AuthDataSource
import com.example.studyapp.data.remote.FirebaseDatabaseDataSource
import com.example.studyapp.data.repo.UserRepository
import com.example.studyapp.ui.composables.screens.loginscreen.TAG
import com.example.studyapp.util.*
import com.example.studyapp.util.State.ApiState
import com.example.studyapp.util.State.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@HiltViewModel
class UserViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {
    //empty constructor necessary for ViewModel()
    constructor() : this(UserRepository(AuthDataSource(), FirebaseDatabaseDataSource()))

    private val tag = "USER_VIEW_MODEL"

    //Login Screen
    private val _loginScreenState: MutableLiveData<ScreenState.LoginScreenState> =
        MutableLiveData(ScreenState.LoginScreenState())
    val loginScreenState: LiveData<ScreenState.LoginScreenState> get() = _loginScreenState

    private val _homeScreenState: MutableLiveData<ScreenState.HomeScreenState> = MutableLiveData()
    val homeScreenState:LiveData<ScreenState.HomeScreenState> get() = _homeScreenState

    //Validation Variables
    private val _validEmail = MutableLiveData(true)
    private val _validPassword = MutableLiveData(true)
    private val _validVerification = MutableLiveData(true)
    val validEmail: LiveData<Boolean> get() = _validEmail
    val validPassword: LiveData<Boolean> get() = _validPassword
    val validVerification: LiveData<Boolean> get() = _validVerification


    fun toggleItems(itemToToggle: Toggleable) {
        with(_loginScreenState) {
            /**
             * isSignUp and showDatePicker both have default values of false
             * on instantiation and are non-Nullable attributes so they
             * will always have a value
             */
            when (itemToToggle) {
                Toggleable.SIGNUP -> {
                    this.value =
                        this.value?.copy(isSignUp = !this.value?.isSignUp!!) //Default value set to false
                }
                Toggleable.DATEPICKER -> {
                    this.value =
                        this.value?.copy(showDatePicker = (this.value?.showDatePicker!!)) //Default value set to false
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    fun onSignUpAttempt(newUser: User, password: String, verifyPW: String, context: Context) {
        when {
            newUser.email?.validateEmail() != true -> {
                _loginScreenState.value?.error = StudyAppError.newBlankInstance().apply {
                    with(this) {
                        message = "Please enter a valid email"
                        shouldShow = true
                        errorType = ErrorType.VALIDATION
                    }
                    Log.e(TAG, "onSignUpAttempt: email validation error: ${this.message}")
                    _validEmail.value = false
                }
            }
            !password.validatePassword() -> {
                _loginScreenState.value?.error = StudyAppError.newBlankInstance().apply {
                    with(this) {
                        message =
                            "Passwords must have at least ${MIN_PW_CHARS} characters with at least one (1) digit."
                        shouldShow = true
                        errorType = ErrorType.VALIDATION
                    }
                    _validPassword.value = false
                    Log.e(TAG, "onSignUpAttempt: password validation error: ${this.message}")
                }
            }
            !verifyPW.validatePassword() -> {
                _loginScreenState.value?.error = StudyAppError.newBlankInstance().apply {
                    with(this) {
                        message =
                            "Verified passwords must have at least $MIN_PW_CHARS characters with at least one (1) digit."
                        shouldShow = true
                        errorType = ErrorType.VALIDATION
                    }
                    _validVerification.value = false
                    Log.e(TAG, "onSignUpAttempt: password validation error: ${this.message}")
                }
            }
            password != verifyPW -> {
                _loginScreenState.value?.error = StudyAppError.newBlankInstance().apply {
                    with(this) {
                        message = "Passwords do not match."
                        shouldShow = true
                        errorType = ErrorType.VALIDATION
                    }
                    _validVerification.value = false
                    Log.e(TAG, "onSignUpAttempt: password validation error: ${this.message}")
                }
            }
            else -> {
                if (_validEmail.value == true &&
                    _validPassword.value == true &&
                    _validVerification.value == true
                ) {
                    Log.e(TAG, "SignUpBlock: email and password are valid.")
                    newUser.email?.let {
                        Log.e(TAG, "SignUpBlock: email was $it.")
                        onLoginAttempt(
                            VerificationOptions.NEW_USER,
                            it, password, context, newUser
                        )
                    }
                } else {
                    Log.e(
                        TAG, "SignUpBlock: at least 1 verification failure:\n" +
                                "valid email: ${_validEmail.value}\n" +
                                "valid password: ${_validPassword.value}\n" +
                                "valid verification: ${_validVerification.value}"
                    )
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    private fun signInWithEmail(email: String, password: String) =
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(tag, "signInWithEmail: Calling Repo with result. State is loading.")
            _loginScreenState.value?.apiState = ApiState.Loading
            repo.signInWithEmail(email, password).collectLatest { state ->
                handleUserState(state)
            }
        }


    @DelicateCoroutinesApi
    private fun signUpWithEmail(user: User, password: String) {
        Log.e(tag, "signUpWithEmail: Calling Repo with result")
        viewModelScope.launch(Dispatchers.IO) {
            _loginScreenState.value?.apiState = ApiState.Loading
            repo.createNewUserProfile(user, password).collectLatest { state ->
                handleUserState(state)
            }
        }
    }

    private fun handleUserState(userState: ApiState<*>) {
        when (userState) {
            is ApiState.Loading -> {
                Log.e(
                    tag,
                    "handleUserState: User changed but not valid yet. Loading or asleep: state= $userState."
                )
            }
            is ApiState.Success.UserApiSuccess -> {
                val user = userState.data as User
                Log.e(TAG, "handleUserState: user was $user")
                if (!user.isDefault) {
                    Log.e(
                        tag,
                        "handleUserState: Got a valid user object. Username: ${user.firstName} email: ${user.email}"
                    )
                    _loginScreenState.value?.apiState = ApiState.Success.UserApiSuccess(user)
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
                    _loginScreenState.value?.error = this
                }

            }
            is ApiState.Success.DefaultUserSuccess, is ApiState.Sleep -> {
                Log.e(tag, "handleUserState: Default user found.")
            }
            else -> {
                Log.e(tag, "handleUserState: AuthResult might be null, authResult:$userState")
            }
        }
    }

    @DelicateCoroutinesApi
    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    fun onLoginAttempt(
        verificationOption: VerificationOptions,
        email: String,
        password: String,
        context: Context,
        user: User? = null
    ) {
        Log.e(
            tag, "exiting screen content. \n VerificationOption:$verificationOption \n " +
                    "Email:$email \n Password:$password"
        )

        when (verificationOption) {
            VerificationOptions.EMAIL_PASSWORD -> {
                Log.e(tag, "onLoginAttempt: in Verification option email/password.")
                signInWithEmail(email, password)
            }
            VerificationOptions.NEW_USER -> {
                user?.let {
                    signUpWithEmail(it, password)
                }
            }
            VerificationOptions.ERROR -> {
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
                toggleItems(Toggleable.SIGNUP)
            }
        }
    }

    private fun updateUser(user: User) = repo.updateUser(user)


    fun clearLoginError() {
        _loginScreenState.value?.error = StudyAppError.newBlankInstance()
    }
}