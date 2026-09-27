package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ThreatSeverity
import com.example.ui.animation.PhotonicSignalPulseIndicator
import com.example.ui.animation.QuantumFluidCrossfade
import com.example.ui.components.EnclaveStatusOverlay
import com.example.ui.components.PhotonicBadge
import com.example.ui.components.SecurityDocumentationOverlay
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.viewmodel.AgisViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    private val viewModel: AgisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                AgisMainApp(viewModel = viewModel)
            }
        }
    }
}

sealed class NavTab(
    val index: Int,
    val title: String,
    val icon: ImageVector
) {
    object Overview : NavTab(0, "Overview", Icons.Default.Dashboard)
    object Recon : NavTab(1, "Radar", Icons.Default.Radar)
    object Matrix : NavTab(2, "Evolution", Icons.Default.CompareArrows)
    object Neural : NavTab(3, "Neural", Icons.Default.Psychology)
    object Shield : NavTab(4, "Shield", Icons.Default.Shield)
    object Enclave : NavTab(5, "Enclave", Icons.Default.VpnKey)
    object Validator : NavTab(6, "Validate", Icons.Default.Verified)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgisMainApp(viewModel: AgisViewModel) {
    val selectedTabIndex by viewModel.selectedTab.collectAsState()
    val threatSeverity by viewModel.globalThreatLevel.collectAsState()
    val biometrics by viewModel.biometrics.collectAsState()
    val alertMessage by viewModel.systemAlertMessage.collectAsState()
    val isEnclaveOverlayVisible by viewModel.isEnclaveOverlayVisible.collectAsState()
    val isSecurityDocsVisible by viewModel.isSecurityDocsVisible.collectAsState()
    val isLatticeVerifying by viewModel.isLatticeVerifying.collectAsState()
    val enclaveKey by viewModel.enclaveKey.collectAsState()
    val context = LocalContext.current

    val tabs = listOf(
        NavTab.Overview,
        NavTab.Recon,
        NavTab.Matrix,
        NavTab.Neural,
        NavTab.Shield,
        NavTab.Enclave,
        NavTab.Validator
    )

    // Auto dismiss alert banner after 4 seconds
    LaunchedEffect(alertMessage) {
        if (alertMessage != null) {
            delay(4000)
            viewModel.clearSystemAlert()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = SpaceCobaltDark,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PhotonicSignalPulseIndicator(
                                signalColor = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson
                                else OperationalEmerald,
                                size = 8.dp,
                                pulseSpeedMs = if (threatSeverity == ThreatSeverity.CRITICAL) 800 else 1800
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "AGIS-2045",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = PhotonicCyan,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Zero-Trust Cyber-Node",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmbientWhiteMuted
                                )
                            }
                        }

                        // Right Top Header Status Pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            PhotonicBadge(
                                text = "${biometrics.neuralPulseBpm} BPM",
                                signalColor = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson else PhotonicCyan,
                                icon = Icons.Default.Favorite
                            )
                            PhotonicBadge(
                                text = "DOCS",
                                signalColor = PhotonicCyan,
                                icon = Icons.Default.MenuBook,
                                modifier = Modifier.clickable { viewModel.setSecurityDocsVisible(true) }
                            )
                            PhotonicBadge(
                                text = "512-BIT PQ",
                                signalColor = OperationalEmerald,
                                icon = Icons.Default.Lock,
                                modifier = Modifier.clickable { viewModel.setEnclaveOverlayVisible(true) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SpaceCobaltDark,
                    titleContentColor = AmbientWhite
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .background(SpaceCobaltDark)
                    .border(
                        width = 1.dp,
                        color = SpaceCobaltGlassBorder.copy(alpha = 0.3f),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    ),
                containerColor = SpaceCobaltSurface,
                contentColor = AmbientWhite,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars
            ) {
                tabs.forEach { tab ->
                    val isSelected = selectedTabIndex == tab.index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.setSelectedTab(tab.index) },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                maxLines = 1
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = PhotonicCyan,
                            selectedTextColor = PhotonicCyan,
                            unselectedIconColor = AmbientWhiteSubtle,
                            unselectedTextColor = AmbientWhiteSubtle,
                            indicatorColor = PhotonicCyan.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(SpaceCobaltDark)
        ) {
            // Screen Switcher with Fluid Quantum Crossfade
            QuantumFluidCrossfade(
                targetState = selectedTabIndex,
                modifier = Modifier.fillMaxSize()
            ) { tabIndex ->
                when (tabIndex) {
                    0 -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigateToTab = { viewModel.setSelectedTab(it) },
                        onInspectLayer = { viewModel.setInspectedLayerId(it) }
                    )
                    1 -> BiometricRadarScreen(viewModel = viewModel)
                    2 -> ArchitectureMatrixScreen(viewModel = viewModel)
                    3 -> NeuralCommandScreen(viewModel = viewModel)
                    4 -> ShieldPipelineScreen(viewModel = viewModel)
                    5 -> EnclaveVaultScreen(viewModel = viewModel)
                    6 -> AutonomousValidationScreen(viewModel = viewModel)
                }
            }

            // Floating System Notification Alert
            AnimatedVisibility(
                visible = alertMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            ) {
                if (alertMessage != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (alertMessage!!.contains("ALERT", ignoreCase = true) || alertMessage!!.contains("Crimson", ignoreCase = true)) ContainmentCrimsonDark.copy(alpha = 0.9f)
                                else SpaceCobaltCard
                            )
                            .border(
                                1.dp,
                                if (alertMessage!!.contains("ALERT", ignoreCase = true) || alertMessage!!.contains("Crimson", ignoreCase = true)) ContainmentCrimson
                                else PhotonicCyan,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (alertMessage!!.contains("ALERT", ignoreCase = true)) Icons.Default.Warning else Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (alertMessage!!.contains("ALERT", ignoreCase = true)) ContainmentCrimson else PhotonicCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = alertMessage!!,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AmbientWhite,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            IconButton(
                                onClick = { viewModel.clearSystemAlert() },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = AmbientWhiteMuted,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 512-bit Post-Quantum Enclave Status Dashboard Overlay
            EnclaveStatusOverlay(
                isVisible = isEnclaveOverlayVisible,
                enclaveKey = enclaveKey,
                isLatticeVerifying = isLatticeVerifying,
                onDismiss = { viewModel.setEnclaveOverlayVisible(false) },
                onRotateKey = { viewModel.rotateEnclaveKey() },
                onVerifyLattice = { viewModel.runLatticeIntegrityScan() },
                onBiometricAuth = { viewModel.authenticateEnclaveWithCredentialManager(context) },
                onLockEnclave = { viewModel.lockEnclaveStorage() }
            )

            // Security Documentation & ECTT Technical Library Overlay
            SecurityDocumentationOverlay(
                isVisible = isSecurityDocsVisible,
                viewModel = viewModel,
                onDismiss = { viewModel.setSecurityDocsVisible(false) }
            )
        }
    }
}
