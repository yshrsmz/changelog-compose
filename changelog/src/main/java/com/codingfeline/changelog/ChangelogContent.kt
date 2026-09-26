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
import androidx.compose.ui.unit.dp

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
 * @param changelogResId The raw resource ID of the XML changelog file (e.g., `R.raw.changelog`).
 *                       The file must be placed in `res/raw/` directory.
 * @param modifier The [Modifier] to be applied to the root composable.
 *                 Defaults to [Modifier] (no modifications).
 * @param contentPadding The [PaddingValues] applied to the changelog list content.
 *                       Defaults to `PaddingValues(16.dp)`. Note that passing a value
 *                       replaces the default padding entirely.
 * @param labels The texts displayed in the error state (retry button and error messages).
 *               Defaults to the built-in English texts. See [ChangelogLabels].
 */
@Composable
fun ChangelogContent(
    @RawRes changelogResId: Int,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    labels: ChangelogLabels = ChangelogLabels(),
) {
    ChangelogContent(
        changelogResId = changelogResId,
        onRetry = {},
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
    viewModel: ChangelogViewModel = viewModel(
        factory = ChangelogViewModel.Factory(
            changelogParser = ChangelogParser(context.applicationContext),
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
