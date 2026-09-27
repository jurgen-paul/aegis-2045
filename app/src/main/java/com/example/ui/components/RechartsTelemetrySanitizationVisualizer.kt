package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TelemetryThroughputPoint
import com.example.ui.theme.*
import com.example.viewmodel.AgisViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.*

/**
 * Recharts-Inspired Telemetry Sanitization & Cyber-Node Data Flow Visualizer.
 *
 * Implements the declarative Recharts paradigm in Jetpack Compose:
 * - <ResponsiveContainer> with dynamic aspect ratio and padding
 * - <AreaChart> / <LineChart> with Monotone Cubic Bézier Spline Curves
 * - <defs><linearGradient> glowing area fills (Ingress Amber, Egress Emerald, Noise Cyan)
 * - <CartesianGrid strokeDasharray="3 3"> with dynamic horizontal/vertical tick lines
 * - <XAxis> & <YAxis> coordinate ticks with unit scaling
 * - <Tooltip content={<RechartsTooltip />}> interactive crosshair & hover card that follows user touch
 * - <Legend> with interactive series visibility toggling
 * - Interactive Cyber-Node Data Flow Pipeline simulating telemetry packets traversing the 5 sanitization stages:
 *   [Raw Ingress] -> [5-Stage Shield] -> [DP Noise Injector (ε=0.5)] -> [Token Redactor] -> [Authenticated Egress]
 */
@Composable
fun RechartsTelemetrySanitizationVisualizer(
    viewModel: AgisViewModel,
    modifier: Modifier = Modifier
) {
    val throughputHistory by viewModel.telemetryThroughputHistory.collectAsState()
    val currentThroughput by viewModel.currentThroughputPoint.collectAsState()
    val isBursting by viewModel.isThroughputBursting.collectAsState()
    val sanitizationPhase by viewModel.sanitizationScanPhase.collectAsState()
    val sanitizationProgress by viewModel.sanitizationScanProgress.collectAsState()
    val isScanning by viewModel.isSanitizationScanningInProgress.collectAsState()

    // Recharts View Modes
    var activeVisualizationTab by remember { mutableStateOf(RechartsTabMode.MONOTONE_AREA_CHART) }

    // Recharts Series Legend Toggles
    var showRawIngressSeries by remember { mutableStateOf(true) }
    var showSanitizedEgressSeries by remember { mutableStateOf(true) }
    var showTokenScrubSeries by remember { mutableStateOf(true) }
    var showDifferentialNoiseSeries by remember { mutableStateOf(false) }

    // Interactive Crosshair & Tooltip Scrubbing State
    var scrubbedFraction by remember { mutableStateOf<Float?>(null) }
    val scrubbedPoint = remember(scrubbedFraction, throughputHistory) {
        val frac = scrubbedFraction
        if (frac != null && throughputHistory.isNotEmpty()) {
            val idx = (frac * (throughputHistory.size - 1)).roundToInt().coerceIn(0, throughputHistory.size - 1)
            throughputHistory[idx]
        } else {
            null
        }
    }

    // Continuous flow animation ticker for cyber-node travelling photon packets
    val infiniteTransition = rememberInfiniteTransition(label = "recharts_flow_pulse")
    val flowPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "flow_phase"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    QuantumGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recharts_telemetry_visualizer_card"),
        borderColor = if (isBursting) SolarAmber else PhotonicCyan.copy(alpha = 0.6f),
        backgroundColor = SpaceCobaltGlassElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            // Header Bar: Recharts Paradigm Branding & Live Metric Badges
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
                            .background(
                                if (isBursting) SolarAmber.copy(alpha = 0.2f)
                                else PhotonicCyan.copy(alpha = 0.15f)
                            )
                            .border(
                                1.dp,
                                if (isBursting) SolarAmber else PhotonicCyan.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = "Recharts Data Visualization",
                            tint = if (isBursting) SolarAmber else PhotonicCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "RECHARTS TELEMETRY VISUALIZER",
                                style = MaterialTheme.typography.labelSmall,
                                color = PhotonicCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(OperationalEmerald.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "LIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = OperationalEmerald,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }

                        Text(
                            text = "Real-Time Cyber-Node Sanitization & Flow Stream",
                            style = MaterialTheme.typography.titleSmall,
                            color = AmbientWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Quick Mode or Attenuation Gauge
                val activePoint = scrubbedPoint ?: currentThroughput
                val attenuationPct = if (activePoint.rawThroughputKbps > 0f) {
                    ((1f - (activePoint.sanitizedThroughputKbps / activePoint.rawThroughputKbps)) * 100f).coerceIn(0f, 100f)
                } else 0f

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = String.format(Locale.US, "-%.1f%%", attenuationPct),
                        style = MaterialTheme.typography.titleMedium,
                        color = OperationalEmerald,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "LEAK ATTENUATION",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 8.sp,
                        color = AmbientWhiteMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Recharts Navigation Tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltDark.copy(alpha = 0.6f))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                RechartsTabMode.values().forEach { mode ->
                    val isSelected = activeVisualizationTab == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) PhotonicCyan.copy(alpha = 0.2f)
                                else Color.Transparent
                            )
                            .border(
                                width = if (isSelected) 1.dp else 0.dp,
                                color = if (isSelected) PhotonicCyan.copy(alpha = 0.8f) else Color.Transparent,
                                shape = RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                activeVisualizationTab = mode
                                scrubbedFraction = null // reset scrub
                            }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.label,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (isSelected) PhotonicCyan else AmbientWhiteMuted,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Recharts Interactive Legend (Filter series on demand)
            RechartsInteractiveLegend(
                showRawIngress = showRawIngressSeries,
                onToggleRawIngress = { showRawIngressSeries = !showRawIngressSeries },
                showSanitizedEgress = showSanitizedEgressSeries,
                onToggleSanitizedEgress = { showSanitizedEgressSeries = !showSanitizedEgressSeries },
                showTokenScrub = showTokenScrubSeries,
                onToggleTokenScrub = { showTokenScrubSeries = !showTokenScrubSeries },
                showDifferentialNoise = showDifferentialNoiseSeries,
                onToggleDifferentialNoise = { showDifferentialNoiseSeries = !showDifferentialNoiseSeries },
                activePoint = scrubbedPoint ?: currentThroughput
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Primary Visualization Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(SpaceCobaltSurface.copy(alpha = 0.7f))
                    .border(1.dp, SpaceCobaltGlassBorder.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .testTag("recharts_canvas_container")
            ) {
                when (activeVisualizationTab) {
                    RechartsTabMode.MONOTONE_AREA_CHART -> {
                        RechartsMonotoneAreaCanvas(
                            history = throughputHistory,
                            showRaw = showRawIngressSeries,
                            showSanitized = showSanitizedEgressSeries,
                            showTokenScrub = showTokenScrubSeries,
                            showNoise = showDifferentialNoiseSeries,
                            flowPhase = flowPhase,
                            glowPulse = glowPulse,
                            scrubbedFraction = scrubbedFraction,
                            onScrubChanged = { scrubbedFraction = it }
                        )
                    }
                    RechartsTabMode.CYBER_NODE_DATA_FLOW -> {
                        CyberNodeDataFlowCanvas(
                            currentPoint = currentThroughput,
                            flowPhase = flowPhase,
                            glowPulse = glowPulse,
                            isBursting = isBursting
                        )
                    }
                    RechartsTabMode.DIFFERENTIAL_SANITY_SPLINE -> {
                        RechartsDifferentialSplineCanvas(
                            history = throughputHistory,
                            flowPhase = flowPhase,
                            glowPulse = glowPulse,
                            scrubbedFraction = scrubbedFraction,
                            onScrubChanged = { scrubbedFraction = it }
                        )
                    }
                }

                // Floating Recharts Tooltip Box (Appears when operator touches or scrubs the chart)
                val activeHover = scrubbedPoint
                if (activeHover != null && activeVisualizationTab != RechartsTabMode.CYBER_NODE_DATA_FLOW) {
                    RechartsFloatingTooltipBox(
                        point = activeHover,
                        modifier = Modifier
                            .align(
                                if ((scrubbedFraction ?: 0.5f) > 0.55f) Alignment.TopStart
                                else Alignment.TopEnd
                            )
                            .padding(10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Cyber-Node Sanitization Flow Pipeline Stage Strip
            CyberNodeSanitizationStagesStrip(
                currentPoint = currentThroughput,
                isBursting = isBursting,
                sanitizationProgress = sanitizationProgress
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive Stream Action Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.triggerTelemetryBurst() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("recharts_btn_burst"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isBursting) SolarAmber.copy(alpha = 0.3f) else SpaceCobaltDark
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isBursting) SolarAmber else SpaceCobaltGlassBorder
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "Trigger Burst",
                        tint = if (isBursting) SolarAmber else PhotonicCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isBursting) "STREAM BURSTING" else "TRIGGER BURST",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isBursting) SolarAmber else AmbientWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = { viewModel.flushPerimeterBuffer() },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("recharts_btn_flush"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpaceCobaltDark
                    ),
                    border = BorderStroke(1.dp, OperationalEmerald.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = "Flush Buffer",
                        tint = OperationalEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "FLUSH BUFFER",
                        style = MaterialTheme.typography.labelSmall,
                        color = OperationalEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }

                Button(
                    onClick = { viewModel.executeDeepSanitizationScan(isManualTrigger = true) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("recharts_btn_scan"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SpaceCobaltDark
                    ),
                    border = BorderStroke(1.dp, QuantumVioletLight.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Scan Stream",
                        tint = QuantumVioletLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isScanning) "SCANNING..." else "SCAN 5-STAGE",
                        style = MaterialTheme.typography.labelSmall,
                        color = QuantumVioletLight,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

/**
 * Recharts Mode Tabs Enum.
 */
enum class RechartsTabMode(val label: String) {
    MONOTONE_AREA_CHART("RECHARTS AREA"),
    CYBER_NODE_DATA_FLOW("CYBER-NODE FLOW"),
    DIFFERENTIAL_SANITY_SPLINE("LEAK ATTENUATION")
}

/**
 * Recharts Legend Component with interactive series pills.
 */
@Composable
private fun RechartsInteractiveLegend(
    showRawIngress: Boolean,
    onToggleRawIngress: () -> Unit,
    showSanitizedEgress: Boolean,
    onToggleSanitizedEgress: () -> Unit,
    showTokenScrub: Boolean,
    onToggleTokenScrub: () -> Unit,
    showDifferentialNoise: Boolean,
    onToggleDifferentialNoise: () -> Unit,
    activePoint: TelemetryThroughputPoint
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Legend Pill 1: Raw Ingress
        RechartsLegendPill(
            label = "RAW INGRESS",
            value = "${activePoint.rawThroughputKbps.roundToInt()} KB/s",
            color = SolarAmber,
            isActive = showRawIngress,
            onClick = onToggleRawIngress
        )

        // Legend Pill 2: Sanitized Egress
        RechartsLegendPill(
            label = "SANITIZED",
            value = "${activePoint.sanitizedThroughputKbps.roundToInt()} KB/s",
            color = OperationalEmerald,
            isActive = showSanitizedEgress,
            onClick = onToggleSanitizedEgress
        )

        // Legend Pill 3: Token Scrub Rate
        RechartsLegendPill(
            label = "PII REDACTED",
            value = "${activePoint.piiScrubbedRate}/s",
            color = QuantumVioletLight,
            isActive = showTokenScrub,
            onClick = onToggleTokenScrub
        )

        // Legend Pill 4: Differential Privacy Noise
        RechartsLegendPill(
            label = "ε NOISE",
            value = "0.50",
            color = PhotonicCyan,
            isActive = showDifferentialNoise,
            onClick = onToggleDifferentialNoise
        )
    }
}

@Composable
private fun RechartsLegendPill(
    label: String,
    value: String,
    color: Color,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (isActive) color.copy(alpha = 0.12f) else SpaceCobaltDark.copy(alpha = 0.4f))
            .border(
                width = 1.dp,
                color = if (isActive) color.copy(alpha = 0.6f) else AmbientWhiteSubtle.copy(alpha = 0.2f),
                shape = RoundedCornerShape(4.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(if (isActive) color else AmbientWhiteSubtle)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 7.sp,
                color = if (isActive) AmbientWhite else AmbientWhiteSubtle,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = if (isActive) color else AmbientWhiteMuted,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

/**
 * Recharts Monotone Cubic Bézier Area Canvas.
 * Implements Recharts' signature smooth monotone spline algorithm, grid lines, and interactive crosshair.
 */
@Composable
private fun RechartsMonotoneAreaCanvas(
    history: List<TelemetryThroughputPoint>,
    showRaw: Boolean,
    showSanitized: Boolean,
    showTokenScrub: Boolean,
    showNoise: Boolean,
    flowPhase: Float,
    glowPulse: Float,
    scrubbedFraction: Float?,
    onScrubChanged: (Float?) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        val padLeft = 46.dp.toPx()
                        val padRight = 14.dp.toPx()
                        val chartW = size.width - padLeft - padRight
                        if (chartW > 0) {
                            val frac = ((offset.x - padLeft) / chartW).coerceIn(0f, 1f)
                            onScrubChanged(frac)
                        }
                    },
                    onTap = {
                        // clear scrub on simple tap toggle
                        onScrubChanged(null)
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val padLeft = 46.dp.toPx()
                        val padRight = 14.dp.toPx()
                        val chartW = size.width - padLeft - padRight
                        if (chartW > 0) {
                            val frac = ((offset.x - padLeft) / chartW).coerceIn(0f, 1f)
                            onScrubChanged(frac)
                        }
                    },
                    onDragEnd = {
                        // keep the point inspected
                    },
                    onDragCancel = {
                        onScrubChanged(null)
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        val padLeft = 46.dp.toPx()
                        val padRight = 14.dp.toPx()
                        val chartW = size.width - padLeft - padRight
                        if (chartW > 0) {
                            val frac = ((change.position.x - padLeft) / chartW).coerceIn(0f, 1f)
                            onScrubChanged(frac)
                        }
                    }
                )
            }
    ) {
        val w = size.width
        val h = size.height
        val padLeft = 46.dp.toPx()
        val padRight = 14.dp.toPx()
        val padTop = 20.dp.toPx()
        val padBottom = 24.dp.toPx()

        val chartW = w - padLeft - padRight
        val chartH = h - padTop - padBottom

        if (chartW <= 0 || chartH <= 0) return@Canvas

        // 1. Determine dynamic Y-axis maximum scale
        val maxRaw = if (history.isNotEmpty()) history.maxOf { it.rawThroughputKbps } else 1000f
        val maxScale = (max(maxRaw, 800f) * 1.15f).coerceAtLeast(600f)

        // 2. Draw Recharts CartesianGrid: Horizontal Grid Lines & Y-Axis Labels
        val yTicks = 4
        for (i in 0..yTicks) {
            val frac = i.toFloat() / yTicks
            val y = padTop + chartH * (1f - frac)
            val tickValue = (maxScale * frac).roundToInt()

            // Cartesian Grid Line (Recharts strokeDasharray="3 3")
            drawLine(
                color = SpaceCobaltGlassBorder.copy(alpha = 0.25f),
                start = Offset(padLeft, y),
                end = Offset(w - padRight, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Y-Axis Tick Text
            drawText(
                textMeasurer = textMeasurer,
                text = "${tickValue}k",
                topLeft = Offset(8.dp.toPx(), y - 7.dp.toPx()),
                style = TextStyle(
                    color = AmbientWhiteSubtle,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        // 3. Draw Vertical Cartesian Grid Lines & X-Axis Time Labels
        val xTicks = 4
        for (i in 0..xTicks) {
            val frac = i.toFloat() / xTicks
            val x = padLeft + chartW * frac

            drawLine(
                color = SpaceCobaltGlassBorder.copy(alpha = 0.15f),
                start = Offset(x, padTop),
                end = Offset(x, h - padBottom),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            )

            // Time label estimation
            val timeOffsetSec = ((1f - frac) * 24).roundToInt()
            val labelText = if (timeOffsetSec == 0) "NOW" else "-${timeOffsetSec}s"
            drawText(
                textMeasurer = textMeasurer,
                text = labelText,
                topLeft = Offset(x - 10.dp.toPx(), h - 18.dp.toPx()),
                style = TextStyle(
                    color = AmbientWhiteSubtle,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        if (history.size < 2) return@Canvas

        val stepX = chartW / (history.size - 1)
        val rawPoints = mutableListOf<Offset>()
        val sanitizedPoints = mutableListOf<Offset>()
        val scrubPoints = mutableListOf<Offset>()

        history.forEachIndexed { index, pt ->
            val px = padLeft + (index * stepX)
            val rawY = padTop + chartH * (1f - (pt.rawThroughputKbps / maxScale).coerceIn(0f, 1f))
            val sanitizedY = padTop + chartH * (1f - (pt.sanitizedThroughputKbps / maxScale).coerceIn(0f, 1f))
            // Scale token scrub (usually 0-40) onto chart bottom 30%
            val scrubY = padTop + chartH * (1f - ((pt.piiScrubbedRate.toFloat() / 50f) * 0.35f).coerceIn(0f, 0.4f))

            rawPoints.add(Offset(px, rawY))
            sanitizedPoints.add(Offset(px, sanitizedY))
            scrubPoints.add(Offset(px, scrubY))
        }

        // 4. Draw Recharts Monotone Area: Raw Ingress (Amber Gradient)
        if (showRaw) {
            val rawSpline = buildMonotoneSplinePath(rawPoints)
            val rawArea = Path().apply {
                addPath(rawSpline)
                lineTo(rawPoints.last().x, padTop + chartH)
                lineTo(rawPoints.first().x, padTop + chartH)
                close()
            }

            drawPath(
                path = rawArea,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SolarAmber.copy(alpha = 0.28f),
                        SolarAmber.copy(alpha = 0.05f),
                        Color.Transparent
                    ),
                    startY = padTop,
                    endY = padTop + chartH
                )
            )

            // Monotone Curve Line Stroke
            drawPath(
                path = rawSpline,
                color = SolarAmber,
                style = Stroke(
                    width = 2.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // 5. Draw Recharts Monotone Area: Sanitized Egress (Emerald Gradient)
        if (showSanitized) {
            val sanitizedSpline = buildMonotoneSplinePath(sanitizedPoints)
            val sanitizedArea = Path().apply {
                addPath(sanitizedSpline)
                lineTo(sanitizedPoints.last().x, padTop + chartH)
                lineTo(sanitizedPoints.first().x, padTop + chartH)
                close()
            }

            drawPath(
                path = sanitizedArea,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        OperationalEmerald.copy(alpha = 0.38f),
                        OperationalEmerald.copy(alpha = 0.08f),
                        Color.Transparent
                    ),
                    startY = padTop,
                    endY = padTop + chartH
                )
            )

            drawPath(
                path = sanitizedSpline,
                color = OperationalEmerald,
                style = Stroke(
                    width = 2.5.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
        }

        // 6. Draw Token Scrub Spline (Violet line with dashed stroke)
        if (showTokenScrub) {
            val scrubSpline = buildMonotoneSplinePath(scrubPoints)
            drawPath(
                path = scrubSpline,
                color = QuantumVioletLight.copy(alpha = 0.85f),
                style = Stroke(
                    width = 1.8.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f), 0f)
                )
            )
        }

        // 7. Draw Differential Noise Threshold Line (ε = 0.50 baseline)
        if (showNoise) {
            val noiseY = padTop + chartH * 0.72f
            drawLine(
                color = PhotonicCyan,
                start = Offset(padLeft, noiseY),
                end = Offset(w - padRight, noiseY),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            )
            drawCircle(
                color = PhotonicCyan,
                radius = 3.dp.toPx(),
                center = Offset(padLeft + chartW * flowPhase, noiseY)
            )
        }

        // 8. Draw Animated Pulse Bead Travelling Along the Sanitized Spline
        if (showSanitized && sanitizedPoints.isNotEmpty()) {
            val beadFraction = flowPhase
            val sampleIdx = (beadFraction * (sanitizedPoints.size - 1)).toInt().coerceIn(0, sanitizedPoints.size - 2)
            val subFrac = (beadFraction * (sanitizedPoints.size - 1)) - sampleIdx
            val pA = sanitizedPoints[sampleIdx]
            val pB = sanitizedPoints[sampleIdx + 1]
            val beadX = pA.x + (pB.x - pA.x) * subFrac
            val beadY = pA.y + (pB.y - pA.y) * subFrac

            // Glowing halo
            drawCircle(
                color = OperationalEmerald.copy(alpha = glowPulse * 0.5f),
                radius = 10.dp.toPx(),
                center = Offset(beadX, beadY)
            )
            // Core
            drawCircle(
                color = OperationalEmeraldLight,
                radius = 4.dp.toPx(),
                center = Offset(beadX, beadY)
            )
        }

        // 9. Interactive Recharts Crosshair Cursor Indicator
        if (scrubbedFraction != null) {
            val scrubX = padLeft + chartW * scrubbedFraction.coerceIn(0f, 1f)

            // Vertical crosshair line (Recharts Cursor)
            drawLine(
                color = PhotonicCyan.copy(alpha = 0.9f),
                start = Offset(scrubX, padTop),
                end = Offset(scrubX, padTop + chartH),
                strokeWidth = 1.5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(5f, 3f), 0f)
            )

            // Find closest data point
            val idx = (scrubbedFraction * (history.size - 1)).roundToInt().coerceIn(0, history.size - 1)
            val pt = history[idx]

            // Highlight node anchors on active series
            if (showRaw) {
                val rawY = padTop + chartH * (1f - (pt.rawThroughputKbps / maxScale).coerceIn(0f, 1f))
                drawCircle(color = SolarAmber, radius = 5.dp.toPx(), center = Offset(scrubX, rawY))
                drawCircle(color = SpaceCobaltDark, radius = 2.5.dp.toPx(), center = Offset(scrubX, rawY))
            }

            if (showSanitized) {
                val sanY = padTop + chartH * (1f - (pt.sanitizedThroughputKbps / maxScale).coerceIn(0f, 1f))
                drawCircle(color = OperationalEmerald, radius = 5.dp.toPx(), center = Offset(scrubX, sanY))
                drawCircle(color = SpaceCobaltDark, radius = 2.5.dp.toPx(), center = Offset(scrubX, sanY))
            }
        }
    }
}

/**
 * Recharts Differential Spline Canvas.
 * Highlights the exact delta area between Raw Ingress and Sanitized Egress.
 */
@Composable
private fun RechartsDifferentialSplineCanvas(
    history: List<TelemetryThroughputPoint>,
    flowPhase: Float,
    glowPulse: Float,
    scrubbedFraction: Float?,
    onScrubChanged: (Float?) -> Unit
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        val padLeft = 46.dp.toPx()
                        val padRight = 14.dp.toPx()
                        val chartW = size.width - padLeft - padRight
                        if (chartW > 0) {
                            onScrubChanged(((offset.x - padLeft) / chartW).coerceIn(0f, 1f))
                        }
                    },
                    onDragEnd = { /* keep point */ },
                    onDragCancel = { onScrubChanged(null) },
                    onDrag = { change, _ ->
                        change.consume()
                        val padLeft = 46.dp.toPx()
                        val padRight = 14.dp.toPx()
                        val chartW = size.width - padLeft - padRight
                        if (chartW > 0) {
                            onScrubChanged(((change.position.x - padLeft) / chartW).coerceIn(0f, 1f))
                        }
                    }
                )
            }
    ) {
        val w = size.width
        val h = size.height
        val padLeft = 46.dp.toPx()
        val padRight = 14.dp.toPx()
        val padTop = 20.dp.toPx()
        val padBottom = 24.dp.toPx()
        val chartW = w - padLeft - padRight
        val chartH = h - padTop - padBottom

        if (chartW <= 0 || chartH <= 0 || history.size < 2) return@Canvas

        val maxRaw = history.maxOf { it.rawThroughputKbps }
        val maxScale = (max(maxRaw, 800f) * 1.15f).coerceAtLeast(600f)

        // Draw background grid lines
        for (i in 0..3) {
            val y = padTop + chartH * (1f - (i / 3f))
            drawLine(
                color = SpaceCobaltGlassBorder.copy(alpha = 0.2f),
                start = Offset(padLeft, y),
                end = Offset(w - padRight, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            )
        }

        val stepX = chartW / (history.size - 1)
        val rawPoints = mutableListOf<Offset>()
        val sanPoints = mutableListOf<Offset>()

        history.forEachIndexed { idx, pt ->
            val px = padLeft + idx * stepX
            val rawY = padTop + chartH * (1f - (pt.rawThroughputKbps / maxScale).coerceIn(0f, 1f))
            val sanY = padTop + chartH * (1f - (pt.sanitizedThroughputKbps / maxScale).coerceIn(0f, 1f))
            rawPoints.add(Offset(px, rawY))
            sanPoints.add(Offset(px, sanY))
        }

        // Build difference ribbon (between Raw Ingress and Sanitized Egress)
        val diffPath = Path().apply {
            moveTo(rawPoints.first().x, rawPoints.first().y)
            for (i in 1 until rawPoints.size) {
                lineTo(rawPoints[i].x, rawPoints[i].y)
            }
            for (i in sanPoints.indices.reversed()) {
                lineTo(sanPoints[i].x, sanPoints[i].y)
            }
            close()
        }

        // Fill scrubbed delta ribbon with shaded warning-to-safe gradient
        drawPath(
            path = diffPath,
            brush = Brush.verticalGradient(
                colors = listOf(
                    SolarAmber.copy(alpha = 0.35f),
                    PhotonicCyan.copy(alpha = 0.15f)
                ),
                startY = padTop,
                endY = padTop + chartH
            )
        )

        // Draw Ingress and Egress Lines
        val rawPath = buildMonotoneSplinePath(rawPoints)
        val sanPath = buildMonotoneSplinePath(sanPoints)

        drawPath(path = rawPath, color = SolarAmber, style = Stroke(width = 2.dp.toPx()))
        drawPath(path = sanPath, color = OperationalEmerald, style = Stroke(width = 2.5.dp.toPx()))

        // Label Difference Ribbon
        val midIdx = history.size / 2
        val midX = rawPoints[midIdx].x
        val midY = (rawPoints[midIdx].y + sanPoints[midIdx].y) / 2f

        drawText(
            textMeasurer = textMeasurer,
            text = "SCRUBBED LEAK DELTA (PII / PROMPT TAINT)",
            topLeft = Offset(midX - 70.dp.toPx(), midY - 6.dp.toPx()),
            style = TextStyle(
                color = SolarAmber,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        )

        // Scrub cursor
        if (scrubbedFraction != null) {
            val scrubX = padLeft + chartW * scrubbedFraction.coerceIn(0f, 1f)
            drawLine(
                color = PhotonicCyan,
                start = Offset(scrubX, padTop),
                end = Offset(scrubX, padTop + chartH),
                strokeWidth = 1.5.dp.toPx()
            )
        }
    }
}

/**
 * Visualizing the Flow of Data Through the Cyber-Node Canvas.
 * Shows 5 distinct cyber-node hardware stages with traveling photon packets.
 */
@Composable
private fun CyberNodeDataFlowCanvas(
    currentPoint: TelemetryThroughputPoint,
    flowPhase: Float,
    glowPulse: Float,
    isBursting: Boolean
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .testTag("cyber_node_flow_canvas")
    ) {
        val w = size.width
        val h = size.height
        val centerY = h * 0.44f

        val nodeCount = 5
        val nodeLabels = listOf(
            "INGRESS",
            "SHIELD",
            "DP NOISE",
            "REDACTOR",
            "EGRESS"
        )
        val nodeSubtitles = listOf(
            "Perimeter",
            "5-Stage",
            "ε=0.50",
            "PII Zero",
            "Kyber-1024"
        )
        val nodeColors = listOf(
            SolarAmber,
            ContainmentCrimsonLight,
            PhotonicCyan,
            QuantumVioletLight,
            OperationalEmerald
        )

        val padLeft = 32.dp.toPx()
        val padRight = 32.dp.toPx()
        val stepX = (w - padLeft - padRight) / (nodeCount - 1)

        val nodeCenters = (0 until nodeCount).map { i ->
            Offset(padLeft + i * stepX, centerY)
        }

        // 1. Draw Inter-Node Connecting Bus Bézier Curves
        for (i in 0 until nodeCount - 1) {
            val p0 = nodeCenters[i]
            val p1 = nodeCenters[i + 1]
            val path = Path().apply {
                moveTo(p0.x, p0.y)
                val midX = (p0.x + p1.x) / 2f
                cubicTo(midX, p0.y - 12.dp.toPx(), midX, p1.y + 12.dp.toPx(), p1.x, p1.y)
            }

            // Background Bus Line
            drawPath(
                path = path,
                color = SpaceCobaltGlassBorder.copy(alpha = 0.5f),
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            // Animated Traveling Photon Flow Packets (simulates data flowing through the cyber node)
            val packetCount = 3
            for (k in 0 until packetCount) {
                val t = (flowPhase + (k.toFloat() / packetCount)) % 1f
                val packetX = p0.x + (p1.x - p0.x) * t
                val packetY = p0.y + (p1.y - p0.y) * t + sin(t * Math.PI).toFloat() * -10.dp.toPx()

                // Color shifts from Amber -> Crimson -> Cyan -> Violet -> Emerald
                val packetColor = when (i) {
                    0 -> SolarAmber
                    1 -> ContainmentCrimsonLight
                    2 -> PhotonicCyan
                    else -> OperationalEmerald
                }

                drawCircle(
                    color = packetColor.copy(alpha = 0.7f),
                    radius = (4.5f + (if (isBursting) 2f else 0f)).dp.toPx(),
                    center = Offset(packetX, packetY)
                )
                drawCircle(
                    color = AmbientWhite,
                    radius = 1.8.dp.toPx(),
                    center = Offset(packetX, packetY)
                )
            }
        }

        // 2. Draw 5 Cyber-Nodes
        nodeCenters.forEachIndexed { i, center ->
            val color = nodeColors[i]
            val nodeRadius = 18.dp.toPx()

            // Outer Pulse Glow
            drawCircle(
                color = color.copy(alpha = 0.15f * glowPulse),
                radius = nodeRadius * 1.5f,
                center = center
            )

            // Glass Core
            drawCircle(
                color = SpaceCobaltDark,
                radius = nodeRadius,
                center = center
            )
            drawCircle(
                color = color.copy(alpha = 0.8f),
                radius = nodeRadius,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Center LED pip
            drawCircle(
                color = color,
                radius = 4.dp.toPx(),
                center = center
            )

            // Node Top Title
            drawText(
                textMeasurer = textMeasurer,
                text = nodeLabels[i],
                topLeft = Offset(center.x - 20.dp.toPx(), center.y - nodeRadius - 16.dp.toPx()),
                style = TextStyle(
                    color = color,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            )

            // Node Bottom Subtitle
            drawText(
                textMeasurer = textMeasurer,
                text = nodeSubtitles[i],
                topLeft = Offset(center.x - 22.dp.toPx(), center.y + nodeRadius + 6.dp.toPx()),
                style = TextStyle(
                    color = AmbientWhiteMuted,
                    fontSize = 8.sp,
                    fontFamily = FontFamily.Monospace
                )
            )
        }

        // 3. Bottom Data Flow Status Summary
        val summaryY = h - 22.dp.toPx()
        val summaryText = "CYBER-NODE TELEMETRY STATUS: ZERO-LEAK POST-QUANTUM ROUTING ENGAGED • SPEED: ${currentPoint.packetsPerSec * 4} PKTS/S"
        drawText(
            textMeasurer = textMeasurer,
            text = summaryText,
            topLeft = Offset(w / 2f - 140.dp.toPx(), summaryY),
            style = TextStyle(
                color = OperationalEmeraldLight,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        )
    }
}

/**
 * Cyber-Node Sanitization Stages Strip.
 * Shows active throughput and attenuation across the pipeline stages.
 */
@Composable
private fun CyberNodeSanitizationStagesStrip(
    currentPoint: TelemetryThroughputPoint,
    isBursting: Boolean,
    sanitizationProgress: Float
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SpaceCobaltDark.copy(alpha = 0.5f))
            .border(1.dp, SpaceCobaltGlassBorder.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        StageMetricColumn(
            label = "STAGE 1 • INGRESS",
            value = "${currentPoint.rawThroughputKbps.roundToInt()} KB/s",
            color = SolarAmber,
            sub = "Raw Inbound"
        )

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Data Flow",
            tint = PhotonicCyan.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
        )

        StageMetricColumn(
            label = "STAGE 3 • NOISE",
            value = "ε=0.50",
            color = PhotonicCyan,
            sub = "Laplace Perturb"
        )

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Data Flow",
            tint = PhotonicCyan.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
        )

        StageMetricColumn(
            label = "STAGE 4 • SCRUB",
            value = "${currentPoint.piiScrubbedRate}/s",
            color = QuantumVioletLight,
            sub = "Tokens Redacted"
        )

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = "Data Flow",
            tint = PhotonicCyan.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp)
        )

        StageMetricColumn(
            label = "STAGE 5 • EGRESS",
            value = "${currentPoint.sanitizedThroughputKbps.roundToInt()} KB/s",
            color = OperationalEmerald,
            sub = "Kyber-1024 Sealed"
        )
    }
}

@Composable
private fun StageMetricColumn(
    label: String,
    value: String,
    color: Color,
    sub: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 7.sp,
            color = AmbientWhiteSubtle,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            color = color,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Text(
            text = sub,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 7.sp,
            color = AmbientWhiteMuted
        )
    }
}

/**
 * Recharts Floating Tooltip Box.
 * Emulates the Recharts <Tooltip /> component with frosted glass card styling.
 */
@Composable
private fun RechartsFloatingTooltipBox(
    point: TelemetryThroughputPoint,
    modifier: Modifier = Modifier
) {
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.US) }
    val formattedTime = remember(point.timestamp) { timeFormat.format(Date(point.timestamp)) }

    Box(
        modifier = modifier
            .width(185.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(SpaceCobaltDark.copy(alpha = 0.94f))
            .border(1.dp, PhotonicCyan.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "RECHARTS TOOLTIP",
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 8.sp,
                    color = PhotonicCyan,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formattedTime,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 9.sp,
                    color = AmbientWhiteMuted,
                    fontFamily = FontFamily.Monospace
                )
            }

            HorizontalDivider(
                color = SpaceCobaltGlassBorder.copy(alpha = 0.3f),
                thickness = 1.dp
            )

            TooltipRow(
                label = "Raw Ingress:",
                value = "${point.rawThroughputKbps} KB/s",
                color = SolarAmber
            )

            TooltipRow(
                label = "Sanitized Egress:",
                value = "${point.sanitizedThroughputKbps} KB/s",
                color = OperationalEmerald
            )

            TooltipRow(
                label = "PII Redacted Rate:",
                value = "${point.piiScrubbedRate} tokens/s",
                color = QuantumVioletLight
            )

            TooltipRow(
                label = "Diff Privacy (ε):",
                value = "0.50 (Laplace)",
                color = PhotonicCyan
            )

            TooltipRow(
                label = "Threat Anomaly:",
                value = String.format(Locale.US, "%.3f", point.threatAnomalyScore),
                color = if (point.threatAnomalyScore > 0.4f) ContainmentCrimson else OperationalEmerald
            )
        }
    }
}

@Composable
private fun TooltipRow(
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(5.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 9.sp,
                color = AmbientWhiteMuted
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = color,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
    }
}

/**
 * Monotone Cubic Bézier Spline builder.
 * Emulates Recharts' monotoneX interpolation algorithm.
 */
private fun buildMonotoneSplinePath(points: List<Offset>): Path {
    val path = Path()
    if (points.isEmpty()) return path

    path.moveTo(points.first().x, points.first().y)
    if (points.size == 1) return path

    for (i in 0 until points.size - 1) {
        val p0 = points[i]
        val p1 = points[i + 1]

        // Cubic control points along horizontal monotone tangent
        val controlX1 = p0.x + (p1.x - p0.x) * 0.45f
        val controlY1 = p0.y
        val controlX2 = p0.x + (p1.x - p0.x) * 0.55f
        val controlY2 = p1.y

        path.cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
    }

    return path
}
