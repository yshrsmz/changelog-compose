package com.codingfeline.changelog

import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import com.codingfeline.changelog.internal.ui.ChangeItem
import com.codingfeline.changelog.internal.ui.ChangeType
import com.codingfeline.changelog.internal.ui.Changelog
import com.codingfeline.changelog.internal.parser.ChangelogParser
import com.codingfeline.changelog.internal.ui.Release
import com.codingfeline.changelog.internal.viewmodel.ChangelogError
import com.codingfeline.changelog.internal.viewmodel.ChangelogViewModel
import android.content.res.Resources
import java.io.IOException
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
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

    private val testDispatcher = UnconfinedTestDispatcher()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule(testDispatcher)

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
        viewModel = ChangelogViewModel(changelogParser, testDispatcher)
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
    fun `viewModel parses changelog on the io dispatcher`() = runTest {
        // Given an io dispatcher that only runs when the scheduler advances
        val ioDispatcher = StandardTestDispatcher(testDispatcher.scheduler)
        val deferredViewModel = ChangelogViewModel(changelogParser, ioDispatcher)
        coEvery { changelogParser.parseChangelog(testResourceId) } returns testChangelog

        // When
        deferredViewModel.loadChangelog(testResourceId)

        // Then parsing is dispatched to the io dispatcher, not run inline on main
        verify(exactly = 0) { changelogParser.parseChangelog(any()) }
        assertThat(deferredViewModel.uiState.value.isLoading).isTrue()

        advanceUntilIdle()

        verify(exactly = 1) { changelogParser.parseChangelog(testResourceId) }
        assertThat(deferredViewModel.uiState.value.isLoading).isFalse()
        assertThat(deferredViewModel.uiState.value.changelog).isEqualTo(testChangelog)
    }

    @Test
    fun `factory creates a viewModel that parses on the given io dispatcher`() = runTest {
        // Given a factory with an io dispatcher that only runs when the scheduler advances
        val ioDispatcher = StandardTestDispatcher(testDispatcher.scheduler)
        val factoryViewModel = ChangelogViewModel.Factory(changelogParser, ioDispatcher)
            .create(ChangelogViewModel::class.java)
        var parseThread: Thread? = null
        coEvery { changelogParser.parseChangelog(testResourceId) } answers {
            parseThread = Thread.currentThread()
            testChangelog
        }

        // When
        factoryViewModel.loadChangelog(testResourceId)

        // Then parsing waits for the given io dispatcher instead of Dispatchers.IO
        verify(exactly = 0) { changelogParser.parseChangelog(any()) }

        advanceUntilIdle()

        // and runs on the test thread, which a real IO dispatcher would not use
        verify(exactly = 1) { changelogParser.parseChangelog(testResourceId) }
        assertThat(parseThread).isSameInstanceAs(Thread.currentThread())
    }

    @Test
    fun `loading a new resource cancels the in-flight load`() = runTest {
        // Given an io dispatcher that only runs when the scheduler advances
        val ioDispatcher = StandardTestDispatcher(testDispatcher.scheduler)
        val deferredViewModel = ChangelogViewModel(changelogParser, ioDispatcher)
        val otherResourceId = 67890
        coEvery { changelogParser.parseChangelog(any()) } returns testChangelog

        // When a second resource is requested before the first one is parsed
        deferredViewModel.loadChangelog(otherResourceId)
        deferredViewModel.loadChangelog(testResourceId)
        advanceUntilIdle()

        // Then only the latest request is parsed
        verify(exactly = 0) { changelogParser.parseChangelog(otherResourceId) }
        verify(exactly = 1) { changelogParser.parseChangelog(testResourceId) }
        assertThat(deferredViewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun `loading the same resource again is skipped once loaded`() = runTest {
        // Given
        coEvery { changelogParser.parseChangelog(testResourceId) } returns testChangelog
        viewModel.loadChangelog(testResourceId)
        advanceUntilIdle()

        // When the same resource is requested again, e.g. after a configuration change
        viewModel.loadChangelog(testResourceId)
        advanceUntilIdle()

        // Then the changelog is not reloaded
        verify(exactly = 1) { changelogParser.parseChangelog(testResourceId) }
        assertThat(viewModel.uiState.value.isLoading).isFalse()
        assertThat(viewModel.uiState.value.changelog).isEqualTo(testChangelog)
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
        assertThat(finalState.error).isEqualTo(ChangelogError.InvalidFormat(exception.message))
    }

    @Test
    fun `viewModel handles missing resource`() = runTest {
        // Given
        coEvery { changelogParser.parseChangelog(testResourceId) } throws Resources.NotFoundException("missing")

        // When
        viewModel.loadChangelog(testResourceId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertThat(finalState.isLoading).isFalse()
        assertThat(finalState.error).isEqualTo(ChangelogError.ResourceNotFound)
    }

    @Test
    fun `viewModel handles read failure`() = runTest {
        // Given
        coEvery { changelogParser.parseChangelog(testResourceId) } throws IOException("stream closed")

        // When
        viewModel.loadChangelog(testResourceId)
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertThat(finalState.isLoading).isFalse()
        assertThat(finalState.error).isEqualTo(ChangelogError.ReadFailed("stream closed"))
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
        val freshViewModel = ChangelogViewModel(changelogParser, testDispatcher)

        // Then
        val initialState = freshViewModel.uiState.value
        assertThat(initialState.isLoading).isTrue()
        assertThat(initialState.changelog).isEqualTo(Changelog(emptyList()))
        assertThat(initialState.error).isNull()
    }
}