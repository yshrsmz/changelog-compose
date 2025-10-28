package com.codingfeline.changelog.internal.ui

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
internal data class ChangeTypeStyle(
    val label: String,
    val surfaceColor: Color,
    val contentColor: Color,
)

@Composable
internal fun ChangeType.toStyle(colorScheme: ColorScheme): ChangeTypeStyle = when (this) {
    ChangeType.FIX -> ChangeTypeStyle(
        label = "FIX",
        surfaceColor = colorScheme.errorContainer,
        contentColor = colorScheme.onErrorContainer,
    )
    ChangeType.NEW -> ChangeTypeStyle(
        label = "NEW",
        surfaceColor = colorScheme.primaryContainer,
        contentColor = colorScheme.onPrimaryContainer,
    )
    ChangeType.BREAKING -> ChangeTypeStyle(
        label = "BREAKING",
        surfaceColor = colorScheme.tertiaryContainer,
        contentColor = colorScheme.onTertiaryContainer,
    )
}
