package com.codingfeline.changelog.internal.parser

import android.content.Context
import androidx.annotation.RawRes
import com.codingfeline.changelog.internal.ui.ChangeItem
import com.codingfeline.changelog.internal.ui.ChangeType
import com.codingfeline.changelog.internal.ui.Changelog
import com.codingfeline.changelog.internal.ui.Release
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStreamReader

class ChangelogParser(
    context: Context,
) {
    private val context: Context = context.applicationContext

    @Suppress("CyclomaticComplexMethod", "NestedBlockDepth")
    fun parseChangelog(@RawRes rawResId: Int): Changelog {
        val releases = mutableListOf<Release>()

        val factory = XmlPullParserFactory.newInstance()
        val parser = factory.newPullParser()

        context.resources.openRawResource(rawResId).use { inputStream ->
            InputStreamReader(inputStream).use { reader ->
                parser.setInput(reader)

                var eventType = parser.eventType
                var currentRelease: Release? = null
                val currentChanges = mutableListOf<ChangeItem>()

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    when (eventType) {
                        XmlPullParser.START_TAG -> {
                            when (parser.name) {
                                "changelogversion" -> {
                                    // Save previous release if exists
                                    if (currentRelease != null) {
                                        releases.add(currentRelease.copy(changes = currentChanges.toList()))
                                    }

                                    currentRelease = Release(
                                        versionName = parser.getAttributeValue(null, "versionName") ?: "",
                                        changeDate = parser.getAttributeValue(null, "changeDate"),
                                        changes = emptyList(),
                                    )
                                    currentChanges.clear()
                                }
                                "changelogtext" -> {
                                    val typeAttribute = parser.getAttributeValue(null, "type")
                                    val type = when (typeAttribute?.lowercase()) {
                                        "fix" -> ChangeType.FIX
                                        "new" -> ChangeType.NEW
                                        "breaking" -> ChangeType.BREAKING
                                        else -> null
                                    }

                                    // Normalize whitespace: trim and replace multiple spaces/newlines with single space
                                    val text = parser.nextText()
                                        .trim()
                                        .replace(Regex("\\s+"), " ")
                                    if (text.isNotEmpty()) {
                                        currentChanges.add(ChangeItem(text = text, type = type))
                                    }
                                }
                            }
                        }
                    }
                    eventType = parser.next()
                }

                // Add the last release
                if (currentRelease != null) {
                    releases.add(currentRelease.copy(changes = currentChanges.toList()))
                }
            }
        }

        return Changelog(releases)
    }
}
