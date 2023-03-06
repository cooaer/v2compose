package io.github.v2compose.ui.login

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import io.github.v2compose.R

@Composable
fun rememberLoginScreenState(context: Context = LocalContext.current): LoginScreenState {
    return remember(context) {
        LoginScreenState(context)
    }
}

@Stable
class LoginScreenState(private val context: Context) {

    var userNameError by mutableStateOf("")
        private set
    var passwordError by mutableStateOf("")
        private set
    var captchaError by mutableStateOf("")
        private set

    fun checkValid(userName: String, password: String, captcha: String): Boolean {
        userNameError =
            if (userName.isBlank()) context.getString(R.string.login_username_blank) else ""
        passwordError =
            if (password.isBlank()) context.getString(R.string.login_password_blank) else ""
        captchaError =
            if (captcha.isBlank()) context.getString(R.string.login_captcha_blank) else ""
        return userNameError.isEmpty() && passwordError.isEmpty() && captchaError.isEmpty()
    }

    fun resetUserNameError() {
        userNameError = ""
    }

    fun resetPasswordError() {
        passwordError = ""
    }

    fun resetCaptchaError() {
        captchaError = ""
    }

}