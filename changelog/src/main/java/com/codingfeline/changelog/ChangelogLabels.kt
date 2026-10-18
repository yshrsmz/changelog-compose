package com.codingfeline.changelog

import androidx.compose.runtime.Immutable

/**
 * Texts displayed by [ChangelogContent].
 *
 * The constructor requires every text, so a forgotten text (or one added in a later version of
 * the library) is a compile error instead of silently falling back to English:
 *
 * ```kotlin
 * ChangelogContent(
 *     changelogResId = R.raw.changelog,
 *     labels = ChangelogLabels(
 *         retry = stringResource(R.string.changelog_retry),
 *         errorResourceNotFound = stringResource(R.string.changelog_error_not_found),
 *         errorInvalidFormat = stringResource(R.string.changelog_error_invalid_format),
 *         errorReadFailed = stringResource(R.string.changelog_error_read_failed),
 *     )
 * )
 * ```
 *
 * To replace only some of the texts and keep the built-in English texts for the rest, use
 * [Default] with [copy]:
 *
 * ```kotlin
 * labels = ChangelogLabels.Default.copy(retry = stringResource(R.string.changelog_retry))
 * ```
 *
 * Note that texts added in a later version of the library then also fall back to English.
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
    val retry: String,
    val errorResourceNotFound: String,
    val errorInvalidFormat: String,
    val errorReadFailed: String,
) {
    /**
     * Returns a copy of these labels with the given texts replaced.
     */
    fun copy(
        retry: String = this.retry,
        errorResourceNotFound: String = this.errorResourceNotFound,
        errorInvalidFormat: String = this.errorInvalidFormat,
        errorReadFailed: String = this.errorReadFailed,
    ): ChangelogLabels = ChangelogLabels(
        retry = retry,
        errorResourceNotFound = errorResourceNotFound,
        errorInvalidFormat = errorInvalidFormat,
        errorReadFailed = errorReadFailed,
    )

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

    companion object {
        /**
         * The library's built-in English texts, used when no labels are passed to [ChangelogContent].
         */
        val Default: ChangelogLabels = ChangelogLabels(
            retry = "Retry",
            errorResourceNotFound = "Changelog resource not found",
            errorInvalidFormat = "Invalid changelog format",
            errorReadFailed = "Failed to read changelog",
        )
    }
}
