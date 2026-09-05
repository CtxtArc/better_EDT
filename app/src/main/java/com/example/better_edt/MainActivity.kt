package com.example.better_edt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.better_edt.ui.ConfigTableScreen
import com.example.better_edt.ui.EdtViewModel
import com.example.better_edt.ui.FullscreenLandscapeTimetableScreen
import com.example.better_edt.ui.TimetableScreen
import com.example.better_edt.ui.theme.BetterEdtTheme

class MainActivity : ComponentActivity() {

    private val viewModel: EdtViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            BetterEdtTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    BetterEdtApp(viewModel = viewModel)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BetterEdtApp(viewModel: EdtViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val isFullscreenLandscape by viewModel.isFullscreenLandscape.collectAsState()
    val context = LocalContext.current

    if (isFullscreenLandscape) {
        // Render 100% Fullscreen Landscape Mode without Scaffold bars
        FullscreenLandscapeTimetableScreen(viewModel = viewModel)
    } else {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = if (selectedTab == 0) "MON EMPLOI DU TEMPS" else "CONFIGURATION (G1-G4)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                        )
                    },
                    actions = {
                        if (selectedTab == 0) {
                            IconButton(onClick = { viewModel.toggleFullscreenLandscape(context) }) {
                                Icon(
                                    imageVector = Icons.Default.Fullscreen,
                                    contentDescription = "Plein Écran Paysage",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { viewModel.setSelectedTab(0) },
                        icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "EDT") },
                        label = { Text("Emploi du Temps", fontWeight = FontWeight.Bold) }
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { viewModel.setSelectedTab(1) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Config") },
                        label = { Text("Configuration", fontWeight = FontWeight.Bold) }
                    )
                }
            }
        ) { innerPadding ->
            Surface(
                modifier = Modifier.padding(innerPadding),
                color = MaterialTheme.colorScheme.background
            ) {
                when (selectedTab) {
                    0 -> TimetableScreen(viewModel = viewModel)
                    1 -> ConfigTableScreen(viewModel = viewModel)
                }
            }
        }
    }
}
