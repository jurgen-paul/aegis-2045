package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuditLogEntity
import com.example.model.EnclaveLockState
import com.example.ui.animation.PhotonicSignalPulseIndicator
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.components.PhotonicBadge
import com.example.ui.components.QuantumGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.AgisViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EnclaveVaultScreen(
    viewModel: AgisViewModel
) {
    val context = LocalContext.current
    val enclaveKey by viewModel.enclaveKey.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    var selectedFilter by remember { mutableStateOf("ALL") }

    val isUnlocked = enclaveKey.lockState == EnclaveLockState.UNLOCKED

    val filteredLogs = remember(auditLogs, selectedFilter) {
        if (selectedFilter == "ALL") auditLogs
        else auditLogs.filter { it.securityTier.contains(selectedFilter, ignoreCase = true) || it.eventType.contains(selectedFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 80.dp)
    ) {
        // Section Header
        item {
            QuantumGlassCard(
                borderColor = if (isUnlocked) OperationalEmerald.copy(alpha = 0.5f) else SpaceCobaltGlassBorder,
                backgroundColor = SpaceCobaltCard
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LAYER 5 • POST-QUANTUM ENCLAVE",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (isUnlocked) OperationalEmeraldLight else PhotonicCyanLight
                        )
                        Text(
                            text = "512-bit PQ Storage Enclave",
                            style = MaterialTheme.typography.titleMedium,
                            color = AmbientWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    PhotonicBadge(
                        text = if (isUnlocked) "BIOMETRIC ATTESTED" else "BIOMETRICALLY LOCKED",
                        signalColor = if (isUnlocked) OperationalEmerald else SolarAmber,
                        icon = if (isUnlocked) Icons.Default.VerifiedUser else Icons.Default.Lock,
                        enablePulse = isUnlocked
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Launch Status HUD Overlay Trigger
                QuantumVolumetricButton(
                    text = "VISUALIZE 512-BIT ENCLAVE STATUS HUD",
                    icon = Icons.Default.DashboardCustomize,
                    primaryColor = OperationalEmerald,
                    secondaryColor = PhotonicCyan,
                    containerColor = SpaceCobaltDark,
                    modifier = Modifier.fillMaxWidth(),
                    shapeRadius = 10.dp,
                    onClick = { viewModel.setEnclaveOverlayVisible(true) }
                )
            }
        }

        // Biometric Authentication Gate via Android Credential Manager
        item {
            QuantumGlassCard(
                borderColor = when (enclaveKey.lockState) {
                    EnclaveLockState.UNLOCKED -> OperationalEmerald.copy(alpha = 0.6f)
                    EnclaveLockState.AUTHENTICATING -> PhotonicCyan.copy(alpha = 0.8f)
                    EnclaveLockState.DENIED -> ContainmentCrimson.copy(alpha = 0.7f)
                    EnclaveLockState.LOCKED -> QuantumViolet.copy(alpha = 0.5f)
                },
                backgroundColor = SpaceCobaltGlassElevated
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        PhotonicSignalPulseIndicator(
                            signalColor = when (enclaveKey.lockState) {
                                EnclaveLockState.UNLOCKED -> OperationalEmerald
                                EnclaveLockState.AUTHENTICATING -> PhotonicCyan
                                EnclaveLockState.DENIED -> ContainmentCrimson
                                EnclaveLockState.LOCKED -> QuantumViolet
                            },
                            size = 10.dp,
                            pulseSpeedMs = if (enclaveKey.lockState == EnclaveLockState.AUTHENTICATING) 600 else 1800
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ANDROID CREDENTIAL MANAGER GATE",
                                style = MaterialTheme.typography.labelSmall,
                                color = PhotonicCyanLight,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = enclaveKey.lockState.label,
                                style = MaterialTheme.typography.titleSmall,
                                color = when (enclaveKey.lockState) {
                                    EnclaveLockState.UNLOCKED -> OperationalEmeraldLight
                                    EnclaveLockState.AUTHENTICATING -> PhotonicCyanLight
                                    EnclaveLockState.DENIED -> ContainmentCrimson
                                    EnclaveLockState.LOCKED -> AmbientWhite
                                },
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (isUnlocked) {
                        QuantumVolumetricButton(
                            text = "SEAL VAULT",
                            icon = Icons.Default.Lock,
                            primaryColor = ContainmentCrimson,
                            secondaryColor = QuantumViolet,
                            containerColor = SpaceCobaltSurface,
                            shapeRadius = 8.dp,
                            onClick = { viewModel.lockEnclaveStorage() }
                        )
                    } else {
                        QuantumVolumetricButton(
                            text = if (enclaveKey.lockState == EnclaveLockState.AUTHENTICATING) "SCANNING..." else "BIOMETRIC AUTH",
                            icon = Icons.Default.Fingerprint,
                            primaryColor = if (enclaveKey.lockState == EnclaveLockState.DENIED) SolarAmber else PhotonicCyan,
                            secondaryColor = OperationalEmerald,
                            containerColor = SpaceCobaltSurface,
                            shapeRadius = 8.dp,
                            onClick = {
                                if (enclaveKey.lockState != EnclaveLockState.AUTHENTICATING) {
                                    viewModel.authenticateEnclaveWithCredentialManager(context)
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Hardware Attestation Specs
                if (isUnlocked && enclaveKey.attestationDetails != null) {
                    val att = enclaveKey.attestationDetails!!
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(OperationalEmeraldDark.copy(alpha = 0.35f))
                            .border(1.dp, OperationalEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = OperationalEmerald,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "HARDWARE ATTESTATION CERTIFICATE ACTIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = OperationalEmeraldLight
                            )
                        }
                        Text(
                            text = "Provider: ${att.credentialType} • Strength: ${att.biometricStrength}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmbientWhite
                        )
                        Text(
                            text = "Attestation Token: ${att.attestationToken}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = PhotonicCyan
                        )
                        Text(
                            text = "Security Module: ${att.hardwareSecurityModule}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmbientWhiteMuted,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Text(
                        text = "Access to decrypted 512-bit Kyber-1024 / Dilithium-5 storage sectors is gated by Android Credential Manager Passkey / Class-3 Biometric Challenge.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmbientWhiteMuted
                    )
                }
            }
        }

        // Hardware Sensor CSPRNG Background Service HUD
        item {
            val entropyState by viewModel.hardwareSensorEntropyState.collectAsState()
            com.example.ui.components.HardwareSensorEntropyCard(
                entropyState = entropyState,
                onForceReseed = { viewModel.forceHardwareSensorReseed() }
            )
        }

        // 512-bit Dynamic Key Card & Decrypted Storage Sectors
        item {
            QuantumGlassCard(
                borderColor = if (isUnlocked) QuantumViolet.copy(alpha = 0.6f) else SpaceCobaltGlassBorder,
                backgroundColor = SpaceCobaltGlassElevated
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VpnKey,
                            contentDescription = "Post Quantum Key",
                            tint = if (isUnlocked) QuantumVioletLight else AmbientWhiteMuted,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "POST-QUANTUM STORAGE KEY",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isUnlocked) QuantumVioletLight else AmbientWhiteMuted,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (isUnlocked) enclaveKey.keyId else "••••••••-••••-••••-••••",
                                style = MaterialTheme.typography.titleSmall,
                                color = AmbientWhite,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    if (isUnlocked) {
                        QuantumVolumetricButton(
                            text = "ROTATE",
                            icon = Icons.Default.Refresh,
                            primaryColor = QuantumVioletLight,
                            secondaryColor = OperationalEmerald,
                            containerColor = SpaceCobaltSurface,
                            shapeRadius = 8.dp,
                            onClick = { viewModel.rotateEnclaveKey() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Specifications Grid
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "LATTICE ALGORITHM" to enclaveKey.algorithm,
                        "HARDWARE SLOT" to enclaveKey.hardwareSlot,
                        "PHYSICAL MEMORY ADDR" to if (isUnlocked) enclaveKey.memoryAddress else "0x7FFF_XXXX_XXXX_SEALED",
                        "EPHEMERAL ROTATION" to "${enclaveKey.rotationRemainingSec}s remaining",
                        "ENCLAVE STATE" to enclaveKey.activeState
                    ).forEach { (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(SpaceCobaltDark)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                color = AmbientWhiteMuted
                            )
                            Text(
                                text = value,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (label.contains("ROTATION")) SolarAmber else if (isUnlocked) PhotonicCyanLight else AmbientWhiteMuted,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Post-Quantum Enclave Storage Sectors (Accessible when Unlocked)
        if (isUnlocked) {
            item {
                QuantumGlassCard(
                    borderColor = OperationalEmerald.copy(alpha = 0.5f),
                    backgroundColor = SpaceCobaltGlassElevated
                ) {
                    Text(
                        text = "DECRYPTED 512-BIT ENCLAVE SECTORS",
                        style = MaterialTheme.typography.labelSmall,
                        color = OperationalEmeraldLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            Triple("SECTOR-01", "Model Weight Matrix (Kyber-1024 Encrypted)", "Verified Integrity · 0x882B"),
                            Triple("SECTOR-02", "Hardware Sensor CSPRNG Master Seed Pool", "512-bit / NIST SP 800-90A"),
                            Triple("SECTOR-03", "Zero-Trust Agent Auth Ring Tokens", "3 Active Sub-Agent Signatures")
                        ).forEach { (sectorId, description, status) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SpaceCobaltSurface)
                                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(6.dp))
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = sectorId,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = PhotonicCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AmbientWhite
                                    )
                                }
                                Text(
                                    text = status,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OperationalEmeraldLight,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Dual Isolation Model Architecture Visualizer
        item {
            QuantumGlassCard(
                borderColor = PhotonicCyan.copy(alpha = 0.3f),
                backgroundColor = SpaceCobaltGlassElevated
            ) {
                Text(
                    text = "DUAL ISOLATION ENCLAVE ARCHITECTURE",
                    style = MaterialTheme.typography.labelSmall,
                    color = PhotonicCyanLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SpaceCobaltSurface)
                            .border(1.dp, OperationalEmerald.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = OperationalEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ISOLATION 1",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = OperationalEmerald,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Hardware Memory Encryption",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmbientWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Non-exportable secure enclave core registers.",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmbientWhiteMuted
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(SpaceCobaltSurface)
                            .border(1.dp, QuantumViolet.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Layers,
                                    contentDescription = null,
                                    tint = QuantumViolet,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "ISOLATION 2",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = QuantumVioletLight,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "OS Kernel Sandboxing",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmbientWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Zero-trust privilege gates preventing cross-app snooping.",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmbientWhiteMuted
                            )
                        }
                    }
                }
            }
        }

        // Room Database Immutable Audit Logs Explorer
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SEALED IMMUTABLE AUDIT LEDGER",
                    style = MaterialTheme.typography.labelLarge,
                    color = PhotonicCyan,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "CLEAR",
                    style = MaterialTheme.typography.labelSmall,
                    color = ContainmentCrimson,
                    modifier = Modifier.clickable { viewModel.clearAuditLedger() }
                )
            }
        }

        // Filter Pills
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ALL", "TIER-0", "TIER-1", "ENCLAVE").forEach { filter ->
                    val isSelected = selectedFilter == filter
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) PhotonicCyan.copy(alpha = 0.25f) else SpaceCobaltGlass)
                            .border(
                                1.dp,
                                if (isSelected) PhotonicCyan else SpaceCobaltGlassBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { selectedFilter = filter }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) PhotonicCyan else AmbientWhiteMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (filteredLogs.isEmpty()) {
            item {
                QuantumGlassCard(
                    borderColor = SpaceCobaltGlassBorder,
                    backgroundColor = SpaceCobaltGlass
                ) {
                    Text(
                        text = "No audit log entries for selected filter.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmbientWhiteMuted
                    )
                }
            }
        }

        items(filteredLogs, key = { it.id }) { log ->
            AuditLogItemCard(log)
        }
    }
}

@Composable
fun AuditLogItemCard(log: AuditLogEntity) {
    val dateStr = remember(log.timestamp) {
        SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(log.timestamp))
    }

    QuantumGlassCard(
        borderColor = if (log.eventType.contains("THREAT")) ContainmentCrimson.copy(alpha = 0.4f)
        else if (log.eventType.contains("ENCLAVE") || log.eventType.contains("BIOMETRIC")) QuantumViolet.copy(alpha = 0.4f)
        else PhotonicCyan.copy(alpha = 0.25f),
        backgroundColor = SpaceCobaltGlassElevated
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${log.eventType} • $dateStr",
                style = MaterialTheme.typography.labelSmall,
                color = PhotonicCyanLight,
                fontWeight = FontWeight.Bold
            )

            PhotonicBadge(
                text = log.subAgentId,
                signalColor = OperationalEmerald
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = log.summary,
            style = MaterialTheme.typography.bodySmall,
            color = AmbientWhite
        )

        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "TIER: ${log.securityTier}",
                style = MaterialTheme.typography.labelSmall,
                color = AmbientWhiteMuted
            )
            Text(
                text = "PROOF: ${log.cryptographicProof.take(20)}...",
                style = MaterialTheme.typography.labelSmall,
                color = SolarAmber
            )
        }
    }
}

