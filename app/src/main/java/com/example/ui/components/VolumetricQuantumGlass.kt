package com.example.ui.components

import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Volumetric Depth Profiles for Quantum Glass.
 * Calibrates camera distance, parallax tilt factor, frosted scatter intensity,
 * and physical optical parameters.
 */
enum class QuantumGlassDepthLevel(
    val label: String,
    val shortName: String,
    val depthFactor: Float,
    val frostedIntensity: Float,
    val cameraDistanceMultiplier: Float,
    val virtualThicknessMm: Float,
    val refractiveIndex: Float
) {
    LEVEL_2D_CLEAN(
        label = "2D Clean Flat",
        shortName = "2D",
        depthFactor = 0.15f,
        frostedIntensity = 0.30f,
        cameraDistanceMultiplier = 8f,
        virtualThicknessMm = 2.0f,
        refractiveIndex = 1.33f
    ),
    LEVEL_25D_VOLUMETRIC(
        label = "2.5D Volumetric",
        shortName = "2.5D",
        depthFactor = 0.65f,
        frostedIntensity = 0.65f,
        cameraDistanceMultiplier = 14f,
        virtualThicknessMm = 6.5f,
        refractiveIndex = 1.52f
    ),
    LEVEL_3D_QUANTUM(
        label = "3D Quantum Volumetric",
        shortName = "3D",
        depthFactor = 1.0f,
        frostedIntensity = 0.88f,
        cameraDistanceMultiplier = 20f,
        virtualThicknessMm = 12.0f,
        refractiveIndex = 1.68f
    ),
    LEVEL_HYPER_ENCLAVE(
        label = "Hyper-Enclave Volumetric",
        shortName = "HYPER",
        depthFactor = 1.45f,
        frostedIntensity = 1.0f,
        cameraDistanceMultiplier = 26f,
        virtualThicknessMm = 18.5f,
        refractiveIndex = 1.94f
    );

    companion object {
        fun fromString(value: String): QuantumGlassDepthLevel {
            return when {
                value.contains("2D", ignoreCase = true) -> LEVEL_2D_CLEAN
                value.contains("2.5D", ignoreCase = true) -> LEVEL_25D_VOLUMETRIC
                value.contains("Hyper", ignoreCase = true) -> LEVEL_HYPER_ENCLAVE
                else -> LEVEL_3D_QUANTUM
            }
        }
    }
}

/**
 * Volumetric Quantum Glass Surface
 *
 * A specialized Material 3 UI component combining [Canvas] and [Modifier.graphicsLayer]
 * to create a semi-transparent, depth-aware frosted glass effect.
 *
 * Key Architectural Features:
 * 1. **GraphicsLayer**:
 *    - Dynamic 3D perspective camera matrix ([cameraDistance]).
 *    - Parallax rotation ([rotationX], [rotationY]) driven by touch/drag or idle micro-drift.
 *    - Depth translation on Z-axis ([translationZ]) with reactive scale spring.
 *    - Colored photonic ambient & spot shadows ([ambientShadowColor], [spotShadowColor]).
 *    - Semi-transparent alpha modulation for physical glass transparency.
 *
 * 2. **Canvas (Depth-Aware Frosted Optics)**:
 *    - Sub-surface micro-crystalline scatter matrix rendered with parallax offset,
 *      giving the appearance that frosted particles reside within the internal thickness of the glass.
 *    - Volumetric radial light diffusion whose focal center tracks the 3D viewing angle.
 *    - Prismatic chromatic aberration rim (dual-wavelength spectral dispersion along beveled edges).
 *    - Dynamic specular caustic sweep passing over the frosted surface.
 *    - Double-beveled frosted perimeter with dual highlight and shadow catchlines.
 */
@Composable
fun VolumetricQuantumGlassSurface(
    modifier: Modifier = Modifier,
    depthLevel: QuantumGlassDepthLevel = QuantumGlassDepthLevel.LEVEL_25D_VOLUMETRIC,
    primarySignalColor: Color = PhotonicCyan,
    secondarySignalColor: Color = OperationalEmerald,
    backgroundColor: Color = SpaceCobaltGlass,
    shapeRadius: Dp = 16.dp,
    frostedRoughness: Float = 0.65f,
    specularIntensity: Float = 0.85f,
    elevation: Dp = 8.dp,
    enableDynamicTilt: Boolean = true,
    testTag: String = "volumetric_quantum_glass_surface",
    onClick: (() -> Unit)? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current

    // Touch & pointer interaction tracking
    var touchOffset by remember { mutableStateOf<Offset?>(null) }
    var isInteracting by remember { mutableStateOf(false) }
    var containerSize by remember { mutableStateOf(Size.Zero) }

    // Idle ambient breathing oscillation
    val infiniteTransition = rememberInfiniteTransition(label = "QuantumGlassOscillation")
    val idlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "IdlePhase"
    )

    val causticSweepPhase by infiniteTransition.animateFloat(
        initialValue = -0.4f,
        targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "CausticSweepPhase"
    )

    // Compute target rotation angles based on interaction or subtle idle drift
    val maxTiltDegrees = 14f * depthLevel.depthFactor

    val targetTiltX = when {
        touchOffset != null && containerSize.height > 0 -> {
            val normalizedY = ((touchOffset!!.y / containerSize.height) - 0.5f) * 2f
            (-normalizedY * maxTiltDegrees).coerceIn(-maxTiltDegrees, maxTiltDegrees)
        }
        enableDynamicTilt -> sin(idlePhase) * (2.2f * depthLevel.depthFactor)
        else -> 0f
    }

    val targetTiltY = when {
        touchOffset != null && containerSize.width > 0 -> {
            val normalizedX = ((touchOffset!!.x / containerSize.width) - 0.5f) * 2f
            (normalizedX * maxTiltDegrees).coerceIn(-maxTiltDegrees, maxTiltDegrees)
        }
        enableDynamicTilt -> cos(idlePhase * 0.8f) * (2.2f * depthLevel.depthFactor)
        else -> 0f
    }

    val targetScale = if (isInteracting) 1.02f else 1.0f

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
        targetValue = targetScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "AnimatedScale"
    )

    val cardShape = RoundedCornerShape(shapeRadius)

    // Gesture input modifier for dynamic tilt interaction
    val gestureModifier = if (enableDynamicTilt) {
        Modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = { offset ->
                        touchOffset = offset
                        isInteracting = true
                        tryAwaitRelease()
                        touchOffset = null
                        isInteracting = false
                    }
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        touchOffset = offset
                        isInteracting = true
                    },
                    onDrag = { change, _ ->
                        change.consume()
                        touchOffset = change.position
                    },
                    onDragEnd = {
                        touchOffset = null
                        isInteracting = false
                    },
                    onDragCancel = {
                        touchOffset = null
                        isInteracting = false
                    }
                )
            }
    } else Modifier

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else Modifier

    // Outer container applying GraphicsLayer 3D perspective, depth transformation, and colored shadows
    Box(
        modifier = modifier
            .testTag(testTag)
            .then(gestureModifier)
            .then(clickableModifier)
            .graphicsLayer {
                // 1. GraphicsLayer Camera Distance & 3D Perspective Rotation
                cameraDistance = depthLevel.cameraDistanceMultiplier * density.density * 12f
                rotationX = animatedTiltX
                rotationY = animatedTiltY
                scaleX = animatedScale
                scaleY = animatedScale

                // 2. GraphicsLayer Depth Shadows (Quantum Signal Tinted)
                val totalElevation = elevation * (1f + depthLevel.depthFactor)
                shadowElevation = totalElevation.toPx()
                spotShadowColor = primarySignalColor.copy(alpha = 0.45f * depthLevel.depthFactor)
                ambientShadowColor = secondarySignalColor.copy(alpha = 0.30f * depthLevel.depthFactor)

                // 3. GraphicsLayer Transparency & Outline Clipping
                alpha = 0.96f
                shape = cardShape
                clip = false
            }
    ) {
        // Background Canvas: Layered Frosted Glass Substrate & Internal Parallax Scatter
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .clip(cardShape)
        ) {
            containerSize = size
            val width = size.width
            val height = size.height
            val cornerRadiusPx = shapeRadius.toPx()

            val roundRect = RoundRect(
                left = 0f,
                top = 0f,
                right = width,
                bottom = height,
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
            val clipPath = Path().apply { addRoundRect(roundRect) }

            clipPath(clipPath) {
                // Layer A: Semi-Transparent Frosted Base with Angle-Dependent Light Center
                val lightCenterX = width * (0.5f + (animatedTiltY / maxTiltDegrees.coerceAtLeast(1f)) * 0.35f)
                val lightCenterY = height * (0.5f - (animatedTiltX / maxTiltDegrees.coerceAtLeast(1f)) * 0.35f)
                val lightCenter = Offset(lightCenterX, lightCenterY)

                // Frosted base gradient
                drawRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            primarySignalColor.copy(alpha = 0.18f * depthLevel.frostedIntensity),
                            backgroundColor.copy(alpha = 0.88f),
                            SpaceCobaltSurface.copy(alpha = 0.94f)
                        ),
                        center = lightCenter,
                        radius = (size.maxDimension * (0.7f + depthLevel.depthFactor * 0.3f)).coerceAtLeast(10f)
                    )
                )

                // Layer B: Frosted Micro-Crystalline Scatter Matrix (Depth Parallax Effect)
                // Micro-crystals shift proportionally to the tilt angle, creating true volumetric depth inside the glass!
                val parallaxShiftX = (animatedTiltY / maxTiltDegrees.coerceAtLeast(1f)) * (16f * depthLevel.depthFactor)
                val parallaxShiftY = -(animatedTiltX / maxTiltDegrees.coerceAtLeast(1f)) * (16f * depthLevel.depthFactor)

                val scatterDensity = (frostedRoughness * depthLevel.frostedIntensity * 36).toInt().coerceIn(12, 48)
                for (i in 0 until scatterDensity) {
                    // Deterministic pseudo-random distribution across grid
                    val seedX = ((i * 37 + 19) % 100) / 100f
                    val seedY = ((i * 73 + 47) % 100) / 100f
                    val basePosX = seedX * width + parallaxShiftX
                    val basePosY = seedY * height + parallaxShiftY

                    val particleAlpha = (((i * 17) % 20) / 100f + 0.04f) * depthLevel.frostedIntensity
                    val particleSize = if (i % 4 == 0) 2.2.dp.toPx() else 1.2.dp.toPx()

                    val particleColor = when (i % 3) {
                        0 -> primarySignalColor.copy(alpha = particleAlpha.coerceIn(0.04f, 0.28f))
                        1 -> AmbientWhite.copy(alpha = (particleAlpha * 1.2f).coerceIn(0.05f, 0.30f))
                        else -> secondarySignalColor.copy(alpha = (particleAlpha * 0.8f).coerceIn(0.03f, 0.22f))
                    }

                    drawCircle(
                        color = particleColor,
                        radius = particleSize,
                        center = Offset(basePosX, basePosY)
                    )

                    // Occasional micro-facet diamond glint
                    if (i % 7 == 0 && depthLevel.depthFactor > 0.5f) {
                        val glintLen = 4.dp.toPx()
                        drawLine(
                            color = AmbientWhite.copy(alpha = particleAlpha * 1.5f),
                            start = Offset(basePosX - glintLen, basePosY),
                            end = Offset(basePosX + glintLen, basePosY),
                            strokeWidth = 1f
                        )
                        drawLine(
                            color = AmbientWhite.copy(alpha = particleAlpha * 1.5f),
                            start = Offset(basePosX, basePosY - glintLen),
                            end = Offset(basePosX, basePosY + glintLen),
                            strokeWidth = 1f
                        )
                    }
                }

                // Layer C: Sub-Surface Quantum Depth Lattice Lines
                if (depthLevel.depthFactor > 0.3f) {
                    val gridSpacing = 28.dp.toPx()
                    val gridCols = (width / gridSpacing).toInt()
                    val gridRows = (height / gridSpacing).toInt()

                    for (c in 1..gridCols) {
                        val gx = c * gridSpacing + parallaxShiftX * 0.5f
                        drawLine(
                            color = primarySignalColor.copy(alpha = 0.04f * depthLevel.depthFactor),
                            start = Offset(gx, 0f),
                            end = Offset(gx, height),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                    for (r in 1..gridRows) {
                        val gy = r * gridSpacing + parallaxShiftY * 0.5f
                        drawLine(
                            color = primarySignalColor.copy(alpha = 0.04f * depthLevel.depthFactor),
                            start = Offset(0f, gy),
                            end = Offset(width, gy),
                            strokeWidth = 1.dp.toPx()
                        )
                    }
                }
            }
        }

        // Inner User-Provided Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
        ) {
            content()
        }

        // Foreground Canvas: Specular Sheen, Prismatic Dispersion & Beveled Glass Rim
        Canvas(
            modifier = Modifier
                .matchParentSize()
                .clip(cardShape)
        ) {
            val width = size.width
            val height = size.height
            val cornerRadiusPx = shapeRadius.toPx()

            val roundRect = RoundRect(
                left = 0f,
                top = 0f,
                right = width,
                bottom = height,
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
            val clipPath = Path().apply { addRoundRect(roundRect) }

            clipPath(clipPath) {
                // 1. Dynamic Specular Caustic Sweep across Frosted Surface
                if (specularIntensity > 0.1f) {
                    val sweepProgress = causticSweepPhase
                    val sweepStartX = sweepProgress * (width + height * 0.6f) - height * 0.3f
                    val sweepStartY = 0f
                    val sweepEndX = sweepStartX - height * 0.6f
                    val sweepEndY = height

                    val sheenAlpha = (0.28f * specularIntensity * depthLevel.frostedIntensity)
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color.Transparent,
                                primarySignalColor.copy(alpha = sheenAlpha * 0.4f),
                                AmbientWhite.copy(alpha = sheenAlpha),
                                primarySignalColor.copy(alpha = sheenAlpha * 0.4f),
                                Color.Transparent
                            ),
                            start = Offset(sweepStartX, sweepStartY),
                            end = Offset(sweepEndX, sweepEndY)
                        ),
                        blendMode = BlendMode.Plus
                    )
                }

                // 2. Optical Chromatic Aberration Rim (Spectral Separation)
                if (depthLevel.depthFactor > 0.4f) {
                    val dispersionOffset = 1.6.dp.toPx() * depthLevel.depthFactor

                    // Blue/Cyan Refracted High-Index Component
                    drawRoundRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primarySignalColor.copy(alpha = 0.45f * depthLevel.depthFactor),
                                Color.Transparent
                            )
                        ),
                        size = Size(width, height),
                        cornerRadius = CornerRadius(cornerRadiusPx),
                        style = Stroke(width = 1.2.dp.toPx())
                    )

                    // Red/Crimson Refracted Low-Index Component (Offset by Dispersion)
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color.Transparent,
                                ContainmentCrimson.copy(alpha = 0.22f * depthLevel.depthFactor)
                            )
                        ),
                        topLeft = Offset(dispersionOffset, dispersionOffset),
                        size = Size(width - dispersionOffset * 2, height - dispersionOffset * 2),
                        cornerRadius = CornerRadius((cornerRadiusPx - dispersionOffset).coerceAtLeast(0f)),
                        style = Stroke(width = 1.0.dp.toPx())
                    )
                }

                // 3. Beveled Physical Frosted Rim (Highlight Catch at top-left, shadow at bottom-right)
                val rimHighlightAlpha = (0.75f * depthLevel.depthFactor).coerceIn(0.2f, 0.85f)
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AmbientWhite.copy(alpha = rimHighlightAlpha),
                            primarySignalColor.copy(alpha = rimHighlightAlpha * 0.7f),
                            secondarySignalColor.copy(alpha = 0.25f),
                            SpaceCobaltDark.copy(alpha = 0.4f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(width, height)
                    ),
                    size = Size(width, height),
                    cornerRadius = CornerRadius(cornerRadiusPx),
                    style = Stroke(width = 1.5.dp.toPx())
                )

                // 4. Inset Frosted Micro-Bevel (1dp inset light rim)
                val insetPx = 1.5.dp.toPx()
                drawRoundRect(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AmbientWhite.copy(alpha = 0.35f * specularIntensity),
                            Color.Transparent,
                            SpaceCobaltSurface.copy(alpha = 0.5f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(width, height)
                    ),
                    topLeft = Offset(insetPx, insetPx),
                    size = Size(width - insetPx * 2, height - insetPx * 2),
                    cornerRadius = CornerRadius((cornerRadiusPx - insetPx).coerceAtLeast(0f)),
                    style = Stroke(width = 1.0.dp.toPx())
                )
            }
        }
    }
}

/**
 * Volumetric Frosted Glass Dashboard Showcase Component
 *
 * An interactive, live calibration panel for the dashboard allowing operators to:
 * - Switch between depth levels (2D Clean, 2.5D Volumetric, 3D Quantum, Hyper-Enclave)
 * - Adjust frosted roughness / micro-crystalline dispersion
 * - Select quantum signal accents (Cyan, Emerald, Violet, Amber, Crimson)
 * - Experience real-time touch parallax with a floating cryptographic core visible
 *   underneath the semi-transparent frosted glass plane.
 */
@Composable
fun VolumetricFrostedGlassShowcase(
    currentDepth: String,
    onDepthChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDepthLevel by remember(currentDepth) {
        mutableStateOf(QuantumGlassDepthLevel.fromString(currentDepth))
    }
    var frostedRoughness by remember { mutableFloatStateOf(0.70f) }
    var selectedAccent by remember { mutableStateOf(PhotonicCyan) }

    // Floating internal core animation underneath the frosted glass
    val infiniteTransition = rememberInfiniteTransition(label = "FloatingCoreAnimation")
    val corePulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "CorePulse"
    )

    VolumetricQuantumGlassSurface(
        depthLevel = selectedDepthLevel,
        primarySignalColor = selectedAccent,
        secondarySignalColor = OperationalEmerald,
        frostedRoughness = frostedRoughness,
        modifier = modifier
            .fillMaxWidth()
            .testTag("volumetric_frosted_glass_showcase")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header: Title, Chip and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(selectedAccent)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "VOLUMETRIC QUANTUM GLASS ENGINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = selectedAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Text(
                            text = "Depth-Aware Frosted Optics & Parallax",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AmbientWhite
                        )
                    }
                }

                // Depth Level Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(selectedAccent.copy(alpha = 0.2f))
                        .border(1.dp, selectedAccent.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = selectedDepthLevel.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = selectedAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Sub-Surface Floating Core Demonstration Area
            // Shows an object "behind" the frosted glass to reveal true semi-transparent depth
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SpaceCobaltDark.copy(alpha = 0.75f))
                    .border(1.dp, SpaceCobaltGlassBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background visual lattice
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cols = 8
                    val rows = 4
                    val stepX = size.width / cols
                    val stepY = size.height / rows
                    for (c in 0..cols) {
                        drawLine(
                            color = SpaceCobaltGlassBorder.copy(alpha = 0.2f),
                            start = Offset(c * stepX, 0f),
                            end = Offset(c * stepX, size.height),
                            strokeWidth = 1f
                        )
                    }
                    for (r in 0..rows) {
                        drawLine(
                            color = SpaceCobaltGlassBorder.copy(alpha = 0.2f),
                            start = Offset(0f, r * stepY),
                            end = Offset(size.width, r * stepY),
                            strokeWidth = 1f
                        )
                    }
                }

                // Floating Holographic Node Core behind the frosted medium
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .graphicsLayer {
                                scaleX = corePulse
                                scaleY = corePulse
                            }
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        selectedAccent.copy(alpha = 0.75f),
                                        selectedAccent.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(1.5.dp, selectedAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Enclave Security Core",
                            tint = AmbientWhite,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "TOUCH OR DRAG TO TEST VOLUMETRIC PARALLAX TILT",
                        style = MaterialTheme.typography.labelSmall,
                        color = selectedAccent.copy(alpha = 0.9f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Depth Level Selector Buttons
            Text(
                text = "OPTICAL DEPTH PROFILE:",
                style = MaterialTheme.typography.labelSmall,
                color = AmbientWhiteSubtle,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                QuantumGlassDepthLevel.values().forEach { depth ->
                    val isSelected = selectedDepthLevel == depth
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) selectedAccent.copy(alpha = 0.25f) else SpaceCobaltSurface)
                            .border(
                                1.dp,
                                if (isSelected) selectedAccent else SpaceCobaltGlassBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedDepthLevel = depth
                                onDepthChanged("${depth.shortName} Volumetric")
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = depth.shortName,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) selectedAccent else AmbientWhiteMuted,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "${depth.virtualThicknessMm}mm",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) AmbientWhite else TextDimmed,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Frosted Scatter Roughness Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FROSTED SCATTER DISPERSION:",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteSubtle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(frostedRoughness * 100).toInt()}% Roughness",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = selectedAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = frostedRoughness,
                onValueChange = { frostedRoughness = it },
                valueRange = 0.1f..1.0f,
                colors = SliderDefaults.colors(
                    thumbColor = selectedAccent,
                    activeTrackColor = selectedAccent,
                    inactiveTrackColor = SpaceCobaltSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("slider_frosted_roughness")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Real-Time Optical Telemetry Metrics Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltDark)
                    .border(1.dp, SpaceCobaltGlassBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("REFRACTIVE INDEX", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                    Text(
                        text = "n = ${selectedDepthLevel.refractiveIndex}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmbientWhite
                    )
                }
                Column {
                    Text("VIRTUAL THICKNESS", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                    Text(
                        text = "${selectedDepthLevel.virtualThicknessMm} mm",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = selectedAccent
                    )
                }
                Column {
                    Text("CAMERA DEPTH", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                    Text(
                        text = "${(selectedDepthLevel.cameraDistanceMultiplier * 12).toInt()} px",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OperationalEmeraldLight
                    )
                }
                Column {
                    Text("CHROMATIC SPLIT", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                    Text(
                        text = if (selectedDepthLevel.depthFactor > 0.4f) "Dual-Prism" else "Monochrome",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = QuantumVioletLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Signal Accent Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PHOTONIC COLOR ACCENT:",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteSubtle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val accents = listOf(
                        PhotonicCyan to "Cyan",
                        OperationalEmerald to "Emerald",
                        QuantumViolet to "Violet",
                        SolarAmber to "Amber",
                        ContainmentCrimson to "Crimson"
                    )

                    accents.forEach { (color, name) ->
                        val isColorSelected = selectedAccent == color
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isColorSelected) 2.5.dp else 1.dp,
                                    color = if (isColorSelected) AmbientWhite else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedAccent = color }
                                .testTag("btn_accent_$name")
                        )
                    }
                }
            }
        }
    }
}
