package com.example.ui.modifiers

import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlin.math.*

/**
 * Optical and volumetric configuration parameters for the AGIS 2045 Volumetric Glass pipeline.
 *
 * Implements physical optical principles:
 * - Snell's Law refraction ray displacement based on virtual thickness and refractive index.
 * - Fresnel reflection equation approximation along grazing angles.
 * - Spectral dispersion (chromatic aberration) separating high and low wavelength refraction rays.
 * - Sub-surface micro-crystalline photon scatter suspended within the glass medium.
 */
@Stable
data class VolumetricGlassConfig(
    val thicknessMm: Float = 12.0f,
    val refractiveIndex: Float = 1.68f,
    val frostedRoughness: Float = 0.65f,
    val specularIntensity: Float = 0.85f,
    val chromaticDispersion: Float = 0.70f,
    val maxTiltAngleDegrees: Float = 14.0f,
    val cameraDistanceFactor: Float = 18.0f,
    val primarySignalColor: Color = PhotonicCyan,
    val secondarySignalColor: Color = OperationalEmerald,
    val glassBackgroundColor: Color = SpaceCobaltGlass,
    val shapeRadius: Dp = 16.dp,
    val elevation: Dp = 8.dp,
    val enablePointerTilt: Boolean = true,
    val enableAmbientOscillation: Boolean = true,
    val enableCausticSweep: Boolean = true,
    val showHudEtchings: Boolean = true
)

/**
 * Observable runtime state for the Volumetric Glass Modifier.
 * Tracks live 3D orientation, touch contact point, focal bloom, and calculated optical displacement.
 */
@Stable
class VolumetricGlassState {
    var tiltX by mutableFloatStateOf(0f)
    var tiltY by mutableFloatStateOf(0f)
    var isInteracting by mutableStateOf(false)
    var touchContactPoint by mutableStateOf<Offset?>(null)
    var snellDisplacementX by mutableFloatStateOf(0f)
    var snellDisplacementY by mutableFloatStateOf(0f)
    var currentFocalBloomRadius by mutableFloatStateOf(0f)
}

@Composable
fun rememberVolumetricGlassState(): VolumetricGlassState {
    return remember { VolumetricGlassState() }
}

/**
 * Core Volumetric Glass Modifier pipeline.
 *
 * Chains:
 * 1. [Modifier.pointerInput] - Captures multi-axis touch drag and tap pressure for 3D parallax tilting.
 * 2. [Modifier.graphicsLayer] - Applies physical 3D perspective projection (rotationX, rotationY, cameraDistance),
 *    volumetric alpha transparency, spring-damped scaling, and photonic tinted shadows.
 * 3. [Modifier.drawBehind] - Renders the internal sub-surface medium: Snell's Law refracted backplane lattice,
 *    angle-dependent radial diffusion center, and suspended micro-crystalline depth scatter particles.
 * 4. [Modifier.drawWithContent] - Renders content followed by top-surface optical effects:
 *    Fresnel border luminance, chromatic aberration fringes (cyan vs crimson split), dynamic specular
 *    caustic sheen sweeps, and laser-etched AGIS 2045 HUD reticles.
 */
fun Modifier.volumetricGlass(
    config: VolumetricGlassConfig = VolumetricGlassConfig(),
    state: VolumetricGlassState? = null
): Modifier = composed {
    val internalState = state ?: remember { VolumetricGlassState() }
    val density = LocalDensity.current

    var containerSize by remember { mutableStateOf(Size.Zero) }

    // Ambient 3D idle breathing oscillation (mimics subtle micro-gyro drift)
    val infiniteTransition = rememberInfiniteTransition(label = "VolumetricGlassAmbientTransition")
    val idlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "IdlePhase"
    )

    // Dynamic traveling specular caustic sweep
    val causticSweepPhase by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CausticSweep"
    )

    // Touch focal bloom expansion
    val focalBloomAnimation by infiniteTransition.animateFloat(
        initialValue = 18f,
        targetValue = 38f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FocalBloom"
    )

    // Calculate target 3D tilt angles from interaction or idle drift
    val targetTiltX = when {
        internalState.touchContactPoint != null && containerSize.height > 0 -> {
            val normalizedY = ((internalState.touchContactPoint!!.y / containerSize.height) - 0.5f) * 2f
            (-normalizedY * config.maxTiltAngleDegrees).coerceIn(-config.maxTiltAngleDegrees, config.maxTiltAngleDegrees)
        }
        config.enableAmbientOscillation -> sin(idlePhase) * (config.maxTiltAngleDegrees * 0.18f)
        else -> 0f
    }

    val targetTiltY = when {
        internalState.touchContactPoint != null && containerSize.width > 0 -> {
            val normalizedX = ((internalState.touchContactPoint!!.x / containerSize.width) - 0.5f) * 2f
            (normalizedX * config.maxTiltAngleDegrees).coerceIn(-config.maxTiltAngleDegrees, config.maxTiltAngleDegrees)
        }
        config.enableAmbientOscillation -> cos(idlePhase * 0.85f) * (config.maxTiltAngleDegrees * 0.18f)
        else -> 0f
    }

    // Spring animations for physical inertia and bounce on release
    val animatedTiltX by animateFloatAsState(
        targetValue = targetTiltX,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "AnimatedTiltX"
    )

    val animatedTiltY by animateFloatAsState(
        targetValue = targetTiltY,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "AnimatedTiltY"
    )

    val animatedScale by animateFloatAsState(
        targetValue = if (internalState.isInteracting) 1.018f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "AnimatedScale"
    )

    // Sync computed values back to runtime state
    SideEffect {
        internalState.tiltX = animatedTiltX
        internalState.tiltY = animatedTiltY
        // Snell's Law simulated displacement: d = t * sin(tilt) / n
        val radX = Math.toRadians(animatedTiltX.toDouble()).toFloat()
        val radY = Math.toRadians(animatedTiltY.toDouble()).toFloat()
        internalState.snellDisplacementX = (config.thicknessMm * sin(radY) / config.refractiveIndex) * 2.8f
        internalState.snellDisplacementY = (-config.thicknessMm * sin(radX) / config.refractiveIndex) * 2.8f
        internalState.currentFocalBloomRadius = focalBloomAnimation
    }

    val shape = RoundedCornerShape(config.shapeRadius)

    this
        // 1. POINTER INTERACTION (Direct multi-touch drag and tap parallax)
        .then(
            if (config.enablePointerTilt) {
                Modifier
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onPress = { offset ->
                                internalState.touchContactPoint = offset
                                internalState.isInteracting = true
                                tryAwaitRelease()
                                internalState.touchContactPoint = null
                                internalState.isInteracting = false
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                internalState.touchContactPoint = offset
                                internalState.isInteracting = true
                            },
                            onDrag = { change, _ ->
                                change.consume()
                                internalState.touchContactPoint = change.position
                            },
                            onDragEnd = {
                                internalState.touchContactPoint = null
                                internalState.isInteracting = false
                            },
                            onDragCancel = {
                                internalState.touchContactPoint = null
                                internalState.isInteracting = false
                            }
                        )
                    }
            } else Modifier
        )
        // 2. 3D GRAPHICS LAYER (Camera matrix, perspective pitch/yaw, transparency, photonic shadows)
        .graphicsLayer {
            // Calibrate camera perspective matrix based on screen density & config
            cameraDistance = config.cameraDistanceFactor * density.density * 12f
            rotationX = animatedTiltX
            rotationY = animatedTiltY
            scaleX = animatedScale
            scaleY = animatedScale

            // Photonic tinted drop-shadows with colored ambient diffusion
            val shadowElev = config.elevation.toPx() * (1f + (config.thicknessMm / 15f))
            shadowElevation = shadowElev
            ambientShadowColor = config.secondarySignalColor.copy(alpha = 0.28f)
            spotShadowColor = config.primarySignalColor.copy(alpha = 0.42f)

            // Volumetric transparency modulation
            alpha = 0.965f
            this.shape = shape
            clip = false
        }
        // 3. DRAW BEHIND (Sub-surface optics: Snell refraction displacement, depth grid, internal scatter particles)
        .drawBehind {
            containerSize = size
            val width = size.width
            val height = size.height
            val cornerRadiusPx = config.shapeRadius.toPx()

            val roundRect = RoundRect(
                left = 0f,
                top = 0f,
                right = width,
                bottom = height,
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
            val clipPath = Path().apply { addRoundRect(roundRect) }

            clipPath(clipPath) {
                // Layer A: Semi-Transparent Frosted Substrate with Angle-Tracked Light Center
                val lightNormX = (animatedTiltY / config.maxTiltAngleDegrees.coerceAtLeast(1f))
                val lightNormY = (-animatedTiltX / config.maxTiltAngleDegrees.coerceAtLeast(1f))
                val lightCenterX = width * (0.5f + lightNormX * 0.38f)
                val lightCenterY = height * (0.5f + lightNormY * 0.38f)
                val lightCenter = Offset(lightCenterX, lightCenterY)

                val baseFrostedAlpha = (0.78f + (config.frostedRoughness * 0.16f)).coerceIn(0.5f, 0.96f)
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            config.primarySignalColor.copy(alpha = 0.22f * config.frostedRoughness),
                            config.glassBackgroundColor.copy(alpha = baseFrostedAlpha),
                            SpaceCobaltSurface.copy(alpha = 0.94f)
                        ),
                        center = lightCenter,
                        radius = (size.maxDimension * 0.85f).coerceAtLeast(10f)
                    )
                )

                // Layer B: Snell's Law Refraction Parallax Shift
                val parallaxX = internalState.snellDisplacementX * density.density
                val parallaxY = internalState.snellDisplacementY * density.density

                // Recessed Cryptographic Quantum Grid (Visible deep inside the glass medium)
                val gridStep = 24.dp.toPx()
                val cols = (width / gridStep).toInt()
                val rows = (height / gridStep).toInt()

                val gridAlpha = (0.05f * (config.thicknessMm / 10f)).coerceIn(0.02f, 0.12f)
                for (c in 1..cols) {
                    val gx = c * gridStep + parallaxX * 0.45f
                    drawLine(
                        color = config.primarySignalColor.copy(alpha = gridAlpha),
                        start = Offset(gx, 0f),
                        end = Offset(gx, height),
                        strokeWidth = 1.dp.toPx()
                    )
                }
                for (r in 1..rows) {
                    val gy = r * gridStep + parallaxY * 0.45f
                    drawLine(
                        color = config.primarySignalColor.copy(alpha = gridAlpha),
                        start = Offset(0f, gy),
                        end = Offset(width, gy),
                        strokeWidth = 1.dp.toPx()
                    )
                }

                // Layer C: Suspended Micro-Crystalline Depth Scatter Particles
                // Stratified into three depth tiers (Z = 0.25, 0.50, 0.75) for stereoscopic depth illusion
                val particleCount = (config.frostedRoughness * 42).toInt().coerceIn(16, 50)
                for (i in 0 until particleCount) {
                    // Seeded deterministic coordinates
                    val seedX = ((i * 47 + 13) % 100) / 100f
                    val seedY = ((i * 83 + 29) % 100) / 100f

                    // Depth tier stratification: deeper particles shift more with parallax
                    val depthTier = ((i % 3) + 1) / 3f // 0.33, 0.66, 1.0
                    val pX = seedX * width + parallaxX * depthTier
                    val pY = seedY * height + parallaxY * depthTier

                    val pAlpha = (((i * 19) % 24) / 100f + 0.05f) * config.frostedRoughness
                    val pRadius = if (i % 5 == 0) 2.4.dp.toPx() else 1.2.dp.toPx()

                    val pColor = when (i % 4) {
                        0 -> config.primarySignalColor.copy(alpha = pAlpha.coerceIn(0.04f, 0.30f))
                        1 -> AmbientWhite.copy(alpha = (pAlpha * 1.3f).coerceIn(0.06f, 0.32f))
                        2 -> config.secondarySignalColor.copy(alpha = (pAlpha * 0.85f).coerceIn(0.03f, 0.24f))
                        else -> QuantumVioletLight.copy(alpha = (pAlpha * 0.9f).coerceIn(0.04f, 0.22f))
                    }

                    drawCircle(
                        color = pColor,
                        radius = pRadius,
                        center = Offset(pX, pY)
                    )

                    // Diamond micro-facet sparkle
                    if (i % 6 == 0 && config.specularIntensity > 0.4f) {
                        val sparkleArm = (3.dp.toPx() * depthTier)
                        drawLine(
                            color = AmbientWhite.copy(alpha = pAlpha * 1.6f),
                            start = Offset(pX - sparkleArm, pY),
                            end = Offset(pX + sparkleArm, pY),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = AmbientWhite.copy(alpha = pAlpha * 1.6f),
                            start = Offset(pX, pY - sparkleArm),
                            end = Offset(pX, pY + sparkleArm),
                            strokeWidth = 1f
                        )
                    }
                }

                // Layer D: Interactive Touch Focal Flare (Refraction bloom where finger touches the glass)
                val contact = internalState.touchContactPoint
                if (contact != null) {
                    val bloomRadius = (internalState.currentFocalBloomRadius * density.density).coerceAtLeast(15f)
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                config.primarySignalColor.copy(alpha = 0.40f),
                                config.primarySignalColor.copy(alpha = 0.12f),
                                Color.Transparent
                            ),
                            center = contact,
                            radius = bloomRadius * 2.2f
                        ),
                        center = contact,
                        radius = bloomRadius * 2.2f
                    )
                    drawCircle(
                        color = AmbientWhite.copy(alpha = 0.65f),
                        radius = 4.dp.toPx(),
                        center = contact
                    )
                }
            }
        }
        // 4. DRAW WITH CONTENT (Top-surface optics: Fresnel border glow, chromatic aberration, caustics, and HUD marks)
        .drawWithContent {
            // Draw user-defined inner content first
            drawContent()

            val width = size.width
            val height = size.height
            val cornerRadiusPx = config.shapeRadius.toPx()

            val roundRect = RoundRect(
                left = 0f,
                top = 0f,
                right = width,
                bottom = height,
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
            val clipPath = Path().apply { addRoundRect(roundRect) }

            clipPath(clipPath) {
                // A. Traveling Specular Caustic Sheen Sweep across Front Face
                if (config.enableCausticSweep && config.specularIntensity > 0.1f) {
                    val sweepProgress = causticSweepPhase
                    val sweepStartX = sweepProgress * (width + height * 0.7f) - height * 0.35f
                    val sweepStartY = 0f
                    val sweepEndX = sweepStartX - height * 0.7f
                    val sweepEndY = height

                    val sheenAlpha = (0.32f * config.specularIntensity * (0.8f + (config.thicknessMm / 20f) * 0.2f))
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                config.primarySignalColor.copy(alpha = sheenAlpha * 0.35f),
                                AmbientWhite.copy(alpha = sheenAlpha),
                                config.primarySignalColor.copy(alpha = sheenAlpha * 0.35f),
                                Color.Transparent
                            ),
                            start = Offset(sweepStartX, sweepStartY),
                            end = Offset(sweepEndX, sweepEndY)
                        ),
                        blendMode = BlendMode.Plus
                    )
                }

                // B. Prismatic Chromatic Aberration Rim (Snell spectral dispersion along beveled perimeter)
                if (config.chromaticDispersion > 0.2f) {
                    val dispersionPx = (1.8.dp.toPx() * config.chromaticDispersion)

                    // High-frequency Blue/Cyan refracted rim
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                config.primarySignalColor.copy(alpha = 0.50f * config.chromaticDispersion),
                                Color.Transparent
                            )
                        ),
                        size = Size(width, height),
                        cornerRadius = CornerRadius(cornerRadiusPx),
                        style = Stroke(width = 1.2.dp.toPx())
                    )

                    // Low-frequency Crimson/Amber refracted rim (displaced diagonally)
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                ContainmentCrimson.copy(alpha = 0.25f * config.chromaticDispersion)
                            )
                        ),
                        topLeft = Offset(dispersionPx, dispersionPx),
                        size = Size(width - dispersionPx * 2, height - dispersionPx * 2),
                        cornerRadius = CornerRadius((cornerRadiusPx - dispersionPx).coerceAtLeast(0f)),
                        style = Stroke(width = 1.0.dp.toPx())
                    )
                }

                // C. Fresnel Edge Reflection & Double-Beveled Glass Chamfer
                val fresnelAlpha = (0.75f * config.specularIntensity).coerceIn(0.25f, 0.90f)
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AmbientWhite.copy(alpha = fresnelAlpha),
                            config.primarySignalColor.copy(alpha = fresnelAlpha * 0.65f),
                            config.secondarySignalColor.copy(alpha = 0.25f),
                            SpaceCobaltDark.copy(alpha = 0.45f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(width, height)
                    ),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(cornerRadiusPx),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // D. Inset Secondary Glass Bevel Catchline
                val insetBevel = 1.5.dp.toPx()
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AmbientWhite.copy(alpha = 0.35f * config.specularIntensity),
                            Color.Transparent,
                            SpaceCobaltSurface.copy(alpha = 0.5f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(width, height)
                    ),
                    topLeft = Offset(insetBevel, insetBevel),
                    size = Size(width - insetBevel * 2, height - insetBevel * 2),
                    cornerRadius = CornerRadius((cornerRadiusPx - insetBevel).coerceAtLeast(0f)),
                    style = Stroke(width = 1.0.dp.toPx())
                )

                // E. AGIS 2045 Laser-Etched HUD Corner Reticles
                if (config.showHudEtchings && width > 120.dp.toPx() && height > 80.dp.toPx()) {
                    val reticleLen = 8.dp.toPx()
                    val reticleInset = 6.dp.toPx()
                    val reticleColor = config.primarySignalColor.copy(alpha = 0.40f)

                    // Top-Left reticle
                    drawLine(reticleColor, Offset(reticleInset, reticleInset), Offset(reticleInset + reticleLen, reticleInset), 1.2f)
                    drawLine(reticleColor, Offset(reticleInset, reticleInset), Offset(reticleInset, reticleInset + reticleLen), 1.2f)

                    // Top-Right reticle
                    drawLine(reticleColor, Offset(width - reticleInset, reticleInset), Offset(width - reticleInset - reticleLen, reticleInset), 1.2f)
                    drawLine(reticleColor, Offset(width - reticleInset, reticleInset), Offset(width - reticleInset, reticleInset + reticleLen), 1.2f)

                    // Bottom-Left reticle
                    drawLine(reticleColor, Offset(reticleInset, height - reticleInset), Offset(reticleInset + reticleLen, height - reticleInset), 1.2f)
                    drawLine(reticleColor, Offset(reticleInset, height - reticleInset), Offset(reticleInset, height - reticleInset - reticleLen), 1.2f)

                    // Bottom-Right reticle
                    drawLine(reticleColor, Offset(width - reticleInset, height - reticleInset), Offset(width - reticleInset - reticleLen, height - reticleInset), 1.2f)
                    drawLine(reticleColor, Offset(width - reticleInset, height - reticleInset), Offset(width - reticleInset, height - reticleInset - reticleLen), 1.2f)
                }
            }
        }
}
