package io.github.v2compose.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.simform.ssjetpackcomposeprogressbuttonlibrary.SSButtonState
import com.simform.ssjetpackcomposeprogressbuttonlibrary.SSButtonType
import com.simform.ssjetpackcomposeprogressbuttonlibrary.SSJetPackComposeProgressButton
import io.github.v2compose.Constants
import io.github.v2compose.R
import io.github.v2compose.ui.common.CloseButton
import io.github.v2compose.ui.common.HtmlAlertDialog
import io.github.v2compose.ui.common.autofill

private const val TAG = "LoginScreen"

@Composable
fun LoginScreenRoute(
    onCloseClick: () -> Unit,
    onSignInWithGoogleClick: (String) -> Unit,
    redirect: String? = null,
    viewModel: LoginViewModel = hiltViewModel(),
    loginScreenState: LoginScreenState = rememberLoginScreenState(),
) {
    val loginParamState by viewModel.loginParam.collectAsStateWithLifecycle()
    val loginState by viewModel.login.collectAsStateWithLifecycle()

    LoginScreen(
        loginScreenState = loginScreenState,
        loginParamState = loginParamState,
        loginState = loginState,
        onCloseClick = onCloseClick,
        login = viewModel::login,
        onSignInWithGoogleClick = onSignInWithGoogleClick,
        reloadLoginParam = viewModel::fetchLoginParam
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreen(
    loginScreenState: LoginScreenState,
    loginParamState: LoginParamState,
    loginState: LoginState,
    onCloseClick: () -> Unit,
    login: (String, String, String) -> Unit,
    onSignInWithGoogleClick: (String) -> Unit,
    reloadLoginParam: () -> Unit,
) {
    val problem = rememberSaveable(loginParamState) {
        if (loginParamState is LoginParamState.Success) loginParamState.data.problem else ""
    }
    HtmlAlertDialog(content = problem)

    Scaffold(
        topBar = { LoginTopBar(onCloseClick = onCloseClick) },
    ) {
        LoginContent(
            loginScreenState = loginScreenState,
            loginParamState = loginParamState,
            loginState = loginState,
            reloadLoginParam = reloadLoginParam,
            login = login,
            onSignInWithGoogleClick = {
                if (loginParamState is LoginParamState.Success) {
                    onSignInWithGoogleClick(loginParamState.data.once)
                }
            },
            modifier = Modifier.padding(it)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginTopBar(onCloseClick: () -> Unit) {
    CenterAlignedTopAppBar(title = { Text(stringResource(id = R.string.login)) },
        navigationIcon = { CloseButton { onCloseClick() } })
}

@Composable
private fun LoginContent(
    loginScreenState: LoginScreenState,
    loginParamState: LoginParamState,
    loginState: LoginState,
    reloadLoginParam: () -> Unit,
    login: (String, String, String) -> Unit,
    onSignInWithGoogleClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var userName by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var captcha by rememberSaveable { mutableStateOf("") }

    val loginButtonEnabled = remember(userName, password, captcha) {
        userName.isNotEmpty() && password.isNotEmpty() && captcha.isNotEmpty()
    }

    val focusRequesters: List<FocusRequester> = remember {
        List(3) { FocusRequester() }
    }

    val onLoginClick = fun() {
        if (loginScreenState.checkValid(userName, password, captcha)) {
            login(userName, password, captcha)
        }
    }

    Column(
        modifier = modifier.padding(horizontal = 32.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        UserName(
            userName = userName,
            error = loginScreenState.userNameError,
            onValueChanged = {
                userName = it
                loginScreenState.resetUserNameError()
            },
            onNextClick = { focusRequesters[1].requestFocus() },
            modifier = Modifier.focusRequester(focusRequesters[0]),
        )
        Spacer(Modifier.height(8.dp))
        Password(
            password = password,
            error = loginScreenState.passwordError,
            onValueChanged = {
                password = it
                loginScreenState.resetPasswordError()
            },
            onNextClick = { focusRequesters[2].requestFocus() },
            modifier = Modifier.focusRequester(focusRequesters[1]),
        )
        Spacer(Modifier.height(8.dp))
        Captcha(
            text = captcha,
            error = loginScreenState.captchaError,
            loginParamState = loginParamState,
            onValueChanged = {
                captcha = it
                loginScreenState.resetCaptchaError()
            },
            onGoClick = onLoginClick,
            reloadLoginParam = reloadLoginParam,
            modifier = Modifier.focusRequester(focusRequesters[2]),
        )
        Spacer(modifier = Modifier.height(8.dp))
        LoginButton(
            loginState = loginState, enabled = loginButtonEnabled, onLoginClick = onLoginClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        SignInWithGoogle(
            loginParamState = loginParamState,
            onClick = onSignInWithGoogleClick,
            modifier = Modifier.align(Alignment.End)
        )
    }

    LaunchedEffect(true) {
        focusRequesters[0].requestFocus()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun UserName(
    userName: String,
    onValueChanged: (String) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    OutlinedTextField(
        value = userName,
        onValueChange = onValueChanged,
        label = { Text(stringResource(id = R.string.login_username)) },
        supportingText = {
            if (!error.isNullOrEmpty()) {
                Text(error)
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next, keyboardType = KeyboardType.Text
        ),
        keyboardActions = KeyboardActions(onNext = { onNextClick() }),
        isError = !error.isNullOrEmpty(),
        modifier = modifier
            .fillMaxWidth()
            .autofill(autofillTypes = listOf(AutofillType.Username), onFill = onValueChanged),
        singleLine = true,
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun Password(
    password: String,
    onValueChanged: (String) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
    error: String? = null,
) {
    val visualTransformation = remember { PasswordVisualTransformation() }
    OutlinedTextField(
        value = password,
        onValueChange = onValueChanged,
        label = { Text(stringResource(id = R.string.login_password)) },
        supportingText = {
            if (!error.isNullOrEmpty()) {
                Text(error)
            }
        },
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Next,
            keyboardType = KeyboardType.Password,
        ),
        keyboardActions = KeyboardActions(onNext = { onNextClick() }),
        isError = !error.isNullOrEmpty(),
        modifier = modifier
            .fillMaxWidth()
            .autofill(autofillTypes = listOf(AutofillType.Password), onFill = onValueChanged),
        singleLine = true,
        visualTransformation = visualTransformation,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Captcha(
    text: String,
    loginParamState: LoginParamState,
    onValueChanged: (String) -> Unit,
    reloadLoginParam: () -> Unit,
    modifier: Modifier = Modifier,
    onGoClick: (() -> Unit)? = null,
    error: String? = null,
) {
    Row(verticalAlignment = Alignment.Top, modifier = modifier) {
        OutlinedTextField(
            value = text,
            onValueChange = onValueChanged,
            label = { Text(stringResource(id = R.string.login_captcha)) },
            supportingText = {
                if (!error.isNullOrEmpty()) {
                    Text(error)
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Go,
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Characters,
            ),
            keyboardActions = KeyboardActions(onNext = { onGoClick?.invoke() }),
            isError = !error.isNullOrEmpty(),
            modifier = Modifier.weight(1f),
            singleLine = true,
        )
        Spacer(Modifier.width(16.dp))
        Box(
            modifier = Modifier
                .weight(1.5f)
                .padding(top = 8.dp)
                .height(52.dp)
        ) {
            when (loginParamState) {
                is LoginParamState.Success -> {
                    val captchaImage =
                        "${Constants.baseUrl}/_captcha?once=${loginParamState.data.once}"
                    AsyncImage(
                        model = captchaImage,
                        contentDescription = "captcha image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable { reloadLoginParam() },
                        contentScale = ContentScale.Fit,
                        placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant)
                    )
                }
                is LoginParamState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    }
                }
                is LoginParamState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.errorContainer)
                            .clickable { reloadLoginParam() },
                    ) {
                        Text(
                            stringResource(id = R.string.load_failed),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoginButton(loginState: LoginState, enabled: Boolean, onLoginClick: () -> Unit) {
    val buttonState = remember(loginState) {
        when (loginState) {
            is LoginState.Idle -> SSButtonState.IDLE
            is LoginState.Error -> SSButtonState.FAILIURE
            is LoginState.Loading -> SSButtonState.LOADING
        }
    }
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    SSJetPackComposeProgressButton(
        type = SSButtonType.CIRCLE,
        width = screenWidthDp - 64.dp,
        height = 48.dp,
        onClick = {
            if (buttonState != SSButtonState.LOADING) {
                onLoginClick()
            }
        },
        assetColor = MaterialTheme.colorScheme.onPrimary,
        successIconColor = MaterialTheme.colorScheme.onPrimary,
        failureIconColor = MaterialTheme.colorScheme.onPrimary,
        buttonState = buttonState,
        enabled = enabled,
        text = stringResource(id = R.string.login),
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        speedMillis = 400,
        successIconPainter = rememberVectorPainter(image = Icons.Rounded.Done),
        failureIconPainter = rememberVectorPainter(image = Icons.Rounded.Info),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            disabledBackgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.medium),
        )
    )
}

@Composable
private fun SignInWithGoogle(
    loginParamState: LoginParamState, onClick: () -> Unit, modifier: Modifier = Modifier
) {
    OutlinedButton(
        enabled = loginParamState is LoginParamState.Success,
        onClick = onClick,
        modifier = modifier.height(48.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.googleg_standard_color),
            contentDescription = "google branding",
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            stringResource(id = R.string.sign_in_with_google),
            style = MaterialTheme.typography.titleMedium,
        )
    }

}