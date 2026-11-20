package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ExamUnit
import com.example.data.Question
import com.example.data.SectionInfo
import com.example.data.local.QuestionProgressEntity
import com.example.ui.theme.AppBackground
import com.example.ui.theme.CardSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldSoft
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavyPillBg
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.NavySurfaceTint
import com.example.ui.theme.NotoDevanagari
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SoftInputBg
import com.example.ui.theme.SprintAmber
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun QuestionBankScreen(
    allQuestions: List<Question>,
    sections: List<SectionInfo>,
    initialSectionId: String?,
    progressMap: Map<String, QuestionProgressEntity>,
    onToggleBookmark: (String) -> Unit,
    onPracticeFiltered: (title: String, questionIds: List<String>) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }

    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable(initialSectionId) {
        mutableStateOf(initialSectionId ?: "ALL")
    }
    var showAnswers by rememberSaveable { mutableStateOf(true) }

    val filteredQuestions = remember(allQuestions, searchQuery, selectedFilter) {
        val byFilter = when (selectedFilter) {
            "ALL" -> allQuestions
            "UNIT_1" -> allQuestions.filter { it.unit == ExamUnit.UNIT_1_MUSIC }
            "GK_ALL" -> allQuestions.filter { it.sectionId.startsWith("gk_") }
            "REAS_ALL" -> allQuestions.filter { it.sectionId.startsWith("reas_") }
            else -> allQuestions.filter { it.sectionId == selectedFilter }
        }
        val query = searchQuery.trim()
        if (query.isEmpty()) {
            byFilter
        } else {
            byFilter.filter { q ->
                q.qHi.contains(query, ignoreCase = true) ||
                    q.qEn.contains(query, ignoreCase = true) ||
                    q.sectionTitle.contains(query, ignoreCase = true) ||
                    q.options.any { it.contains(query, ignoreCase = true) } ||
                    q.explanation.contains(query, ignoreCase = true)
            }
        }
    }

    val musicCount = remember(allQuestions) { allQuestions.count { it.unit == ExamUnit.UNIT_1_MUSIC } }
    val gkCount = remember(allQuestions) { allQuestions.count { it.sectionId.startsWith("gk_") } }
    val reasCount = remember(allQuestions) { allQuestions.count { it.sectionId.startsWith("reas_") } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
    ) {
        // Navy Top Bar
        Surface(color = NavyPrimary, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("question_bank_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Complete Question Bank",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${filteredQuestions.size} of ${allQuestions.size} Verified Qs",
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Surface(
                    color = NavyPillBg,
                    shape = RoundedCornerShape(12.dp),
                    onClick = { showAnswers = !showAnswers },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .testTag("toggle_show_answers")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (showAnswers) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle Answers",
                            tint = SprintAmber,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (showAnswers) "Ans ON" else "Ans OFF",
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Search Bar & Filter Chips
        Surface(color = CardSurface, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search राग, ताल, बिहार, संविधान, reasoning...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = TextMuted
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Clear Search",
                                    tint = TextMuted
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NavyPrimary,
                        unfocusedBorderColor = SlateBorder,
                        focusedContainerColor = SoftInputBg,
                        unfocusedContainerColor = SoftInputBg
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("question_bank_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val primaryChips = listOf(
                        "ALL" to "All (${allQuestions.size})",
                        "UNIT_1" to "Music ($musicCount)",
                        "GK_ALL" to "GK & Bihar ($gkCount)",
                        "REAS_ALL" to "Reasoning ($reasCount)"
                    )
                    items(primaryChips) { (key, label) ->
                        val selected = selectedFilter == key
                        FilterChip(
                            selected = selected,
                            onClick = { selectedFilter = key },
                            modifier = Modifier.testTag("bank_filter_$key"),
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = SoftInputBg,
                                labelColor = TextSecondary
                            )
                        )
                    }
                    items(sections, key = { it.id }) { sec ->
                        val selected = selectedFilter == sec.id
                        FilterChip(
                            selected = selected,
                            onClick = { selectedFilter = sec.id },
                            modifier = Modifier.testTag("bank_filter_${sec.id}"),
                            label = {
                                Text(
                                    text = "${sec.shortTitle} (${sec.questionCount})",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = NavyPrimary,
                                selectedLabelColor = Color.White,
                                containerColor = SoftInputBg,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }
            }
        }

        // Question list or empty search state
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .navigationBarsPadding()
                .testTag("question_bank_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredQuestions.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = CardSurface),
                        border = BorderStroke(1.dp, SlateBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Filled.SearchOff,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Matching Questions Found",
                                color = TextPrimary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try a different Hindi/English keyword or reset your filters to browse all ${allQuestions.size} questions.",
                                color = TextSecondary,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            OutlinedButton(
                                onClick = {
                                    searchQuery = ""
                                    selectedFilter = "ALL"
                                },
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, NavyPrimary),
                                modifier = Modifier.testTag("reset_bank_filters_button")
                            ) {
                                Text("Reset Filters", color = NavyPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                item {
                    val isLargeSet = filteredQuestions.size > 150
                    val practiceIds = if (isLargeSet) {
                        filteredQuestions.shuffled().take(50).map { it.id }
                    } else {
                        filteredQuestions.map { it.id }
                    }
                    val buttonLabel = if (isLargeSet) {
                        "Practice Quick 50-Q CBT Drill from Selection"
                    } else {
                        "Practice These ${filteredQuestions.size} Qs in CBT Mode"
                    }
                    Button(
                        onClick = {
                            val title = sections.firstOrNull { it.id == selectedFilter }?.title
                                ?: if (isLargeSet) "Quick 50-Q Practice Drill"
                                else "Filtered Practice (${filteredQuestions.size} Qs)"
                            onPracticeFiltered(title, practiceIds)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyPrimary,
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("practice_filtered_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = buttonLabel,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                itemsIndexed(filteredQuestions, key = { _, q -> q.id }) { idx, q ->
                    val isBookmarked = progressMap[q.id]?.isBookmarked == true
                    QuestionBankItemCard(
                        displayNumber = idx + 1,
                        question = q,
                        showAnswers = showAnswers,
                        isBookmarked = isBookmarked,
                        onToggleBookmark = { onToggleBookmark(q.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun QuestionBankItemCard(
    displayNumber: Int,
    question: Question,
    showAnswers: Boolean,
    isBookmarked: Boolean,
    onToggleBookmark: () -> Unit
) {
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
                        text = "Q.$displayNumber • ${question.sectionShort}",
                        color = NavyPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.testTag("bank_bookmark_${question.id}")
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark Question",
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
                val isCorrect = showAnswers && optIdx == question.answerIndex
                Surface(
                    color = if (isCorrect) EmeraldSoft else SoftInputBg,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isCorrect) EmeraldGreen else SlateBorder),
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
                            color = if (isCorrect) EmeraldGreen else NavyPrimary,
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
                                text = "✓",
                                color = EmeraldGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (showAnswers && question.explanation.isNotBlank()) {
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
        }
    }
}
