package com.codingfeline.changelog.internal.ui

import androidx.compose.runtime.Immutable

@Immutable
data class Changelog(
    val releases: List<Release>,
)

@Immutable
data class Release(
    val versionName: String,
    val changeDate: String?,
    val changes: List<ChangeItem>,
)

@Immutable
data class ChangeItem(
    val text: String,
    val type: ChangeType? = null,
)

enum class ChangeType {
    FIX,
    NEW,
    BREAKING,
}
