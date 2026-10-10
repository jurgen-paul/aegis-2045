package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.neural.HapticAestheticProfile
import com.example.neural.NeuralGestureIntentHandler
import com.example.neural.NeuralHapticFeedbackManager
import com.example.neural.NeuralPathwayNode
import com.example.neural.NeuralPathwayType
import com.example.neural.SynapticRouteActivation
import com.example.ui.modifiers.interceptNeuralGestures
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.sin

/**
 * AGIS-2045 Neural Gesture & Synaptic Pathway HUD.
 *
 * Displays real-time neural pathway propagation, synaptic membrane potentials,
 * confidence scoring, and an interactive gesture interceptor pad that drives navigation.
 */
@Composable
fun NeuralGesturePathwayHUD(
    handler: NeuralGestureIntentHandler,
    modifier: Modifier = Modifier,
    onNavigateToTab: ((Int) -> Unit)? = null
) {
    val activePathway by handler.activePathway.collectAsState()
    val lastIntent by handler.lastDecodedIntent.collectAsState()
    val pathwayHistory by handler.pathwayHistory.collectAsState()
    val gestureTrail by handler.gestureTrail.collectAsState()
    val isGestureActive by handler.isGestureActive.collectAsState()
    val isAutoNavEnabled by handler.isAutoNavigationEnabled.collectAsState()
    val sensitivity by handler.sensitivityMultiplier.collectAsState()

    // Breathing pulse for active neural nodes
    val infiniteTransition = rememberInfiniteTransition(label = "SynapticHudTransition")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseGlow"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SpaceCobaltGlass)
            .border(1.dp, PhotonicCyan.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("neural_gesture_pathway_hud")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PhotonicCyan.copy(alpha = 0.15f))
                        .border(1.dp, PhotonicCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Gesture,
                        contentDescription = "Neural Gesture Interceptor",
                        tint = PhotonicCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "NEURAL GESTURE INTENT HANDLER",
                        style = MaterialTheme.typography.titleMedium,
                        color = AmbientWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Real-time Spiking Pathway Router • AGIS 2045",
                        style = MaterialTheme.typography.labelSmall,
                        color = PhotonicCyanLight,
                        fontSize = 10.sp
                    )
                }
            }

            // Auto-Navigation Toggle Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(SpaceCobaltDark)
                    .border(
                        1.dp,
                        if (isAutoNavEnabled) OperationalEmerald else SpaceCobaltGlassBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { handler.setAutoNavigationEnabled(!isAutoNavEnabled) }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isAutoNavEnabled) OperationalEmerald else TextDimmed)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isAutoNavEnabled) "AUTO-NAV ON" else "TELEMETRY ONLY",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isAutoNavEnabled) OperationalEmeraldLight else AmbientWhiteMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // INTERACTIVE GESTURE INTERCEPTION TOUCHPAD
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SpaceCobaltDark.copy(alpha = 0.90f))
                .border(
                    width = if (isGestureActive) 1.5.dp else 1.dp,
                    color = if (isGestureActive) PhotonicCyan else SpaceCobaltGlassBorder,
                    shape = RoundedCornerShape(12.dp)
                )
                .interceptNeuralGestures(handler)
                .testTag("neural_gesture_interceptor_pad"),
            contentAlignment = Alignment.Center
        ) {
            // Background Canvas Drawing the Gestural Synaptic Trail & Waveguides
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Grid background
                val step = 20.dp.toPx()
                var x = step
                while (x < w) {
                    drawLine(
                        color = PhotonicCyan.copy(alpha = 0.04f),
                        start = Offset(x, 0f),
                        end = Offset(x, h),
                        strokeWidth = 1f
                    )
                    x += step
                }
                var y = step
                while (y < h) {
                    drawLine(
                        color = PhotonicCyan.copy(alpha = 0.04f),
                        start = Offset(0f, y),
                        end = Offset(w, y),
                        strokeWidth = 1f
                    )
                    y += step
                }

                // Synaptic Lightning Trail
                if (gestureTrail.size >= 2) {
                    for (i in 0 until gestureTrail.size - 1) {
                        val p1 = gestureTrail[i]
                        val p2 = gestureTrail[i + 1]
                        val progress = (i + 1).toFloat() / gestureTrail.size

                        // Photonic outer glow
                        drawLine(
                            color = PhotonicCyan.copy(alpha = progress * 0.45f),
                            start = p1,
                            end = p2,
                            strokeWidth = 6.dp.toPx() * progress,
                            cap = StrokeCap.Round
                        )
                        // Crisp inner energy core
                        drawLine(
                            color = AmbientWhite.copy(alpha = progress * 0.95f),
                            start = p1,
                            end = p2,
                            strokeWidth = 2.dp.toPx() * progress,
                            cap = StrokeCap.Round
                        )
                    }

                    // Spark flare at pointer head
                    val head = gestureTrail.last()
                    drawCircle(
                        color = PhotonicCyan,
                        radius = 8.dp.toPx(),
                        center = head
                    )
                    drawCircle(
                        color = AmbientWhite,
                        radius = 4.dp.toPx(),
                        center = head
                    )
                }
            }

            // HUD Center Guidance Overlay
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (isGestureActive) Icons.Default.TouchApp else Icons.Default.Swipe,
                    contentDescription = null,
                    tint = if (isGestureActive) PhotonicCyan else AmbientWhiteMuted,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isGestureActive) "SYNAPTIC TRACE IN PROGRESS..." else "SWIPE, FLING, TAP, OR HOLD TO FIRE PATHWAYS",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isGestureActive) PhotonicCyan else AmbientWhiteMuted,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "◀ Swipe L (Tab+)  •  Swipe R (Tab-)  •  ▲ Swipe Up (Enclave)  •  ▼ Swipe Down (Purge)",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteSubtle,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Live Firing Badge (Top-End)
            activePathway?.let { pathway ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(pathway.pathwayType.defaultColor.copy(alpha = 0.25f))
                        .border(1.dp, pathway.pathwayType.defaultColor, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "FIRED: ${pathway.pathwayType.pathwayCode}",
                        style = MaterialTheme.typography.labelSmall,
                        color = pathway.pathwayType.defaultColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 8.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // SYNAPTIC NODE PATHWAY DIAGRAM (Sensory -> Hidden -> Gating -> Executive)
        Text(
            text = "SYNAPTIC PATHWAY TOPOLOGY:",
            style = MaterialTheme.typography.labelSmall,
            color = AmbientWhiteSubtle,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val nodes = activePathway?.nodes ?: getDefaultTopologyNodes()

            nodes.forEachIndexed { index, node ->
                SynapticNodeCard(
                    node = node,
                    pulseGlow = if (node.isSpiking) pulseGlow else 0.3f,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // LIVE TELEMETRY MATRIX
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SpaceCobaltDark)
                .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val pathway = activePathway
            val confidence = pathway?.firingConfidence ?: 0.94f
            val latency = pathway?.synapticLatencyMs ?: 18L
            val spikeHz = pathway?.synapticSpikeHz ?: 86.4f
            val potential = pathway?.membranePotentialMv ?: 32.5f

            Column {
                Text("CONFIDENCE", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                Text(
                    text = "${(confidence * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = OperationalEmeraldLight,
                    fontFamily = FontFamily.Monospace
                )
            }
            Column {
                Text("SYNAPTIC LATENCY", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                Text(
                    text = "${latency} ms",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PhotonicCyan,
                    fontFamily = FontFamily.Monospace
                )
            }
            Column {
                Text("SPIKE FREQ", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                Text(
                    text = "${String.format("%.1f", spikeHz)} Hz",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = QuantumVioletLight,
                    fontFamily = FontFamily.Monospace
                )
            }
            Column {
                Text("MEMBRANE Vm", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                Text(
                    text = "+${String.format("%.1f", potential)} mV",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = SolarAmber,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        // Active Decoded Intent Action Banner
        lastIntent?.let { intent ->
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(intent.pathwayActivation.pathwayType.defaultColor.copy(alpha = 0.15f))
                    .border(1.dp, intent.pathwayActivation.pathwayType.defaultColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = null,
                        tint = intent.pathwayActivation.pathwayType.defaultColor,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "DECODED: ${intent.gestureType.title.uppercase()} → ${intent.pathwayActivation.pathwayType.pathwayName}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = intent.pathwayActivation.pathwayType.defaultColor,
                            fontSize = 9.sp
                        )
                        Text(
                            text = intent.statusMessage,
                            style = MaterialTheme.typography.bodySmall,
                            color = AmbientWhite,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // DIRECT SIMULATED PATHWAY INJECTION BUTTONS (Chips for rapid testing)
        Text(
            text = "DIRECT SYNAPTIC PATHWAY DISPATCH:",
            style = MaterialTheme.typography.labelSmall,
            color = AmbientWhiteSubtle,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val pathways = listOf(
                NeuralPathwayType.PROPAGATE_FORWARD to "Forward (+1)",
                NeuralPathwayType.PROPAGATE_BACKWARD to "Backward (-1)",
                NeuralPathwayType.ELEVATE_ENCLAVE to "Enclave Vault",
                NeuralPathwayType.TELEMETRY_PURGE to "Purge Telemetry",
                NeuralPathwayType.MACRO_OVERVIEW to "Overview",
                NeuralPathwayType.DEEP_NEURAL_STREAM to "Neural Command",
                NeuralPathwayType.AUTONOMOUS_VALIDATION to "Validate Proofs",
                NeuralPathwayType.ENTROPY_RESEED to "Reseed Entropy"
            )

            items(pathways) { (type, label) ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(type.defaultColor.copy(alpha = 0.18f))
                        .border(1.dp, type.defaultColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .clickable { handler.triggerSimulatedPathway(type) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("btn_trigger_pathway_${type.name}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(type.defaultColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            color = type.defaultColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // SENSITIVITY CALIBRATOR SLIDER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "GESTURE SENSITIVITY MULTIPLIER:",
                style = MaterialTheme.typography.labelSmall,
                color = AmbientWhiteSubtle,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${String.format("%.2f", sensitivity)}x Gain",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = PhotonicCyan,
                fontWeight = FontWeight.Bold
            )
        }

        Slider(
            value = sensitivity,
            onValueChange = { handler.setSensitivityMultiplier(it) },
            valueRange = 0.5f..2.5f,
            colors = SliderDefaults.colors(
                thumbColor = PhotonicCyan,
                activeTrackColor = PhotonicCyan,
                inactiveTrackColor = SpaceCobaltSurface
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("slider_gesture_sensitivity")
        )

        // HIGH-FIDELITY HAPTIC FEEDBACK CONTROLLER & TELEMETRY
        handler.hapticManager?.let { haptics ->
            val isHapticsEnabled by haptics.isHapticsEnabled.collectAsState()
            val hapticIntensity by haptics.hapticIntensity.collectAsState()
            val activeHapticProfile by haptics.activeProfile.collectAsState()
            val lastHapticPattern by haptics.lastTriggeredPattern.collectAsState()

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SpaceCobaltDark)
                    .border(1.dp, SpaceCobaltGlassBorder.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
                    .testTag("haptic_feedback_manager_panel")
            ) {
                // Header & Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Vibration,
                            contentDescription = "Haptic Feedback Engine",
                            tint = if (isHapticsEnabled) SolarAmber else TextDimmed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "HIGH-FIDELITY HAPTIC FEEDBACK",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isHapticsEnabled) AmbientWhite else TextDimmed,
                                fontSize = 10.sp
                            )
                            Text(
                                text = if (isHapticsEnabled) "Subtle Waveform Detents Active" else "Haptics Disabled",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isHapticsEnabled) SolarAmber else TextDimmed,
                                fontSize = 8.sp
                            )
                        }
                    }

                    Switch(
                        checked = isHapticsEnabled,
                        onCheckedChange = { haptics.setHapticsEnabled(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SolarAmber,
                            checkedTrackColor = SolarAmber.copy(alpha = 0.35f),
                            uncheckedThumbColor = TextDimmed,
                            uncheckedTrackColor = SpaceCobaltSurface
                        ),
                        modifier = Modifier
                            .scale(0.8f)
                            .testTag("switch_haptics_enabled")
                    )
                }

                if (isHapticsEnabled) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Aesthetic Profile Selector Chips
                    Text(
                        text = "TACTILE AESTHETIC PROFILE:",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmbientWhiteSubtle,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        HapticAestheticProfile.values().forEach { profile ->
                            val isSelected = activeHapticProfile == profile
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) SolarAmber.copy(alpha = 0.22f) else SpaceCobaltSurface)
                                    .border(
                                        1.dp,
                                        if (isSelected) SolarAmber else SpaceCobaltGlassBorder,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        haptics.setActiveProfile(profile)
                                        haptics.triggerThresholdTick()
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = profile.title.split(" ").first(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSelected) SolarAmber else AmbientWhiteMuted,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Intensity Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "VIBRATION INTENSITY:",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmbientWhiteSubtle,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${(hapticIntensity * 100).toInt()}% Amplitude",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = SolarAmber,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Slider(
                        value = hapticIntensity,
                        onValueChange = { haptics.setHapticIntensity(it) },
                        valueRange = 0.1f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = SolarAmber,
                            activeTrackColor = SolarAmber,
                            inactiveTrackColor = SpaceCobaltSurface
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("slider_haptic_intensity")
                    )

                    // Last Triggered Pattern Telemetry Pill & Test Pulse Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LAST: ${lastHapticPattern ?: "None"}",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = AmbientWhiteMuted,
                            fontSize = 8.sp,
                            maxLines = 1
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(SolarAmber.copy(alpha = 0.15f))
                                .border(1.dp, SolarAmber.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .clickable {
                                    haptics.triggerPathwayHaptic(NeuralPathwayType.PROPAGATE_FORWARD, 0.95f)
                                }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("btn_test_haptic_pulse")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = SolarAmber,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "TEST WAVEFORM",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SolarAmber,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Pathway History Collapsible/Snippet
        if (pathwayHistory.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "RECENT SYNAPTIC FIRINGS:",
                style = MaterialTheme.typography.labelSmall,
                color = AmbientWhiteSubtle,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))

            pathwayHistory.take(3).forEach { hist ->
                val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(hist.timestamp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "[$timeStr] ${hist.triggerGesture.title} → ${hist.pathwayType.pathwayCode}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = hist.pathwayType.defaultColor,
                        fontSize = 9.sp
                    )
                    Text(
                        text = "${(hist.firingConfidence * 100).toInt()}% • ${hist.synapticLatencyMs}ms",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = AmbientWhiteMuted,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

/**
 * Individual synaptic node card representing a layer in the spiking pathway.
 */
@Composable
private fun SynapticNodeCard(
    node: NeuralPathwayNode,
    pulseGlow: Float,
    modifier: Modifier = Modifier
) {
    val borderColor = if (node.isSpiking) PhotonicCyan.copy(alpha = pulseGlow) else SpaceCobaltGlassBorder
    val bgColor = if (node.isSpiking) PhotonicCyan.copy(alpha = 0.12f * pulseGlow) else SpaceCobaltDark

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = node.layerType,
                style = MaterialTheme.typography.labelSmall,
                color = if (node.isSpiking) PhotonicCyan else TextDimmed,
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${String.format("%.1f", node.membranePotentialMv)}mV",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = if (node.isSpiking) AmbientWhite else AmbientWhiteMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "w: ${String.format("%.2f", node.synapticWeight)}",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = TextDimmed,
                fontSize = 7.sp
            )
        }
    }
}

/**
 * Fallback static nodes for initial render before any pathway fires.
 */
private fun getDefaultTopologyNodes(): List<NeuralPathwayNode> {
    return listOf(
        NeuralPathwayNode("IN-01", "Sensory", "SENSORY", -65f, 0.85f, false),
        NeuralPathwayNode("HID-02", "Cortical", "HIDDEN", -62f, 0.92f, false),
        NeuralPathwayNode("GATE-03", "Zero-Trust", "GATING", -58f, 0.78f, false),
        NeuralPathwayNode("EXEC-04", "Motor", "EXECUTIVE", -70f, 0.88f, false)
    )
}
