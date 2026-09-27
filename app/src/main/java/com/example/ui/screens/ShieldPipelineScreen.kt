package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ThreatIncidentEntity
import com.example.model.PolicyEnforcementLevel
import com.example.model.SecurityPolicyRule
import com.example.ui.animation.PhotonicSignalPulseIndicator
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.components.PhotonicBadge
import com.example.ui.components.QuantumGlassCard
import com.example.ui.components.SanitizationScanControlCard
import com.example.ui.theme.*
import com.example.viewmodel.AgisViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ShieldPipelineScreen(
    viewModel: AgisViewModel
) {
    val threatIncidents by viewModel.threatIncidents.collectAsState()
    val globalThreatLevel by viewModel.globalThreatLevel.collectAsState()
    val policyRules by viewModel.securityPolicyRules.collectAsState()
    val enforcementLevel by viewModel.policyEnforcementLevel.collectAsState()
    val isPolicyAuditRunning by viewModel.isPolicyAuditRunning.collectAsState()

    var activeSubTab by remember { mutableStateOf("POLICIES") } // "POLICIES" or "PIPELINE" or "THREAT_SIM"

    val pipelineSteps = listOf(
        Triple("1", "Continuous Authentication", "Biometric + Hardware Token (Real-time verification)"),
        Triple("2", "Threat Scanning", "Heuristic Anomaly Detection & Prompt Injection Defense"),
        Triple("3", "Data Sanitization", "Differential Privacy (ε=0.5) & Telemetry Leak Stripping"),
        Triple("4", "Provenance Tracing", "Immutable Cryptographic Chain-of-Custody Signatures"),
        Triple("5", "Audit Logging", "512-bit PQ Sealed Hardware Enclave Ledger")
    )

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
                borderColor = PhotonicCyan.copy(alpha = 0.3f),
                backgroundColor = SpaceCobaltCard
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "LAYER 4 • SHIELD PROTECTION PATH",
                            style = MaterialTheme.typography.labelMedium,
                            color = PhotonicCyanLight
                        )
                        Text(
                            text = "Security Policies & Defense Pipeline",
                            style = MaterialTheme.typography.titleMedium,
                            color = AmbientWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    PhotonicBadge(
                        text = enforcementLevel.name,
                        signalColor = when (enforcementLevel) {
                            PolicyEnforcementLevel.STRICT -> OperationalEmerald
                            PolicyEnforcementLevel.BALANCED -> PhotonicCyan
                            PolicyEnforcementLevel.DEVELOPMENT -> SolarAmber
                        },
                        icon = Icons.Default.Security
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sub-Tab Navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        "POLICIES" to "Security Policy Rules",
                        "PIPELINE" to "5-Stage Defense Path",
                        "THREAT_SIM" to "Threat Injection Lab"
                    ).forEach { (tabKey, tabLabel) ->
                        val isSelected = activeSubTab == tabKey
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PhotonicCyan.copy(alpha = 0.25f) else SpaceCobaltGlassElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) PhotonicCyan else SpaceCobaltGlassBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { activeSubTab = tabKey }
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tabLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) PhotonicCyan else AmbientWhiteMuted,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        // Sub-Tab: Security Policy Rules & Posture Configuration
        if (activeSubTab == "POLICIES") {
            // Posture Profile Selector Card
            item {
                QuantumGlassCard(
                    borderColor = PhotonicCyan.copy(alpha = 0.45f),
                    backgroundColor = SpaceCobaltGlassElevated
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            PhotonicSignalPulseIndicator(
                                signalColor = OperationalEmerald,
                                size = 8.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "ZERO-TRUST POSTURE PROFILE",
                                style = MaterialTheme.typography.labelSmall,
                                color = PhotonicCyanLight,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "RESET DEFAULT",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            modifier = Modifier.clickable { viewModel.resetSecurityPoliciesToDefault() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Posture Level Options
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        PolicyEnforcementLevel.entries.forEach { level ->
                            val isSelected = enforcementLevel == level
                            val badgeColor = when (level) {
                                PolicyEnforcementLevel.STRICT -> OperationalEmerald
                                PolicyEnforcementLevel.BALANCED -> PhotonicCyan
                                PolicyEnforcementLevel.DEVELOPMENT -> SolarAmber
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) badgeColor.copy(alpha = 0.18f) else SpaceCobaltDark)
                                    .border(
                                        1.dp,
                                        if (isSelected) badgeColor else SpaceCobaltGlassBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.setPolicyEnforcementLevel(level) }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { viewModel.setPolicyEnforcementLevel(level) },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = badgeColor,
                                                unselectedColor = TextDimmed
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = level.name,
                                                style = MaterialTheme.typography.titleSmall,
                                                color = if (isSelected) AmbientWhite else AmbientWhiteMuted,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = level.label,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontSize = 11.sp,
                                                color = AmbientWhiteSubtle
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    QuantumVolumetricButton(
                        text = if (isPolicyAuditRunning) "AUDITING SYSTEM POLICIES..." else "EXECUTE POLICY ATTESTATION AUDIT",
                        icon = if (isPolicyAuditRunning) Icons.Default.Sync else Icons.Default.VerifiedUser,
                        primaryColor = OperationalEmerald,
                        secondaryColor = PhotonicCyan,
                        containerColor = SpaceCobaltDark,
                        modifier = Modifier.fillMaxWidth(),
                        shapeRadius = 8.dp,
                        onClick = {
                            if (!isPolicyAuditRunning) {
                                viewModel.runSecurityPolicyAudit()
                            }
                        }
                    )
                }
            }

            // Editable Security Policy Rules Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ENFORCED SECURITY POLICY RULES",
                        style = MaterialTheme.typography.labelLarge,
                        color = PhotonicCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${policyRules.count { it.isEnabled }}/${policyRules.size} ACTIVE",
                        style = MaterialTheme.typography.labelSmall,
                        color = OperationalEmeraldLight,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Policy Rule Items
            items(policyRules, key = { it.id }) { rule ->
                SecurityPolicyRuleCard(
                    rule = rule,
                    onToggle = { viewModel.toggleSecurityPolicyRule(rule.id) }
                )
            }
        }

        // Sub-Tab: 5-Stage Visual Pipeline
        if (activeSubTab == "PIPELINE") {
            item {
                QuantumGlassCard(
                    borderColor = PhotonicCyan.copy(alpha = 0.4f),
                    backgroundColor = SpaceCobaltGlassElevated
                ) {
                    Text(
                        text = "CONTINUOUS ZERO-TRUST PIPELINE STAGES",
                        style = MaterialTheme.typography.labelSmall,
                        color = PhotonicCyanLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        pipelineSteps.forEach { (stepNum, title, description) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SpaceCobaltSurface)
                                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(PhotonicCyan.copy(alpha = 0.2f))
                                        .border(1.dp, PhotonicCyan, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stepNum,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = PhotonicCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = title,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = AmbientWhite,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = AmbientWhiteMuted
                                    )
                                }
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = OperationalEmerald,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Interactive Sanitization Scan & Post-Quantum Enclave Status Feedback
            item {
                SanitizationScanControlCard(viewModel = viewModel)
            }

            // Technical Documentation & ECTT Protocol Library Affordance
            item {
                QuantumGlassCard(
                    borderColor = PhotonicCyan.copy(alpha = 0.4f),
                    backgroundColor = SpaceCobaltGlassElevated,
                    onClick = { viewModel.setSecurityDocsVisible(true) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(PhotonicCyan.copy(alpha = 0.15f))
                                    .border(1.dp, PhotonicCyan.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MenuBook,
                                    contentDescription = "Security Docs",
                                    tint = PhotonicCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ECTT & ZERO-TRUST SPECIFICATION",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = PhotonicCyan,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Inspect STRIDE threat models, Laplace ε=0.5 formulas & NIST specs",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AmbientWhiteMuted
                                )
                            }
                        }
                        PhotonicBadge(
                            text = "OPEN SPEC",
                            signalColor = PhotonicCyan,
                            icon = Icons.Default.Article
                        )
                    }
                }
            }
        }

        // Sub-Tab: Threat Injection Simulator Lab
        if (activeSubTab == "THREAT_SIM") {
            item {
                QuantumGlassCard(
                    borderColor = ContainmentCrimson.copy(alpha = 0.4f),
                    backgroundColor = SpaceCobaltGlassElevated
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "THREAT INJECTION SIMULATOR LAB",
                                style = MaterialTheme.typography.labelSmall,
                                color = ContainmentCrimson,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Test instant Photonic Crimson Quarantining",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmbientWhiteMuted
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.BugReport,
                            contentDescription = "Threat Simulator",
                            tint = ContainmentCrimson,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(
                            "PROMPT_INJECTION" to ("Prompt Injection Vector" to "Injects jailbreak attempting to dump 512-bit Kyber key"),
                            "EXFILTRATION_PROBE" to ("Exfiltration Probe" to "Simulates unauthorized HTTP/2 telemetry exfiltration attempt"),
                            "TAINTED_MEMORY" to ("Memory Taint / Buffer Overflow" to "Attempts buffer overflow on enclave memory address 0x7FFF8000"),
                            "CROSS_DOMAIN_BYPASS" to ("Cross-Domain Escalation" to "Attempts to bypass explicit neural confirmation gate")
                        ).forEach { (threatKey, details) ->
                            val (threatTitle, threatDesc) = details
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(SpaceCobaltDark)
                                    .border(1.dp, ContainmentCrimson.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        viewModel.injectSimulatedThreat(threatKey, "untrusted.external.attacker")
                                    }
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = threatTitle,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = AmbientWhite,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = threatDesc,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AmbientWhiteSubtle
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    QuantumVolumetricButton(
                                        text = "INJECT",
                                        icon = Icons.Default.BugReport,
                                        primaryColor = ContainmentCrimson,
                                        secondaryColor = SolarAmber,
                                        containerColor = ContainmentCrimsonDark.copy(alpha = 0.6f),
                                        shapeRadius = 8.dp,
                                        onClick = {
                                            viewModel.injectSimulatedThreat(threatKey, "untrusted.external.attacker")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Quarantined Threat Incidents Log
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "QUARANTINED THREAT INCIDENTS",
                        style = MaterialTheme.typography.labelLarge,
                        color = PhotonicCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${threatIncidents.size} INCIDENTS",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmbientWhiteSubtle
                    )
                }
            }

            if (threatIncidents.isEmpty()) {
                item {
                    QuantumGlassCard(
                        borderColor = SpaceCobaltGlassBorder,
                        backgroundColor = SpaceCobaltGlass
                    ) {
                        Text(
                            text = "Shield posture 100% operational. Zero active threats detected. Inject a threat above to test real-time photonic containment.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmbientWhiteMuted
                        )
                    }
                }
            }

            items(threatIncidents, key = { it.id }) { incident ->
                ThreatIncidentItemCard(incident)
            }
        }
    }
}

@Composable
fun SecurityPolicyRuleCard(
    rule: SecurityPolicyRule,
    onToggle: () -> Unit
) {
    QuantumGlassCard(
        borderColor = if (rule.isEnabled) OperationalEmerald.copy(alpha = 0.45f) else SolarAmber.copy(alpha = 0.35f),
        backgroundColor = SpaceCobaltGlassElevated
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (rule.isEnabled) SpaceCobaltSurface else SpaceCobaltDark)
                        .border(1.dp, if (rule.isEnabled) OperationalEmerald else TextDimmed, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "T${rule.minimumTier}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (rule.isEnabled) OperationalEmeraldLight else TextDimmed
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = rule.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (rule.isEnabled) AmbientWhite else AmbientWhiteMuted
                    )
                    Text(
                        text = rule.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = PhotonicCyanLight,
                        fontSize = 10.sp
                    )
                }
            }

            Switch(
                checked = rule.isEnabled,
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = AmbientWhite,
                    checkedTrackColor = OperationalEmerald,
                    uncheckedThumbColor = TextDimmed,
                    uncheckedTrackColor = SpaceCobaltDark
                )
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = rule.description,
            style = MaterialTheme.typography.bodySmall,
            color = AmbientWhiteMuted,
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(SpaceCobaltDark)
                .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(6.dp))
                .padding(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (rule.requiresBiometricConfirmation) Icons.Default.Fingerprint else Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (rule.isEnabled) PhotonicCyan else TextDimmed,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ACTION: ${rule.enforcementAction}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (rule.isEnabled) OperationalEmeraldLight else TextDimmed,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun ThreatIncidentItemCard(incident: ThreatIncidentEntity) {
    val dateStr = remember(incident.timestamp) {
        SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(incident.timestamp))
    }

    QuantumGlassCard(
        borderColor = if (incident.status.contains("Contained")) ContainmentCrimson else OperationalEmerald,
        backgroundColor = SpaceCobaltGlassElevated
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = if (incident.status.contains("Contained")) ContainmentCrimson else OperationalEmerald,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${incident.threatType} • $dateStr",
                    style = MaterialTheme.typography.labelSmall,
                    color = PhotonicCyanLight,
                    fontWeight = FontWeight.Bold
                )
            }

            PhotonicBadge(
                text = incident.status,
                signalColor = if (incident.status.contains("Contained")) ContainmentCrimson else OperationalEmerald
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "PAYLOAD: ${incident.payloadSnippet}",
            style = MaterialTheme.typography.bodySmall,
            color = AmbientWhite
        )

        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(SpaceCobaltDark)
                .padding(8.dp)
        ) {
            Text(
                text = "ACTION: ${incident.containmentAction}",
                style = MaterialTheme.typography.labelSmall,
                color = OperationalEmerald
            )
        }
    }
}
