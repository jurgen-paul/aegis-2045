package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.NeuralCommandEntity
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.animation.volumetricQuantumGlass
import com.example.ui.components.CyberNodeArchitectureVisualizer
import com.example.ui.components.NeuralIntentNodeConnectionGraph
import com.example.ui.components.NeuralIntentRouterView
import com.example.ui.components.NeuralIntentRoutingDashboard
import com.example.ui.components.PhotonicBadge
import com.example.ui.components.QuantumGlassCard
import com.example.ui.theme.*
import com.example.viewmodel.AgisViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NeuralCommandScreen(
    viewModel: AgisViewModel
) {
    var promptInput by remember { mutableStateOf("") }
    var selectedDomain by remember { mutableStateOf("local.enclave.core") }
    var isCrossDomain by remember { mutableStateOf(false) }
    var selectedSubTab by remember { mutableStateOf(0) } // 0: Neural Intent Router, 1: Cyber-Node Canvas, 2: Intent Stream, 3: Dispatch Studio

    val prioritizedTasks by viewModel.prioritizedTasks.collectAsState()
    val selectedTaskFilter by viewModel.selectedTaskCategoryFilter.collectAsState()
    val intentRouterSearchQuery by viewModel.intentRouterSearchQuery.collectAsState()
    val isEvaluatingAction by viewModel.isEvaluatingAction.collectAsState()

    val pendingConfirmation by viewModel.pendingConfirmation.collectAsState()
    val neuralCommands by viewModel.neuralCommands.collectAsState()
    val biometrics by viewModel.biometrics.collectAsState()
    val intentStream by viewModel.neuralIntentStream.collectAsState()
    val topologyNodes by viewModel.topologyNodes.collectAsState()
    val selectedFilter by viewModel.selectedIntentFilter.collectAsState()

    val cyberNodes by viewModel.cyberNodes.collectAsState()
    val activeNeuralRoutes by viewModel.activeNeuralRoutes.collectAsState()
    val selectedCyberRouteId by viewModel.selectedCyberRouteId.collectAsState()
    val selectedCyberNodeId by viewModel.selectedCyberNodeId.collectAsState()
    val activeHopIndex by viewModel.activeHopIndex.collectAsState()
    val isRouteSimulationRunning by viewModel.isRouteSimulationRunning.collectAsState()


    val domainOptions = listOf(
        "local.enclave.core" to false,
        "internal.agent.sandbox" to false,
        "remote.agi.cluster" to true,
        "external.untrusted.bridge" to true
    )

    // Neural Confirmation Modal Dialog for Cross-Domain Safety Gate
    if (pendingConfirmation != null) {
        AlertDialog(
            onDismissRequest = { viewModel.confirmPendingNeuralGate(false) },
            containerColor = SpaceCobaltSurface,
            titleContentColor = PhotonicCyan,
            textContentColor = AmbientWhite,
            shape = RoundedCornerShape(16.dp),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Fingerprint,
                        contentDescription = "Neural Gate",
                        tint = PhotonicCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "EXPLICIT NEURAL CONFIRMATION",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = PhotonicCyan
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "A cross-domain intent has reached the Layer-2 Zero-Trust Gate. Biomorphic validation required before routing.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmbientWhiteMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SpaceCobaltDark)
                            .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "DESTINATION DOMAIN",
                                style = MaterialTheme.typography.labelSmall,
                                color = SolarAmber,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = pendingConfirmation!!.targetDomain,
                                style = MaterialTheme.typography.labelLarge,
                                color = AmbientWhite
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "INTENT PAYLOAD",
                                style = MaterialTheme.typography.labelSmall,
                                color = PhotonicCyanLight
                            )
                            Text(
                                text = pendingConfirmation!!.prompt,
                                style = MaterialTheme.typography.bodySmall,
                                color = AmbientWhite
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "NEURAL SYNC: ${(biometrics.neuralSyncRatio * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = OperationalEmerald
                        )
                        Text(
                            text = "TOKEN: ${biometrics.sessionTokenHash}",
                            style = MaterialTheme.typography.labelSmall,
                            color = AmbientWhiteSubtle
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmPendingNeuralGate(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PhotonicCyan,
                        contentColor = SpaceCobaltDark
                    )
                ) {
                    Text("AUTHORIZE (SYNC)")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.confirmPendingNeuralGate(false) },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ContainmentCrimson),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(ContainmentCrimson))
                ) {
                    Text("PURGE ROUTE")
                }
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                            text = "LAYER 2 • NEURAL INTENT ROUTING",
                            style = MaterialTheme.typography.labelMedium,
                            color = PhotonicCyanLight
                        )
                        Text(
                            text = "Cognitive Topology & Routing Hub",
                            style = MaterialTheme.typography.titleMedium,
                            color = AmbientWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    PhotonicBadge(
                        text = "ENCLAVE ACTIVE",
                        signalColor = OperationalEmerald
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Sub-View Mode Switcher
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(SpaceCobaltDark)
                        .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "INTENT ROUTER",
                        "CYBER-NODES",
                        "STREAM GRAPH",
                        "DISPATCH"
                    ).forEachIndexed { index, label ->
                        val isSelected = selectedSubTab == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSelected) PhotonicCyan.copy(alpha = 0.25f) else Color.Transparent)
                                .border(
                                    1.dp,
                                    if (isSelected) PhotonicCyan else Color.Transparent,
                                    RoundedCornerShape(6.dp)
                                )
                                .clickable { selectedSubTab = index }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PhotonicCyan else AmbientWhiteMuted,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // Sub-Tab 0: Neural Intent Router Interface (Action Categorization & Dynamic Task Prioritization)
        if (selectedSubTab == 0) {
            item {
                NeuralIntentRouterView(
                    prioritizedTasks = prioritizedTasks,
                    selectedCategoryFilter = selectedTaskFilter,
                    searchQuery = intentRouterSearchQuery,
                    isEvaluating = isEvaluatingAction,
                    onSelectFilter = { viewModel.setTaskCategoryFilter(it) },
                    onSearchQueryChange = { viewModel.setIntentRouterSearchQuery(it) },
                    onSubmitAction = { action, urgency -> viewModel.submitUserActionForRouting(action, urgency) },
                    onExecuteTask = { taskId -> viewModel.executePrioritizedTask(taskId) },
                    onAdjustUrgency = { taskId, newUrgency -> viewModel.updateTaskUrgency(taskId, newUrgency) },
                    onPurgeTask = { taskId -> viewModel.purgePrioritizedTask(taskId) },
                    onResetQueue = { viewModel.resetPrioritizedTasks() }
                )
            }
        } else if (selectedSubTab == 1) {
            // Sub-Tab 1: Real-Time Node Connection Graph & Cyber-Node Architecture
            item {
                NeuralIntentNodeConnectionGraph(
                    cyberNodes = cyberNodes,
                    routes = activeNeuralRoutes,
                    activeTasks = prioritizedTasks,
                    selectedRouteId = selectedCyberRouteId,
                    selectedNodeId = selectedCyberNodeId,
                    onSelectRoute = { routeId -> viewModel.selectCyberRoute(routeId) },
                    onSelectNode = { nodeId -> viewModel.selectCyberNode(nodeId) },
                    onDispatchRouteIntent = { routeId -> viewModel.dispatchNeuralRoutePacket(routeId) }
                )
            }
            item {
                CyberNodeArchitectureVisualizer(
                    cyberNodes = cyberNodes,
                    routes = activeNeuralRoutes,
                    selectedRouteId = selectedCyberRouteId,
                    selectedNodeId = selectedCyberNodeId,
                    activeHopIndex = activeHopIndex,
                    isSimulationRunning = isRouteSimulationRunning,
                    onSelectRoute = { routeId -> viewModel.selectCyberRoute(routeId) },
                    onSelectNode = { nodeId -> viewModel.selectCyberNode(nodeId) },
                    onDispatchPacket = { routeId -> viewModel.dispatchNeuralRoutePacket(routeId) }
                )
            }
        } else if (selectedSubTab == 2) {
            // Sub-Tab 2: Neural Intent Routing Diagnostic Dashboard
            item {
                NeuralIntentRoutingDashboard(
                    intentStream = intentStream,
                    topologyNodes = topologyNodes,
                    selectedFilter = selectedFilter,
                    onSelectFilter = { viewModel.setSelectedIntentFilter(it) },
                    onInjectIntent = { type, desc, risk ->
                        viewModel.injectSimulatedIntentPattern(type, desc, risk)
                    }
                )
            }
        } else {
            // Sub-Tab 3: Interactive Glass Input Pane & Execution Ledger
            item {
                QuantumGlassCard(
                    borderColor = PhotonicCyan.copy(alpha = 0.4f),
                    backgroundColor = SpaceCobaltGlassElevated
                ) {
                    Text(
                        text = "VOLUMETRIC GLASS INPUT SURFACE",
                        style = MaterialTheme.typography.labelSmall,
                        color = PhotonicCyanLight,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        placeholder = {
                            Text(
                                "Enter multi-modal neural intent (e.g. Execute zero-trust perimeter leak proof...)",
                                color = AmbientWhiteSubtle,
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PhotonicCyan,
                            unfocusedBorderColor = SpaceCobaltGlassBorder,
                            focusedTextColor = AmbientWhite,
                            unfocusedTextColor = AmbientWhite,
                            focusedContainerColor = SpaceCobaltDark,
                            unfocusedContainerColor = SpaceCobaltDark
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Prompt Templates
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "Verify Enclave Lattice",
                            "Sanitize Egress Telemetry",
                            "Cross-Domain Sync"
                        ).forEach { preset ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(SpaceCobaltGlass)
                                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(6.dp))
                                    .clickable { promptInput = preset }
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = preset,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmbientWhiteMuted
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Target Domain Selection
                    Text(
                        text = "TARGET DOMAIN ROUTING",
                        style = MaterialTheme.typography.labelSmall,
                        color = PhotonicCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        domainOptions.forEach { (domain, requiresCrossDomain) ->
                            val isSelected = selectedDomain == domain
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) PhotonicCyan.copy(alpha = 0.15f) else SpaceCobaltSurface)
                                    .border(
                                        1.dp,
                                        if (isSelected) PhotonicCyan else SpaceCobaltGlassBorder,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        selectedDomain = domain
                                        isCrossDomain = requiresCrossDomain
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) PhotonicCyan else AmbientWhiteSubtle)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = domain,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (isSelected) AmbientWhite else AmbientWhiteMuted,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                }

                                if (requiresCrossDomain) {
                                    PhotonicBadge(
                                        text = "CROSS-DOMAIN (NEURAL CONFIRM)",
                                        signalColor = SolarAmber
                                    )
                                } else {
                                    PhotonicBadge(
                                        text = "ISOLATED",
                                        signalColor = OperationalEmerald
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Volumetric Interactive Action Button
                    QuantumVolumetricButton(
                        text = if (isCrossDomain) "SUBMIT CROSS-DOMAIN (REQUIRES GATE)" else "EXECUTE NEURAL INTENT",
                        icon = if (isCrossDomain) Icons.Default.LockOpen else Icons.Default.Send,
                        primaryColor = if (isCrossDomain) SolarAmber else PhotonicCyan,
                        secondaryColor = OperationalEmerald,
                        containerColor = SpaceCobaltSurface,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            viewModel.submitNeuralCommand(
                                prompt = promptInput.ifBlank { "Execute zero-trust state verification" },
                                domain = selectedDomain,
                                isCrossDomain = isCrossDomain
                            )
                            promptInput = ""
                        }
                    )
                }
            }

            // Neural Execution History
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NEURAL COMMAND EXECUTION LEDGER",
                        style = MaterialTheme.typography.labelLarge,
                        color = PhotonicCyan,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${neuralCommands.size} ENTRIES",
                        style = MaterialTheme.typography.labelSmall,
                        color = AmbientWhiteSubtle
                    )
                }
            }

            if (neuralCommands.isEmpty()) {
                item {
                    QuantumGlassCard(
                        borderColor = SpaceCobaltGlassBorder,
                        backgroundColor = SpaceCobaltGlass
                    ) {
                        Text(
                            text = "No neural commands executed yet. Submit an intent above to record verifiable transactions.",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmbientWhiteMuted
                        )
                    }
                }
            }

            items(neuralCommands, key = { it.id }) { cmd ->
                NeuralCommandItemCard(cmd)
            }
        }
    }
}


@Composable
fun NeuralCommandItemCard(cmd: NeuralCommandEntity) {
    val dateStr = remember(cmd.timestamp) {
        SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(cmd.timestamp))
    }

    QuantumGlassCard(
        borderColor = if (cmd.isCrossDomain) SolarAmber.copy(alpha = 0.4f) else PhotonicCyan.copy(alpha = 0.25f),
        backgroundColor = SpaceCobaltGlassElevated
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "INTENT #${cmd.id} • $dateStr",
                style = MaterialTheme.typography.labelSmall,
                color = PhotonicCyanLight,
                fontWeight = FontWeight.Bold
            )

            PhotonicBadge(
                text = if (cmd.neuralConfirmed) "NEURAL CONFIRMED" else "AWAITING GATE",
                signalColor = if (cmd.neuralConfirmed) OperationalEmerald else SolarAmber
            )
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "\"${cmd.prompt}\"",
            style = MaterialTheme.typography.bodyMedium,
            color = AmbientWhite,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "DOMAIN: ${cmd.targetDomain}",
                style = MaterialTheme.typography.labelSmall,
                color = AmbientWhiteMuted
            )
            Text(
                text = "LATENCY: ${cmd.latencyMs}ms",
                style = MaterialTheme.typography.labelSmall,
                color = OperationalEmerald
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "STATUS: ${cmd.executionResult}",
            style = MaterialTheme.typography.labelSmall,
            color = if (cmd.neuralConfirmed) OperationalEmerald else SolarAmber
        )
    }
}
