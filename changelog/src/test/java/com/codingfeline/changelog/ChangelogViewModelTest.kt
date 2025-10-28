package com.codingfeline.changelog

import io.mockk.coEvery
import io.mockk.mockk
import com.codingfeline.changelog.internal.ui.ChangeItem
import com.codingfeline.changelog.internal.ui.ChangeType
import com.codingfeline.changelog.internal.ui.Changelog
import com.codingfeline.changelog.internal.parser.ChangelogParser
import com.codingfeline.changelog.internal.ui.Release
import com.codingfeline.changelog.internal.viewmodel.ChangelogViewModel
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runners.model.Statement
import org.junit.runner.Description
import org.xmlpull.v1.XmlPullParserException

@OptIn(ExperimentalCoroutinesApi::class)
class MainCoroutineRule(
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
) : TestRule {
    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                Dispatchers.setMain(dispatcher)
                try {
                    base.evaluate()
                } finally {
                    Dispatchers.resetMain()
                }
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class ChangelogViewModelTest {

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(UnconfinedTestDispatcher())

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var changelogParser: ChangelogParser
    private lateinit var viewModel: ChangelogViewModel

    private val testChangelog = Changelog(
        releases = listOf(
            Release(
                versionName = "2.0.0",
                changeDate = "Jun 18th, 2023",
                changes = listOf(
                    ChangeItem("Major update", null),
                    ChangeItem("New features", ChangeType.NEW),
                ),
            ),
            Release(
                versionName = "1.0.0",
                changeDate = null,
                changes = listOf(
                    ChangeItem("Initial release", null),
                ),
            ),
        ),
    )

    private val testResourceId = 12345 // Mock resource ID

    @Before
    fun setup() {
        changelogParser = mockk()
        viewModel = ChangelogViewModel(changelogParser)
    }

    @Test
    fun `viewModel loads changelog when loadChangelog is called`() = runTest {
        // Given
        coEvery { changelogParser.parseChangelog(testResourceId) } returns testChangelog

        // When
        viewModel.loadChangelog(testResourceId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertThat(finalState.isLoading).isFalse()
        assertThat(finalState.changelog).isEqualTo(testChangelog)
        assertThat(finalState.error).isNull()
    }

    @Test
    fun `viewModel handles parsing error`() = runTest {
        // Given
        val errorMessage = "Failed to parse XML"
        val exception = XmlPullParserException(errorMessage)
        coEvery { changelogParser.parseChangelog(testResourceId) } throws exception

        // When
        viewModel.loadChangelog(testResourceId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertThat(finalState.isLoading).isFalse()
        assertThat(finalState.changelog).isEqualTo(Changelog(emptyList()))
        assertThat(finalState.error).isEqualTo("Invalid changelog format: ${exception.message}")
    }

    @Test
    fun `retry reloads changelog after error`() = runTest {
        // Given
        val exception = XmlPullParserException("Error")
        coEvery { changelogParser.parseChangelog(testResourceId) } throws exception andThen testChangelog

        // When - first load with error
        viewModel.loadChangelog(testResourceId)
        advanceUntilIdle()

        // Then verify error state
        val errorState = viewModel.uiState.value
        assertThat(errorState.isLoading).isFalse()
        assertThat(errorState.error).isNotNull()

        // When retry
        viewModel.retry(testResourceId)
        advanceUntilIdle()

        // Then verify successful reload
        val successState = viewModel.uiState.value
        assertThat(successState.isLoading).isFalse()
        assertThat(successState.changelog).isEqualTo(testChangelog)
        assertThat(successState.error).isNull()
    }

    @Test
    fun `initial state is loading with empty changelog`() = runTest {
        // Given a new ViewModel
        val freshViewModel = ChangelogViewModel(changelogParser)

        // Then
        val initialState = freshViewModel.uiState.value
        assertThat(initialState.isLoading).isTrue()
        assertThat(initialState.changelog).isEqualTo(Changelog(emptyList()))
        assertThat(initialState.error).isNull()
    }
}