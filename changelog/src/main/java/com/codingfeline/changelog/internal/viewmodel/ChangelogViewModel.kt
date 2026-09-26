package com.codingfeline.changelog.internal.viewmodel

import android.content.res.Resources
import androidx.annotation.RawRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.codingfeline.changelog.internal.parser.ChangelogParser
import com.codingfeline.changelog.internal.ui.Changelog
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import androidx.compose.runtime.Immutable

@Immutable
internal sealed interface ChangelogError {
    data object ResourceNotFound : ChangelogError
    data class InvalidFormat(val message: String?) : ChangelogError
    data class ReadFailed(val message: String?) : ChangelogError
}

@Immutable
internal data class ChangelogUiState(
    val changelog: Changelog = Changelog(emptyList()),
    val isLoading: Boolean = true,
    val error: ChangelogError? = null,
)

internal class ChangelogViewModel(
    private val changelogParser: ChangelogParser,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangelogUiState())
    val uiState: StateFlow<ChangelogUiState> = _uiState.asStateFlow()

    fun loadChangelog(@RawRes rawResId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                // Parse the changelog XML file
                val changelog = changelogParser.parseChangelog(rawResId)

                _uiState.update {
                    it.copy(
                        changelog = changelog,
                        isLoading = false,
                        error = null,
                    )
                }
            } catch (e: Exception) {
                val error = when (e) {
                    is Resources.NotFoundException -> ChangelogError.ResourceNotFound
                    is XmlPullParserException -> ChangelogError.InvalidFormat(e.message)
                    is IOException -> ChangelogError.ReadFailed(e.message)
                    else -> throw e
                }
                _uiState.update { it.copy(isLoading = false, error = error) }
            }
        }
    }

    fun retry(@RawRes rawResId: Int) {
        loadChangelog(rawResId)
    }

    class Factory(
        private val changelogParser: ChangelogParser,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ChangelogViewModel::class.java)) {
                return ChangelogViewModel(changelogParser) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
