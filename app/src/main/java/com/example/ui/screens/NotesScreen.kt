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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RevisionNoteCapsule
import com.example.data.RevisionNotesData
import com.example.ui.theme.CardSurface
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

@Composable
fun NotesScreen() {
    var selectedCategory by rememberSaveable { mutableStateOf("All") }
    val categories = listOf("All", "Unit I • Music", "Unit II • General")

    val capsules = remember(selectedCategory) {
        if (selectedCategory == "All") {
            RevisionNotesData.capsules
        } else {
            RevisionNotesData.capsules.filter { it.category == selectedCategory }
        }
    }

    var expandedIds by remember {
        mutableStateOf(setOf(RevisionNotesData.capsules.first().id, RevisionNotesData.capsules[1].id))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notes_screen_list"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Surface(
                color = NavySurfaceTint,
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NavyBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(NavyPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MenuBook,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Last-Day Rapid Revision Capsules",
                            color = NavyPrimary,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Distilled strictly from your 460 verified STET Music, Bihar GK & Reasoning questions.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    val selected = selectedCategory == category
                    FilterChip(
                        selected = selected,
                        onClick = { selectedCategory = category },
                        modifier = Modifier.testTag("notes_filter_${category.lowercase().replace(" ", "_")}"),
                        label = {
                            Text(
                                text = category,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NavyPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = CardSurface,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        items(capsules, key = { it.id }) { capsule ->
            val isExpanded = capsule.id in expandedIds
            RevisionCapsuleCard(
                capsule = capsule,
                isExpanded = isExpanded,
                onToggle = {
                    expandedIds = if (isExpanded) expandedIds - capsule.id else expandedIds + capsule.id
                }
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun RevisionCapsuleCard(
    capsule: RevisionNoteCapsule,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    val isMusic = capsule.category.startsWith("Unit I")
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardSurface),
        border = BorderStroke(1.dp, if (isExpanded) NavyBorder else SlateBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("note_capsule_${capsule.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = if (isMusic) NavySurfaceTint else EmeraldSoft,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = capsule.category,
                                color = if (isMusic) NavyPrimary else EmeraldGreen,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            color = SprintAmberSoft,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = capsule.badge,
                                color = SprintAmberDark,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = capsule.title,
                        color = TextPrimary,
                        fontFamily = NotoDevanagari,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        lineHeight = 23.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = capsule.subtitle,
                        color = TextMuted,
                        fontFamily = NotoDevanagari,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse Note" else "Expand Note",
                    tint = NavyPrimary
                )
            }

            if (isExpanded) {
                HorizontalDivider(color = SlateBorder)
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    capsule.highlights.forEach { bullet ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(NavyPrimary)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = bullet,
                                color = TextPrimary,
                                fontFamily = NotoDevanagari,
                                fontSize = 14.sp,
                                lineHeight = 21.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    if (capsule.tableHeaders.isNotEmpty() && capsule.tableRows.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, SlateBorder),
                            color = SoftInputBg,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(NavyPrimary)
                                        .padding(horizontal = 10.dp, vertical = 8.dp)
                                ) {
                                    capsule.tableHeaders.forEachIndexed { i, header ->
                                        val w = if (i == capsule.tableHeaders.lastIndex) 1.5f else 1f
                                        Text(
                                            text = header,
                                            color = Color.White,
                                            fontFamily = NotoDevanagari,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.weight(w)
                                        )
                                    }
                                }
                                capsule.tableRows.forEachIndexed { rowIdx, rowCells ->
                                    val rowBg = if (rowIdx % 2 == 0) CardSurface else SoftInputBg
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(rowBg)
                                            .padding(horizontal = 10.dp, vertical = 8.dp)
                                    ) {
                                        rowCells.forEachIndexed { i, cell ->
                                            val w = if (i == rowCells.lastIndex) 1.5f else 1f
                                            Text(
                                                text = cell,
                                                color = if (i == 0) NavyPrimary else TextPrimary,
                                                fontFamily = NotoDevanagari,
                                                fontWeight = if (i == 0) FontWeight.SemiBold else FontWeight.Normal,
                                                fontSize = 12.sp,
                                                lineHeight = 17.sp,
                                                modifier = Modifier
                                                    .weight(w)
                                                    .padding(end = 6.dp)
                                            )
                                        }
                                    }
                                    if (rowIdx < capsule.tableRows.lastIndex) {
                                        HorizontalDivider(color = SlateBorder)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
