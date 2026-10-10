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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.modifiers.VolumetricGlassConfig
import com.example.ui.modifiers.VolumetricGlassState
import com.example.ui.modifiers.rememberVolumetricGlassState
import com.example.ui.modifiers.volumetricGlass
import com.example.ui.theme.*

/**
 * High-Level Reusable Volumetric Glass Card UI Component.
 *
 * Implements the AGIS 2045 aesthetic with semi-transparent frosted optics,
 * 3D parallax tilt, Snell's Law refraction ray displacement, and chromatic dispersion.
 */
@Composable
fun AgisVolumetricGlassCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    badgeText: String? = null,
    badgeColor: Color = PhotonicCyan,
    icon: ImageVector? = null,
    config: VolumetricGlassConfig = VolumetricGlassConfig(),
    state: VolumetricGlassState = rememberVolumetricGlassState(),
    testTag: String = "agis_volumetric_glass_card",
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .volumetricGlass(config = config, state = state)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (title != null || icon != null || badgeText != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f, fill = false)
                    ) {
                        if (icon != null) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(config.primarySignalColor.copy(alpha = 0.15f))
                                    .border(1.dp, config.primarySignalColor.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = title ?: "Volumetric Glass Icon",
                                    tint = config.primarySignalColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                        }

                        Column {
                            if (subtitle != null) {
                                Text(
                                    text = subtitle.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = config.primarySignalColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.1.sp
                                )
                            }
                            if (title != null) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = AmbientWhite,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (badgeText != null) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(badgeColor.copy(alpha = 0.2f))
                                .border(1.dp, badgeColor.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = badgeText,
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

/**
 * Optical Medium Profiles for the Volumetric Glass Diagnostic UI Component.
 */
enum class OpticalGlassMaterial(
    val title: String,
    val refractiveIndex: Float,
    val virtualThicknessMm: Float,
    val abbeNumber: Float,
    val description: String
) {
    CRYO_FLUID("Cryo-Aqueous", 1.33f, 4.5f, 55.8f, "Low-index transparent substrate"),
    CROWN_GLASS("Boro-Crown Glass", 1.52f, 8.0f, 64.2f, "Balanced optical dispersion"),
    SAPPHIRE_CRYSTAL("Sapphire Crystal", 1.76f, 14.0f, 72.2f, "High-durability quantum prism"),
    DIAMOND_ENCLAVE("Hyper-Diamond", 2.42f, 20.0f, 86.4f, "Maximum light trapping & refraction");

    companion object {
        fun fromIndex(index: Int): OpticalGlassMaterial {
            return values().getOrElse(index) { SAPPHIRE_CRYSTAL }
        }
    }
}

/**
 * Interactive Diagnostic Volumetric Glass UI Component.
 *
 * Demonstrates real-time 3D parallax tilt, Snell's law refraction displacement,
 * chromatic aberration, caustic sweeps, and multi-plane depth stratification.
 */
@Composable
fun AgisVolumetricGlassDiagnosticPanel(
    modifier: Modifier = Modifier,
    initialMaterial: OpticalGlassMaterial = OpticalGlassMaterial.SAPPHIRE_CRYSTAL
) {
    var selectedMaterial by remember { mutableStateOf(initialMaterial) }
    var userThicknessMm by remember { mutableFloatStateOf(initialMaterial.virtualThicknessMm) }
    var selectedColor by remember { mutableStateOf(PhotonicCyan) }
    var enableCausticSheen by remember { mutableStateOf(true) }
    var enableDispersion by remember { mutableStateOf(true) }

    val glassState = rememberVolumetricGlassState()

    val currentConfig = remember(selectedMaterial, userThicknessMm, selectedColor, enableCausticSheen, enableDispersion) {
        VolumetricGlassConfig(
            thicknessMm = userThicknessMm,
            refractiveIndex = selectedMaterial.refractiveIndex,
            frostedRoughness = 0.72f,
            specularIntensity = if (enableCausticSheen) 0.90f else 0.20f,
            chromaticDispersion = if (enableDispersion) 0.75f else 0.0f,
            primarySignalColor = selectedColor,
            secondarySignalColor = OperationalEmerald,
            glassBackgroundColor = SpaceCobaltGlass,
            elevation = 10.dp,
            enablePointerTilt = true,
            enableAmbientOscillation = true,
            enableCausticSweep = enableCausticSheen,
            showHudEtchings = true
        )
    }

    // Holographic Core breathing animation for internal suspended object
    val infiniteTransition = rememberInfiniteTransition(label = "InternalHoloCoreTransition")
    val holoCorePulse by infiniteTransition.animateFloat(
        initialValue = 0.90f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "HoloCorePulse"
    )

    val holoCoreRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HoloCoreRotation"
    )

    AgisVolumetricGlassCard(
        title = "Volumetric Glass UI Engine",
        subtitle = "AGIS 2045 Optics // Compose Modifiers",
        badgeText = "${selectedMaterial.title.split(" ").first()} (${selectedMaterial.refractiveIndex}n)",
        badgeColor = selectedColor,
        icon = Icons.Default.ViewInAr,
        config = currentConfig,
        state = glassState,
        testTag = "agis_volumetric_glass_diagnostic_panel",
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {

            // Interactive 3D Holographic Touch Slab
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SpaceCobaltDark.copy(alpha = 0.85f))
                    .border(1.dp, SpaceCobaltGlassBorder.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Quantum Lattice that shifts with depth parallax
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val pX = glassState.snellDisplacementX * 3.5f
                    val pY = glassState.snellDisplacementY * 3.5f

                    val cols = 8
                    val rows = 4
                    val stepW = size.width / cols
                    val stepH = size.height / rows

                    for (c in 0..cols) {
                        val gx = c * stepW + pX * 0.6f
                        drawLine(
                            color = selectedColor.copy(alpha = 0.15f),
                            start = Offset(gx, 0f),
                            end = Offset(gx, size.height),
                            strokeWidth = 1f
                        )
                    }
                    for (r in 0..rows) {
                        val gy = r * stepH + pY * 0.6f
                        drawLine(
                            color = selectedColor.copy(alpha = 0.15f),
                            start = Offset(0f, gy),
                            end = Offset(size.width, gy),
                            strokeWidth = 1f
                        )
                    }
                }

                // Mid-Volume Holographic Suspended Core (Floats between front and back surfaces)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.graphicsLayer {
                        translationX = glassState.snellDisplacementX * 1.8f
                        translationY = glassState.snellDisplacementY * 1.8f
                    }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .graphicsLayer {
                                scaleX = holoCorePulse
                                scaleY = holoCorePulse
                                rotationZ = holoCoreRotation
                            }
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        selectedColor.copy(alpha = 0.80f),
                                        selectedColor.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                            .border(1.5.dp, selectedColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AllInclusive,
                            contentDescription = "Volumetric Quantum Core",
                            tint = AmbientWhite,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (glassState.isInteracting) "TACTILE PARALLAX TRACKING ENGAGED" else "DRAG / TOUCH GLASS TO SIMULATE 3D DEPTH",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (glassState.isInteracting) selectedColor else AmbientWhiteMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                }

                // Live 3D Tilt Telemetry HUD overlay in top-right
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SpaceCobaltSurface.copy(alpha = 0.85f))
                        .border(0.8.dp, selectedColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "θx: ${String.format("%.1f", glassState.tiltX)}°  θy: ${String.format("%.1f", glassState.tiltY)}°",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        color = selectedColor
                    )
                }

                // Snell's Law Displacement HUD overlay in bottom-left
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .clip(RoundedCornerShape(4.dp))
                        .background(SpaceCobaltSurface.copy(alpha = 0.85f))
                        .border(0.8.dp, OperationalEmerald.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "SNELL DISP: Δx ${String.format("%.2f", glassState.snellDisplacementX)}mm",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 8.sp,
                        color = OperationalEmeraldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Optical Material Selector Chips
            Text(
                text = "REFRACTIVE MEDIUM / SUBSTRATE:",
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
                OpticalGlassMaterial.values().forEach { material ->
                    val isSelected = selectedMaterial == material
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) selectedColor.copy(alpha = 0.25f) else SpaceCobaltSurface)
                            .border(
                                1.dp,
                                if (isSelected) selectedColor else SpaceCobaltGlassBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                selectedMaterial = material
                                userThicknessMm = material.virtualThicknessMm
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = material.title.split(" ").first(),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) selectedColor else AmbientWhiteMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "n = ${material.refractiveIndex}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) AmbientWhite else TextDimmed,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Virtual Thickness Calibrator Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "VIRTUAL GLASS THICKNESS:",
                    style = MaterialTheme.typography.labelSmall,
                    color = AmbientWhiteSubtle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${String.format("%.1f", userThicknessMm)} mm Depth",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = selectedColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Slider(
                value = userThicknessMm,
                onValueChange = { userThicknessMm = it },
                valueRange = 2.0f..22.0f,
                colors = SliderDefaults.colors(
                    thumbColor = selectedColor,
                    activeTrackColor = selectedColor,
                    inactiveTrackColor = SpaceCobaltSurface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("slider_glass_thickness")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Real-Time Optical Telemetry Matrix (4 Columns)
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
                        text = "n = ${selectedMaterial.refractiveIndex}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmbientWhite,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Column {
                    Text("ABBE DISPERSION", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                    Text(
                        text = "ν = ${selectedMaterial.abbeNumber}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = QuantumVioletLight,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Column {
                    Text("FRESNEL COEFF", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                    val fresnelR0 = ((selectedMaterial.refractiveIndex - 1f) / (selectedMaterial.refractiveIndex + 1f)).let { it * it }
                    Text(
                        text = "R₀ = ${String.format("%.1f", fresnelR0 * 100)}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OperationalEmeraldLight,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Column {
                    Text("OPTICAL MATRIX", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                    Text(
                        text = "AGIS-2045",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = selectedColor,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Photonic Signal Color Accent Switcher & Effect Toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Color Palette
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val accents = listOf(
                        PhotonicCyan to "Cyan",
                        OperationalEmerald to "Emerald",
                        QuantumViolet to "Violet",
                        SolarAmber to "Amber",
                        ContainmentCrimson to "Crimson"
                    )

                    accents.forEach { (color, name) ->
                        val isColorSelected = selectedColor == color
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isColorSelected) 2.5.dp else 1.dp,
                                    color = if (isColorSelected) AmbientWhite else Color.Transparent,
                                    shape = CircleShape
                                )
                                .clickable { selectedColor = color }
                                .testTag("btn_glass_color_$name")
                        )
                    }
                }

                // Toggles for Specular Sweep & Prismatic Dispersion
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = enableCausticSheen,
                        onClick = { enableCausticSheen = !enableCausticSheen },
                        label = { Text("Caustics", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = selectedColor.copy(alpha = 0.25f),
                            selectedLabelColor = selectedColor
                        ),
                        modifier = Modifier.testTag("chip_toggle_caustics")
                    )

                    FilterChip(
                        selected = enableDispersion,
                        onClick = { enableDispersion = !enableDispersion },
                        label = { Text("Prism", fontSize = 10.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = selectedColor.copy(alpha = 0.25f),
                            selectedLabelColor = selectedColor
                        ),
                        modifier = Modifier.testTag("chip_toggle_dispersion")
                    )
                }
            }
        }
    }
}
