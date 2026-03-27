package com.photogram.feature.events

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.photogram.core.designsystem.LocalLanguageCode
import com.photogram.core.designsystem.PhotogramBottomNav
import com.photogram.core.designsystem.PhotogramNavDestination
import com.photogram.core.designsystem.PhotogramGold
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.ui.text.input.KeyboardType

// ── Local design tokens ────────────────────────────────────────────────────────
private val EventBg         = Color(0xFF050505)
private val EventSurface    = Color(0xFF121214)
private val CountdownBoxBg  = Color(0xFF121214)   // dark surface — cinematic
private val CountdownNumFg  = Color(0xFFF5F5F3)   // off-white text on dark
private val HeroTop         = Color(0xFF101A0E)
private val HeroMid         = Color(0xFF090F07)
private val HeroBase        = Color(0xFF050505)
private val GuestColors     = listOf(
    Color(0xFF2A4A2A), Color(0xFF3A3220),
    Color(0xFF1E3A2E), Color(0xFF2A2018), Color(0xFF1A2A1C),
)
private val CameraAccent    = Color(0xFFC9A96E)   // camera icon circle — premium gold
private val EditSurface     = Color(0xFF242422)
private val DeleteRedTint   = Color(0xFFFF6B6B)
private val DeleteRedBg     = Color(0x22FF6B6B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventScreen(
    onNavigate: (String) -> Unit,
    viewModel: EventViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val strings = EventStrings.forCode(LocalLanguageCode.current)

    LaunchedEffect(Unit) {
        viewModel.navEvent.collect { route -> onNavigate(route) }
    }

    var countdown by remember { mutableStateOf(uiState.countdown) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1_000L)
            val total = countdown.days * 86_400L +
                        countdown.hours * 3_600L +
                        countdown.minutes * 60L +
                        countdown.seconds.toLong()
            if (total > 0L) countdown = CountdownState.fromSeconds(total - 1L)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EventBg),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 160.dp),
        ) {
            HeroSection(uiState.event)
            Spacer(Modifier.height(20.dp))
            CountdownSection(countdown)
            Spacer(Modifier.height(32.dp))
            TimelineSection(uiState.timeline)
            Spacer(Modifier.height(32.dp))
            GuestsSection(
                guestCount       = uiState.guestCount,
                additionalGuests = uiState.additionalGuests,
                onGuestList      = { viewModel.onAction(EventUiAction.GuestListClicked) },
            )
            Spacer(Modifier.height(24.dp))
            ShareCard(onUpload = { viewModel.onAction(EventUiAction.UploadPhotosClicked) })
            Spacer(Modifier.height(24.dp))
        }

        PhotogramBottomNav(
            modifier          = Modifier.align(Alignment.BottomCenter).fillMaxWidth().navigationBarsPadding(),
            activeDestination = PhotogramNavDestination.None,
            onHome    = { viewModel.onAction(EventUiAction.HomeNavClicked) },
            onGallery = { viewModel.onAction(EventUiAction.GalleryNavClicked) },
            onCreate  = { viewModel.onAction(EventUiAction.CreateNavClicked) },
            onChat    = { viewModel.onAction(EventUiAction.ChatNavClicked) },
            onProfile = { viewModel.onAction(EventUiAction.ProfileNavClicked) },
        )

        // Fixed: edit button (top-right overlay above hero)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(top = 12.dp, end = 16.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0x88000000))
                .clickable { viewModel.onAction(EventUiAction.EditEventClicked) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.Edit,
                contentDescription = strings.editEventDesc,
                tint               = Color.White,
                modifier           = Modifier.size(20.dp),
            )
        }
    }

    if (uiState.isEditSheetVisible) {
        EditEventSheet(
            draft    = uiState.editDraft,
            onAction = { viewModel.onAction(it) },
        )
    }
}

// ── Hero ──────────────────────────────────────────────────────────────────────

@Composable
private fun HeroSection(event: EventDetail) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp),
    ) {
        // Simulated dark forest background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(HeroTop, HeroMid, HeroBase),
                    ),
                ),
        )

        // Bottom scrim for text legibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0xE0060606)),
                    ),
                ),
        )

        // Title + chip overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 24.dp, end = 24.dp, bottom = 28.dp),
        ) {
            Text(
                text  = event.title,
                style = MaterialTheme.typography.displaySmall.copy(
                    fontStyle  = FontStyle.Italic,
                    fontWeight = FontWeight.Light,
                    lineHeight = 40.sp,
                ),
                color = Color.White,
            )
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Color(0x66000000))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) {
                Text(
                    text  = event.dateLabel,
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.8.sp),
                    color = Color.White.copy(alpha = 0.90f),
                )
            }
        }
    }
}

// ── Countdown ─────────────────────────────────────────────────────────────────

@Composable
private fun CountdownSection(countdown: CountdownState) {
    val strings = EventStrings.forCode(LocalLanguageCode.current)
    Row(
        modifier             = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        CountdownBox(value = countdown.days,    label = strings.days,  modifier = Modifier.weight(1f))
        CountdownBox(value = countdown.hours,   label = strings.hours, modifier = Modifier.weight(1f))
        CountdownBox(value = countdown.minutes, label = strings.min,   modifier = Modifier.weight(1f))
        CountdownBox(value = countdown.seconds, label = strings.sec,   modifier = Modifier.weight(1f))
    }
}

@Composable
private fun CountdownBox(value: Int, label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(CountdownBoxBg)
            .border(
                width = 2.5.dp,
                brush = Brush.verticalGradient(colors = listOf(Color.Transparent, PhotogramGold)),
                shape = RoundedCornerShape(8.dp),
            )
            .padding(vertical = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text  = "%02d".format(value),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                color = CountdownNumFg,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight    = FontWeight.Medium,
                    letterSpacing = 0.6.sp,
                ),
                color = Color.White.copy(alpha = 0.40f),
            )
        }
    }
}

// ── Timeline ──────────────────────────────────────────────────────────────────

@Composable
private fun TimelineSection(items: List<TimelineEntry>) {
    val strings = EventStrings.forCode(LocalLanguageCode.current)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        SectionHeader(strings.itinerary)
        Spacer(Modifier.height(18.dp))

        items.forEachIndexed { index, entry ->
            TimelineItem(entry = entry, isLast = index == items.lastIndex)
            // Orange connector between items
            if (index != items.lastIndex) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.width(24.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(12.dp)
                                .background(PhotogramGold.copy(alpha = 0.50f)),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineItem(entry: TimelineEntry, isLast: Boolean) {
    Row(
        modifier          = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
        verticalAlignment = Alignment.Top,
    ) {
        // Left: dot + vertical line
        Column(
            modifier            = Modifier.fillMaxHeight().width(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(PhotogramGold),
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .weight(1f)
                    .background(
                        if (isLast) Color.Transparent else PhotogramGold.copy(alpha = 0.50f),
                    ),
            )
        }

        Spacer(Modifier.width(14.dp))

        // Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(EventSurface)
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Text(
                text  = entry.time,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = PhotogramGold,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = entry.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = entry.location,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.52f),
            )
        }
    }
}

// ── Guests ────────────────────────────────────────────────────────────────────

@Composable
private fun GuestsSection(
    guestCount: Int,
    additionalGuests: Int,
    onGuestList: () -> Unit,
) {
    val strings = EventStrings.forCode(LocalLanguageCode.current)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
    ) {
        SectionHeader(strings.guests)
        Spacer(Modifier.height(18.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier          = Modifier.fillMaxWidth(),
        ) {
            // Overlapping avatar circles + overflow badge
            val avatarSize   = 38.dp
            val avatarOffset = 24.dp
            val totalWidth   = avatarOffset * GuestColors.size + avatarSize

            Box(modifier = Modifier.width(totalWidth)) {
                GuestColors.forEachIndexed { i, color ->
                    Box(
                        modifier = Modifier
                            .offset(x = avatarOffset * i)
                            .size(avatarSize)
                            .clip(CircleShape)
                            .border(1.5.dp, EventBg, CircleShape)
                            .background(color),
                    )
                }
                // Overflow count badge
                Box(
                    modifier = Modifier
                        .offset(x = avatarOffset * GuestColors.size)
                        .size(avatarSize)
                        .clip(CircleShape)
                        .border(1.5.dp, EventBg, CircleShape)
                        .background(Color(0xFF2A2A28)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text  = "+$additionalGuests",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text  = "$guestCount ${strings.guestsConfirmed}",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Color.White,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text     = strings.viewFullList,
                    style    = MaterialTheme.typography.labelSmall,
                    color    = PhotogramGold,
                    modifier = Modifier.clickable(onClick = onGuestList),
                )
            }
        }
    }
}

// ── Share card ────────────────────────────────────────────────────────────────

@Composable
private fun ShareCard(onUpload: () -> Unit) {
    val strings = EventStrings.forCode(LocalLanguageCode.current)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(EventSurface)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(CameraAccent),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector        = Icons.Default.CameraAlt,
                contentDescription = null,
                tint               = Color.White,
                modifier           = Modifier.size(30.dp),
            )
        }
        Spacer(Modifier.height(16.dp))
        Text(
            text      = strings.shareYourMoments,
            style     = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color     = Color.White,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text      = strings.captureEverySmile,
            style     = MaterialTheme.typography.bodySmall,
            color     = Color.White.copy(alpha = 0.52f),
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(50))
                .background(PhotogramGold)
                .clickable(onClick = onUpload)
                .padding(vertical = 15.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text  = strings.uploadPhotos,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight    = FontWeight.Bold,
                    letterSpacing = 1.8.sp,
                ),
                color = Color.White,
            )
        }
    }
}


// ── Section header ────────────────────────────────────────────────────────────

@Composable
private fun SectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(PhotogramGold),
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text  = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight    = FontWeight.SemiBold,
                letterSpacing = 1.5.sp,
            ),
            color = Color.White.copy(alpha = 0.72f),
        )
    }
}

// ── Edit event sheet ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditEventSheet(
    draft: EditDraft,
    onAction: (EventUiAction) -> Unit,
) {
    val strings = EventStrings.forCode(LocalLanguageCode.current)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { onAction(EventUiAction.EditDismissed) },
        sheetState       = sheetState,
        containerColor   = Color(0xFF0B0B0D),
        contentColor     = Color.White,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 14.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.22f)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp),
        ) {
            // ── Header ────────────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text  = strings.editEventTitle,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White,
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.08f))
                        .clickable { onAction(EventUiAction.EditDismissed) },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector        = Icons.Default.Close,
                        contentDescription = strings.closeDesc,
                        tint               = Color.White.copy(alpha = 0.70f),
                        modifier           = Modifier.size(16.dp),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Portada placeholder ───────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EditSurface)
                    .border(1.dp, Color.White.copy(alpha = 0.10f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector        = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint               = Color.White.copy(alpha = 0.35f),
                        modifier           = Modifier.size(26.dp),
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text  = "Cambiar portada  ·  placeholder",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.35f),
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Título ────────────────────────────────────────────────────────
            EditTextField(
                value         = draft.title,
                onValueChange = { onAction(EventUiAction.DraftTitleChanged(it)) },
                label         = strings.eventTitleField,
            )

            Spacer(Modifier.height(12.dp))

            // ── Fecha + Lugar ─────────────────────────────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                EditTextField(
                    value         = draft.date,
                    onValueChange = { onAction(EventUiAction.DraftDateChanged(it)) },
                    label         = strings.dateField,
                    modifier      = Modifier.weight(1f),
                )
                EditTextField(
                    value         = draft.location,
                    onValueChange = { onAction(EventUiAction.DraftLocationChanged(it)) },
                    label         = strings.locationField,
                    modifier      = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Itinerario ────────────────────────────────────────────────────
            SectionHeader(strings.itinerarySection)
            Spacer(Modifier.height(14.dp))

            draft.timeline.forEach { item ->
                EditTimelineItemRow(
                    item             = item,
                    onTimeChange     = { onAction(EventUiAction.DraftTimelineTimeChanged(item.id, it)) },
                    onTitleChange    = { onAction(EventUiAction.DraftTimelineTitleChanged(item.id, it)) },
                    onLocationChange = { onAction(EventUiAction.DraftTimelineLocationChanged(item.id, it)) },
                    onRemove         = { onAction(EventUiAction.RemoveTimelineItem(item.id)) },
                )
                Spacer(Modifier.height(10.dp))
            }

            // Add item button
            Row(
                modifier              = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, PhotogramGold.copy(alpha = 0.40f), RoundedCornerShape(8.dp))
                    .clickable { onAction(EventUiAction.AddTimelineItem) }
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = null,
                    tint               = PhotogramGold,
                    modifier           = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text  = strings.addItem,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = PhotogramGold,
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Invitados ─────────────────────────────────────────────────────
            SectionHeader(strings.guestsSection)
            Spacer(Modifier.height(14.dp))
            EditTextField(
                value         = draft.guestCountText,
                onValueChange = { onAction(EventUiAction.DraftGuestCountChanged(it)) },
                label         = strings.confirmedGuests,
                keyboardType  = KeyboardType.Number,
            )

            Spacer(Modifier.height(32.dp))

            // ── Guardar ───────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50))
                    .background(PhotogramGold)
                    .clickable { onAction(EventUiAction.EditSaved) }
                    .padding(vertical = 15.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text  = strings.saveChanges,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight    = FontWeight.Bold,
                        letterSpacing = 1.5.sp,
                    ),
                    color = Color.White,
                )
            }
        }
    }
}

// ── Edit timeline item row ─────────────────────────────────────────────────────

@Composable
private fun EditTimelineItemRow(
    item: EditTimelineItem,
    onTimeChange: (String) -> Unit,
    onTitleChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onRemove: () -> Unit,
) {
    val strings = EventStrings.forCode(LocalLanguageCode.current)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(EditSurface)
            .padding(12.dp),
    ) {
        Row(
            modifier          = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            EditTextField(
                value         = item.time,
                onValueChange = onTimeChange,
                label         = strings.timeField,
                modifier      = Modifier.width(96.dp),
            )
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(DeleteRedBg)
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector        = Icons.Default.Delete,
                    contentDescription = strings.deleteDesc,
                    tint               = DeleteRedTint,
                    modifier           = Modifier.size(16.dp),
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        EditTextField(value = item.title,    onValueChange = onTitleChange,    label = strings.nameField)
        Spacer(Modifier.height(8.dp))
        EditTextField(value = item.location, onValueChange = onLocationChange, label = strings.locationField)
    }
}

// ── Edit text field ────────────────────────────────────────────────────────────

@Composable
private fun EditTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value          = value,
        onValueChange  = onValueChange,
        label          = { Text(label) },
        singleLine     = true,
        modifier       = modifier,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        colors         = OutlinedTextFieldDefaults.colors(
            focusedTextColor        = Color.White,
            unfocusedTextColor      = Color.White.copy(alpha = 0.80f),
            focusedBorderColor      = PhotogramGold,
            unfocusedBorderColor    = Color.White.copy(alpha = 0.22f),
            cursorColor             = PhotogramGold,
            focusedContainerColor   = EditSurface,
            unfocusedContainerColor = Color(0xFF121214),
            focusedLabelColor       = PhotogramGold,
            unfocusedLabelColor     = Color.White.copy(alpha = 0.48f),
        ),
    )
}
