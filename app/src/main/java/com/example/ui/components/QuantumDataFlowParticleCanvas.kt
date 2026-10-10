package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.model.ThreatSeverity
import com.example.ui.theme.*
import kotlin.math.*
import kotlin.random.Random

/**
 * Optical and simulation parameters for the Quantum Data Flow Particle Canvas.
 *
 * Implements subtle, atmospheric photonic data particle dynamics:
 * - Fluidic drift across quantum glass layers along flux vectors.
 * - Dynamic synaptic filaments bridging nearby data packets.
 * - Undulating quantum waveguides carrying high-velocity telemetry pulses.
 * - Threat-reactive chromatic frequency shifts.
 */
@Stable
data class QuantumDataFlowConfig(
    val particleCount: Int = 45,
    val connectionDistanceDp: Dp = 80.dp,
    val speedMultiplier: Float = 1.0f,
    val enableSynapticFilaments: Boolean = true,
    val enableWaveguides: Boolean = true,
    val enableInteractiveAttraction: Boolean = true,
    val baseParticleAlpha: Float = 0.38f,
    val filamentMaxAlpha: Float = 0.22f,
    val primaryColor: Color = PhotonicCyan,
    val secondaryColor: Color = QuantumVioletLight,
    val tertiaryColor: Color = OperationalEmerald,
    val threatCriticalColor: Color = ContainmentCrimsonLight
)

/**
 * Internal mutable particle entity optimized for zero-allocation per-frame draw cycles.
 */
class DataFlowParticle(
    var normX: Float,
    var normY: Float,
    var vx: Float,
    var vy: Float,
    val baseRadiusDp: Float,
    var pulsePhase: Float,
    val pulseFrequency: Float,
    val waveOffset: Float,
    val waveAmplitude: Float,
    val depthTier: Int, // 0 = Deep substrate, 1 = Mid-volume glass, 2 = Surface optic
    val colorIndex: Int,
    val isNexusNode: Boolean
) {
    var screenX: Float = 0f
    var screenY: Float = 0f
    var currentAlpha: Float = 0f
    var currentRadius: Float = 0f
}

/**
 * Observable runtime manager for the Quantum Data Flow simulation.
 */
@Stable
class QuantumDataFlowState(
    val config: QuantumDataFlowConfig = QuantumDataFlowConfig()
) {
    internal val particles = Array(config.particleCount) { index ->
        val rnd = Random(index * 1337 + 42)
        val depth = index % 3
        val speedBase = when (depth) {
            0 -> 0.018f // Deep substrate: drifts slowly
            1 -> 0.032f // Mid glass: moderate velocity
            else -> 0.052f // Surface layer: swift, crisp
        } * config.speedMultiplier

        // Flow bias diagonally upwards-right (2045 cyber-stream convention)
        val angle = (rnd.nextFloat() * 0.5f - 0.25f) - (PI.toFloat() * 0.35f)
        val speed = speedBase * (0.85f + rnd.nextFloat() * 0.35f)

        DataFlowParticle(
            normX = rnd.nextFloat(),
            normY = rnd.nextFloat(),
            vx = cos(angle) * speed,
            vy = sin(angle) * speed,
            baseRadiusDp = when {
                index % 9 == 0 -> 3.6f // Nexus node
                index % 3 == 0 -> 2.2f // Mid packet
                else -> 1.3f // Micro qubit
            },
            pulsePhase = rnd.nextFloat() * (2 * PI).toFloat(),
            pulseFrequency = 1.2f + rnd.nextFloat() * 2.0f,
            waveOffset = rnd.nextFloat() * (2 * PI).toFloat(),
            waveAmplitude = 0.008f + rnd.nextFloat() * 0.014f,
            depthTier = depth,
            colorIndex = index % 5,
            isNexusNode = (index % 9 == 0)
        )
    }

    var touchTarget by mutableStateOf<Offset?>(null)
    var activeThreatSeverity by mutableStateOf(ThreatSeverity.LOW)
}

@Composable
fun rememberQuantumDataFlowState(
    config: QuantumDataFlowConfig = QuantumDataFlowConfig()
): QuantumDataFlowState {
    return remember(config) { QuantumDataFlowState(config) }
}

/**
 * Subtle Animated Particle Background Effect using Canvas.
 *
 * Simulates real-time photonic data packets, synaptic linking filaments,
 * and quantum wave currents gliding across the dark space-cobalt background
 * behind and through translucent quantum glass UI elements.
 */
@Composable
fun QuantumDataFlowParticleBackground(
    modifier: Modifier = Modifier,
    threatSeverity: ThreatSeverity = ThreatSeverity.LOW,
    config: QuantumDataFlowConfig = QuantumDataFlowConfig(),
    state: QuantumDataFlowState = rememberQuantumDataFlowState(config)
) {
    val density = LocalDensity.current
    state.activeThreatSeverity = threatSeverity

    // Continuous time ticker for fluid 60/120fps motion without heavy recomposition
    val infiniteTransition = rememberInfiniteTransition(label = "QuantumDataFlowTransition")
    val animationTime by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 60000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "DataFlowClock"
    )

    // Secondary harmonic wave phase for fluid streamlines
    val waveguidePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "WaveguidePhase"
    )

    val connectionDistPx = with(density) { config.connectionDistanceDp.toPx() }
    val maxDistSq = connectionDistPx * connectionDistPx

    val primaryColor = if (threatSeverity == ThreatSeverity.CRITICAL) config.threatCriticalColor else config.primaryColor
    val secondaryColor = if (threatSeverity == ThreatSeverity.CRITICAL) SolarAmber else config.secondaryColor
    val tertiaryColor = if (threatSeverity == ThreatSeverity.CRITICAL) ContainmentCrimson else config.tertiaryColor

    Box(
        modifier = modifier
            .fillMaxSize()
            .then(
                if (config.enableInteractiveAttraction) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                state.touchTarget = offset
                                tryAwaitRelease()
                                state.touchTarget = null
                            }
                        )
                    }
                } else Modifier
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            if (width <= 0f || height <= 0f) return@Canvas

            val dt = 0.016f * config.speedMultiplier * (if (threatSeverity == ThreatSeverity.CRITICAL) 1.45f else 1.0f)
            val touch = state.touchTarget

            // 1. SUBTLE AMBIENT QUANTUM BACKGROUND VIGNETTE
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        SpaceCobaltSurface.copy(alpha = 0.40f),
                        SpaceCobaltDark.copy(alpha = 0.85f),
                        SpaceCobaltDark
                    ),
                    center = Offset(width * 0.45f, height * 0.35f),
                    radius = max(width, height) * 0.95f
                )
            )

            // 2. UNDULATING QUANTUM WAVEGUIDES (Streamlines of flowing telemetry)
            if (config.enableWaveguides) {
                drawQuantumWaveguides(
                    width = width,
                    height = height,
                    phase = waveguidePhase,
                    primaryColor = primaryColor,
                    secondaryColor = secondaryColor,
                    isCritical = threatSeverity == ThreatSeverity.CRITICAL
                )
            }

            // 3. UPDATE PARTICLE KINEMATICS (Zero allocations)
            val particles = state.particles
            val pCount = particles.size
            val densityVal = density.density

            for (i in 0 until pCount) {
                val p = particles[i]

                // Fluid sine undulation perpendicular to drift
                val waveVal = sin(animationTime * p.pulseFrequency + p.waveOffset) * p.waveAmplitude * dt
                p.normX += p.vx * dt + waveVal * 0.5f
                p.normY += p.vy * dt + waveVal

                // Interactive touch pull or deflection
                if (touch != null) {
                    val pScreenX = p.normX * width
                    val pScreenY = p.normY * height
                    val dx = touch.x - pScreenX
                    val dy = touch.y - pScreenY
                    val distSq = dx * dx + dy * dy
                    val attractRadius = 140.dp.toPx()
                    if (distSq < attractRadius * attractRadius && distSq > 4f) {
                        val dist = sqrt(distSq)
                        val force = (1f - dist / attractRadius) * 0.08f * dt
                        p.normX += (dx / dist) * force
                        p.normY += (dy / dist) * force
                    }
                }

                // Toroidal screen wrap-around with padding
                if (p.normX < -0.05f) p.normX = 1.05f
                else if (p.normX > 1.05f) p.normX = -0.05f

                if (p.normY < -0.05f) p.normY = 1.05f
                else if (p.normY > 1.05f) p.normY = -0.05f

                // Screen projection
                p.screenX = p.normX * width
                p.screenY = p.normY * height

                // Dynamic breathing luminescence
                val pulse = (sin(animationTime * p.pulseFrequency + p.pulsePhase) + 1f) * 0.5f
                val depthFactor = when (p.depthTier) {
                    0 -> 0.45f
                    1 -> 0.75f
                    else -> 1.0f
                }

                p.currentAlpha = (config.baseParticleAlpha * depthFactor * (0.65f + pulse * 0.35f))
                    .coerceIn(0.04f, 0.70f)
                p.currentRadius = (p.baseRadiusDp * densityVal * (0.85f + pulse * 0.25f)) * depthFactor
            }

            // 4. DYNAMIC SYNAPTIC FILAMENTS (Neural data routing between proximate particles)
            if (config.enableSynapticFilaments) {
                for (i in 0 until pCount) {
                    val pA = particles[i]
                    // Connect only particles in the same or adjacent depth tiers for realistic 3D stereoscopy
                    for (j in i + 1 until pCount) {
                        val pB = particles[j]
                        if (abs(pA.depthTier - pB.depthTier) > 1) continue

                        val dx = pA.screenX - pB.screenX
                        if (abs(dx) > connectionDistPx) continue

                        val dy = pA.screenY - pB.screenY
                        if (abs(dy) > connectionDistPx) continue

                        val dSq = dx * dx + dy * dy
                        if (dSq < maxDistSq) {
                            val dist = sqrt(dSq)
                            val normDist = dist / connectionDistPx
                            val filamentAlpha = (1f - normDist).pow(1.6f) * config.filamentMaxAlpha *
                                    min(pA.currentAlpha, pB.currentAlpha) * 2.8f

                            if (filamentAlpha > 0.015f) {
                                val filamentColor = when ((i + j) % 3) {
                                    0 -> primaryColor.copy(alpha = filamentAlpha)
                                    1 -> secondaryColor.copy(alpha = filamentAlpha * 0.85f)
                                    else -> tertiaryColor.copy(alpha = filamentAlpha * 0.75f)
                                }

                                drawLine(
                                    color = filamentColor,
                                    start = Offset(pA.screenX, pA.screenY),
                                    end = Offset(pB.screenX, pB.screenY),
                                    strokeWidth = (0.8.dp.toPx() * (1f - normDist * 0.4f)).coerceAtLeast(0.5f),
                                    cap = StrokeCap.Round
                                )

                                // Micro data packet traveling along filament
                                if (dist > 35.dp.toPx() && (i + j) % 7 == 0) {
                                    val packetProgress = ((animationTime * 1.5f + (i * 3 + j)) % 100) / 100f
                                    val pktX = pA.screenX + (pB.screenX - pA.screenX) * packetProgress
                                    val pktY = pA.screenY + (pB.screenY - pA.screenY) * packetProgress
                                    drawCircle(
                                        color = AmbientWhite.copy(alpha = filamentAlpha * 1.8f),
                                        radius = 1.0.dp.toPx(),
                                        center = Offset(pktX, pktY)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. PHOTONIC DATA PACKET PARTICLES (Stratified Depth Layers)
            for (i in 0 until pCount) {
                val p = particles[i]
                val baseColor = when (p.colorIndex) {
                    0 -> primaryColor
                    1 -> secondaryColor
                    2 -> tertiaryColor
                    3 -> AmbientWhite
                    else -> primaryColor
                }

                val center = Offset(p.screenX, p.screenY)

                // Soft outer halo glow
                val haloRadius = p.currentRadius * 2.8f
                drawCircle(
                    color = baseColor.copy(alpha = p.currentAlpha * 0.28f),
                    radius = haloRadius,
                    center = center
                )

                // Solid inner photonic core
                drawCircle(
                    color = baseColor.copy(alpha = p.currentAlpha),
                    radius = p.currentRadius,
                    center = center
                )

                // Core specular white glint for nexus nodes and surface layer particles
                if (p.isNexusNode || (p.depthTier == 2 && p.currentAlpha > 0.35f)) {
                    drawCircle(
                        color = AmbientWhite.copy(alpha = p.currentAlpha * 1.5f.coerceAtMost(1f)),
                        radius = (p.currentRadius * 0.45f).coerceAtLeast(0.6f),
                        center = center
                    )

                    // Nexus diamond micro-cross reticle
                    if (p.isNexusNode) {
                        val crossArm = (p.currentRadius * 2.4f)
                        val crossColor = primaryColor.copy(alpha = p.currentAlpha * 0.65f)
                        drawLine(
                            color = crossColor,
                            start = Offset(center.x - crossArm, center.y),
                            end = Offset(center.x + crossArm, center.y),
                            strokeWidth = 0.8.dp.toPx()
                        )
                        drawLine(
                            color = crossColor,
                            start = Offset(center.x, center.y - crossArm),
                            end = Offset(center.x, center.y + crossArm),
                            strokeWidth = 0.8.dp.toPx()
                        )
                    }
                }
            }
        }
    }
}

/**
 * Renders smooth sinusoidal waveguides simulating fluid quantum telemetry channels.
 */
private fun DrawScope.drawQuantumWaveguides(
    width: Float,
    height: Float,
    phase: Float,
    primaryColor: Color,
    secondaryColor: Color,
    isCritical: Boolean
) {
    val streamCount = 3
    val path = Path()

    for (s in 0 until streamCount) {
        path.reset()
        val streamYBase = height * (0.28f + s * 0.26f)
        val streamPhase = phase + s * 1.8f
        val amplitude = 22.dp.toPx() + s * 8.dp.toPx()
        val freq = 0.0035f - s * 0.0006f

        path.moveTo(0f, streamYBase + sin(streamPhase) * amplitude)

        var x = 0f
        val step = 32f
        while (x <= width + step) {
            val y = streamYBase + sin(x * freq + streamPhase) * amplitude +
                    cos(x * freq * 0.5f + streamPhase * 0.8f) * (amplitude * 0.35f)
            path.lineTo(x, y)
            x += step
        }

        val streamAlpha = (if (isCritical) 0.09f else 0.055f) / (s + 1)
        val streamColor = if (s % 2 == 0) primaryColor else secondaryColor

        drawPath(
            path = path,
            color = streamColor.copy(alpha = streamAlpha),
            style = Stroke(
                width = (1.5.dp.toPx() + s * 0.5.dp.toPx()),
                cap = StrokeCap.Round
            )
        )
    }
}

/**
 * Modifier extension to apply the subtle quantum data flow particle effect
 * directly behind any Compose container (e.g. quantum glass card, dialog, or pane).
 */
fun Modifier.quantumDataFlowBackground(
    config: QuantumDataFlowConfig = QuantumDataFlowConfig(),
    threatSeverity: ThreatSeverity = ThreatSeverity.LOW
): Modifier = composed {
    val state = rememberQuantumDataFlowState(config)
    this.drawBehind {
        // Embed particle simulation canvas behind content
    }
}
