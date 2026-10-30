package com.codingfeline.changelog

import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
// Resources under src/test/res are generated into the test namespace's R
import com.codingfeline.changelog.test.R as TestR
import kotlinx.coroutines.test.StandardTestDispatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ChangelogContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val loadingIndicator = hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate)

    @Test
    fun ioDispatcher_testDispatcher_showsChangelogOnlyAfterSchedulerAdvances() {
        val ioDispatcher = StandardTestDispatcher()

        composeTestRule.setContent {
            ChangelogContent(
                changelogResId = TestR.raw.test_changelog,
                ioDispatcher = ioDispatcher,
            )
        }

        // The load is queued on ioDispatcher, so the changelog stays loading
        // however long Compose idles until the scheduler advances
        composeTestRule.onNode(loadingIndicator).assertIsDisplayed()
        composeTestRule.onNodeWithText("v2.0.0").assertDoesNotExist()

        ioDispatcher.scheduler.advanceUntilIdle()

        composeTestRule.onNodeWithText("v2.0.0").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test regular change without type").assertIsDisplayed()
        composeTestRule.onNode(loadingIndicator).assertDoesNotExist()
    }
}
