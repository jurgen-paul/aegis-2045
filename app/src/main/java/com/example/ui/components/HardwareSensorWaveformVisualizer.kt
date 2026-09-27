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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HardwareSensorEntropyState
import com.example.ui.animation.PhotonicSignalPulseIndicator
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.theme.*
import java.util.Locale

enum class EntropyWaveformChannel(val label: String, val unitLabel: String, val shortDesc: String) {
    SENSOR_JITTER("CH-1: Jitter Perturbation", "μg / m/s²", "Physical Sensor XYZ Micro-Fluctuations"),
    CSPRNG_DIFFUSION("CH-2: CSPRNG Diffusion", "0..1 Range", "HMAC-SHA512 Whitened Diffusion"),
    SHANNON_SPECTRAL("CH-3: Shannon Dispersion", "bits/byte", "Real-Time Entropy bits (Target: 8.00)")
}

/**
 * Diagnostic UI component that visualizes the real-time activity of the
 * hardware sensor-based entropy generation service using a live oscilloscope waveform graph.
 *
 * Features:
 * - Real-time animated waveform with cubic spline curve smoothing
 * - Dual-layer glow bloom and gradient fills under the wave
 * - Oscilloscope reticle grid lines with dBm / amplitude axis scales
 * - Sweeping phosphor beam scanner line simulating a hardware oscilloscope
 * - Dynamic channel switcher (Sensor Jitter, CSPRNG Diffusion, Shannon Dispersion)
 * - Interactive Freeze / Run scan controls and sweep speed adjustments (1x, 2x, 4x)
 * - Real-time RF telemetry metrics (Noise Floor, Peak Jitter, Nyquist Rate, Shannon Index)
 */
@Composable
fun HardwareSensorWaveformVisualizer(
    entropyState: HardwareSensorEntropyState,
    onForceReseed: () -> Unit,
    modifier: Modifier = Modifier,
    wrapInCard: Boolean = true
) {
    var selectedChannel by remember { mutableStateOf(EntropyWaveformChannel.SENSOR_JITTER) }
    var isFrozen by remember { mutableStateOf(false) }
    var sweepMultiplier by remember { mutableStateOf(1) } // 1x, 2x, 4x

    // Frozen snapshot buffer when user pauses scanning
    var frozenPoints by remember { mutableStateOf<List<Float>>(emptyList()) }

    // Waveform points to render based on selected channel
    val activePoints = remember(entropyState, selectedChannel, isFrozen) {
        if (isFrozen && frozenPoints.isNotEmpty()) {
            frozenPoints
        } else {
            val points = when (selectedChannel) {
                EntropyWaveformChannel.SENSOR_JITTER -> entropyState.waveformPoints.ifEmpty {
                    // Fallback synthetic baseline if service is initializing
                    List(50) { i -> (kotlin.math.sin(i * 0.3f) * 0.5f) }
                }
                EntropyWaveformChannel.CSPRNG_DIFFUSION -> entropyState.csprngDiffusionWave.ifEmpty {
                    List(50) { i -> ((i * 17 % 100) / 100f) }
                }
                EntropyWaveformChannel.SHANNON_SPECTRAL -> entropyState.shannonDispersionWave.ifEmpty {
                    List(30) { 7.97f }
                }
            }
            if (!isFrozen) {
                frozenPoints = points
            }
            points
        }
    }

    // Oscilloscope sweeping beam animation
    val infiniteTransition = rememberInfiniteTransition(label = "OscilloscopeBeam")
    val beamSweepDuration = (1800 / sweepMultiplier).coerceAtLeast(400)
    val beamProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = beamSweepDuration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "BeamSweep"
    )

    val reticlePulse by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ReticlePulse"
    )

    val contentBlock: @Composable ColumnScope.() -> Unit = {
        // Diagnostic Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(SpaceCobaltDark)
                        .border(1.dp, OperationalEmerald.copy(alpha = reticlePulse), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Waveform Oscilloscope",
                        tint = OperationalEmeraldLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "HARDWARE ENTROPY OSCILLOSCOPE",
                        style = MaterialTheme.typography.labelSmall,
                        color = OperationalEmeraldLight,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = selectedChannel.shortDesc,
                        style = MaterialTheme.typography.bodySmall,
                        color = AmbientWhiteMuted,
                        fontSize = 11.sp
                    )
                }
            }

            // Live status badge with interactive freeze toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                PhotonicBadge(
                    text = if (isFrozen) "TRACE FROZEN" else "LIVE ${entropyState.nyquistRateHz * sweepMultiplier}Hz",
                    signalColor = if (isFrozen) SolarAmber else OperationalEmerald,
                    icon = if (isFrozen) Icons.Default.Pause else Icons.Default.PlayArrow,
                    enablePulse = !isFrozen
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Channel Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(SpaceCobaltSurface)
                .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            EntropyWaveformChannel.values().forEach { channel ->
                val isSelected = selectedChannel == channel
                val activeColor = when (channel) {
                    EntropyWaveformChannel.SENSOR_JITTER -> OperationalEmerald
                    EntropyWaveformChannel.CSPRNG_DIFFUSION -> PhotonicCyan
                    EntropyWaveformChannel.SHANNON_SPECTRAL -> QuantumVioletLight
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isSelected) activeColor.copy(alpha = 0.22f) else Color.Transparent)
                        .border(
                            1.dp,
                            if (isSelected) activeColor.copy(alpha = 0.8f) else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clickable { selectedChannel = channel }
                        .padding(vertical = 6.dp, horizontal = 4.dp)
                        .testTag("waveform_channel_${channel.name.lowercase(Locale.US)}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (channel) {
                            EntropyWaveformChannel.SENSOR_JITTER -> "CH-1 JITTER"
                            EntropyWaveformChannel.CSPRNG_DIFFUSION -> "CH-2 CSPRNG"
                            EntropyWaveformChannel.SHANNON_SPECTRAL -> "CH-3 SHANNON"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) AmbientWhite else AmbientWhiteMuted,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Primary Live Oscilloscope Canvas Graph
        val textMeasurer = rememberTextMeasurer()
        val traceColor = when (selectedChannel) {
            EntropyWaveformChannel.SENSOR_JITTER -> OperationalEmerald
            EntropyWaveformChannel.CSPRNG_DIFFUSION -> PhotonicCyan
            EntropyWaveformChannel.SHANNON_SPECTRAL -> QuantumVioletLight
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SpaceCobaltDark)
                .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(10.dp))
                .testTag("live_waveform_canvas_container")
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("live_waveform_canvas")
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val paddingLeft = 40.dp.toPx()
                val paddingRight = 12.dp.toPx()
                val paddingTop = 12.dp.toPx()
                val paddingBottom = 16.dp.toPx()

                val plotWidth = canvasWidth - paddingLeft - paddingRight
                val plotHeight = canvasHeight - paddingTop - paddingBottom
                if (plotWidth <= 0 || plotHeight <= 0) return@Canvas

                // 1. Draw Oscilloscope Reticle Grid Lines (5 Horizontal & 6 Vertical)
                val gridColor = Color(0x1800F5FF)
                val centerGridColor = Color(0x3500F5FF)

                // Vertical grid lines
                val vDivisions = 6
                for (i in 0..vDivisions) {
                    val x = paddingLeft + (plotWidth / vDivisions) * i
                    drawLine(
                        color = if (i == 0 || i == vDivisions) centerGridColor else gridColor,
                        start = Offset(x, paddingTop),
                        end = Offset(x, paddingTop + plotHeight),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Horizontal grid lines and Scale Labels
                val hDivisions = 4
                for (i in 0..hDivisions) {
                    val y = paddingTop + (plotHeight / hDivisions) * i
                    val isCenter = (i == 2 && selectedChannel == EntropyWaveformChannel.SENSOR_JITTER)
                    drawLine(
                        color = if (isCenter) OperationalEmerald.copy(alpha = 0.35f) else gridColor,
                        start = Offset(paddingLeft, y),
                        end = Offset(paddingLeft + plotWidth, y),
                        strokeWidth = if (isCenter) 1.5.dp.toPx() else 1.dp.toPx(),
                        pathEffect = if (isCenter) PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f) else null
                    )

                    // Draw amplitude scale axis text
                    val labelText = when (selectedChannel) {
                        EntropyWaveformChannel.SENSOR_JITTER -> when (i) {
                            0 -> "+1.0"
                            1 -> "+0.5"
                            2 -> " 0.0"
                            3 -> "-0.5"
                            else -> "-1.0"
                        }
                        EntropyWaveformChannel.CSPRNG_DIFFUSION -> when (i) {
                            0 -> "1.00"
                            1 -> "0.75"
                            2 -> "0.50"
                            3 -> "0.25"
                            else -> "0.00"
                        }
                        EntropyWaveformChannel.SHANNON_SPECTRAL -> when (i) {
                            0 -> "8.00"
                            1 -> "7.90"
                            2 -> "7.80"
                            3 -> "7.70"
                            else -> "7.60"
                        }
                    }

                    drawText(
                        textMeasurer = textMeasurer,
                        text = labelText,
                        topLeft = Offset(4.dp.toPx(), y - 7.dp.toPx()),
                        style = TextStyle(
                            color = AmbientWhiteSubtle,
                            fontSize = 8.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                // 2. Map Active Points to Coordinates
                if (activePoints.size >= 2) {
                    val coordinates = activePoints.mapIndexed { index, value ->
                        val normX = index.toFloat() / (activePoints.size - 1)
                        val x = paddingLeft + normX * plotWidth

                        val normY = when (selectedChannel) {
                            EntropyWaveformChannel.SENSOR_JITTER -> {
                                // -1.0 is bottom, +1.0 is top
                                val clamped = value.coerceIn(-1.0f, 1.0f)
                                1.0f - ((clamped + 1.0f) / 2.0f)
                            }
                            EntropyWaveformChannel.CSPRNG_DIFFUSION -> {
                                // 0.0 is bottom, 1.0 is top
                                val clamped = value.coerceIn(0.0f, 1.0f)
                                1.0f - clamped
                            }
                            EntropyWaveformChannel.SHANNON_SPECTRAL -> {
                                // 7.60 is bottom, 8.00 is top
                                val clamped = value.coerceIn(7.60f, 8.00f)
                                1.0f - ((clamped - 7.60f) / 0.40f)
                            }
                        }
                        val y = paddingTop + normY * plotHeight
                        Offset(x, y)
                    }

                    // 3. Build Smooth Waveform Path (Cubic Bezier Spline)
                    val wavePath = Path().apply {
                        moveTo(coordinates.first().x, coordinates.first().y)
                        for (i in 0 until coordinates.size - 1) {
                            val p0 = coordinates[i]
                            val p1 = coordinates[i + 1]
                            val controlX1 = p0.x + (p1.x - p0.x) / 2f
                            val controlY1 = p0.y
                            val controlX2 = p0.x + (p1.x - p0.x) / 2f
                            val controlY2 = p1.y
                            cubicTo(controlX1, controlY1, controlX2, controlY2, p1.x, p1.y)
                        }
                    }

                    // 4. Area Gradient Bloom Under Curve
                    val baselineY = when (selectedChannel) {
                        EntropyWaveformChannel.SENSOR_JITTER -> paddingTop + plotHeight / 2f
                        else -> paddingTop + plotHeight
                    }

                    val fillPath = Path().apply {
                        addPath(wavePath)
                        lineTo(coordinates.last().x, baselineY)
                        lineTo(coordinates.first().x, baselineY)
                        close()
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                traceColor.copy(alpha = 0.35f),
                                traceColor.copy(alpha = 0.04f),
                                Color.Transparent
                            ),
                            startY = paddingTop,
                            endY = paddingTop + plotHeight
                        )
                    )

                    // 5. Thicker Glow Bloom Stroke
                    drawPath(
                        path = wavePath,
                        color = traceColor.copy(alpha = 0.30f),
                        style = Stroke(
                            width = 6.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // 6. Primary Crisp Waveform Trace with Horizontal Spectrum Gradient
                    drawPath(
                        path = wavePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                traceColor,
                                PhotonicCyan,
                                traceColor,
                                SolarAmber.copy(alpha = 0.9f)
                            ),
                            startX = paddingLeft,
                            endX = paddingLeft + plotWidth
                        ),
                        style = Stroke(
                            width = 2.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // 7. Oscilloscope Sweeping Beam & Phosphor Persistence
                    if (!isFrozen) {
                        val beamX = paddingLeft + beamProgress * plotWidth

                        // Phosphor afterglow fade bar
                        val beamTrailWidth = 35.dp.toPx()
                        drawRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    traceColor.copy(alpha = 0.08f),
                                    traceColor.copy(alpha = 0.25f)
                                ),
                                startX = beamX - beamTrailWidth,
                                endX = beamX
                            ),
                            topLeft = Offset((beamX - beamTrailWidth).coerceAtLeast(paddingLeft), paddingTop),
                            size = Size(
                                width = beamTrailWidth.coerceAtMost(beamX - paddingLeft),
                                height = plotHeight
                            )
                        )

                        // Leading scan cursor line
                        drawLine(
                            color = traceColor.copy(alpha = 0.9f),
                            start = Offset(beamX, paddingTop),
                            end = Offset(beamX, paddingTop + plotHeight),
                            strokeWidth = 1.5.dp.toPx()
                        )

                        // Intersection Reticle on Beam
                        val interpIndex = ((beamProgress * (coordinates.size - 1)).toInt()).coerceIn(0, coordinates.size - 1)
                        val targetCoord = coordinates[interpIndex]
                        drawCircle(
                            color = traceColor,
                            radius = 3.5.dp.toPx(),
                            center = Offset(beamX, targetCoord.y)
                        )
                        drawCircle(
                            color = AmbientWhite,
                            radius = 1.5.dp.toPx(),
                            center = Offset(beamX, targetCoord.y)
                        )
                    }

                    // 8. Peak & Valley Telemetry Markers
                    val maxPt = coordinates.minByOrNull { it.y } // minimum Y is maximum value
                    if (maxPt != null) {
                        drawCircle(
                            color = SolarAmber,
                            radius = 4.dp.toPx(),
                            center = maxPt,
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }

            // Top-right corner channel indicator
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(SpaceCobaltSurface.copy(alpha = 0.85f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(traceColor)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = selectedChannel.unitLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = traceColor,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Diagnostic Telemetry Ribbon (RF Noise Floor, Peak Jitter, Nyquist Rate, Shannon Index)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Noise Floor
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(6.dp)
            ) {
                Text(
                    text = "NOISE FLOOR",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteMuted,
                    fontSize = 9.sp
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", entropyState.noiseFloorDb)} dBm",
                    style = MaterialTheme.typography.titleSmall,
                    color = PhotonicCyan,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            // Peak Jitter Amplitude
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(6.dp)
            ) {
                Text(
                    text = "PEAK JITTER",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteMuted,
                    fontSize = 9.sp
                )
                Text(
                    text = "±${String.format(Locale.US, "%.1f", entropyState.peakJitterMicroG)} μg",
                    style = MaterialTheme.typography.titleSmall,
                    color = SolarAmber,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            // Nyquist Sampling Rate
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(6.dp)
            ) {
                Text(
                    text = "NYQUIST RATE",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteMuted,
                    fontSize = 9.sp
                )
                Text(
                    text = "${entropyState.nyquistRateHz * sweepMultiplier} S/s",
                    style = MaterialTheme.typography.titleSmall,
                    color = OperationalEmeraldLight,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            // Shannon Rating
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(6.dp)
            ) {
                Text(
                    text = "SHANNON",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteMuted,
                    fontSize = 9.sp
                )
                Text(
                    text = "${String.format(Locale.US, "%.3f", entropyState.estimatedShannonEntropy)} b",
                    style = MaterialTheme.typography.titleSmall,
                    color = QuantumVioletLight,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Diagnostic Controls Row (Freeze/Run, Sweep 1x/2x/4x, Reseed Button)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Freeze / Hold Trace Button
            Button(
                onClick = { isFrozen = !isFrozen },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isFrozen) SolarAmber.copy(alpha = 0.2f) else SpaceCobaltSurface
                ),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isFrozen) SolarAmber else SpaceCobaltGlassBorder
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                modifier = Modifier
                    .height(38.dp)
                    .testTag("waveform_freeze_toggle")
            ) {
                Icon(
                    imageVector = if (isFrozen) Icons.Default.PlayArrow else Icons.Default.Pause,
                    contentDescription = if (isFrozen) "Resume" else "Freeze",
                    tint = if (isFrozen) SolarAmber else AmbientWhite,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isFrozen) "RUN" else "HOLD",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isFrozen) SolarAmber else AmbientWhite,
                    fontWeight = FontWeight.Bold
                )
            }

            // Sweep Speed Selector (1x, 2x, 4x)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(2.dp)
            ) {
                listOf(1, 2, 4).forEach { multiplier ->
                    val isSpeedSelected = sweepMultiplier == multiplier
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSpeedSelected) PhotonicCyan.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable { sweepMultiplier = multiplier }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("sweep_speed_${multiplier}x"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${multiplier}X",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSpeedSelected) PhotonicCyanLight else AmbientWhiteMuted,
                            fontWeight = if (isSpeedSelected) FontWeight.Bold else FontWeight.Normal,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Fast reseed button
            QuantumVolumetricButton(
                text = "RE-SEED",
                icon = Icons.Default.Refresh,
                primaryColor = OperationalEmerald,
                secondaryColor = PhotonicCyan,
                containerColor = SpaceCobaltDark,
                modifier = Modifier.height(38.dp).testTag("waveform_reseed_action"),
                shapeRadius = 8.dp,
                onClick = onForceReseed
            )
        }
    }

    if (wrapInCard) {
        QuantumGlassCard(
            borderColor = when (selectedChannel) {
                EntropyWaveformChannel.SENSOR_JITTER -> OperationalEmerald.copy(alpha = 0.7f)
                EntropyWaveformChannel.CSPRNG_DIFFUSION -> PhotonicCyan.copy(alpha = 0.7f)
                EntropyWaveformChannel.SHANNON_SPECTRAL -> QuantumVioletLight.copy(alpha = 0.7f)
            },
            backgroundColor = SpaceCobaltGlassElevated,
            modifier = modifier.testTag("hardware_sensor_waveform_visualizer"),
            content = contentBlock
        )
    } else {
        Column(
            modifier = modifier.testTag("hardware_sensor_waveform_visualizer"),
            content = contentBlock
        )
    }
}
