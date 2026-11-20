package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.MockTestMeta
import com.example.data.local.TestAttemptEntity
import com.example.ui.components.ExamPatternInfoCard
import com.example.ui.components.MockTestCard
import com.example.ui.theme.CardSurface
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextMuted

@Composable
fun TestsScreen(
    initialSubTab: Int,
    fullMocks: List<MockTestMeta>,
    sectionalTests: List<MockTestMeta>,
    attempts: List<TestAttemptEntity>,
    onStartMock: (MockTestMeta) -> Unit
) {
    var selectedSubTab by rememberSaveable(initialSubTab) { mutableIntStateOf(initialSubTab) }
    val bestScoreByTestId = attempts.groupBy { it.testId }
        .mapValues { (_, list) -> list.maxOf { it.correctCount } }

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
                modifier = Modifier.testTag("tab_full_mocks"),
                text = {
                    Text(
                        text = "Full Mocks (${fullMocks.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (selectedSubTab == 0) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedSubTab == 0) NavyPrimary else TextMuted
                    )
                }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                modifier = Modifier.testTag("tab_sectional_drills"),
                text = {
                    Text(
                        text = "Sectional (${sectionalTests.size})",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = if (selectedSubTab == 1) FontWeight.Bold else FontWeight.Medium,
                        color = if (selectedSubTab == 1) NavyPrimary else TextMuted
                    )
                }
            )
        }

        val activeList = if (selectedSubTab == 0) fullMocks else sectionalTests
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("tests_list"),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (selectedSubTab == 0) {
                item { ExamPatternInfoCard(modifier = Modifier.fillMaxWidth()) }
            }
            items(activeList, key = { it.id }) { mock ->
                MockTestCard(
                    meta = mock,
                    bestScore = bestScoreByTestId[mock.id],
                    onStartClick = { onStartMock(mock) }
                )
            }
        }
    }
}
