package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HardwareSensorEntropyState
import com.example.ui.animation.PhotonicSignalPulseIndicator
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.theme.*

/**
 * Visual dashboard card for the Hardware Sensor CSPRNG Background Service.
 * Displays:
 * - Active physical sensor harvesters (Accelerometer, Gyro, Magnetometer, Barometer, Light)
 * - 512-bit Master entropy seed stream
 * - Shannon entropy estimate (bits/byte)
 * - Live sample counts and cycles
 * - Recent pseudo-random numbers assigned to system security protocols
 * - Interactive button to force an immediate high-entropy re-seed
 */
@Composable
fun HardwareSensorEntropyCard(
    entropyState: HardwareSensorEntropyState,
    onForceReseed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isWaveformExpanded by remember { mutableStateOf(true) }

    val infiniteTransition = rememberInfiniteTransition(label = "EntropyPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlow"
    )

    QuantumGlassCard(
        borderColor = if (entropyState.isRunning) OperationalEmerald.copy(alpha = 0.6f) else SolarAmber.copy(alpha = 0.5f),
        backgroundColor = SpaceCobaltGlassElevated,
        modifier = modifier.testTag("hardware_sensor_entropy_card")
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (entropyState.isRunning) OperationalEmeraldDark else SolarAmberDark)
                        .border(
                            1.dp,
                            if (entropyState.isRunning) OperationalEmerald.copy(alpha = pulseGlow) else SolarAmber,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Sensors,
                        contentDescription = "Hardware Sensors",
                        tint = if (entropyState.isRunning) OperationalEmeraldLight else SolarAmber,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "HARDWARE SENSOR CSPRNG SERVICE",
                        style = MaterialTheme.typography.labelSmall,
                        color = OperationalEmeraldLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "512-Bit Physical Entropy Engine",
                        style = MaterialTheme.typography.titleSmall,
                        color = AmbientWhite,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            PhotonicBadge(
                text = if (entropyState.isRunning) "LIVE HARVESTING" else "IDLE",
                signalColor = if (entropyState.isRunning) OperationalEmerald else SolarAmber,
                icon = if (entropyState.isRunning) Icons.Default.Bolt else Icons.Default.Pause,
                enablePulse = entropyState.isRunning
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Metrics Grid (Entropy quality & Cycles)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Shannon Entropy Metric
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "SHANNON ENTROPY",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteMuted,
                    fontSize = 9.sp
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format(java.util.Locale.US, "%.3f", entropyState.estimatedShannonEntropy),
                        style = MaterialTheme.typography.titleMedium,
                        color = PhotonicCyan,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "/ 8.0 bits",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmbientWhiteMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // Total Cycles Harvested
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "HARVEST CYCLES",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteMuted,
                    fontSize = 9.sp
                )
                Text(
                    text = "#${entropyState.totalCyclesHarvested}",
                    style = MaterialTheme.typography.titleMedium,
                    color = OperationalEmeraldLight,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Pool Entropy Bits
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    text = "ENTROPY POOL",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteMuted,
                    fontSize = 9.sp
                )
                Text(
                    text = "${entropyState.poolEntropyBits}-bit",
                    style = MaterialTheme.typography.titleMedium,
                    color = QuantumVioletLight,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Active Hardware Sensors Chip List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SpaceCobaltSurface)
                .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ATTACHED PHYSICAL HARVESTERS",
                    style = MaterialTheme.typography.labelSmall,
                    color = PhotonicCyanLight,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${entropyState.sampleCount} raw events",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (entropyState.activeSensors.isEmpty()) {
                Text(
                    text = "Initializing sensor listeners...",
                    style = MaterialTheme.typography.bodySmall,
                    color = AmbientWhiteMuted
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    entropyState.activeSensors.take(4).forEach { sensorName ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PhotonicSignalPulseIndicator(
                                signalColor = OperationalEmerald,
                                size = 6.dp,
                                pulseSpeedMs = 900
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = sensorName,
                                style = MaterialTheme.typography.bodySmall,
                                color = AmbientWhite,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Real-Time Oscilloscope Diagnostic Waveform Section Header / Accordion
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SpaceCobaltSurface)
                .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                .clickable { isWaveformExpanded = !isWaveformExpanded }
                .padding(horizontal = 10.dp, vertical = 8.dp)
                .testTag("toggle_entropy_waveform_section"),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.GraphicEq,
                    contentDescription = null,
                    tint = OperationalEmeraldLight,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REAL-TIME OSCILLOSCOPE WAVEFORM",
                    style = MaterialTheme.typography.labelSmall,
                    color = OperationalEmeraldLight,
                    fontWeight = FontWeight.Bold
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isWaveformExpanded) "COLLAPSE" else "EXPAND GRAPH",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteMuted,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = if (isWaveformExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isWaveformExpanded) "Collapse" else "Expand",
                    tint = AmbientWhiteMuted,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        AnimatedVisibility(
            visible = isWaveformExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column {
                Spacer(modifier = Modifier.height(8.dp))
                HardwareSensorWaveformVisualizer(
                    entropyState = entropyState,
                    onForceReseed = onForceReseed,
                    wrapInCard = false
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Master 512-bit Seed Display
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SpaceCobaltDark)
                .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                .padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LATEST MASTER SEED (NIST HMAC-SHA512 + HKDF)",
                    style = MaterialTheme.typography.labelSmall,
                    color = OperationalEmeraldLight,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = null,
                    tint = OperationalEmerald,
                    modifier = Modifier.size(14.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (entropyState.latestHexSeed.isNotEmpty()) entropyState.latestHexSeed else "HARVESTING_HARDWARE_ENTROPY_STREAM...",
                style = MaterialTheme.typography.bodySmall,
                fontFamily = FontFamily.Monospace,
                color = PhotonicCyan,
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Recent Generated CSPRNG Samples Preview
        if (entropyState.recentGeneratedNumbers.isNotEmpty()) {
            val latest = entropyState.recentGeneratedNumbers.first()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LAST GENERATED RANDOM SAMPLE",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmbientWhiteMuted,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "0x${latest.randomHex.take(16)}...",
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = SolarAmber,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Assigned: ${latest.securityProtocolAssigned}",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmbientWhiteMuted,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = "Float: ${String.format(java.util.Locale.US, "%.5f", latest.randomFloatUnit)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = PhotonicCyanLight,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // Action Button: Force Immediate Hardware Reseed
        QuantumVolumetricButton(
            text = "POLL SENSORS & FORCE CSPRNG RE-SEED",
            icon = Icons.Default.Refresh,
            primaryColor = OperationalEmerald,
            secondaryColor = PhotonicCyan,
            containerColor = SpaceCobaltDark,
            modifier = Modifier.fillMaxWidth().testTag("force_csprng_reseed_button"),
            shapeRadius = 8.dp,
            onClick = onForceReseed
        )
    }
}
