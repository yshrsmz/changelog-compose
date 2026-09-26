package com.codingfeline.changelog

import com.codingfeline.changelog.internal.ui.toText
import com.codingfeline.changelog.internal.viewmodel.ChangelogError
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ChangelogErrorTextTest {

    @Test
    fun `default labels produce the built-in English texts`() {
        val labels = ChangelogLabels()

        assertThat(ChangelogError.ResourceNotFound.toText(labels))
            .isEqualTo("Changelog resource not found")
        assertThat(ChangelogError.InvalidFormat("unexpected tag").toText(labels))
            .isEqualTo("Invalid changelog format: unexpected tag")
        assertThat(ChangelogError.ReadFailed("stream closed").toText(labels))
            .isEqualTo("Failed to read changelog: stream closed")
    }

    @Test
    fun `custom labels replace the error texts`() {
        val labels = ChangelogLabels(
            errorResourceNotFound = "変更履歴が見つかりません",
            errorInvalidFormat = "変更履歴の形式が不正です",
            errorReadFailed = "変更履歴を読み込めません",
        )

        assertThat(ChangelogError.ResourceNotFound.toText(labels))
            .isEqualTo("変更履歴が見つかりません")
        assertThat(ChangelogError.InvalidFormat("unexpected tag").toText(labels))
            .isEqualTo("変更履歴の形式が不正です: unexpected tag")
        assertThat(ChangelogError.ReadFailed("stream closed").toText(labels))
            .isEqualTo("変更履歴を読み込めません: stream closed")
    }

    @Test
    fun `error without message shows only the label`() {
        val labels = ChangelogLabels()

        assertThat(ChangelogError.InvalidFormat(null).toText(labels))
            .isEqualTo("Invalid changelog format")
        assertThat(ChangelogError.ReadFailed(null).toText(labels))
            .isEqualTo("Failed to read changelog")
    }
}
