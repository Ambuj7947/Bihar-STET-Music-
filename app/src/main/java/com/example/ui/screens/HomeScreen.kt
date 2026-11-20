package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.MockTestMeta
import com.example.data.local.QuestionProgressEntity
import com.example.data.local.TestAttemptEntity
import com.example.ui.components.ExamPatternInfoCard
import com.example.ui.components.MockTestCard
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonSoft
import com.example.ui.theme.EmeraldBorder
import com.example.ui.theme.EmeraldDarkText
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.EmeraldSubText
import com.example.ui.theme.FlagPurple
import com.example.ui.theme.FlagPurpleSoft
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyDeep
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySurfaceTint
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SprintAmber
import com.example.ui.theme.SprintAmberSoft
import com.example.ui.theme.TealBadgeBg
import com.example.ui.theme.TealBadgeBorder
import com.example.ui.theme.TealBadgeText
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun HomeScreen(
    totalQuestions: Int,
    musicCount: Int,
    gkCount: Int,
    reasoningCount: Int,
    fullMocks: List<MockTestMeta>,
    attempts: List<TestAttemptEntity>,
    progressMap: Map<String, QuestionProgressEntity>,
    onStartNextMock: () -> Unit,
    onStartMock: (MockTestMeta) -> Unit,
    onOpenQuestionBank: () -> Unit,
    onOpenMistakes: () -> Unit,
    onOpenBookmarks: () -> Unit,
    onOpenSectionalTests: () -> Unit,
    onOpenNotes: () -> Unit
) {
    val completedFullMockIds = attempts.filter { it.isFullLength }.map { it.testId }.toSet()
    val completedCount = completedFullMockIds.size
    val totalMocks = fullMocks.size.coerceAtLeast(1)
    val completionPct = (completedCount * 100) / totalMocks

    val totalAttemptedQs = progressMap.values.sumOf { it.timesAttempted }
    val totalCorrectQs = progressMap.values.sumOf { it.timesCorrect }
    val accuracyPct = if (totalAttemptedQs == 0) 0 else (totalCorrectQs * 100) / totalAttemptedQs

    val mistakeCount = progressMap.values.count { it.isMistake }
    val bookmarkCount = progressMap.values.count { it.isBookmarked }

    val bestScoreByTestId = attempts.groupBy { it.testId }
        .mapValues { (_, list) -> list.maxOf { it.correctCount } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Sprint Banner Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(NavyDark, NavyDeep, NavyLight)
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Surface(
                            color = SprintAmber,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            Text(
                                text = stringResource(R.string.hero_badge_sprint),
                                color = NavyDark,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.ExtraBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = TealBadgeBg.copy(alpha = 0.78f),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, TealBadgeBorder)
                        ) {
                            Text(
                                text = stringResource(R.string.hero_badge_no_neg),
                                color = TealBadgeText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                lineHeight = 14.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = stringResource(R.string.hero_title),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.hero_subtitle_hi),
                        color = Color.White.copy(alpha = 0.88f),
                        style = MaterialTheme.typography.bodySmall,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        HeroMiniStat("150 MCQs", "100 Music + 50", Modifier.weight(1f))
                        HeroMiniStat("150 Marks", "+1 / No Neg", Modifier.weight(1f))
                        HeroMiniStat("150 Mins", "Real CBT", Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Final Mocks Completed: $completedCount / $totalMocks",
                            color = Color.White.copy(alpha = 0.92f),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Text(
                            text = "$completionPct%",
                            color = SprintAmber,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { completedCount.toFloat() / totalMocks.toFloat() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = SprintAmber,
                        trackColor = Color.White.copy(alpha = 0.22f),
                        strokeCap = StrokeCap.Round
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onStartNextMock,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SprintAmber,
                                contentColor = NavyDark
                            ),
                            contentPadding = PaddingValues(vertical = 13.dp),
                            modifier = Modifier
                                .weight(1.25f)
                                .testTag("hero_start_mock_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(R.string.btn_start_mock),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                        OutlinedButton(
                            onClick = onOpenQuestionBank,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.45f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White.copy(alpha = 0.12f),
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(vertical = 13.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("hero_question_bank_button")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MenuBook,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$totalQuestions Qs",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 2. Three KPI Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSummaryCard(
                    label = "Final Mocks",
                    value = "$completedCount / $totalMocks",
                    subLabel = "CBT Completed",
                    valueColor = NavyPrimary,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    label = "Accuracy",
                    value = "$accuracyPct%",
                    subLabel = "Exam Precision",
                    valueColor = EmeraldGreen,
                    modifier = Modifier.weight(1f)
                )
                MetricSummaryCard(
                    label = "Question Bank",
                    value = "$totalQuestions",
                    subLabel = "All Qs Preserved",
                    valueColor = AmberAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 3. Quick Revision & Question Bank Section
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.section_quick_revision),
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionTile(
                        title = "Mistake",
                        subtitle = "$mistakeCount Weak Qs",
                        icon = Icons.Filled.ErrorOutline,
                        iconTint = CrimsonRed,
                        iconBg = CrimsonSoft,
                        onClick = onOpenMistakes,
                        tag = "quick_mistakes_tile",
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        title = "Saved",
                        subtitle = "$bookmarkCount Starred Qs",
                        icon = Icons.Filled.Bookmark,
                        iconTint = AmberAccent,
                        iconBg = SprintAmberSoft,
                        onClick = onOpenBookmarks,
                        tag = "quick_saved_tile",
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickActionTile(
                        title = "Sectional & Topic",
                        subtitle = "15 PDF Chapters",
                        icon = Icons.Filled.Quiz,
                        iconTint = NavyPrimary,
                        iconBg = NavySurfaceTint,
                        onClick = onOpenSectionalTests,
                        tag = "quick_sectional_tile",
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionTile(
                        title = "High-Yield",
                        subtitle = "Rapid Revision",
                        icon = Icons.Filled.MenuBook,
                        iconTint = FlagPurple,
                        iconBg = FlagPurpleSoft,
                        onClick = onOpenNotes,
                        tag = "quick_notes_tile",
                        modifier = Modifier.weight(1f)
                    )
                }

                // Complete Question Bank Green Highlight Card
                Surface(
                    color = EmeraldSoft,
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, EmeraldBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(onClick = onOpenQuestionBank)
                        .testTag("complete_question_bank_card")
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(EmeraldGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.MenuBook,
                                contentDescription = "Complete Question Bank",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Complete Question Bank ($totalQuestions Qs)",
                                color = EmeraldDarkText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Browse all $musicCount Classical Music + $gkCount GK/Bihar GK + $reasoningCount Reasoning questions anytime",
                                color = EmeraldSubText,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Open Question Bank",
                            tint = EmeraldGreen
                        )
                    }
                }
            }
        }

        // 4. 10 Final CBT Mock Tests Header + Official Pattern Box
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = stringResource(R.string.section_mock_series),
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = stringResource(R.string.section_mock_series_sub),
                    color = NavyPrimary,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                ExamPatternInfoCard()
            }
        }

        // 5. All 10 Full-Length Mock Test Cards
        items(fullMocks, key = { it.id }) { mock ->
            MockTestCard(
                meta = mock,
                bestScore = bestScoreByTestId[mock.id],
                onStartClick = { onStartMock(mock) }
            )
        }
    }
}

@Composable
private fun HeroMiniStat(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.White.copy(alpha = 0.12f),
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 10.dp)) {
            Text(
                text = title,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun MetricSummaryCard(
    label: String,
    value: String,
    subLabel: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                color = TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = valueColor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subLabel,
                color = TextMuted,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun QuickActionTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    onClick: () -> Unit,
    tag: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(tag)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    style = MaterialTheme.typography.labelSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
