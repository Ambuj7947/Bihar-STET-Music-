package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.ExamUnit
import com.example.ui.theme.ActiveOutlineBlue
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.FlagPurple
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateGrey
import com.example.ui.theme.SoftInputBg
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.ActiveCbtState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun CbtQuestionPaletteSheet(
    state: ActiveCbtState,
    onDismiss: () -> Unit,
    onJumpToQuestion: (Int) -> Unit,
    onRequestSubmit: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var paletteSectionTab by remember(state.currentIndex) {
        mutableIntStateOf(if (state.currentQuestion.unit == ExamUnit.UNIT_1_MUSIC) 0 else 1)
    }
    val answeredOnlyCount = state.selectedAnswers.keys.count { it !in state.flaggedIndices }
    val ansAndFlaggedCount = state.selectedAnswers.keys.count { it in state.flaggedIndices }
    val flaggedOnlyCount = state.flaggedIndices.count { it !in state.selectedAnswers.keys }
    val skippedCount = state.visitedIndices.count { it !in state.selectedAnswers.keys && it !in state.flaggedIndices }
    val notVisitedCount = state.totalCount - state.visitedIndices.size

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CardSurface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Question Palette (${state.totalCount} Qs)",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.palette_subtitle),
                        color = TextMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close Palette", tint = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))

            // Legend Summary Card
            Surface(
                color = SoftInputBg,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PaletteLegendDot(EmeraldGreen, "$answeredOnlyCount Answered")
                        PaletteLegendDot(CrimsonRed, "$skippedCount Skipped")
                        PaletteLegendDot(FlagPurple, "$flaggedOnlyCount Flagged")
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        PaletteLegendDot(FlagPurple, "$ansAndFlaggedCount Ans & Flagged", hasInnerDot = true)
                        PaletteLegendDot(SlateGrey, "$notVisitedCount Not Visited")
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            val u1Range = state.unit1Indices
            val u2Range = state.unit2Indices
            if (u1Range != null && u2Range != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PaletteUnitSwitch(
                        text = "Unit I: Music\n(${u1Range.first + 1}-${u1Range.last + 1})",
                        selected = paletteSectionTab == 0,
                        onClick = { paletteSectionTab = 0 },
                        modifier = Modifier.weight(1f)
                    )
                    PaletteUnitSwitch(
                        text = "Unit II: General\n(${u2Range.first + 1}-${u2Range.last + 1})",
                        selected = paletteSectionTab == 1,
                        onClick = { paletteSectionTab = 1 },
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            val displayIndices = when {
                u1Range != null && u2Range != null && paletteSectionTab == 0 -> u1Range.toList()
                u1Range != null && u2Range != null && paletteSectionTab == 1 -> u2Range.toList()
                else -> state.questions.indices.toList()
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
            ) {
                items(displayIndices, key = { it }) { idx ->
                    val isAns = idx in state.selectedAnswers
                    val isFlag = idx in state.flaggedIndices
                    val isVisited = idx in state.visitedIndices
                    val isCurrent = idx == state.currentIndex

                    val (bg, fg) = when {
                        isAns && isFlag -> FlagPurple to Color.White
                        isAns -> EmeraldGreen to Color.White
                        isFlag -> FlagPurple to Color.White
                        isVisited -> CrimsonRed to Color.White
                        else -> SoftInputBg to TextPrimary
                    }

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(bg)
                            .border(
                                width = if (isCurrent) 2.5.dp else 1.dp,
                                color = if (isCurrent) ActiveOutlineBlue else SlateBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                onJumpToQuestion(idx)
                                onDismiss()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${idx + 1}",
                            color = fg,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (isAns && isFlag) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(4.dp)
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldGreen)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = {
                    onDismiss()
                    onRequestSubmit()
                },
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyPrimary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("palette_submit_button")
            ) {
                Text(
                    text = stringResource(R.string.palette_submit_btn),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
internal fun CbtInstructionsDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = {
            Text(
                text = "Bihar STET Paper 1 Music • Instructions",
                color = NavyPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Conducting Body: Bihar School Examination Board (BSEB)",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• Total Duration: 150 Minutes (2 Hours 30 Minutes) in CBT Mode.",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• Marking Scheme: +1 mark per correct answer. NO Negative Marking (0.00).",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• Official Pattern:\n  - Unit I — Music (Specific Subject): 100 Qs | 100 Marks\n  - Unit II — Teaching & General Skills: 50 Qs | 50 Marks (Art of Teaching 30, GK 5, EVS 5, Math Aptitude 5, Logical Reasoning 5)",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    "• Question Source: All questions in this mock are drawn exclusively from your 460 attached verified Music, GK & Reasoning questions.",
                    color = NavyPrimary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Got It")
            }
        }
    )
}

@Composable
internal fun CbtSubmitConfirmDialog(
    state: ActiveCbtState,
    onDismiss: () -> Unit,
    onConfirmSubmit: () -> Unit
) {
    val unanswered = state.totalCount - state.answeredCount
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = {
            Text(
                text = "Submit CBT Test?",
                color = NavyPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("Answered: ${state.answeredCount} / ${state.totalCount}", color = EmeraldGreen, fontWeight = FontWeight.Bold)
                Text("Unanswered / Skipped: $unanswered", color = CrimsonRed, fontWeight = FontWeight.Medium)
                Text("Flagged for Review: ${state.flaggedIndices.size}", color = FlagPurple, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tip: There is NO negative marking in Bihar STET! You can attempt all questions before submitting.",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirmSubmit()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                modifier = Modifier.testTag("confirm_submit_test_button")
            ) {
                Text("Submit Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Resume Test")
            }
        }
    )
}

@Composable
internal fun CbtExitConfirmDialog(
    onDismiss: () -> Unit,
    onSubmitAndViewScore: () -> Unit,
    onExitWithoutSaving: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardSurface,
        title = { Text("Leave Active Test?", fontWeight = FontWeight.Bold, color = NavyPrimary) },
        text = { Text("You can either submit your current answers to generate a scorecard or exit without saving.") },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onSubmitAndViewScore()
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Submit & View Score")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                onDismiss()
                onExitWithoutSaving()
            }) {
                Text("Exit", color = CrimsonRed)
            }
        }
    )
}

@Composable
private fun PaletteLegendDot(
    color: Color,
    label: String,
    hasInnerDot: Boolean = false
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            if (hasInnerDot) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(EmeraldGreen)
                )
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = TextPrimary,
            style = MaterialTheme.typography.labelMedium
        )
    }
}

@Composable
private fun PaletteUnitSwitch(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (selected) NavyPrimary else SoftInputBg,
        shape = RoundedCornerShape(12.dp),
        border = if (selected) null else BorderStroke(1.dp, SlateBorder),
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = if (selected) Color.White else TextPrimary,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}
