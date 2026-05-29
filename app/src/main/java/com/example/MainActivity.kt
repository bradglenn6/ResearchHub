package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.ResearchViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: ResearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppLayout(viewModel)
            }
        }
    }
}

@Composable
fun AppHeader(
    syncStatus: String,
    syncing: Boolean,
    onForceSync: () -> Unit,
    activeTab: Int,
    viewModel: ResearchViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(start = 24.dp, end = 24.dp, top = 20.dp, bottom = 12.dp)
    ) {
        // Row 1: Actions, Status Indicator, and Avatar initials
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(
                onClick = { viewModel.showToast("Research directory catalog active.") },
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu icon",
                    modifier = Modifier.size(24.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cloud Sync State Symbol with tooltip toast onClick
                IconButton(
                    onClick = onForceSync,
                    enabled = !syncing
                ) {
                    if (syncing) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.CloudDone,
                            contentDescription = "All directories synchronized.",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Initial-bearing profile avatar
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "BG", // User initials (Brad Glenn = BG)
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = Color.White,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Row 2: Heavy display bold title matching Design HTML exactly
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "RESEARCH",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "HUB.",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Row 3: Tag info matching design template exactly
        val descriptor = when (activeTab) {
            0 -> "Research Intelligence Telemetry"
            1 -> "Active Library Cache & Verification"
            2 -> "External Scholar Database Gateway"
            3 -> "Shared Workspaces & Nodes"
            4 -> "AI Funding Finder & Grants"
            5 -> "AES-256 Symmetric Encryption Session"
            else -> "End-to-End Encrypted Session"
        }
        Text(
            text = descriptor.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
            color = MaterialTheme.colorScheme.secondary,
            letterSpacing = 1.25.sp
        )
    }
}

@Composable
fun MainAppLayout(viewModel: ResearchViewModel) {
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val syncStatus by viewModel.syncStatus.collectAsStateWithLifecycle()
    val syncing by viewModel.syncing.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Observe Toast Messages
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.background,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = activeTab == 0,
                    onClick = { viewModel.selectTab(0) },
                    icon = { Icon(if (activeTab == 0) Icons.Filled.Dashboard else Icons.Outlined.Dashboard, null) },
                    label = { Text("Intel", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )

                NavigationBarItem(
                    selected = activeTab == 1,
                    onClick = { viewModel.selectTab(1) },
                    icon = { Icon(if (activeTab == 1) Icons.Filled.Inventory2 else Icons.Outlined.Inventory2, null) },
                    label = { Text("Library", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_library")
                )

                NavigationBarItem(
                    selected = activeTab == 2,
                    onClick = { viewModel.selectTab(2) },
                    icon = { Icon(if (activeTab == 2) Icons.Filled.Psychology else Icons.Outlined.Psychology, null) },
                    label = { Text("Capture", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_capture")
                )

                NavigationBarItem(
                    selected = activeTab == 3,
                    onClick = { viewModel.selectTab(3) },
                    icon = { Icon(if (activeTab == 3) Icons.Filled.Group else Icons.Outlined.Group, null) },
                    label = { Text("Shared", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_workspaces")
                )

                NavigationBarItem(
                    selected = activeTab == 4,
                    onClick = { viewModel.selectTab(4) },
                    icon = { Icon(if (activeTab == 4) Icons.Filled.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet, null) },
                    label = { Text("Grants", fontWeight = FontWeight.Bold, fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_grants")
                )

                NavigationBarItem(
                    selected = activeTab == 5,
                    onClick = { viewModel.selectTab(5) },
                    icon = { Icon(if (activeTab == 5) Icons.Filled.Settings else Icons.Outlined.Settings, null) },
                    label = { Text("Keyring", fontWeight = FontWeight.Bold, fontSize = 9.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_privacy")
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars) // Respect Status Notch bar
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // High fidelity styled header matching HTML layout
            AppHeader(
                syncStatus = syncStatus,
                syncing = syncing,
                onForceSync = { viewModel.triggerCloudSync() },
                activeTab = activeTab,
                viewModel = viewModel
            )

            // Dynamic Tab Views
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                when (activeTab) {
                    0 -> DashboardScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    1 -> LibraryScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    2 -> BrowserCaptureScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    3 -> WorkspacesScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    4 -> FundingScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    5 -> SecuritySettingsScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                    else -> DashboardScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
