package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Question
import com.example.data.local.QuestionProgressEntity
import com.example.ui.theme.CardSurface
import com.example.ui.theme.CrimsonRed
import com.example.ui.theme.CrimsonSoft
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySurfaceTint
import com.example.ui.theme.NotoDevanagari
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SoftInputBg
import com.example.ui.theme.SprintAmber
import com.example.ui.theme.SprintAmberSoft
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MistakesScreen(
    initialSubTab: Int,
    allQuestions: List<Question>,
    progressMap: Map<String, QuestionProgressEntity>,
    onToggleBookmark: (String) -> Unit,
    onClearMistake: (String) -> Unit,
    onPracticeQuestions: (title: String, ids: List<String>) -> Unit
) {
    var selectedSubTab by rememberSaveable(initialSubTab) { mutableIntStateOf(initialSubTab) }

    val mistakeQuestions = allQuestions.filter { progressMap[it.id]?.isMistake == true }
    val bookmarkedQuestions = allQuestions.filter { progressMap[it.id]?.isBookmarked == true }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = CardSurface,
            contentColor = NavyPrimary,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    height = 3.dp,
                    color = NavyPrimary
                )
            }
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                modifier = Modifier.testTag("tab_mistakes"),
                text = {
                    Text(
                        text = "Mistakes (${mistakeQuestions.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedSubTab == 0) NavyPrimary else TextMuted
                    )
                }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                modifier = Modifier.testTag("tab_bookmarks"),
                text = {
                    Text(
                        text = "Bookmarks (${bookmarkedQuestions.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedSubTab == 1) NavyPrimary else TextMuted
                    )
                }
            )
        }

        val activeList = if (selectedSubTab == 0) mistakeQuestions else bookmarkedQuestions

        if (activeList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                EmptyNotebookCard(isMistakesTab = selectedSubTab == 0)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("mistakes_bookmarks_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Button(
                        onClick = {
                            val title = if (selectedSubTab == 0) {
                                "Mistake Revision Drill (${activeList.size} Qs)"
                            } else {
                                "Bookmarked Questions Drill (${activeList.size} Qs)"
                            }
                            onPracticeQuestions(title, activeList.map { it.id })
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 13.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("practice_notebook_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (selectedSubTab == 0) {
                                "Practice All ${activeList.size} Mistakes in CBT Mode"
                            } else {
                                "Practice All ${activeList.size} Bookmarked Qs"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                itemsIndexed(activeList, key = { _, q -> q.id }) { idx, q ->
                    val prog = progressMap[q.id]
                    NotebookQuestionCard(
                        index = idx + 1,
                        question = q,
                        progress = prog,
                        isMistakesTab = selectedSubTab == 0,
                        onToggleBookmark = { onToggleBookmark(q.id) },
                        onMarkMastered = { onClearMistake(q.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun EmptyNotebookCard(isMistakesTab: Boolean) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(if (isMistakesTab) EmeraldGreen else SprintAmberSoft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isMistakesTab) Icons.Filled.Check else Icons.Filled.Bookmark,
                    contentDescription = null,
                    tint = if (isMistakesTab) Color.White else SprintAmber,
                    modifier = Modifier.size(34.dp)
                )
            }
            Spacer(modifier = Modifier.height(18.dp))
            Text(
                text = if (isMistakesTab) "Mistake Notebook is Empty!" else "No Bookmarks Saved Yet",
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (isMistakesTab) {
                    "Great job! You haven't made any mistakes yet or all mistakes were corrected."
                } else {
                    "Tap the bookmark icon during any mock test, scorecard, or question bank review to save important questions here."
                },
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun NotebookQuestionCard(
    index: Int,
    question: Question,
    progress: QuestionProgressEntity?,
    isMistakesTab: Boolean,
    onToggleBookmark: () -> Unit,
    onMarkMastered: () -> Unit
) {
    val isBookmarked = progress?.isBookmarked == true
    val lastWrongIdx = progress?.lastSelectedIndex ?: -1
    val letters = listOf("A", "B", "C", "D")

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, SlateBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = NavySurfaceTint,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "#$index • ${question.sectionShort}",
                        color = NavyPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("notebook_bookmark_${question.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Toggle Bookmark",
                        tint = if (isBookmarked) SprintAmber else TextMuted
                    )
                }
            }

            if (question.qEn.isNotBlank()) {
                Text(
                    text = question.qEn,
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 21.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = question.qHi,
                color = TextPrimary,
                fontFamily = NotoDevanagari,
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                lineHeight = 23.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            question.options.forEachIndexed { optIdx, optText ->
                val isCorrect = optIdx == question.answerIndex
                val isLastWrong = isMistakesTab && optIdx == lastWrongIdx && !isCorrect
                val bg = when {
                    isCorrect -> EmeraldSoft
                    isLastWrong -> CrimsonSoft
                    else -> SoftInputBg
                }
                val border = when {
                    isCorrect -> EmeraldGreen
                    isLastWrong -> CrimsonRed
                    else -> SlateBorder
                }
                Surface(
                    color = bg,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${letters.getOrElse(optIdx) { "?" }}.",
                            color = if (isCorrect) EmeraldGreen else if (isLastWrong) CrimsonRed else NavyPrimary,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = optText,
                            color = TextPrimary,
                            fontFamily = NotoDevanagari,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (isCorrect) {
                            Text(
                                text = "Correct",
                                color = EmeraldGreen,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        } else if (isLastWrong) {
                            Text(
                                text = "Your Ans",
                                color = CrimsonRed,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
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
                        text = "व्याख्या: ${question.explanation}",
                        color = TextPrimary,
                        fontFamily = NotoDevanagari,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }

            if (isMistakesTab) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onMarkMastered,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, EmeraldGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mark_mastered_${question.id}")
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mark as Corrected / Mastered",
                        color = EmeraldGreen,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
