package com.codingfeline.changelog

import androidx.compose.runtime.Immutable

/**
 * Texts displayed by [ChangelogContent].
 *
 * Every text defaults to the library's built-in English text, so you only need to
 * pass the ones you want to replace (e.g. with values from `stringResource`):
 *
 * ```kotlin
 * ChangelogContent(
 *     changelogResId = R.raw.changelog,
 *     labels = ChangelogLabels(
 *         retry = stringResource(R.string.changelog_retry),
 *         errorResourceNotFound = stringResource(R.string.changelog_error_not_found),
 *     )
 * )
 * ```
 *
 * The change type badges (`NEW`, `FIX`, `BREAKING`) are not customizable.
 *
 * @param retry The label of the retry button shown when loading the changelog fails.
 * @param errorResourceNotFound The error text shown when the changelog resource does not exist.
 * @param errorInvalidFormat The error text shown when the changelog XML is malformed.
 *                           The parser's message is appended as `"<text>: <message>"` when available.
 * @param errorReadFailed The error text shown when reading the changelog resource fails.
 *                        The underlying message is appended as `"<text>: <message>"` when available.
 */
@Immutable
class ChangelogLabels(
    val retry: String = "Retry",
    val errorResourceNotFound: String = "Changelog resource not found",
    val errorInvalidFormat: String = "Invalid changelog format",
    val errorReadFailed: String = "Failed to read changelog",
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ChangelogLabels) return false
        return retry == other.retry &&
            errorResourceNotFound == other.errorResourceNotFound &&
            errorInvalidFormat == other.errorInvalidFormat &&
            errorReadFailed == other.errorReadFailed
    }

    override fun hashCode(): Int {
        var result = retry.hashCode()
        result = 31 * result + errorResourceNotFound.hashCode()
        result = 31 * result + errorInvalidFormat.hashCode()
        result = 31 * result + errorReadFailed.hashCode()
        return result
    }

    override fun toString(): String =
        "ChangelogLabels(retry=$retry, errorResourceNotFound=$errorResourceNotFound, " +
            "errorInvalidFormat=$errorInvalidFormat, errorReadFailed=$errorReadFailed)"
}
