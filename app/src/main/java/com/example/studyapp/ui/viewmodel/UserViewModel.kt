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
import com.example.studyapp.ui.composables.screens.loginscreen.LoginContract
import com.example.studyapp.ui.composables.screens.loginscreen.TAG
import com.example.studyapp.util.*
import com.example.studyapp.util.State.ApiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject


@DelicateCoroutinesApi
@InternalCoroutinesApi
@ExperimentalCoroutinesApi
@HiltViewModel
class UserViewModel @Inject constructor(private val repo: UserRepository) : ViewModel() {
    //empty constructor necessary for ViewModel()
    constructor() : this(UserRepository(AuthDataSource(), FirebaseDatabaseDataSource()))

    private val tag = "USER_VIEW_MODEL"

    //Login Screen Contract
    private val _loginScreenContract: MutableStateFlow<LoginContract> =
        MutableStateFlow(LoginContract())
    val loginScreenContract: StateFlow<LoginContract> get() = _loginScreenContract

    //Validation Variables
    private val _validEmail = MutableLiveData(true)
    private val _validPassword = MutableLiveData(true)
    private val _validVerification = MutableLiveData(true)
    val validEmail: LiveData<Boolean> get() = _validEmail
    val validPassword: LiveData<Boolean> get() = _validPassword
    val validVerification: LiveData<Boolean> get() = _validVerification


    fun toggleItems(itemToToggle: Toggleable, verification: VerificationOptions?) {
        with(loginScreenContract.value) {
            /**
             * signInOption and showDatePicker both have default values
             * on instantiation and are non-Nullable attributes so they
             * will always have a value
             */
            when (itemToToggle) {
                Toggleable.VERIFICATION -> {
                    verification?.let {
                        Log.e(
                            TAG,
                            "toggleItems: Toggling verification to $it, sign in Option = ${screenState.signInOption}",
                        )
                        screenState = screenState.copy(
                            signInOption = this.screenState.signInOption,
                            loginOption = it
                        )
                    }
                }
                Toggleable.DATEPICKER -> {
                    screenState = screenState.copy(
                        showDatePicker = (!screenState.showDatePicker)
                    )
                    //Default value set to false
                }
            }
        }
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    fun onSignUpAttempt(newUser: User, password: String, verifyPW: String, context: Context) {
        when {
            newUser.email?.validateEmail() != true -> {
                showValidationError(
                    message = "Please enter a valid email"
                )
                _validEmail.value = false
            }
            !password.validatePassword() -> {
                showValidationError(
                    message =
                    "Passwords must have at least $MIN_PW_CHARS characters with at least one (1) digit."
                )
                _validPassword.value = false
            }
            !verifyPW.validatePassword() -> {
                showValidationError(
                    message =
                    "Verified passwords must have at least $MIN_PW_CHARS characters with at least one (1) digit."
                )
                _validVerification.value = false
            }
            password != verifyPW -> {
                showValidationError(
                    message = "Passwords do not match."
                )
                _validVerification.value = false
            }
            else -> {
                if (_validEmail.value == true &&
                    _validPassword.value == true &&
                    _validVerification.value == true
                ) {
                    Log.e(TAG, "SignUpBlock: email and password are valid.")
                    newUser.email?.let {
                        Log.e(TAG, "SignUpBlock: email was $it.")
                        onSignUpAttempt(
                            VerificationOptions.SIGN_UP,
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

    private fun showValidationError(
        message: String,
        shouldShow: Boolean = true,
        errorType: ErrorType = ErrorType.VALIDATION
    ) {
        _loginScreenContract.value.screenState.error =
            _loginScreenContract.value.screenState.error.copy(
                message = message,
                shouldShow = shouldShow,
                errorType = errorType
            )
        Log.e(
            TAG,
            "onSignUpAttempt: Validation error: ${_loginScreenContract.value.screenState.error.message}"
        )
    }

    @ExperimentalCoroutinesApi
    @InternalCoroutinesApi
    private fun signInWithEmail(email: String, password: String) =
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(tag, "signInWithEmail: Calling Repo with result. State is loading.")
            _loginScreenContract.value.screenState.apiState = ApiState.Loading
            repo.signInWithEmail(email, password).collectLatest { state ->
                handleUserState(state)
            }
        }


    @DelicateCoroutinesApi
    private fun signUpWithEmail(user: User, password: String) {
        Log.e(tag, "signUpWithEmail: Calling Repo with result")
        viewModelScope.launch(Dispatchers.IO) {
            _loginScreenContract.value.screenState.apiState = ApiState.Loading
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
                    _loginScreenContract.value.screenState.apiState =
                        ApiState.Success.UserApiSuccess(user)
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
                    _loginScreenContract.value.screenState.error = this
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
        signInOption: SignInOptions,
        email: String,
        password: String,
    ) {
        Log.e(
            tag, "exiting screen content. \n VerificationOption:$signInOption \n " +
                    "Email:$email \n Password:$password"
        )

        when (signInOption) {
            SignInOptions.EMAIL_PASSWORD -> {
                Log.e(tag, "onLoginAttempt: in Verification option email/password.")
                signInWithEmail(email, password)
            }
            else -> {
                Log.e(TAG, "onLoginAttempt: sign in option selected was $signInOption")
            }
        }
    }

    private fun onSignUpAttempt(
        verificationOptions: VerificationOptions,
        email: String,
        password: String,
        context: Context,
        currentUser: User? = null
    ) {
        when (verificationOptions) {
            VerificationOptions.SIGN_UP -> {
                currentUser?.let {
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
            else -> {
                Log.e(
                    TAG,
                    "onSignUpAttempt: unhandled verification option was $verificationOptions",
                )
            }
        }

    }

    private fun updateUser(user: User) = repo.updateUser(user)


    fun clearLoginError() {
        _loginScreenContract.value.screenState.error = StudyAppError.newBlankInstance()
    }

    fun changeLoginMethod(signInMethod: SignInOptions) {
        when (signInMethod) {
            SignInOptions.EMAIL_PASSWORD -> {
                _loginScreenContract.value.screenState =
                    _loginScreenContract.value.screenState.copy(
                        signInOption = SignInOptions.EMAIL_PASSWORD
                    )
            }
            SignInOptions.GOOGLE -> TODO()
        }
    }

    fun setSideEffect(effect: SideEffects.LoginScreenSideEffects) {
        _loginScreenContract.value = _loginScreenContract.value.copy(screenSideEffects = effect)
    }

    fun setEvent(event: Events.LoginScreenEvents) {
        _loginScreenContract.value = _loginScreenContract.value.copy(screenEvent = event)
    }
}