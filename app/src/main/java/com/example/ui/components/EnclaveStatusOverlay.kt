package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BiometricAttestationDetails
import com.example.model.EnclaveKeyInfo
import com.example.model.EnclaveLockState
import com.example.ui.animation.PhotonicSignalPulseIndicator
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

/**
 * 512-bit Post-Quantum Enclave Status Dashboard Overlay
 * Immersive Emerald & Cobalt HUD visualizing real-time Kyber-1024 / Dilithium-5
 * lattice matrix, cryptographic entropy pool, and hardware security state.
 */
@Composable
fun EnclaveStatusOverlay(
    isVisible: Boolean,
    enclaveKey: EnclaveKeyInfo,
    isLatticeVerifying: Boolean,
    onDismiss: () -> Unit,
    onRotateKey: () -> Unit,
    onVerifyLattice: () -> Unit,
    onBiometricAuth: () -> Unit,
    onLockEnclave: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(250)) + scaleIn(initialScale = 0.92f, animationSpec = tween(300, easing = FastOutSlowInEasing)),
        exit = fadeOut(tween(200)) + scaleOut(targetScale = 0.94f, animationSpec = tween(200)),
        modifier = modifier.fillMaxSize()
    ) {
        // Scrim backdrop
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceCobaltDark.copy(alpha = 0.88f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDismiss
                ),
            contentAlignment = Alignment.Center
        ) {
            // Main HUD Modal Card
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .fillMaxHeight(0.88f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                SpaceCobaltGlassElevated,
                                SpaceCobaltSurface,
                                SpaceCobaltDark
                            )
                        )
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                OperationalEmerald,
                                SpaceCobaltGlassBorder,
                                OperationalEmeraldDark.copy(alpha = 0.5f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(20.dp), spotColor = OperationalEmerald)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { /* Consume clicks inside modal */ }
                    )
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Top Bar & Dismiss Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PhotonicSignalPulseIndicator(
                                signalColor = if (enclaveKey.lockState == EnclaveLockState.UNLOCKED) OperationalEmerald else OperationalEmeraldLight,
                                size = 12.dp,
                                pulseSpeedMs = 1200
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "512-BIT POST-QUANTUM ENCLAVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OperationalEmeraldLight,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp
                                )
                                Text(
                                    text = "Encryption Status HUD",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AmbientWhite
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(SpaceCobaltSurface)
                                .border(1.dp, SpaceCobaltGlassBorder, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close HUD",
                                tint = AmbientWhite,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    // Status Pill Banner
                    val isUnlocked = enclaveKey.lockState == EnclaveLockState.UNLOCKED
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isUnlocked) OperationalEmeraldDark.copy(alpha = 0.35f) else SpaceCobaltCard)
                            .border(
                                1.dp,
                                if (isUnlocked) OperationalEmerald.copy(alpha = 0.6f) else SpaceCobaltGlassBorder,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isUnlocked) Icons.Default.VerifiedUser else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (isUnlocked) OperationalEmerald else OperationalEmeraldLight,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUnlocked) "HARDWARE UNLOCKED & ATTESTED" else "SEALED HARDWARE STORAGE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isUnlocked) OperationalEmeraldLight else AmbientWhite
                            )
                        }

                        Text(
                            text = "NIST FIPS-203",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            color = PhotonicCyan,
                            fontSize = 11.sp
                        )
                    }

                    // 512-bit Post-Quantum Lattice Matrix Visualizer
                    EnclaveLatticeMatrixVisualizer(
                        keyId = enclaveKey.keyId,
                        isVerifying = isLatticeVerifying,
                        isUnlocked = isUnlocked,
                        rotationSec = enclaveKey.rotationRemainingSec,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                    )

                    // Ephemeral Key & Rotation Countdown
                    QuantumGlassCard(
                        borderColor = OperationalEmerald.copy(alpha = 0.4f),
                        backgroundColor = SpaceCobaltSurface
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "ACTIVE DYNAMIC LATTICE KEY",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OperationalEmeraldLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isUnlocked) enclaveKey.keyId else "PQK-512-••••-••••-••••",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = AmbientWhite
                                )
                            }

                            PhotonicBadge(
                                text = "${enclaveKey.rotationRemainingSec}s RE-KEY",
                                signalColor = if (enclaveKey.rotationRemainingSec < 15) SolarAmber else OperationalEmerald,
                                enablePulse = true
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Linear Rotation Progress Bar
                        LinearProgressIndicator(
                            progress = { (enclaveKey.rotationRemainingSec / 60f).coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = OperationalEmerald,
                            trackColor = SpaceCobaltDark
                        )
                    }

                    // Security Specifications & Lattice Telemetry
                    QuantumGlassCard(
                        borderColor = SpaceCobaltGlassBorder,
                        backgroundColor = SpaceCobaltGlass
                    ) {
                        Text(
                            text = "ENCLAVE TELEMETRY & HARDWARE SPECS",
                            style = MaterialTheme.typography.labelSmall,
                            color = PhotonicCyan,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        val specs = listOf(
                            "ALGORITHM SUITE" to enclaveKey.algorithm,
                            "QUANTUM RESISTANCE" to "512-bit / Hard Lattice RLWE",
                            "SECURITY CLASS" to "NIST Category 5 (Beyond Shor & Grover)",
                            "HARDWARE ZONE" to enclaveKey.hardwareSlot,
                            "ISOLATED MEMORY" to if (isUnlocked) enclaveKey.memoryAddress else "0x7FFF_XXXX_XXXX_SEALED",
                            "ENTROPY SOURCE" to "Hardware Sensor CSPRNG (NIST SP 800-90A)",
                            "THERMAL STABILITY" to "99.98% Coherent (0 Bit Drift)"
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            specs.forEach { (label, value) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontSize = 11.sp,
                                        color = TextDimmed
                                    )
                                    Text(
                                        text = value,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = if (value.startsWith("0x")) FontFamily.Monospace else FontFamily.Default,
                                        color = if (label.contains("QUANTUM") || label.contains("ALGORITHM")) OperationalEmeraldLight else AmbientWhite
                                    )
                                }
                            }
                        }
                    }

                    // Hardware Attestation Certificate (If Unlocked)
                    if (isUnlocked && enclaveKey.attestationDetails != null) {
                        val att = enclaveKey.attestationDetails
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(OperationalEmeraldDark.copy(alpha = 0.25f))
                                .border(1.dp, OperationalEmerald.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = OperationalEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "HARDWARE ATTESTATION ACTIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = OperationalEmeraldLight
                                )
                            }
                            Text(
                                text = "Provider: ${att.credentialType} (${att.biometricStrength})",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmbientWhite,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Token: ${att.attestationToken}",
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = FontFamily.Monospace,
                                color = PhotonicCyan,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Interactive Action Buttons Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Lattice Integrity Scan Button
                        QuantumVolumetricButton(
                            text = if (isLatticeVerifying) "VERIFYING..." else "SCAN LATTICE",
                            icon = Icons.Default.CheckCircle,
                            primaryColor = OperationalEmerald,
                            secondaryColor = PhotonicCyan,
                            containerColor = SpaceCobaltSurface,
                            modifier = Modifier.weight(1f),
                            onClick = onVerifyLattice
                        )

                        // Rotate Key Button
                        QuantumVolumetricButton(
                            text = "ROTATE KEY",
                            icon = Icons.Default.Refresh,
                            primaryColor = PhotonicCyanLight,
                            secondaryColor = OperationalEmerald,
                            containerColor = SpaceCobaltSurface,
                            modifier = Modifier.weight(1f),
                            onClick = onRotateKey
                        )
                    }

                    // Biometric Authentication / Lock Action
                    if (isUnlocked) {
                        QuantumVolumetricButton(
                            text = "SEAL ENCLAVE STORAGE",
                            icon = Icons.Default.Lock,
                            primaryColor = ContainmentCrimson,
                            secondaryColor = OperationalEmerald,
                            containerColor = SpaceCobaltSurface,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onLockEnclave
                        )
                    } else {
                        QuantumVolumetricButton(
                            text = if (enclaveKey.lockState == EnclaveLockState.AUTHENTICATING) "AUTHENTICATING..." else "BIOMETRIC ATTESTATION CHALLENGE",
                            icon = Icons.Default.Fingerprint,
                            primaryColor = OperationalEmerald,
                            secondaryColor = PhotonicCyan,
                            containerColor = SpaceCobaltSurface,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onBiometricAuth
                        )
                    }
                }
            }
        }
    }
}

/**
 * Custom Canvas Visualizer for the 512-bit Post-Quantum Lattice Matrix
 * Uses emerald nodes, undulating polynomial vector lines, and deep cobalt cell shading.
 */
@Composable
fun EnclaveLatticeMatrixVisualizer(
    keyId: String,
    isVerifying: Boolean,
    isUnlocked: Boolean,
    rotationSec: Int,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "LatticeMatrixTransition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(if (isVerifying) 1000 else 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "LatticePhase"
    )

    val wavePulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "WavePulse"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(SpaceCobaltDark.copy(alpha = 0.95f))
            .border(1.dp, OperationalEmerald.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val cols = 16
            val rows = 8
            val cellWidth = width / cols
            val cellHeight = height / rows

            // Draw 512-bit cell grid (16 cols x 8 rows x 4 bytes = 512 bits)
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val cellIndex = r * cols + c
                    val seed = (keyId.hashCode() + cellIndex * 31) % 100
                    val isBitActive = seed > 45

                    val cx = c * cellWidth + cellWidth / 2f
                    val cy = r * cellHeight + cellHeight / 2f

                    // Color selection based on lattice state and emerald/cobalt theme
                    val cellColor = if (isUnlocked) {
                        if (isBitActive) OperationalEmerald.copy(alpha = 0.65f + 0.25f * wavePulse)
                        else SpaceCobaltGlassElevated.copy(alpha = 0.5f)
                    } else {
                        if (isBitActive) OperationalEmeraldDark.copy(alpha = 0.35f)
                        else SpaceCobaltSurface.copy(alpha = 0.4f)
                    }

                    drawRect(
                        color = cellColor,
                        topLeft = Offset(c * cellWidth + 2f, r * cellHeight + 2f),
                        size = Size(cellWidth - 4f, cellHeight - 4f)
                    )

                    // Draw center emerald dot on active bits
                    if (isBitActive) {
                        drawCircle(
                            color = if (isUnlocked) OperationalEmeraldLight else OperationalEmeraldDark,
                            radius = (1.5.dp.toPx() * (if (isUnlocked) 1f + 0.3f * wavePulse else 1f)),
                            center = Offset(cx, cy)
                        )
                    }
                }
            }

            // Draw Undulating Lattice Polynomial Vectors across the matrix
            val path = Path()
            val numPoints = 40
            for (i in 0..numPoints) {
                val x = (i / numPoints.toFloat()) * width
                val angle = (phase + i * 18).toDouble() * Math.PI / 180.0
                val y = (height / 2f) + (sin(angle) * (height * 0.28f) * (0.8f + 0.2f * wavePulse)).toFloat()

                if (i == 0) path.moveTo(x, y)
                else path.lineTo(x, y)
            }

            // Glow vector path
            drawPath(
                path = path,
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        OperationalEmeraldDark.copy(alpha = 0.2f),
                        OperationalEmerald.copy(alpha = 0.85f),
                        PhotonicCyanLight.copy(alpha = 0.9f),
                        OperationalEmerald.copy(alpha = 0.85f),
                        OperationalEmeraldDark.copy(alpha = 0.2f)
                    )
                ),
                style = Stroke(width = if (isVerifying) 4.dp.toPx() else 2.2.dp.toPx(), cap = StrokeCap.Round)
            )

            // Dynamic scan line during verification
            if (isVerifying) {
                val scanX = (phase / 360f) * width
                drawLine(
                    color = PhotonicCyan,
                    start = Offset(scanX, 0f),
                    end = Offset(scanX, height),
                    strokeWidth = 2.5.dp.toPx()
                )
            }
        }

        // Overlay Matrix Caption
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(SpaceCobaltDark.copy(alpha = 0.75f))
                .padding(horizontal = 10.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "KYBER-1024 / 512-BIT VECTOR GRID",
                style = MaterialTheme.typography.labelSmall,
                color = OperationalEmeraldLight,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp
            )
            Text(
                text = if (isVerifying) "INTEGRITY SCANNING..." else "COHERENCE: 99.98%",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = FontFamily.Monospace,
                color = if (isVerifying) PhotonicCyan else OperationalEmerald,
                fontSize = 9.sp
            )
        }
    }
}
