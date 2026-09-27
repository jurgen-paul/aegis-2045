package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SanitizationScanPhase
import com.example.ui.animation.PhotonicSignalPulseIndicator
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.theme.*
import com.example.viewmodel.AgisViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Interactive UI Control & Real-Time Feedback HUD for Simulated Telemetry Stream Sanitization Scans.
 * Evaluates live ingress packets, performs differential privacy noise injection (ε=0.50),
 * and provides continuous mathematical feedback on the 512-bit Post-Quantum Hardware Enclave status.
 */
@Composable
fun SanitizationScanControlCard(
    viewModel: AgisViewModel,
    modifier: Modifier = Modifier,
    onOpenEnclaveHud: (() -> Unit)? = null
) {
    val isScanActive by viewModel.isSanitizationScanActive.collectAsState()
    val isScanningInProgress by viewModel.isSanitizationScanningInProgress.collectAsState()
    val scanProgress by viewModel.sanitizationScanProgress.collectAsState()
    val scanPhase by viewModel.sanitizationScanPhase.collectAsState()
    val scanFeedback by viewModel.sanitizationScanFeedback.collectAsState()
    val enclaveKey by viewModel.enclaveKey.collectAsState()

    val animatedProgress by animateFloatAsState(
        targetValue = if (isScanningInProgress) scanProgress else (if (isScanActive) 1f else 0f),
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "sanitizationScanProgressAnim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "scanLaserWave")
    val laserPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "laserSweepPhase"
    )

    QuantumGlassCard(
        borderColor = when {
            isScanningInProgress -> PhotonicCyan
            isScanActive -> OperationalEmerald.copy(alpha = 0.6f)
            else -> SpaceCobaltGlassBorder
        },
        backgroundColor = SpaceCobaltSurface,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header Row with Main Scan Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PhotonicSignalPulseIndicator(
                        signalColor = if (isScanActive) OperationalEmerald else SolarAmber,
                        size = 14.dp
                    )
                    Column {
                        Text(
                            text = "TELEMETRY SANITIZATION SCAN",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isScanActive) OperationalEmeraldLight else SolarAmber,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.1.sp
                        )
                        Text(
                            text = "Post-Quantum Enclave Sentinel",
                            style = MaterialTheme.typography.titleSmall,
                            color = AmbientWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Interactive UI Toggle Switch
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PhotonicBadge(
                        text = if (isScanActive) "SCANNING ACTIVE" else "PASSIVE MODE",
                        signalColor = if (isScanActive) OperationalEmerald else SolarAmber,
                        icon = if (isScanActive) Icons.Default.Shield else Icons.Default.PauseCircle
                    )

                    Switch(
                        checked = isScanActive,
                        onCheckedChange = { viewModel.setSanitizationScanActive(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = OperationalEmerald,
                            checkedTrackColor = OperationalEmeraldDark.copy(alpha = 0.5f),
                            checkedBorderColor = OperationalEmerald,
                            uncheckedThumbColor = SolarAmber,
                            uncheckedTrackColor = SpaceCobaltGlassElevated,
                            uncheckedBorderColor = SpaceCobaltGlassBorder
                        )
                    )
                }
            }

            // Animated Laser Scanning HUD Beam (When Active or Scanning)
            if (isScanActive || isScanningInProgress) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SpaceCobaltDark)
                        .border(1.dp, PhotonicCyan.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                ) {
                    // Moving photon beam canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Background grid lines
                        val step = canvasWidth / 12
                        for (i in 0..12) {
                            drawLine(
                                color = PhotonicCyan.copy(alpha = 0.15f),
                                start = Offset(i * step, 0f),
                                end = Offset(i * step, canvasHeight),
                                strokeWidth = 1f
                            )
                        }

                        // Progress bar fill
                        val fillWidth = canvasWidth * animatedProgress
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    OperationalEmeraldDark.copy(alpha = 0.4f),
                                    OperationalEmerald.copy(alpha = 0.6f),
                                    PhotonicCyan.copy(alpha = 0.8f)
                                )
                            ),
                            topLeft = Offset.Zero,
                            size = androidx.compose.ui.geometry.Size(fillWidth, canvasHeight)
                        )

                        // Laser sweep line
                        val laserX = (laserPhase * canvasWidth)
                        drawLine(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    PhotonicCyanLight,
                                    OperationalEmeraldLight,
                                    Color.Transparent
                                )
                            ),
                            start = Offset(laserX, 0f),
                            end = Offset(laserX, canvasHeight),
                            strokeWidth = 3f
                        )
                    }

                    // Status Text Overlay
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isScanningInProgress) scanPhase.label else "Continuous Enclave Attestation • 0 Leaks",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmbientWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 10.sp
                        )

                        Text(
                            text = "${(animatedProgress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = PhotonicCyanLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            // Post-Quantum Enclave Status Feedback Panel
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SpaceCobaltGlassElevated)
                    .border(
                        1.dp,
                        if (isScanActive) OperationalEmerald.copy(alpha = 0.35f) else SpaceCobaltGlassBorder,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EnhancedEncryption,
                                contentDescription = "Enclave Security",
                                tint = OperationalEmerald,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "POST-QUANTUM ENCLAVE STATUS",
                                style = MaterialTheme.typography.labelSmall,
                                color = OperationalEmeraldLight,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        PhotonicBadge(
                            text = "100% PQC RESISTANT",
                            signalColor = OperationalEmerald
                        )
                    }

                    // Enclave Hardware Details Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EnclaveInfoMiniTile(
                            label = "ALGORITHM",
                            value = enclaveKey.algorithm.substringBefore(" ("),
                            subvalue = "NIST FIPS 203/204",
                            accentColor = PhotonicCyan,
                            modifier = Modifier.weight(1f)
                        )
                        EnclaveInfoMiniTile(
                            label = "HARDWARE SLOT",
                            value = enclaveKey.hardwareSlot,
                            subvalue = "Physical eUICC Core",
                            accentColor = OperationalEmerald,
                            modifier = Modifier.weight(1f)
                        )
                        EnclaveInfoMiniTile(
                            label = "MEMORY BARRIER",
                            value = enclaveKey.memoryAddress,
                            subvalue = "Isolated & Bound",
                            accentColor = QuantumVioletLight,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Feedback Narrative / Scan Summary
                    scanFeedback?.let { feedback ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(SpaceCobaltDark)
                                .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "LAST SCAN: ${SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(feedback.timestamp))}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = AmbientWhiteMuted,
                                        fontSize = 9.sp
                                    )
                                    Text(
                                        text = "PROOF: ${feedback.dilithiumProofDigest.take(16)}...",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = OperationalEmerald,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp
                                    )
                                }
                                Text(
                                    text = feedback.summary,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AmbientWhite,
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }

            // Quick Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuantumVolumetricButton(
                    text = if (isScanningInProgress) "Scanning..." else "🔬 Run Deep Scan",
                    onClick = { viewModel.executeDeepSanitizationScan(isManualTrigger = true) },
                    primaryColor = PhotonicCyan,
                    secondaryColor = OperationalEmerald,
                    icon = Icons.Default.Radar,
                    modifier = Modifier.weight(1f)
                )

                QuantumVolumetricButton(
                    text = "🔐 Enclave HUD",
                    onClick = {
                        onOpenEnclaveHud?.invoke() ?: viewModel.openEnclaveOverlay()
                    },
                    primaryColor = OperationalEmerald,
                    secondaryColor = PhotonicCyan,
                    icon = Icons.Default.Lock,
                    modifier = Modifier.weight(1f)
                )

                QuantumVolumetricButton(
                    text = "🧹 Flush Buffer",
                    onClick = { viewModel.flushPerimeterBuffer() },
                    primaryColor = SolarAmber,
                    secondaryColor = ContainmentCrimson,
                    icon = Icons.Default.CleaningServices,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun EnclaveInfoMiniTile(
    label: String,
    value: String,
    subvalue: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(SpaceCobaltDark)
            .border(1.dp, accentColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
            .padding(6.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AmbientWhiteMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                color = accentColor,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                maxLines = 1
            )
            Text(
                text = subvalue,
                style = MaterialTheme.typography.labelSmall,
                color = AmbientWhiteMuted,
                fontSize = 8.sp,
                maxLines = 1
            )
        }
    }
}
