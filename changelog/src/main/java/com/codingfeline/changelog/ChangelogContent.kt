package com.codingfeline.changelog

import android.content.Context
import androidx.annotation.RawRes
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.codingfeline.changelog.internal.parser.ChangelogParser
import com.codingfeline.changelog.internal.ui.ChangelogErrorContent
import com.codingfeline.changelog.internal.ui.ChangelogList
import com.codingfeline.changelog.internal.ui.ChangelogLoadingContent
import com.codingfeline.changelog.internal.viewmodel.ChangelogViewModel
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Displays a changelog parsed from an XML resource file.
 *
 * This composable automatically handles loading, error, and success states.
 * The changelog is parsed from an XML file located in your app's `res/raw` directory.
 *
 * ## XML Format
 *
 * The expected XML format is:
 * ```xml
 * <changelog>
 *     <changelogversion versionName="1.0.0" changeDate="2024-10-28">
 *         <changelogtext type="new">New feature description</changelogtext>
 *         <changelogtext type="fix">Bug fix description</changelogtext>
 *         <changelogtext type="breaking">Breaking change</changelogtext>
 *         <changelogtext>General change (no type)</changelogtext>
 *     </changelogversion>
 * </changelog>
 * ```
 *
 * ## Usage Example
 *
 * ```kotlin
 * @Composable
 * fun MyScreen() {
 *     ChangelogContent(
 *         changelogResId = R.raw.changelog,
 *         modifier = Modifier.fillMaxSize()
 *     )
 * }
 * ```
 *
 * ## Edge-to-Edge Support
 *
 * When placed inside a [androidx.compose.material3.Scaffold], pass the scaffold's
 * `innerPadding` as [contentPadding] (instead of `Modifier.padding(innerPadding)`)
 * so the list content scrolls behind the system bars:
 *
 * ```kotlin
 * Scaffold { innerPadding ->
 *     ChangelogContent(
 *         changelogResId = R.raw.changelog,
 *         modifier = Modifier.fillMaxSize(),
 *         contentPadding = innerPadding
 *     )
 * }
 * ```
 *
 * ## Testing
 *
 * The changelog XML is read and parsed on [ioDispatcher]. In a Compose UI test, pass a
 * `TestDispatcher` so the test can synchronize with the load instead of
 * waiting in real time. With an `UnconfinedTestDispatcher`, the changelog is loaded by the time
 * `waitForIdle()` returns:
 *
 * ```kotlin
 * composeTestRule.setContent {
 *     ChangelogContent(
 *         changelogResId = R.raw.changelog,
 *         ioDispatcher = UnconfinedTestDispatcher()
 *     )
 * }
 * composeTestRule.onNodeWithText("v1.0.0").assertIsDisplayed()
 * ```
 *
 * If the test replaces `Dispatchers.Main` with a `StandardTestDispatcher`, advance that
 * dispatcher's scheduler as well, since the load result is delivered on the main dispatcher.
 *
 * In Android Studio's Preview and in screenshot tests that set `LocalInspectionMode` to true,
 * [ioDispatcher] is not used and the changelog is parsed in place, so the capture shows the
 * changelog instead of the loading state.
 *
 * @param changelogResId The raw resource ID of the XML changelog file (e.g., `R.raw.changelog`).
 *                       The file must be placed in `res/raw/` directory.
 * @param modifier The [Modifier] to be applied to the root composable.
 *                 Defaults to [Modifier] (no modifications).
 * @param contentPadding The [PaddingValues] applied to the changelog list content.
 *                       Defaults to `PaddingValues(16.dp)`. Note that passing a value
 *                       replaces the default padding entirely.
 * @param labels The texts displayed in the error state (retry button and error messages).
 *               Defaults to [ChangelogLabels.Default], the built-in English texts.
 * @param ioDispatcher The [CoroutineDispatcher] on which the changelog XML is read and parsed.
 *                     Defaults to [Dispatchers.IO]. Pass a test dispatcher in UI tests to
 *                     synchronize with the load. The dispatcher is captured when the internal
 *                     view model is first created, so changing it afterwards has no effect.
 *                     Ignored when `LocalInspectionMode` is true (see Testing above).
 */
@Composable
fun ChangelogContent(
    @RawRes changelogResId: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    labels: ChangelogLabels = ChangelogLabels.Default,
    ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    ChangelogContent(
        changelogResId = changelogResId,
        onRetry = {},
        modifier = modifier,
        ioDispatcher = ioDispatcher,
        contentPadding = contentPadding,
        labels = labels,
    )
}

// Keeps binaries compiled against 1.0.x, which lack the ioDispatcher parameter, working
@Deprecated("Kept for binary compatibility", level = DeprecationLevel.HIDDEN)
@Composable
fun ChangelogContent(
    @RawRes changelogResId: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    labels: ChangelogLabels = ChangelogLabels.Default,
) {
    ChangelogContent(
        changelogResId = changelogResId,
        modifier = modifier,
        contentPadding = contentPadding,
        labels = labels,
    )
}

@Composable
internal fun ChangelogContent(
    @RawRes changelogResId: Int,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    ioDispatcher: CoroutineDispatcher,
    viewModel: ChangelogViewModel = viewModel(
        factory = ChangelogViewModel.Factory(
            changelogParser = ChangelogParser(context.applicationContext),
            // Previews and screenshot tests capture without waiting for work on other
            // threads, so parse in place there to show the changelog instead of the
            // loading state
            ioDispatcher = if (LocalInspectionMode.current) Dispatchers.Unconfined else ioDispatcher,
        ),
    ),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    labels: ChangelogLabels,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(changelogResId) {
        viewModel.loadChangelog(changelogResId)
    }

    val error = uiState.error

    when {
        uiState.isLoading -> {
            ChangelogLoadingContent(modifier = modifier)
        }

        error != null -> {
            ChangelogErrorContent(
                error = error,
                labels = labels,
                onRetry = {
                    onRetry()
                    viewModel.retry(changelogResId)
                },
                modifier = modifier,
            )
        }

        else -> {
            ChangelogList(
                changelog = uiState.changelog,
                modifier = modifier,
                contentPadding = contentPadding,
            )
        }
    }
}
