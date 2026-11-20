package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Question
import com.example.data.SectionInfo
import com.example.data.local.QuestionProgressEntity
import com.example.data.local.TestAttemptEntity
import com.example.ui.components.formatDurationReadable
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonSoft
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySurfaceTint
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SoftInputBg
import com.example.ui.theme.SprintAmber
import com.example.ui.theme.SprintAmberDark
import com.example.ui.theme.SprintAmberSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrackLight
import com.example.ui.theme.TrackNeutral
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AnalyticsScreen(
    allQuestions: List<Question>,
    sections: List<SectionInfo>,
    attempts: List<TestAttemptEntity>,
    progressMap: Map<String, QuestionProgressEntity>,
    onTakeFullMock: () -> Unit,
    onOpenAttemptScorecard: (TestAttemptEntity) -> Unit,
    onPracticeSection: (String) -> Unit
) {
    val totalQuestions = allQuestions.size
    val practicedUnique = progressMap.values.count { it.timesAttempted > 0 }
    val masteredUnique = progressMap.values.count { it.timesAttempted > 0 && !it.isMistake }
    val totalAttemptedCount = progressMap.values.sumOf { it.timesAttempted }
    val totalCorrectCount = progressMap.values.sumOf { it.timesCorrect }
    val accuracyPct = if (totalAttemptedCount == 0) 0 else (totalCorrectCount * 100) / totalAttemptedCount

    val questionsBySection = remember(allQuestions) {
        allQuestions.groupBy { it.sectionId }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("analytics_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            PerformanceOverviewCard(
                accuracyPct = accuracyPct,
                practicedUnique = practicedUnique,
                totalQuestions = totalQuestions,
                masteredUnique = masteredUnique,
                totalTests = attempts.size
            )
        }

        item {
            UnitWiseStrengthCard(
                sections = sections,
                questionsBySection = questionsBySection,
                progressMap = progressMap,
                onPracticeSection = onPracticeSection
            )
        }

        item {
            Text(
                text = "Recent Test Attempts History",
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (attempts.isEmpty()) {
            item {
                EmptyHistoryCard(onTakeFullMock = onTakeFullMock)
            }
        } else {
            items(attempts, key = { it.attemptId }) { attempt ->
                AttemptHistoryCard(
                    attempt = attempt,
                    onClick = { onOpenAttemptScorecard(attempt) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun PerformanceOverviewCard(
    accuracyPct: Int,
    practicedUnique: Int,
    totalQuestions: Int,
    masteredUnique: Int,
    totalTests: Int
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(NavySurfaceTint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.BarChart,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Performance Overview",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Bihar STET Music Analytics Dashboard",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Box(
                    modifier = Modifier.size(116.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val sweepColor = when {
                        accuracyPct >= 75 -> EmeraldGreen
                        accuracyPct >= 50 -> SprintAmber
                        accuracyPct > 0 -> CrimsonRed
                        else -> SlateBorder
                    }
                    Canvas(modifier = Modifier.fillMaxSize().padding(6.dp)) {
                        val strokeWidth = 11.dp.toPx()
                        val arcSize = Size(size.width - strokeWidth, size.height - strokeWidth)
                        val topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f)

                        drawArc(
                            color = TrackNeutral,
                            startAngle = 135f,
                            sweepAngle = 270f,
                            useCenter = false,
                            topLeft = topLeft,
                            size = arcSize,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                        if (accuracyPct > 0) {
                            drawArc(
                                color = sweepColor,
                                startAngle = 135f,
                                sweepAngle = 270f * (accuracyPct.coerceIn(0, 100) / 100f),
                                useCenter = false,
                                topLeft = topLeft,
                                size = arcSize,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$accuracyPct%",
                            color = TextPrimary,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Accuracy",
                            color = TextMuted,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OverviewStatPill(
                        label = "Total Practiced",
                        value = "$practicedUnique / $totalQuestions Qs",
                        valueColor = NavyPrimary
                    )
                    OverviewStatPill(
                        label = "Mastered Qs",
                        value = "$masteredUnique / $practicedUnique",
                        valueColor = EmeraldGreen
                    )
                    OverviewStatPill(
                        label = "Total Tests Taken",
                        value = "$totalTests ${if (totalTests == 1) "Test" else "Tests"}",
                        valueColor = SprintAmberDark
                    )
                }
            }
        }
    }
}

@Composable
private fun OverviewStatPill(
    label: String,
    value: String,
    valueColor: Color
) {
    Surface(
        color = SoftInputBg,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                color = TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                color = valueColor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun UnitWiseStrengthCard(
    sections: List<SectionInfo>,
    questionsBySection: Map<String, List<Question>>,
    progressMap: Map<String, QuestionProgressEntity>,
    onPracticeSection: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Unit-wise Strength & Accuracy",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${sections.size} Syllabus\nSections",
                    color = TextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            sections.forEachIndexed { idx, section ->
                val secQuestions = questionsBySection[section.id].orEmpty()
                val totalSecAttempts = secQuestions.sumOf { progressMap[it.id]?.timesAttempted ?: 0 }
                val totalSecCorrect = secQuestions.sumOf { progressMap[it.id]?.timesCorrect ?: 0 }
                val uniquePracticed = secQuestions.count { (progressMap[it.id]?.timesAttempted ?: 0) > 0 }
                val uniqueMastered = secQuestions.count {
                    val p = progressMap[it.id]
                    p != null && p.timesAttempted > 0 && !p.isMistake
                }
                val pct = if (totalSecAttempts == 0) 0 else (totalSecCorrect * 100) / totalSecAttempts

                val (badgeText, badgeBg, badgeColor) = when {
                    totalSecAttempts == 0 -> Triple("Not Started", SoftInputBg, TextMuted)
                    pct >= 75 -> Triple("Strong • $pct%", EmeraldSoft, EmeraldGreen)
                    pct >= 50 -> Triple("Good • $pct%", SprintAmberSoft, SprintAmberDark)
                    else -> Triple("Needs Work • $pct%", CrimsonSoft, CrimsonRed)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onPracticeSection(section.id) }
                        .padding(vertical = 8.dp)
                        .testTag("analytics_section_${section.id}")
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(section.accentHex))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = section.shortTitle,
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "$uniqueMastered/$uniquePracticed right",
                            color = TextMuted,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = badgeBg,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = badgeText,
                                color = badgeColor,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { if (totalSecAttempts == 0) 0f else (totalSecCorrect.toFloat() / totalSecAttempts.toFloat()).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(section.accentHex),
                        trackColor = TrackLight,
                        strokeCap = StrokeCap.Round
                    )
                }

                if (idx < sections.lastIndex) {
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryCard(onTakeFullMock: () -> Unit) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(NavySurfaceTint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(30.dp)
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "No Mock Tests Attempted Yet",
                color = TextPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Attempt a full 150-Q CBT mock test or sectional drill to unlock detailed scorecards and unit accuracy.",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onTakeFullMock,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyPrimary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                modifier = Modifier.testTag("analytics_take_full_mock_button")
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Take Full Mock Test",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun AttemptHistoryCard(
    attempt: TestAttemptEntity,
    onClick: () -> Unit
) {
    val dateLabel = remember(attempt.timestamp) {
        SimpleDateFormat("dd MMM • hh:mm a", Locale.getDefault()).format(Date(attempt.timestamp))
    }
    val pct = if (attempt.totalQuestions == 0) 0 else (attempt.correctCount * 100) / attempt.totalQuestions
    val badgeColor = when {
        pct >= 70 -> EmeraldGreen
        pct >= 50 -> SprintAmberDark
        else -> CrimsonRed
    }
    val badgeBg = when {
        pct >= 70 -> EmeraldSoft
        pct >= 50 -> SprintAmberSoft
        else -> CrimsonSoft
    }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("attempt_history_${attempt.attemptId}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = badgeBg,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(52.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "$pct%",
                        color = badgeColor,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = attempt.testTitle,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Score: ${attempt.correctCount}/${attempt.totalQuestions} • Wrong: ${attempt.incorrectCount} • ${formatDurationReadable(attempt.durationSeconds)}",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateLabel,
                    color = TextMuted,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Open Scorecard",
                tint = TextMuted
            )
        }
    }
}
