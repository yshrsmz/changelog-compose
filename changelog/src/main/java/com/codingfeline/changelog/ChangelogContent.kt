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

@Composable
fun ChangelogContent(
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
