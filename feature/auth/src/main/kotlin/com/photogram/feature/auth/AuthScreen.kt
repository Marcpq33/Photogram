package com.photogram.feature.auth

import android.app.Activity
import com.photogram.feature.auth.BuildConfig
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.PhotogramTheme
import kotlin.random.Random

// ── Auth-local palette ─────────────────────────────────────────────────────────
private val AuthBg             = Color(0xFF050505)
private val Gold               = Color(0xFFC9A96E)
private val WordmarkPhoto      = Color(0xFFE6E1DD)   // "Photo" — warm off-white (matches splash)
private val WordmarkGram       = Color(0xFFC9A96E)   // "gram"  — gold (matches splash)
private val Terracotta         = Color(0xFFC5663E)
private val CtaBlue            = Color(0xFFC9A96E)
private val FieldGlass         = Color(0x1AFFFFFF)
private val FieldGlassFocused  = Color(0x26FFFFFF)
private val FieldBorder        = Color(0x33FFFFFF)
private val FieldBorderFocused = Gold
private val Placeholder        = Color(0x66FFFFFF)
private val IconTint           = Color(0x99FFFFFF)
private val TextMuted          = Color(0x80FFFFFF)
private val SocialDark         = Color(0x14FFFFFF)
private val SocialDarkStroke   = Color(0x26FFFFFF)
private val SheetSurface       = Color(0xFF121214)
// Light input field tokens (replace dark glass on create/sign-in form)
private val FieldLight         = Color(0xFFEBEBEB)
private val FieldLightFocused  = Color(0xFFFFFFFF)
private val FieldLightBorder   = Color(0xFFCCCCCC)
private val FieldText          = Color(0xFF1A1A1A)
private val FieldHint          = Color(0xFF9E9E9E)

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate to Home only AFTER setProtoMode(true) has been written to DataStore.
    // This ensures all destination ViewModels read isDemoMode = true on their first
    // userData.first() call and populate mock/proto content correctly.
    LaunchedEffect(Unit) {
        viewModel.devBypassNavEvent.collect { onAuthSuccess() }
    }

    val activity = LocalContext.current as Activity
    PhotogramTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize()) {
            AuthContent(uiState = uiState, onAction = viewModel::onAction, activity = activity)
            // ── Email confirmation overlay ───────────────────────────────────────
            // Shown when signUp succeeded but Supabase requires the user to confirm
            // their email before a session is issued. Rendered on top of AuthContent.
            if (uiState.pendingEmailConfirmation) {
                EmailConfirmationScreen(
                    email = uiState.email,
                    onAcknowledge = { viewModel.onAction(AuthUiAction.ConfirmationAcknowledged) },
                )
            }
            // ── Debug-only bypass ───────────────────────────────────────────────
            // BuildConfig.DEBUG is false in release builds. R8 removes this branch
            // entirely at shrink time. Never ships to production.
            // Dispatches DevBypassClicked → ViewModel writes isProtoMode=true to
            // DataStore → emits devBypassNavEvent → LaunchedEffect calls onAuthSuccess.
            // Navigation is deferred until after the DataStore write so destination
            // ViewModels reliably read isDemoMode = true.
            if (BuildConfig.DEBUG && !uiState.pendingEmailConfirmation) {
                TextButton(
                    onClick = { viewModel.onAction(AuthUiAction.DevBypassClicked) },
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp),
                ) {
                    Text(
                        text = "⚡ Dev bypass → Home",
                        color = Color.Yellow.copy(alpha = 0.65f),
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AuthContent(
    uiState: AuthUiState,
    onAction: (AuthUiAction) -> Unit,
    activity: Activity,
) {
    val strings = AuthStrings.forCode(uiState.selectedLanguageCode)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBg),
    ) {
        WarmGlow()
        FilmGrain()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(20.dp))

            // Wordmark centered; language pill pinned to end
            Box(modifier = Modifier.fillMaxWidth()) {
                Wordmark(modifier = Modifier.align(Alignment.Center))
                LanguagePill(
                    label = uiState.selectedLanguageCode,
                    onClick = { onAction(AuthUiAction.LanguageSelectorClicked) },
                    modifier = Modifier.align(Alignment.CenterEnd),
                )
            }

            Spacer(Modifier.height(48.dp))
            HeroLine(isCreateMode = uiState.isCreateMode, strings = strings)
            Spacer(Modifier.height(40.dp))

                SocialRow(onAction = onAction, strings = strings, activity = activity)
                Spacer(Modifier.height(22.dp))
                OrEmailDivider(isCreateMode = uiState.isCreateMode, strings = strings)
                Spacer(Modifier.height(22.dp))

                // Full name — create mode only
                if (uiState.isCreateMode) {
                    FieldLabel(strings.placeholderFullName)
                    Spacer(Modifier.height(6.dp))
                    GlassInputField(
                        value = uiState.fullName,
                        onValueChange = { onAction(AuthUiAction.FullNameChanged(it)) },
                        placeholder = strings.placeholderFullName,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                        ),
                    )
                    Spacer(Modifier.height(14.dp))
                }

                // Email
                FieldLabel(strings.placeholderEmail)
                Spacer(Modifier.height(6.dp))
                GlassInputField(
                    value = uiState.email,
                    onValueChange = { onAction(AuthUiAction.EmailChanged(it)) },
                    placeholder = strings.placeholderEmail,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next,
                    ),
                )
                Spacer(Modifier.height(14.dp))

                // Password
                FieldLabel(strings.placeholderPassword)
                Spacer(Modifier.height(6.dp))
                GlassInputField(
                    value = uiState.password,
                    onValueChange = { onAction(AuthUiAction.PasswordChanged(it)) },
                    placeholder = strings.placeholderPassword,
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (uiState.isPasswordVisible)
                        VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (uiState.email.isNotBlank() && uiState.password.isNotBlank() && !uiState.isLoading) {
                                onAction(AuthUiAction.ContinueClicked)
                            }
                        }
                    ),
                    trailingContent = {
                        IconButton(
                            onClick = { onAction(AuthUiAction.TogglePasswordVisibility) },
                            modifier = Modifier.size(36.dp),
                        ) {
                            Icon(
                                imageVector = if (uiState.isPasswordVisible)
                                    Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (uiState.isPasswordVisible)
                                    strings.hidePassword else strings.showPassword,
                                tint = IconTint,
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    },
                )

                uiState.error?.let { err ->
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = err,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }

                Spacer(Modifier.height(22.dp))
                CtaButton(uiState = uiState, onAction = onAction, strings = strings)
                Spacer(Modifier.height(24.dp))
                ModeToggleRow(isCreateMode = uiState.isCreateMode, onAction = onAction, strings = strings)
                Spacer(Modifier.height(12.dp))
                LegalFooter(strings = strings)

            Spacer(Modifier.height(32.dp))
        }
    }

    if (uiState.isLanguageSheetVisible) {
        LanguageSheet(
            selectedCode = uiState.selectedLanguageCode,
            onSelect = { onAction(AuthUiAction.LanguageSelected(it)) },
            onDismiss = { onAction(AuthUiAction.LanguageSheetDismissed) },
            languageTitle = strings.languageTitle,
        )
    }
}

// ── Background layers ──────────────────────────────────────────────────────────

@Composable
private fun WarmGlow() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x22C5663E),
                    Color(0x12265D91),
                    Color.Transparent,
                ),
                center = Offset(size.width / 2f, size.height * 0.28f),
                radius = size.width * 0.90f,
            ),
            radius = size.width * 0.90f,
            center = Offset(size.width / 2f, size.height * 0.28f),
        )
    }
}

@Composable
private fun FilmGrain() {
    val noise = remember {
        val rnd = Random(1337)
        Array(2000) { Offset(rnd.nextFloat(), rnd.nextFloat()) }
    }
    Canvas(modifier = Modifier.fillMaxSize().alpha(0.038f)) {
        drawPoints(
            points = noise.map { Offset(it.x * size.width, it.y * size.height) },
            pointMode = PointMode.Points,
            color = Color.White,
            strokeWidth = 1.5.dp.toPx(),
        )
    }
}

// ── Header ─────────────────────────────────────────────────────────────────────

@Composable
private fun LanguagePill(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // Outer Box: 48dp minimum touch area (a11y) with clip so the ripple is pill-shaped.
    // Visual content (Row) is the small pill — centered inside the larger touch target.
    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            modifier = Modifier
                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(20.dp))
                .border(0.5.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                .padding(horizontal = 12.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = Color.White.copy(alpha = 0.65f),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.75f),
            )
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(13.dp),
                tint = Color.White.copy(alpha = 0.65f),
            )
        }
    }
}

@Composable
private fun Wordmark(modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.ExtraLight,
                fontSize = 24.sp,
                color = WordmarkPhoto,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 0.5.sp,
            )
        ) { append("Photo") }
        withStyle(
            SpanStyle(
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
                fontSize = 24.sp,
                color = WordmarkGram,
                fontFamily = FontFamily.Serif,
            )
        ) { append("gram") }
    }
    Text(text = text, modifier = modifier)
}

@Composable
private fun HeroLine(isCreateMode: Boolean, strings: AuthStrings) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = if (isCreateMode) strings.heroCreate else strings.heroSignIn,
            style = MaterialTheme.typography.headlineLarge.copy(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Normal,
                fontSize = 28.sp,
                lineHeight = 34.sp,
                letterSpacing = (-0.5).sp,
            ),
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

// ── Social buttons ─────────────────────────────────────────────────────────────

@Composable
private fun SocialRow(
    onAction: (AuthUiAction) -> Unit,
    strings: AuthStrings,
    activity: Activity,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        GoogleButton(
            onClick = { onAction(AuthUiAction.GoogleSignInClicked(activity)) },
            label = strings.continueWithGoogle,
            modifier = Modifier.fillMaxWidth(),
        )
        AppleButton(
            onClick = { onAction(AuthUiAction.AppleSignInClicked) },
            label = strings.continueWithApple,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun GoogleButton(onClick: () -> Unit, label: String, modifier: Modifier = Modifier) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(18.dp),
        color = Color.White,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            GoogleGIcon(modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Text(
                text = label,
                color = Color(0xFF1F1F1F),
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
            )
        }
    }
}

@Composable
private fun AppleButton(onClick: () -> Unit, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(54.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF121214))
            .border(0.5.dp, SocialDarkStroke, RoundedCornerShape(18.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            AppleLogoIcon(modifier = Modifier.size(18.dp), tint = Color.White)
            Spacer(Modifier.width(10.dp))
            Text(
                text = label,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
            )
        }
    }
}

@Composable
private fun GoogleGIcon(modifier: Modifier = Modifier) {
    Icon(
        painter = painterResource(R.drawable.ic_google_g),
        contentDescription = null,
        modifier = modifier,
        tint = Color.Unspecified,
    )
}

@Composable
private fun AppleLogoIcon(modifier: Modifier = Modifier, tint: Color = Color.White) {
    Icon(
        painter = painterResource(R.drawable.ic_apple),
        contentDescription = null,
        modifier = modifier,
        tint = tint,
    )
}

// ── Divider ────────────────────────────────────────────────────────────────────

@Composable
private fun OrEmailDivider(isCreateMode: Boolean, strings: AuthStrings) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 0.5.dp,
            color = Color.White.copy(alpha = 0.15f),
        )
        Text(
            text = if (isCreateMode) strings.orSignUpEmail
            else strings.orSignInEmail,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 0.5.dp,
            color = Color.White.copy(alpha = 0.15f),
        )
    }
}

// ── Field label ────────────────────────────────────────────────────────────────

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text.uppercase(),
        style = MaterialTheme.typography.labelSmall.copy(
            fontFamily = FontFamily.SansSerif,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            letterSpacing = 1.5.sp,
        ),
        color = TextMuted,
        modifier = Modifier.fillMaxWidth(),
    )
}

// ── Glass input field ──────────────────────────────────────────────────────────

@Composable
private fun FieldIcon(icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        modifier = Modifier.size(18.dp),
        tint = IconTint,
    )
}

@Composable
private fun GlassInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.height(58.dp),
        singleLine = true,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        interactionSource = interactionSource,
        cursorBrush = SolidColor(Terracotta),
        textStyle = TextStyle(
            color = FieldText,
            fontSize = 15.sp,
            fontFamily = FontFamily.SansSerif,
        ),
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isFocused) FieldLightFocused else FieldLight)
                    .border(
                        width = 1.dp,
                        color = if (isFocused) Terracotta.copy(alpha = 0.45f) else FieldLightBorder,
                        shape = RoundedCornerShape(18.dp),
                    )
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                leadingIcon?.invoke()
                Box(modifier = Modifier.weight(1f)) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = FieldHint,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.SansSerif,
                        )
                    }
                    innerTextField()
                }
                trailingContent?.invoke()
            }
        },
    )
}

// ── CTA button ─────────────────────────────────────────────────────────────────

@Composable
private fun CtaButton(
    uiState: AuthUiState,
    onAction: (AuthUiAction) -> Unit,
    strings: AuthStrings,
) {
    Button(
        onClick = { onAction(AuthUiAction.ContinueClicked) },
        enabled = !uiState.isLoading,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = CtaBlue,
            contentColor = Color.White,
            disabledContainerColor = CtaBlue.copy(alpha = 0.55f),
            disabledContentColor = Color.White.copy(alpha = 0.75f),
        ),
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = Color.White,
            )
        } else {
            Text(
                text = if (uiState.isCreateMode) strings.ctaCreate else strings.ctaSignIn,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

// ── Footer ─────────────────────────────────────────────────────────────────────

@Composable
private fun ModeToggleRow(
    isCreateMode: Boolean,
    onAction: (AuthUiAction) -> Unit,
    strings: AuthStrings,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (isCreateMode) strings.haveAccount else strings.noAccount,
            style = MaterialTheme.typography.bodySmall,
            color = TextMuted,
        )
        TextButton(
            onClick = { onAction(AuthUiAction.ToggleModeClicked) },
            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
        ) {
            Text(
                text = if (isCreateMode) strings.signInLink else strings.createOneLink,
                style = MaterialTheme.typography.bodySmall,
                color = Gold,
            )
        }
    }
}

@Composable
private fun LegalFooter(strings: AuthStrings) {
    Text(
        text = strings.legalFooter,
        style = MaterialTheme.typography.labelSmall,
        color = Color.White.copy(alpha = 0.25f),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    )
}

// ── Email confirmation screen ──────────────────────────────────────────────────

/**
 * Full-screen overlay shown when signUp succeeded but email confirmation is required.
 * Covers the auth form so the user cannot interact with it until they acknowledge.
 */
@Composable
private fun EmailConfirmationScreen(
    email: String,
    onAcknowledge: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AuthBg)
            .pointerInput(Unit) { /* consume all touches — prevent interaction with form below */ },
        contentAlignment = Alignment.Center,
    ) {
        WarmGlow()
        FilmGrain()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Mail icon drawn with Canvas — no external resource needed
            Canvas(modifier = Modifier.size(72.dp)) {
                val stroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                val inset = 6.dp.toPx()
                val left = inset
                val top = size.height * 0.28f
                val right = size.width - inset
                val bottom = size.height * 0.72f
                // envelope body
                drawRoundRect(
                    color = Color(0xFFC9A96E),
                    topLeft = Offset(left, top),
                    size = Size(right - left, bottom - top),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(6.dp.toPx()),
                    style = stroke,
                )
                // envelope flap (V shape)
                val path = Path().apply {
                    moveTo(left, top)
                    lineTo(size.width / 2f, (top + bottom) / 2f - 2.dp.toPx())
                    lineTo(right, top)
                }
                drawPath(path = path, color = Color(0xFFC9A96E), style = stroke)
            }

            Text(
                text = "Check your inbox",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    letterSpacing = (-0.3).sp,
                ),
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            Text(
                text = "We sent a confirmation link to\n${email}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.65f),
                textAlign = TextAlign.Center,
            )

            Text(
                text = "Tap the link in the email to activate your account, then come back and sign in.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.45f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp),
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = onAcknowledge,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold,
                    contentColor = Color(0xFF1A1A18),
                ),
            ) {
                Text(
                    text = "Back to sign in",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

// ── Language bottom sheet ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LanguageSheet(
    selectedCode: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    languageTitle: String = "Language",
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SheetSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.18f),
                        shape = RoundedCornerShape(2.dp),
                    ),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 36.dp),
        ) {
            Text(
                text = languageTitle,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                modifier = Modifier.padding(bottom = 20.dp),
            )

            val languages = listOf(
                "English" to "EN",
                "Español" to "ES",
                "Français" to "FR",
                "Italiano" to "IT",
                "中文" to "ZH",
                "日本語" to "JA",
            )

            languages.forEachIndexed { index, (name, code) ->
                val isSelected = code == selectedCode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .pointerInput(code) {
                            detectTapGestures(onTap = { onSelect(code) })
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) Gold else Color.White.copy(alpha = 0.65f),
                    )
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Gold, RoundedCornerShape(4.dp)),
                        )
                    }
                }
                if (index < languages.lastIndex) {
                    HorizontalDivider(
                        color = Color.White.copy(alpha = 0.06f),
                        thickness = 0.5.dp,
                    )
                }
            }
        }
    }
}
