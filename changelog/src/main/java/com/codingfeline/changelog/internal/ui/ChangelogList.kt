package com.codingfeline.changelog.internal.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

private object ChangelogDimensions {
    val LabelHorizontalPadding = 6.dp
    val LabelVerticalPadding = 2.dp
    val LabelTrailingSpacing = 4.dp
}

@Composable
private fun calculateLabelWidth(
    labelText: String,
    textMeasurer: TextMeasurer,
    density: Density,
): TextUnit {
    val labelStyle = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
    val textLayoutResult = remember(labelText, labelStyle) {
        textMeasurer.measure(text = labelText, style = labelStyle)
    }

    return remember(labelText, labelStyle, density) {
        with(density) {
            val horizontalPadding = ChangelogDimensions.LabelHorizontalPadding.toPx() * 2
            val spacing = ChangelogDimensions.LabelTrailingSpacing.toPx()
            (textLayoutResult.size.width + horizontalPadding + spacing + 1.dp.toPx()).toSp()
        }
    }
}

@Composable
fun ChangelogList(
    changelog: Changelog,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(16.dp),
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = changelog.releases,
            key = { it.versionName },
        ) { release ->
            ReleaseItem(release = release)
        }
    }
}

@Composable
private fun ReleaseItem(
    release: Release,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Version header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "v${release.versionName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                release.changeDate?.let { date ->
                    Text(
                        text = date,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Changes list
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                release.changes.forEach { changeItem ->
                    ChangeItem(changeItem = changeItem)
                }
            }
        }
    }
}

@Composable
private fun ChangeItem(
    changeItem: ChangeItem,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        // Bullet point
        Text(
            text = "•",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        // Content with inline type label
        if (changeItem.type != null) {
            val textMeasurer = rememberTextMeasurer()
            val density = LocalDensity.current

            // Measure the actual label text width
            val labelText = changeItem.type.toStyle(MaterialTheme.colorScheme).label

            val labelWidth = calculateLabelWidth(
                labelText = labelText,
                textMeasurer = textMeasurer,
                density = density,
            )

            val annotatedString = buildAnnotatedString {
                appendInlineContent("typeLabel", "[${changeItem.type}]")
                append(changeItem.text)
            }

            val inlineContent = mapOf(
                "typeLabel" to InlineTextContent(
                    placeholder = Placeholder(
                        width = labelWidth,
                        height = MaterialTheme.typography.bodyMedium.lineHeight,
                        placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter,
                    ),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = ChangelogDimensions.LabelTrailingSpacing),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        ChangeTypeLabel(
                            type = changeItem.type,
                        )
                    }
                },
            )

            Text(
                text = annotatedString,
                inlineContent = inlineContent,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
        } else {
            Text(
                text = changeItem.text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ChangeTypeLabel(
    type: ChangeType,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val style = type.toStyle(colorScheme)

    Surface(
        shape = MaterialTheme.shapes.small,
        color = style.surfaceColor,
        modifier = modifier,
    ) {
        Text(
            text = style.label,
            style = MaterialTheme.typography.labelSmall,
            color = style.contentColor,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(
                horizontal = ChangelogDimensions.LabelHorizontalPadding,
                vertical = ChangelogDimensions.LabelVerticalPadding,
            ),
        )
    }
}
