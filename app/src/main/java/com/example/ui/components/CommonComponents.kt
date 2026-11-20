package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockTestMeta
import com.example.ui.theme.CardSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavyPillBg
import com.example.ui.theme.NavySurfaceTint
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SprintAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.MainTab

@Composable
fun ExamTopHeaderBar(
    title: String,
    subtitle: String,
    totalQuestionCount: Int,
    onQuestionBankClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = NavyPrimary,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyPillBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.MusicNote,
                    contentDescription = "STET Music Prep",
                    tint = SprintAmber,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.80f),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
                color = NavyPillBg,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .clickable(onClick = onQuestionBankClick)
                    .testTag("header_question_bank_badge")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.MenuBook,
                        contentDescription = "Total Questions",
                        tint = SprintAmber,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "$totalQuestionCount Qs",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ExamBottomNavBar(
    selectedTab: MainTab,
    onSelectTab: (MainTab) -> Unit
) {
    NavigationBar(
        containerColor = CardSurface,
        tonalElevation = 4.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        val items = listOf(
            Triple(MainTab.HOME, androidx.compose.ui.res.stringResource(com.example.R.string.nav_home), Icons.Filled.Home to Icons.Outlined.Home),
            Triple(MainTab.TESTS, androidx.compose.ui.res.stringResource(com.example.R.string.nav_tests), Icons.Filled.Quiz to Icons.Outlined.Quiz),
            Triple(MainTab.ANALYTICS, androidx.compose.ui.res.stringResource(com.example.R.string.nav_analytics), Icons.Filled.BarChart to Icons.Outlined.BarChart),
            Triple(MainTab.MISTAKES, androidx.compose.ui.res.stringResource(com.example.R.string.nav_mistakes), Icons.Filled.Bookmark to Icons.Outlined.BookmarkBorder),
            Triple(MainTab.NOTES, androidx.compose.ui.res.stringResource(com.example.R.string.nav_notes), Icons.Filled.MenuBook to Icons.Outlined.MenuBook)
        )
        items.forEach { (tab, label, icons) ->
            val selected = selectedTab == tab
            NavigationBarItem(
                selected = selected,
                onClick = { onSelectTab(tab) },
                modifier = Modifier.testTag("nav_${label.lowercase()}"),
                icon = {
                    Icon(
                        imageVector = if (selected) icons.first else icons.second,
                        contentDescription = label
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavyPrimary,
                    selectedTextColor = NavyPrimary,
                    indicatorColor = NavySurfaceTint,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}

@Composable
fun ExamPatternInfoCard(modifier: Modifier = Modifier) {
    Surface(
        color = NavySurfaceTint,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, NavyBorder),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Exam Pattern: Unit I (100 Music) + Unit II (50 General)",
                color = NavyPrimary,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "BSEB Bihar STET Paper 1 Music • 150 Qs | 150 Marks | 150 Mins | No Negative Marking\nUnit I: Q1–100 Music (100 Marks) • Unit II: Q101–150 Teaching & General Skills (GK/Bihar GK, Science/EVS & Reasoning from your 460-Q bank)",
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
fun MockTestCard(
    meta: MockTestMeta,
    bestScore: Int?,
    onStartClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completed = bestScore != null
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = NavyPrimary,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = meta.badge,
                        color = Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = EmeraldSoft,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (meta.isFullLength) "CBT WINDOW" else "SECTIONAL",
                        color = EmeraldGreen,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (completed) Icons.Filled.CheckBox else Icons.Filled.CheckBoxOutlineBlank,
                        contentDescription = if (completed) "Completed" else "Pending",
                        tint = if (completed) EmeraldGreen else TextMuted,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (completed) "$bestScore/${meta.totalQuestions}" else "Pending",
                        color = if (completed) EmeraldGreen else TextMuted,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (completed) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = meta.title,
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = meta.subtitle,
                color = TextSecondary,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${meta.totalQuestions} Qs",
                    color = NavyPrimary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "•", color = TextMuted)
                Text(
                    text = "${meta.totalQuestions} Marks",
                    color = TextSecondary,
                    style = MaterialTheme.typography.labelLarge
                )
                Text(text = "•", color = TextMuted)
                Icon(
                    imageVector = Icons.Filled.Timer,
                    contentDescription = "Duration",
                    tint = EmeraldGreen,
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "${meta.durationMinutes} Mins",
                    color = EmeraldGreen,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Button(
                onClick = onStartClick,
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NavyPrimary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 13.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("start_mock_${meta.id}")
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (meta.isFullLength) {
                        val numStr = meta.number.toString().padStart(2, '0')
                        if (completed) "Re-attempt CBT Mock #$numStr" else "Start CBT Mock #$numStr"
                    } else {
                        if (completed) "Re-attempt Practice" else "Start Sectional Drill"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

fun formatDurationHms(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val hrs = safe / 3600
    val mins = (safe % 3600) / 60
    val secs = safe % 60
    return "%02d:%02d:%02d".format(hrs, mins, secs)
}

fun formatDurationMs(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val mins = safe / 60
    val secs = safe % 60
    return "%02d:%02d".format(mins, secs)
}

fun formatDurationReadable(totalSeconds: Int): String {
    val safe = totalSeconds.coerceAtLeast(0)
    val mins = safe / 60
    val secs = safe % 60
    return "%02dm %02ds".format(mins, secs)
}
