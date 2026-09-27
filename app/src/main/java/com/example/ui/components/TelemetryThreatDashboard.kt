package com.example.ui.components

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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TelemetryThroughputPoint
import com.example.model.ThreatCategoryMetric
import com.example.model.ThreatSeverity
import com.example.ui.animation.PhotonicSignalPulseIndicator
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.theme.*
import com.example.viewmodel.AgisViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.*

/**
 * High-performance real-time data visualization dashboard in Jetpack Compose
 * displaying live telemetry sanitization throughput, differential privacy noise rates,
 * and threat detection & containment status.
 */
@Composable
fun TelemetryThreatDashboard(
    viewModel: AgisViewModel,
    modifier: Modifier = Modifier
) {
    val throughputHistory by viewModel.telemetryThroughputHistory.collectAsState()
    val currentThroughput by viewModel.currentThroughputPoint.collectAsState()
    val threatMetrics by viewModel.threatCategoryMetrics.collectAsState()
    val threatSeverity by viewModel.globalThreatLevel.collectAsState()
    val isBursting by viewModel.isThroughputBursting.collectAsState()

    var activeViewMode by remember { mutableStateOf("THROUGHPUT") } // "THROUGHPUT", "THREATS", "COMPOSITE"

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top KPI Status Bar
        QuantumGlassCard(
            borderColor = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson else PhotonicCyan.copy(alpha = 0.4f),
            backgroundColor = SpaceCobaltSurface
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                            signalColor = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson else OperationalEmerald,
                            size = 12.dp
                        )
                        Column {
                            Text(
                                text = "REAL-TIME TELEMETRY & THREAT MATRIX",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson else PhotonicCyanLight,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Sanitization Throughput & Heuristic Sentinel",
                                style = MaterialTheme.typography.titleSmall,
                                color = AmbientWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    PhotonicBadge(
                        text = if (threatSeverity == ThreatSeverity.CRITICAL) "THREAT ISOLATED" else "ZERO LEAK PASS",
                        signalColor = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson else OperationalEmerald,
                        icon = if (threatSeverity == ThreatSeverity.CRITICAL) Icons.Default.Shield else Icons.Default.CheckCircle
                    )
                }

                // Metric Counters
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiMetricTile(
                        label = "RAW INGESTION",
                        value = "${currentThroughput.rawThroughputKbps.toInt()} KB/s",
                        subtext = "${currentThroughput.packetsPerSec} pkts/s",
                        accentColor = PhotonicCyan,
                        modifier = Modifier.weight(1f)
                    )
                    KpiMetricTile(
                        label = "SANITIZED EGRESS",
                        value = "${currentThroughput.sanitizedThroughputKbps.toInt()} KB/s",
                        subtext = "ε = 0.50 DP Noise",
                        accentColor = OperationalEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    KpiMetricTile(
                        label = "PII STRIPPED",
                        value = "${currentThroughput.piiScrubbedRate} /sec",
                        subtext = "100% Zero-Leak",
                        accentColor = QuantumVioletLight,
                        modifier = Modifier.weight(1f)
                    )
                }

                // View Mode Switcher
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "THROUGHPUT" to "Throughput Waves",
                        "THREATS" to "Threat Detection",
                        "COMPOSITE" to "Composite Radar"
                    ).forEach { (modeKey, label) ->
                        val isSelected = activeViewMode == modeKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) PhotonicCyan.copy(alpha = 0.25f)
                                    else SpaceCobaltGlassElevated
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) PhotonicCyan else SpaceCobaltGlassBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { activeViewMode = modeKey }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) PhotonicCyan else AmbientWhiteMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Main Visualizer Panel
        when (activeViewMode) {
            "THROUGHPUT" -> {
                RealTimeThroughputChartCard(
                    throughputHistory = throughputHistory,
                    currentThroughput = currentThroughput,
                    isBursting = isBursting
                )
            }
            "THREATS" -> {
                ThreatDetectionMatrixCard(
                    threatMetrics = threatMetrics,
                    threatSeverity = threatSeverity,
                    anomalyScore = currentThroughput.threatAnomalyScore
                )
            }
            "COMPOSITE" -> {
                CompositeTelemetryRadarCard(
                    currentThroughput = currentThroughput,
                    threatSeverity = threatSeverity,
                    threatMetrics = threatMetrics
                )
            }
        }

        // Simulated Sanitization Scan & Post-Quantum Enclave Status Feedback Control
        SanitizationScanControlCard(viewModel = viewModel)

        // Live Action Dispatch Bar
        QuantumGlassCard(
            borderColor = SpaceCobaltGlassBorder,
            backgroundColor = SpaceCobaltCard
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "REAL-TIME TELEMETRY STIMULUS CONTROLS",
                    style = MaterialTheme.typography.labelSmall,
                    color = PhotonicCyanLight
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuantumVolumetricButton(
                        text = if (isBursting) "Bursting..." else "⚡ Ingest Burst",
                        onClick = { viewModel.triggerTelemetryBurst() },
                        primaryColor = PhotonicCyan,
                        secondaryColor = OperationalEmerald,
                        icon = Icons.Default.FlashOn,
                        modifier = Modifier.weight(1f)
                    )

                    QuantumVolumetricButton(
                        text = "🛡️ Inject Probe",
                        onClick = {
                            viewModel.injectSimulatedThreat("PROMPT_INJECTION", "external.ai.adversarial-cluster")
                        },
                        primaryColor = ContainmentCrimson,
                        secondaryColor = SolarAmber,
                        icon = Icons.Default.Warning,
                        modifier = Modifier.weight(1f)
                    )

                    QuantumVolumetricButton(
                        text = "🧹 Flush Buffer",
                        onClick = { viewModel.flushPerimeterBuffer() },
                        primaryColor = OperationalEmerald,
                        secondaryColor = PhotonicCyan,
                        icon = Icons.Default.CleaningServices,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

/**
 * Metric tile showing single KPI stat
 */
@Composable
private fun KpiMetricTile(
    label: String,
    value: String,
    subtext: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(SpaceCobaltGlassElevated)
            .border(1.dp, accentColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = AmbientWhiteMuted,
                fontSize = 9.sp
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = accentColor,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = AmbientWhiteMuted,
                fontSize = 10.sp
            )
        }
    }
}

/**
 * High-performance animated dual-line & area chart for Inbound vs Sanitized Throughput.
 */
@Composable
private fun RealTimeThroughputChartCard(
    throughputHistory: List<TelemetryThroughputPoint>,
    currentThroughput: TelemetryThroughputPoint,
    isBursting: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "chartPhotonPhase")
    val pulsePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulsePhase"
    )

    QuantumGlassCard(
        borderColor = if (isBursting) PhotonicCyan else SpaceCobaltGlassBorder,
        backgroundColor = SpaceCobaltCard
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "REAL-TIME SANITIZATION THROUGHPUT STREAM",
                        style = MaterialTheme.typography.labelSmall,
                        color = PhotonicCyanLight
                    )
                    Text(
                        text = "Raw Ingress vs Differential Privacy Egress (KB/s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmbientWhiteMuted
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PhotonicCyan)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Raw Ingest", style = MaterialTheme.typography.labelSmall, color = AmbientWhiteMuted)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(OperationalEmerald)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sanitized", style = MaterialTheme.typography.labelSmall, color = AmbientWhiteMuted)
                    }
                }
            }

            // Real-Time Canvas Area Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawThroughputAreaChart(
                        history = throughputHistory,
                        pulsePhase = pulsePhase,
                        isBursting = isBursting
                    )
                }
            }

            // Bottom Chart Metrics Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "◀ Past 24 Seconds Live Stream",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteMuted
                )
                Text(
                    text = "Perimeter Leak Proof: 100% Clean (0 leaks) ▶",
                    style = MaterialTheme.typography.labelSmall,
                    color = OperationalEmeraldLight,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

/**
 * Custom Canvas drawing routine for Dual-Series Bezier Area Chart with glowing pulse packet.
 */
private fun DrawScope.drawThroughputAreaChart(
    history: List<TelemetryThroughputPoint>,
    pulsePhase: Float,
    isBursting: Boolean
) {
    if (history.size < 2) return

    val w = size.width
    val h = size.height
    val padTop = 16.dp.toPx()
    val padBottom = 20.dp.toPx()
    val padLeft = 10.dp.toPx()
    val padRight = 10.dp.toPx()

    val chartW = w - padLeft - padRight
    val chartH = h - padTop - padBottom

    // Determine max scale dynamically
    val maxRaw = history.maxOfOrNull { it.rawThroughputKbps } ?: 1000f
    val maxScale = (max(maxRaw, 800f) * 1.15f).coerceAtLeast(600f)

    // 1. Draw horizontal gridlines and threshold markers
    val gridSteps = 4
    for (step in 0..gridSteps) {
        val frac = step.toFloat() / gridSteps
        val y = padTop + chartH * (1f - frac)
        drawLine(
            color = SpaceCobaltGlassBorder.copy(alpha = 0.4f),
            start = Offset(padLeft, y),
            end = Offset(w - padRight, y),
            strokeWidth = 1.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
        )
    }

    // 2. Compute points for Raw Ingress and Sanitized Egress
    val stepX = chartW / (history.size - 1).coerceAtLeast(1)
    val rawPoints = mutableListOf<Offset>()
    val sanitizedPoints = mutableListOf<Offset>()

    history.forEachIndexed { index, pt ->
        val px = padLeft + (index * stepX)
        val rawY = padTop + chartH * (1f - (pt.rawThroughputKbps / maxScale).coerceIn(0f, 1f))
        val sanitizedY = padTop + chartH * (1f - (pt.sanitizedThroughputKbps / maxScale).coerceIn(0f, 1f))
        rawPoints.add(Offset(px, rawY))
        sanitizedPoints.add(Offset(px, sanitizedY))
    }

    // Helper to build smooth cubic spline
    fun buildSplinePath(points: List<Offset>): Path {
        val path = Path()
        if (points.isEmpty()) return path
        path.moveTo(points.first().x, points.first().y)
        for (i in 0 until points.size - 1) {
            val p0 = points[i]
            val p1 = points[i + 1]
            val midX = (p0.x + p1.x) / 2f
            path.cubicTo(midX, p0.y, midX, p1.y, p1.x, p1.y)
        }
        return path
    }

    // 3. Draw Sanitized Area Fill
    val sanitizedPath = buildSplinePath(sanitizedPoints)
    val areaPath = Path().apply {
        addPath(sanitizedPath)
        lineTo(sanitizedPoints.last().x, padTop + chartH)
        lineTo(sanitizedPoints.first().x, padTop + chartH)
        close()
    }

    drawPath(
        path = areaPath,
        brush = Brush.verticalGradient(
            colors = listOf(
                OperationalEmerald.copy(alpha = 0.35f),
                OperationalEmerald.copy(alpha = 0.08f),
                Color.Transparent
            ),
            startY = padTop,
            endY = padTop + chartH
        )
    )

    // 4. Draw Sanitized Egress Line (Emerald)
    drawPath(
        path = sanitizedPath,
        brush = Brush.horizontalGradient(
            colors = listOf(PhotonicCyan, OperationalEmerald, OperationalEmeraldLight),
            startX = padLeft,
            endX = w - padRight
        ),
        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )

    // 5. Draw Raw Ingress Line (Cyan / Amber glow)
    val rawPath = buildSplinePath(rawPoints)
    drawPath(
        path = rawPath,
        color = if (isBursting) SolarAmber else PhotonicCyan.copy(alpha = 0.75f),
        style = Stroke(
            width = 1.8.dp.toPx(),
            cap = StrokeCap.Round,
            join = StrokeJoin.Round,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 4f), 0f)
        )
    )

    // 6. Highlight Latest Point with Hot Photon Node
    val latestSanitized = sanitizedPoints.last()
    drawCircle(
        color = OperationalEmerald.copy(alpha = 0.4f),
        radius = 8.dp.toPx() * (1f + pulsePhase * 0.4f),
        center = latestSanitized
    )
    drawCircle(
        color = OperationalEmeraldLight,
        radius = 4.dp.toPx(),
        center = latestSanitized
    )
    drawCircle(
        color = AmbientWhite,
        radius = 2.dp.toPx(),
        center = latestSanitized
    )
}

/**
 * Threat Detection & Anomaly Status Matrix Card with radar breakdown
 */
@Composable
private fun ThreatDetectionMatrixCard(
    threatMetrics: List<ThreatCategoryMetric>,
    threatSeverity: ThreatSeverity,
    anomalyScore: Float
) {
    QuantumGlassCard(
        borderColor = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson else SpaceCobaltGlassBorder,
        backgroundColor = SpaceCobaltCard
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HEURISTIC SENTINEL THREAT SPECTRUM",
                        style = MaterialTheme.typography.labelSmall,
                        color = ContainmentCrimson
                    )
                    Text(
                        text = "Real-time Adversarial Anomaly Distribution",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmbientWhiteMuted
                    )
                }

                PhotonicBadge(
                    text = "SEVERITY: ${threatSeverity.name}",
                    signalColor = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson else SolarAmber,
                    icon = Icons.Default.Security
                )
            }

            // Anomaly Index Gauge Bar
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Adversarial Anomaly Index", style = MaterialTheme.typography.labelSmall, color = AmbientWhiteMuted)
                    Text(
                        text = String.format(Locale.US, "%.1f%% Threat Vector", anomalyScore * 100f),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (anomalyScore > 0.5f) ContainmentCrimson else OperationalEmeraldLight,
                        fontWeight = FontWeight.Bold
                    )
                }

                LinearProgressIndicator(
                    progress = { anomalyScore.coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = if (anomalyScore > 0.5f) ContainmentCrimson else PhotonicCyan,
                    trackColor = SpaceCobaltGlassElevated
                )
            }

            HorizontalDivider(color = SpaceCobaltGlassBorder)

            // Threat Category Breakdown Bars
            Text(
                text = "INTERCEPTED ATTACK VECTORS BY CATEGORY",
                style = MaterialTheme.typography.labelSmall,
                color = PhotonicCyanLight
            )

            threatMetrics.forEach { metric ->
                val barColor = Color(android.graphics.Color.parseColor(metric.accentColorHex))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = metric.categoryName,
                            style = MaterialTheme.typography.bodySmall,
                            color = AmbientWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${metric.incidentCount} intercepted (${(metric.riskRatio * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = barColor,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(SpaceCobaltGlassElevated)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(metric.riskRatio)
                                .clip(RoundedCornerShape(3.dp))
                                .background(barColor)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Composite Telemetry Radar & Privacy Budget Gauge
 */
@Composable
private fun CompositeTelemetryRadarCard(
    currentThroughput: TelemetryThroughputPoint,
    threatSeverity: ThreatSeverity,
    threatMetrics: List<ThreatCategoryMetric>
) {
    QuantumGlassCard(
        borderColor = SpaceCobaltGlassBorder,
        backgroundColor = SpaceCobaltCard
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "COMPOSITE ZERO-TRUST TELEMETRY AUDIT",
                style = MaterialTheme.typography.labelSmall,
                color = PhotonicCyanLight
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Differential Privacy Gauge
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SpaceCobaltGlassElevated)
                        .border(1.dp, PhotonicCyan.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("DIFFERENTIAL PRIVACY", style = MaterialTheme.typography.labelSmall, color = AmbientWhiteMuted, fontSize = 9.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("ε = 0.50", style = MaterialTheme.typography.titleLarge, color = OperationalEmeraldLight, fontWeight = FontWeight.Bold)
                        Text("Laplace Perturbation", style = MaterialTheme.typography.labelSmall, color = AmbientWhiteMuted, fontSize = 9.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        PhotonicBadge(text = "DELTA 10^-5", signalColor = OperationalEmerald)
                    }
                }

                // Photonic Shield Status
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SpaceCobaltGlassElevated)
                        .border(
                            1.dp,
                            if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson.copy(alpha = 0.5f) else OperationalEmerald.copy(alpha = 0.3f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(10.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("PHOTONIC SHIELD", style = MaterialTheme.typography.labelSmall, color = AmbientWhiteMuted, fontSize = 9.sp)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (threatSeverity == ThreatSeverity.CRITICAL) "CONTAINED" else "ARMED",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson else OperationalEmerald,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Heuristic Sentinel", style = MaterialTheme.typography.labelSmall, color = AmbientWhiteMuted, fontSize = 9.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        PhotonicBadge(
                            text = "1.2ms Latency",
                            signalColor = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson else PhotonicCyan
                        )
                    }
                }
            }

            // Zero-Trust Boundary Summary
            Box(
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
                    Column {
                        Text(
                            text = "CRYPTOGRAPHIC ZERO-LEAK ASSURANCE",
                            style = MaterialTheme.typography.labelSmall,
                            color = OperationalEmeraldLight,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "All outgoing network packets verified with SHA-512 non-repudiation proof.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmbientWhiteMuted,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = "Zero Leak Pass",
                        tint = OperationalEmerald,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
