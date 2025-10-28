package com.codingfeline.changelog.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import com.codingfeline.changelog.ChangelogContent
import com.codingfeline.changelog.sample.ui.theme.ChangelogSampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChangelogSampleTheme {
                ChangelogScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogScreen() {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Changelog Demo",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        ChangelogContent(
            changelogResId = R.raw.changelog,
            onRetry = {
                // Handle retry action if needed
                // For example, you could show a toast or log
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ChangelogScreenPreview() {
    ChangelogSampleTheme {
        ChangelogScreen()
    }
}