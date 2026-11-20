package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.formatDurationHms
import com.example.ui.components.formatDurationMs
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AppBackground
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.FlagPurple
import com.example.ui.theme.FlagPurpleSoft
import com.example.ui.theme.JetBrainsMono
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySurfaceTint
import com.example.ui.theme.NotoDevanagari
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateGrey
import com.example.ui.theme.SoftInputBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ActiveCbtState

@Composable
fun CbtTestScreen(
    state: ActiveCbtState,
    isCurrentBookmarked: Boolean,
    onSelectOption: (Int) -> Unit,
    onSaveAndNext: () -> Unit,
    onPrevious: () -> Unit,
    onSkip: () -> Unit,
    onClear: () -> Unit,
    onToggleFlag: () -> Unit,
    onToggleBookmark: () -> Unit,
    onJumpToQuestion: (Int) -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onResetZoom: () -> Unit,
    onSubmitTest: () -> Unit,
    onExitWithoutSaving: () -> Unit
) {
    var showPaletteSheet by remember { mutableStateOf(false) }
    var showInstructions by remember { mutableStateOf(false) }
    var showSubmitConfirm by remember { mutableStateOf(false) }
    var showExitConfirm by remember { mutableStateOf(false) }

    val questionScrollState = rememberScrollState()
    LaunchedEffect(state.currentIndex) {
        questionScrollState.scrollTo(0)
    }

    BackHandler {
        showExitConfirm = true
    }

    val currentQ = state.currentQuestion
    val selectedOption = state.selectedAnswers[state.currentIndex]
    val isFlagged = state.currentIndex in state.flaggedIndices
    val scale = state.fontScalePercent / 100f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // 1. Navy Top CBT Header Bar
        Surface(color = NavyPrimary, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { showExitConfirm = true },
                    modifier = Modifier.testTag("cbt_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Exit Test",
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = state.testMeta.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = stringResource(R.string.cbt_no_neg_subtitle),
                        color = Color.White.copy(alpha = 0.80f),
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                }
                OutlinedButton(
                    onClick = { showPaletteSheet = true },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White.copy(alpha = 0.12f),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("cbt_palette_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.GridView,
                        contentDescription = "Question Palette",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.btn_palette),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // 2. Timer + Progress + Instructions Bar
        Surface(
            color = CardSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (state.remainingSeconds < 600) CrimsonRed else EmeraldGreen,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = "Remaining Time",
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = formatDurationHms(state.remainingSeconds),
                            color = Color.White,
                            fontFamily = JetBrainsMono,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${state.answeredCount}/${state.totalCount}",
                            color = TextPrimary,
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${state.progressPercent}%",
                            color = NavyPrimary,
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (state.totalCount == 0) 0f
                            else state.answeredCount.toFloat() / state.totalCount.toFloat()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = NavyPrimary,
                        trackColor = SlateBorder,
                        strokeCap = StrokeCap.Round
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = { showInstructions = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyPrimary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
                    modifier = Modifier.testTag("cbt_instructions_button")
                ) {
                    Icon(
                        imageVector = Icons.Filled.Visibility,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = stringResource(R.string.btn_instructions),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
        HorizontalDivider(color = SlateBorder)

        // 3. Unit Section Switcher Tabs (shown when both Unit I & Unit II exist)
        val u1Range = state.unit1Indices
        val u2Range = state.unit2Indices
        if (u1Range != null && u2Range != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardSurface)
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val inU1 = state.currentIndex in u1Range
                SectionPillTab(
                    label = "Unit I: Music (${u1Range.first + 1}-${u1Range.last + 1})",
                    selected = inU1,
                    onClick = { if (!inU1) onJumpToQuestion(u1Range.first) }
                )
                SectionPillTab(
                    label = "Unit II: General Skills (${u2Range.first + 1}-${u2Range.last + 1})",
                    selected = !inU1,
                    onClick = { if (inU1) onJumpToQuestion(u2Range.first) }
                )
            }
            HorizontalDivider(color = SlateBorder)
        }

        // 4. Scrollable Question & Options Content
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(questionScrollState)
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Per-question controls row: Timer, +1/0, Bookmark, Zoom (accessible touch targets)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = NavySurfaceTint,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, NavyBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Timer,
                            contentDescription = "Question Time",
                            tint = NavyPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatDurationMs(state.currentQuestionSeconds),
                            color = NavyPrimary,
                            fontFamily = JetBrainsMono,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                    color = EmeraldSoft,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "+1 / 0",
                        color = EmeraldGreen,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("cbt_bookmark_button")
                ) {
                    Icon(
                        imageVector = if (isCurrentBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark Question",
                        tint = if (isCurrentBookmarked) AmberAccent else TextSecondary
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    color = CardSurface,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SlateBorder)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onZoomOut,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("cbt_zoom_out_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ZoomOut,
                                contentDescription = "Zoom Out",
                                tint = NavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = "${state.fontScalePercent}%",
                            color = TextPrimary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable(enabled = state.fontScalePercent != 100, onClick = onResetZoom)
                                .padding(horizontal = 4.dp)
                        )
                        IconButton(
                            onClick = onZoomIn,
                            modifier = Modifier
                                .size(40.dp)
                                .testTag("cbt_zoom_in_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ZoomIn,
                                contentDescription = "Zoom In",
                                tint = NavyPrimary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Question Card
            Surface(
                color = CardSurface,
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentQ.unit.shortLabel,
                            color = TextMuted,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentQ.sectionTitle,
                            color = NavyPrimary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    val qNumPrefix = "Q${state.currentIndex + 1}. "
                    if (currentQ.qEn.isNotBlank()) {
                        Text(
                            text = qNumPrefix + currentQ.qEn,
                            color = TextPrimary,
                            fontSize = (16f * scale).sp,
                            lineHeight = (23f * scale).sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = currentQ.qHi,
                            color = TextPrimary,
                            fontFamily = NotoDevanagari,
                            fontSize = (15.5f * scale).sp,
                            lineHeight = (23f * scale).sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = qNumPrefix + currentQ.qHi,
                            color = TextPrimary,
                            fontFamily = NotoDevanagari,
                            fontSize = (16.5f * scale).sp,
                            lineHeight = (24.5f * scale).sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.select_one_option),
                        color = TextMuted,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            // 4 Option Cards
            currentQ.options.forEachIndexed { idx, optionText ->
                val isSelected = selectedOption == idx
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${idx + 1}.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(24.dp)
                    )
                    Surface(
                        color = if (isSelected) NavySurfaceTint else CardSurface,
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(
                            width = if (isSelected) 1.8.dp else 1.dp,
                            color = if (isSelected) NavyPrimary else SlateBorder
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectOption(idx) }
                            .testTag("cbt_option_$idx")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onSelectOption(idx) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = NavyPrimary,
                                    unselectedColor = SlateGrey
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = optionText,
                                color = if (isSelected) NavyPrimary else TextPrimary,
                                fontFamily = NotoDevanagari,
                                fontSize = (15f * scale).sp,
                                lineHeight = (21f * scale).sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 5. Pinned 2-Row Bottom Action Dock
        Surface(
            color = CardSurface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onPrevious,
                        enabled = state.currentIndex > 0,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (state.currentIndex > 0) NavyBorder else SlateBorder),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("cbt_previous_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(stringResource(R.string.btn_previous), style = MaterialTheme.typography.labelLarge)
                    }
                    Button(
                        onClick = {
                            if (state.currentIndex == state.questions.lastIndex) {
                                showSubmitConfirm = true
                            } else {
                                onSaveAndNext()
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        modifier = Modifier
                            .weight(1.25f)
                            .testTag("cbt_save_next_button")
                    ) {
                        Text(
                            text = if (state.currentIndex == state.questions.lastIndex) {
                                "Save & Finish"
                            } else {
                                stringResource(R.string.btn_save_next)
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onSkip,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NavyPrimary),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("cbt_skip_button")
                    ) {
                        Text(
                            stringResource(R.string.btn_skip),
                            color = NavyPrimary,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedButton(
                        onClick = onClear,
                        enabled = selectedOption != null,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (selectedOption != null) NavyBorder else SlateBorder),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(0.9f)
                            .testTag("cbt_clear_button")
                    ) {
                        Text(
                            stringResource(R.string.btn_clear),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    OutlinedButton(
                        onClick = onToggleFlag,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (isFlagged) FlagPurple else SlateBorder),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isFlagged) FlagPurpleSoft else Color.Transparent,
                            contentColor = if (isFlagged) FlagPurple else TextPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("cbt_flag_button")
                    ) {
                        Icon(
                            imageVector = if (isFlagged) Icons.Filled.Flag else Icons.Outlined.Flag,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            if (isFlagged) "Flagged" else stringResource(R.string.btn_flag),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Button(
                        onClick = { showSubmitConfirm = true },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
                        modifier = Modifier
                            .weight(1.1f)
                            .testTag("cbt_end_test_button")
                    ) {
                        Text(
                            stringResource(R.string.btn_end_test),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    if (showPaletteSheet) {
        CbtQuestionPaletteSheet(
            state = state,
            onDismiss = { showPaletteSheet = false },
            onJumpToQuestion = onJumpToQuestion,
            onRequestSubmit = { showSubmitConfirm = true }
        )
    }
    if (showInstructions) {
        CbtInstructionsDialog(onDismiss = { showInstructions = false })
    }
    if (showSubmitConfirm) {
        CbtSubmitConfirmDialog(
            state = state,
            onDismiss = { showSubmitConfirm = false },
            onConfirmSubmit = onSubmitTest
        )
    }
    if (showExitConfirm) {
        CbtExitConfirmDialog(
            onDismiss = { showExitConfirm = false },
            onSubmitAndViewScore = onSubmitTest,
            onExitWithoutSaving = onExitWithoutSaving
        )
    }
}

@Composable
private fun SectionPillTab(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (selected) NavyPrimary else SoftInputBg,
        shape = RoundedCornerShape(12.dp),
        border = if (selected) null else BorderStroke(1.dp, SlateBorder),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else TextSecondary,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
        )
    }
}
