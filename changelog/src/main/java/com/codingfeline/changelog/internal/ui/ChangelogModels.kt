package com.codingfeline.changelog.internal.ui

import androidx.compose.runtime.Immutable

@Immutable
internal data class Changelog(
    val releases: List<Release>,
)

@Immutable
internal data class Release(
    val versionName: String,
    val changeDate: String?,
    val changes: List<ChangeItem>,
)

@Immutable
internal data class ChangeItem(
    val text: String,
    val type: ChangeType? = null,
)

internal enum class ChangeType {
    FIX,
    NEW,
    BREAKING,
}
