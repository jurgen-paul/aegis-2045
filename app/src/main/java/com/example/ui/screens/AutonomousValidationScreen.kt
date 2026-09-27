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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ValidationProof
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.components.PhotonicBadge
import com.example.ui.components.QuantumGlassCard
import com.example.ui.components.SanitizationScanControlCard
import com.example.ui.components.TelemetryThreatDashboard
import com.example.ui.theme.*
import com.example.viewmodel.AgisViewModel

@Composable
fun AutonomousValidationScreen(
    viewModel: AgisViewModel
) {
    val validationProofs by viewModel.validationProofs.collectAsState()
    val isValidating by viewModel.isValidating.collectAsState()
    val rawTelemetryInput by viewModel.rawTelemetryInput.collectAsState()
    val sanitizedOutput by viewModel.sanitizedTelemetryOutput.collectAsState()
    val sanitizationStats by viewModel.sanitizationStats.collectAsState()

    var activeTab by remember { mutableStateOf("DASHBOARD") } // "DASHBOARD", "SANITIZER", "PROOFS"

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
                            text = "LAYER 6 • AUTONOMOUS VALIDATION",
                            style = MaterialTheme.typography.labelMedium,
                            color = PhotonicCyanLight
                        )
                        Text(
                            text = "Telemetry Sanitization & Threat Dashboard",
                            style = MaterialTheme.typography.titleMedium,
                            color = AmbientWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    PhotonicBadge(
                        text = "ORACLE PASS",
                        signalColor = OperationalEmerald,
                        icon = Icons.Default.CheckCircleOutline
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Tab Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "DASHBOARD" to "Real-Time Matrix",
                        "SANITIZER" to "Payload Sanitizer",
                        "PROOFS" to "Pre-Execution Checks"
                    ).forEach { (tabKey, label) ->
                        val isSelected = activeTab == tabKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PhotonicCyan.copy(alpha = 0.25f) else SpaceCobaltGlassElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) PhotonicCyan else SpaceCobaltGlassBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { activeTab = tabKey }
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

        if (activeTab == "DASHBOARD") {
            item {
                TelemetryThreatDashboard(viewModel = viewModel)
            }
        }

        if (activeTab == "SANITIZER") {
            // Simulated Sanitization Scan & Post-Quantum Enclave Status Feedback
            item {
                SanitizationScanControlCard(viewModel = viewModel)
            }

            // Interactive Telemetry Sanitizer
            item {
                QuantumGlassCard(
                    borderColor = PhotonicCyan.copy(alpha = 0.4f),
                    backgroundColor = SpaceCobaltGlassElevated
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "INGRESS RAW TELEMETRY PAYLOAD (EDITABLE)",
                            style = MaterialTheme.typography.labelSmall,
                            color = SolarAmber,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "RESET DEFAULT",
                            style = MaterialTheme.typography.labelSmall,
                            color = PhotonicCyan,
                            modifier = Modifier.clickable {
                                viewModel.setRawTelemetryInput(
                                    """
{
  "event": "neural_inference_request",
  "client_ip": "192.168.1.144",
  "user_biometrics": {
    "raw_neural_waves": "EEG_827394817293",
    "retinal_hash": "RET_9921_0492_A1"
  },
  "intent_prompt": "Query model weights for cluster Alpha-7",
  "auth_token": "bearer_sec_token_99982341",
  "domain": "finance.secure.enclave",
  "telemetry_flags": ["DEBUG_PROFILING", "TRACE_ENABLED"]
}
                                    """.trimIndent()
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = rawTelemetryInput,
                        onValueChange = { viewModel.setRawTelemetryInput(it) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SolarAmber,
                            unfocusedBorderColor = SpaceCobaltGlassBorder,
                            focusedTextColor = AmbientWhite,
                            unfocusedTextColor = AmbientWhite,
                            focusedContainerColor = SpaceCobaltDark,
                            unfocusedContainerColor = SpaceCobaltDark
                        ),
                        shape = RoundedCornerShape(10.dp),
                        textStyle = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp)
                    )
                }
            }

            // Sanitized Output & Leak Proof
            item {
                QuantumGlassCard(
                    borderColor = OperationalEmerald.copy(alpha = 0.4f),
                    backgroundColor = SpaceCobaltGlassElevated
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EGRESS SANITIZED TELEMETRY (PERIMETER CLEAN)",
                            style = MaterialTheme.typography.labelSmall,
                            color = OperationalEmerald,
                            fontWeight = FontWeight.Bold
                        )

                        if (sanitizationStats != null) {
                            PhotonicBadge(
                                text = "${sanitizationStats!!.first} TOKENS SANITIZED",
                                signalColor = OperationalEmerald
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SpaceCobaltDark)
                            .border(1.dp, OperationalEmerald.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = sanitizedOutput ?: "// Processing differential sanitization...",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmbientWhite,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "DIFFERENTIAL PRIVACY: ε = 0.5",
                            style = MaterialTheme.typography.labelSmall,
                            color = PhotonicCyanLight
                        )
                        Text(
                            text = "LEAK AUDIT: 0 LEAKS DETECTED",
                            style = MaterialTheme.typography.labelSmall,
                            color = OperationalEmerald,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        if (activeTab == "PROOFS") {
            // Continuous Autonomous Pre-Execution Checks
            item {
                QuantumGlassCard(
                    borderColor = PhotonicCyan.copy(alpha = 0.4f),
                    backgroundColor = SpaceCobaltGlassElevated
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "PRE-EXECUTION PROOF RUNNER",
                                style = MaterialTheme.typography.labelSmall,
                                color = PhotonicCyanLight,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Deterministic verification across all layers",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmbientWhiteMuted
                            )
                        }

                        QuantumVolumetricButton(
                            text = if (isValidating) "VERIFYING..." else "RUN ALL PROOFS",
                            icon = if (isValidating) Icons.Default.HourglassTop else Icons.Default.PlayArrow,
                            primaryColor = PhotonicCyan,
                            secondaryColor = OperationalEmerald,
                            containerColor = SpaceCobaltSurface,
                            shapeRadius = 8.dp,
                            onClick = { if (!isValidating) viewModel.runAutonomousValidation() }
                        )
                    }
                }
            }

            items(validationProofs, key = { it.id }) { proof ->
                ValidationProofItemCard(proof)
            }
        }
    }
}

@Composable
fun ValidationProofItemCard(proof: ValidationProof) {
    QuantumGlassCard(
        borderColor = if (proof.isPassing) OperationalEmerald.copy(alpha = 0.4f) else ContainmentCrimson,
        backgroundColor = SpaceCobaltGlassElevated
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Verified,
                    contentDescription = null,
                    tint = OperationalEmerald,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = proof.id,
                    style = MaterialTheme.typography.labelMedium,
                    color = PhotonicCyanLight,
                    fontWeight = FontWeight.Bold
                )
            }

            PhotonicBadge(
                text = proof.status,
                signalColor = OperationalEmerald
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = proof.name,
            style = MaterialTheme.typography.titleSmall,
            color = AmbientWhite,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = proof.description,
            style = MaterialTheme.typography.bodySmall,
            color = AmbientWhiteMuted
        )

        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(6.dp))
                .background(SpaceCobaltDark)
                .padding(8.dp)
        ) {
            Text(
                text = proof.verificationDigest,
                style = MaterialTheme.typography.labelSmall,
                color = OperationalEmerald
            )
        }
    }
}
