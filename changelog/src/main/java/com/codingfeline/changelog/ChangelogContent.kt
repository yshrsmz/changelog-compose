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
 * @param changelogResId The raw resource ID of the XML changelog file (e.g., `R.raw.changelog`).
 *                       The file must be placed in `res/raw/` directory.
 * @param modifier The [Modifier] to be applied to the root composable.
 *                 Defaults to [Modifier] (no modifications).
 */
@Composable
fun ChangelogContent(
    @RawRes changelogResId: Int,
    modifier: Modifier = Modifier,
) {
    ChangelogContent(
        changelogResId = changelogResId,
        onRetry = {},
        modifier = modifier
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
    retryLabel: String = "Retry",
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(changelogResId) {
        viewModel.loadChangelog(changelogResId)
    }

    when {
        uiState.isLoading -> {
            ChangelogLoadingContent(modifier = modifier)
        }

        uiState.error != null -> {
            ChangelogErrorContent(
                error = uiState.error ?: "Unknown error",
                onRetry = {
                    onRetry()
                    viewModel.retry(changelogResId)
                },
                modifier = modifier,
                retryLabel = retryLabel,
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
