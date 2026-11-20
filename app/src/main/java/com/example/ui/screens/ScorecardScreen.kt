package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Question
import com.example.data.local.QuestionProgressEntity
import com.example.data.local.SubmissionReason
import com.example.data.local.TestAttemptEntity
import com.example.ui.components.formatDurationReadable
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.AppBackground
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonDarkText
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonSoft
import com.example.ui.theme.EmeraldDarkText
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySurfaceTint
import com.example.ui.theme.NotoDevanagari
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SoftInputBg
import com.example.ui.theme.SprintAmberDark
import com.example.ui.theme.SprintAmberSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun ScorecardScreen(
    attempt: TestAttemptEntity,
    questions: List<Question>,
    answersMap: Map<String, Int>,
    progressMap: Map<String, QuestionProgressEntity>,
    onToggleBookmark: (String) -> Unit,
    onGoHome: () -> Unit,
    onReattempt: () -> Unit
) {
    BackHandler { onGoHome() }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var filterMode by remember { mutableStateOf("ALL") }

    val attemptedCount = attempt.correctCount + attempt.incorrectCount
    val accuracyPct = if (attemptedCount == 0) 0 else (attempt.correctCount * 100) / attemptedCount
    val scorePct = if (attempt.totalQuestions == 0) 0 else (attempt.correctCount * 100) / attempt.totalQuestions

    val performanceLabel = when {
        scorePct >= 75 -> "Exam Ready! Outstanding Score"
        scorePct >= 50 -> "Qualifying Zone • Keep Pushing"
        else -> "Needs Revision & Practice"
    }
    val ringColor = when {
        scorePct >= 75 -> EmeraldGreen
        scorePct >= 50 -> AmberAccent
        else -> CrimsonRed
    }

    val filteredPairs = remember(questions, answersMap, filterMode) {
        questions.mapIndexed { idx, q -> idx to q }.filter { (_, q) ->
            val sel = answersMap[q.id]
            when (filterMode) {
                "CORRECT" -> sel != null && sel == q.answerIndex
                "INCORRECT" -> sel != null && sel != q.answerIndex
                "SKIPPED" -> sel == null
                else -> true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Top Navy Header
        Surface(color = NavyPrimary, modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Text(
                    text = stringResource(R.string.scorecard_title),
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${attempt.testTitle} • ${formatDurationReadable(attempt.durationSeconds)}",
                    color = Color.White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        val submissionReason = remember(attempt.submissionReason) {
            SubmissionReason.fromValue(attempt.submissionReason)
        }
        val answerKeyHeaderIndex = if (submissionReason != SubmissionReason.MANUAL) 2 else 1

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (submissionReason != SubmissionReason.MANUAL) {
                item {
                    AutoSubmittedNoticeBanner(reason = submissionReason)
                }
            }

            item {
                ScorecardSummaryCard(
                    attempt = attempt,
                    accuracyPct = accuracyPct,
                    scorePct = scorePct,
                    performanceLabel = performanceLabel,
                    ringColor = ringColor,
                    onJumpToExplanations = {
                        coroutineScope.launch { listState.animateScrollToItem(answerKeyHeaderIndex) }
                    }
                )
            }

            // Answer Key Header & Filter Chips
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Answer Key & Explanations (सभी उत्तर और व्याख्या)",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Detailed explanation of every answer to help you revise your mistakes.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val filters = listOf(
                            "ALL" to "All (${attempt.totalQuestions})",
                            "INCORRECT" to "Wrong (${attempt.incorrectCount})",
                            "CORRECT" to "Correct (${attempt.correctCount})",
                            "SKIPPED" to "Skipped (${attempt.skippedCount})"
                        )
                        items(filters) { (key, label) ->
                            FilterChip(
                                selected = filterMode == key,
                                onClick = { filterMode = key },
                                modifier = Modifier.testTag("scorecard_filter_${key.lowercase()}"),
                                label = { Text(label, style = MaterialTheme.typography.labelMedium) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = NavyPrimary,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Answer Key Items
            itemsIndexed(filteredPairs, key = { _, pair -> pair.second.id }) { _, (origIdx, q) ->
                ScorecardAnswerItemCard(
                    questionNumber = origIdx + 1,
                    question = q,
                    userChoice = answersMap[q.id],
                    isBookmarked = progressMap[q.id]?.isBookmarked == true,
                    onToggleBookmark = { onToggleBookmark(q.id) }
                )
            }
        }

        // Sticky Bottom Bar: Home | Re-attempt Test
        Surface(
            color = CardSurface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onGoHome,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.2.dp, NavyPrimary),
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier
                        .weight(0.9f)
                        .testTag("scorecard_home_button")
                ) {
                    Icon(Icons.Filled.Home, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Home", color = NavyPrimary, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onReattempt,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary, contentColor = Color.White),
                    contentPadding = PaddingValues(vertical = 13.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("scorecard_reattempt_button")
                ) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Re-attempt Test", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun AutoSubmittedNoticeBanner(reason: SubmissionReason) {
    val isViolation = reason == SubmissionReason.APP_SWITCH_OR_EXIT ||
        reason == SubmissionReason.MULTI_WINDOW_VIOLATION
    val bgColor = if (isViolation) CrimsonSoft else SprintAmberSoft
    val borderColor = if (isViolation) CrimsonRed else SprintAmberDark
    val titleColor = if (isViolation) CrimsonDarkText else SprintAmberDark
    val icon = if (isViolation) Icons.Filled.WarningAmber else Icons.Filled.Timer

    val (titleRes, descRes) = when (reason) {
        SubmissionReason.APP_SWITCH_OR_EXIT ->
            R.string.auto_submit_app_leave_title to R.string.auto_submit_app_leave_desc
        SubmissionReason.MULTI_WINDOW_VIOLATION ->
            R.string.auto_submit_multi_window_title to R.string.auto_submit_multi_window_desc
        SubmissionReason.TIME_UP ->
            R.string.auto_submit_time_up_title to R.string.auto_submit_time_up_desc
        SubmissionReason.MANUAL -> return
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.2.dp, borderColor.copy(alpha = 0.5f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("scorecard_auto_submit_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = borderColor,
                modifier = Modifier
                    .padding(top = 2.dp)
                    .size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = stringResource(titleRes),
                    color = titleColor,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = stringResource(descRes),
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ScorecardSummaryCard(
    attempt: TestAttemptEntity,
    accuracyPct: Int,
    scorePct: Int,
    performanceLabel: String,
    ringColor: Color,
    onJumpToExplanations: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.size(136.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                    drawCircle(color = ringColor.copy(alpha = 0.16f), style = stroke)
                    if (attempt.correctCount > 0 && attempt.totalQuestions > 0) {
                        val sweep = (attempt.correctCount.toFloat() / attempt.totalQuestions.toFloat()) * 360f
                        drawArc(
                            color = ringColor,
                            startAngle = -90f,
                            sweepAngle = sweep.coerceAtLeast(6f),
                            useCenter = false,
                            style = stroke
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "%.2f".format(attempt.correctCount.toFloat()),
                        color = ringColor,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "/ ${attempt.totalQuestions}",
                        color = TextMuted,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = performanceLabel,
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Accuracy: $accuracyPct% • Score: $scorePct%",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ScorePillBox("${attempt.correctCount}", "Correct", EmeraldGreen, EmeraldSoft, Modifier.weight(1f))
                ScorePillBox("${attempt.incorrectCount}", "Incorrect", CrimsonRed, CrimsonSoft, Modifier.weight(1f))
                ScorePillBox("${attempt.skippedCount}", "Skipped", TextSecondary, SoftInputBg, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                color = SoftInputBg,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, SlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Section-wise Performance",
                        color = NavyPrimary,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    if (attempt.unit1Total > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Unit I: Music (${attempt.unit1Total})",
                                color = TextPrimary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${attempt.unit1Correct}/${attempt.unit1Total} (Wrong: ${attempt.unit1Wrong})",
                                color = EmeraldGreen,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    if (attempt.unit2Total > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Unit II: General Skills (${attempt.unit2Total})",
                                color = TextPrimary,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${attempt.unit2Correct}/${attempt.unit2Total} (Wrong: ${attempt.unit2Wrong})",
                                color = NavyPrimary,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            OutlinedButton(
                onClick = onJumpToExplanations,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SlateBorder),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("view_all_explanations_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.MenuBook,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View All Answers & Explanations (${attempt.totalQuestions})",
                    color = NavyPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ScorecardAnswerItemCard(
    questionNumber: Int,
    question: Question,
    userChoice: Int?,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit
) {
    val isCorrect = userChoice != null && userChoice == question.answerIndex
    val isSkipped = userChoice == null
    val statusText = when {
        isSkipped -> "Skipped"
        isCorrect -> "Correct (+1)"
        else -> "Incorrect"
    }
    val (statusBg, statusColor) = when {
        isSkipped -> SoftInputBg to TextSecondary
        isCorrect -> EmeraldSoft to EmeraldGreen
        else -> CrimsonSoft to CrimsonRed
    }
    val optLabels = listOf("A", "B", "C", "D")

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(color = statusBg, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "Q$questionNumber • $statusText",
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("scorecard_bookmark_${question.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Toggle Bookmark",
                        tint = if (isBookmarked) AmberAccent else TextMuted,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            if (question.qEn.isNotBlank()) {
                Text(
                    text = question.qEn,
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(3.dp))
            }
            Text(
                text = question.qHi,
                color = TextPrimary,
                fontFamily = NotoDevanagari,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(10.dp))
            question.options.forEachIndexed { optIdx, text ->
                val isAns = optIdx == question.answerIndex
                val isUserWrong = userChoice == optIdx && !isAns
                val bg = when {
                    isAns -> EmeraldSoft
                    isUserWrong -> CrimsonSoft
                    else -> SoftInputBg
                }
                val borderCol = when {
                    isAns -> EmeraldGreen
                    isUserWrong -> CrimsonRed
                    else -> SlateBorder
                }
                Surface(
                    color = bg,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, borderCol),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Text(
                        text = "${optLabels.getOrElse(optIdx) { "" }}. $text" +
                            when {
                                isAns -> "  ✓"
                                isUserWrong -> "  ✗ (Your choice)"
                                else -> ""
                            },
                        color = when {
                            isAns -> EmeraldDarkText
                            isUserWrong -> CrimsonDarkText
                            else -> TextPrimary
                        },
                        fontFamily = NotoDevanagari,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (isAns || isUserWrong) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            }

            if (question.explanation.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    color = NavySurfaceTint,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, NavyBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "व्याख्या / Explanation: ${question.explanation}",
                        color = NavyPrimary,
                        fontFamily = NotoDevanagari,
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ScorePillBox(
    value: String,
    label: String,
    valueColor: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                color = valueColor,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                color = valueColor,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
