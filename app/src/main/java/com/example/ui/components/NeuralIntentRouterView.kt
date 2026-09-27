package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.service.NeuralIntentRouterEngine
import com.example.ui.animation.PhotonicSignalPulseIndicator
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Neural Intent Router Interface
 * Categorizes user actions into distinct cognitive intent domains and dynamically
 * prioritizes tasks based on a real-time, multi-variable intent-scoring system.
 */
@Composable
fun NeuralIntentRouterView(
    prioritizedTasks: List<PrioritizedNeuralTask>,
    selectedCategoryFilter: String,
    searchQuery: String,
    isEvaluating: Boolean,
    onSelectFilter: (String) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSubmitAction: (String, Float?) -> Unit,
    onExecuteTask: (String) -> Unit,
    onAdjustUrgency: (String, Float) -> Unit,
    onPurgeTask: (String) -> Unit,
    onResetQueue: () -> Unit,
    modifier: Modifier = Modifier
) {
    var customActionInput by remember { mutableStateOf("") }
    var urgencySliderValue by remember { mutableFloatStateOf(8.0f) }
    var showBreakdownId by remember { mutableStateOf<String?>(null) }

    // Real-time categorization preview of what the user is currently typing
    val previewTask = remember(customActionInput, urgencySliderValue) {
        if (customActionInput.isBlank()) null
        else NeuralIntentRouterEngine.analyzeAndScoreIntent(customActionInput, urgencySliderValue)
    }

    // Filter tasks based on category filter and search query
    val filteredTasks = remember(prioritizedTasks, selectedCategoryFilter, searchQuery) {
        prioritizedTasks.filter { task ->
            val matchesCategory = when (selectedCategoryFilter) {
                "ALL" -> true
                "CRITICAL_SECURITY" -> task.category == UserActionCategory.CRITICAL_SECURITY
                "IDENTITY_ATTESTATION" -> task.category == UserActionCategory.IDENTITY_ATTESTATION
                "DATA_TRANSMISSION" -> task.category == UserActionCategory.DATA_TRANSMISSION
                "SYSTEM_OPTIMIZATION" -> task.category == UserActionCategory.SYSTEM_OPTIMIZATION
                "ROUTINE_MONITORING" -> task.category == UserActionCategory.ROUTINE_MONITORING
                else -> true
            }
            val matchesSearch = if (searchQuery.isBlank()) true else {
                task.actionTitle.contains(searchQuery, ignoreCase = true) ||
                task.rawActionDescription.contains(searchQuery, ignoreCase = true) ||
                task.id.contains(searchQuery, ignoreCase = true) ||
                task.targetNode.contains(searchQuery, ignoreCase = true)
            }
            matchesCategory && matchesSearch
        }
    }

    // Summary metrics
    val criticalCount = remember(prioritizedTasks) {
        prioritizedTasks.count { it.priorityTier == PriorityTier.TIER_P1_CRITICAL && it.status != NeuralTaskStatus.COMPLETED }
    }
    val avgScore = remember(prioritizedTasks) {
        if (prioritizedTasks.isEmpty()) 0
        else (prioritizedTasks.map { it.priorityScore }.average()).toInt()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("neural_intent_router_container"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Router Engine Header & Metrics Overview
        QuantumGlassCard(
            borderColor = PhotonicCyan.copy(alpha = 0.5f),
            backgroundColor = SpaceCobaltGlassElevated
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PhotonicSignalPulseIndicator(
                        signalColor = PhotonicCyan,
                        size = 12.dp,
                        pulseSpeedMs = 1200
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "NEURAL INTENT ROUTER ENGINE",
                            style = MaterialTheme.typography.labelSmall,
                            color = PhotonicCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Dynamic Intent-Scoring & Prioritization",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AmbientWhite
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PhotonicBadge(
                        text = "$criticalCount P1 CRITICAL",
                        signalColor = if (criticalCount > 0) ContainmentCrimson else OperationalEmerald,
                        icon = Icons.Default.WarningAmber
                    )
                    IconButton(
                        onClick = onResetQueue,
                        modifier = Modifier.size(32.dp).testTag("btn_reset_queue")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset Queue",
                            tint = AmbientWhiteMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time System Intent Scoring Metrics
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
                    Text("ACTIVE QUEUE", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 9.sp)
                    Text(
                        text = "${prioritizedTasks.count { it.status != NeuralTaskStatus.COMPLETED }} Tasks",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmbientWhite
                    )
                }
                Column {
                    Text("AVG INTENT SCORE", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 9.sp)
                    Text(
                        text = "$avgScore / 100",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (avgScore > 75) SolarAmber else OperationalEmeraldLight
                    )
                }
                Column {
                    Text("DISPATCH POLICY", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 9.sp)
                    Text(
                        text = "Score-Descending",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PhotonicCyanLight
                    )
                }
                Column {
                    Text("LATTICE ENCLAVE", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 9.sp)
                    Text(
                        text = "Hardware Gated",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = QuantumVioletLight
                    )
                }
            }
        }

        // Action Input & Real-Time Intent Categorization Panel
        QuantumGlassCard(
            borderColor = OperationalEmerald.copy(alpha = 0.45f),
            backgroundColor = SpaceCobaltGlass
        ) {
            Text(
                text = "ACTION CATEGORIZATION & SCORING DISPATCHER",
                style = MaterialTheme.typography.labelSmall,
                color = OperationalEmeraldLight,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Type any natural language command to automatically evaluate its cognitive domain and calculate its dynamic priority score:",
                style = MaterialTheme.typography.bodySmall,
                color = AmbientWhiteMuted,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Input TextField
            OutlinedTextField(
                value = customActionInput,
                onValueChange = { customActionInput = it },
                placeholder = {
                    Text(
                        "e.g. Quarantine rogue sub-agent node #04, or export ECTT telemetry batch...",
                        color = AmbientWhiteSubtle,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_user_action"),
                maxLines = 3,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PhotonicCyan,
                    unfocusedBorderColor = SpaceCobaltGlassBorder,
                    focusedTextColor = AmbientWhite,
                    unfocusedTextColor = AmbientWhite,
                    focusedContainerColor = SpaceCobaltDark,
                    unfocusedContainerColor = SpaceCobaltDark
                ),
                shape = RoundedCornerShape(10.dp),
                trailingIcon = {
                    if (customActionInput.isNotEmpty()) {
                        IconButton(onClick = { customActionInput = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = AmbientWhiteMuted)
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Suggested Action Template Chips
            Text(
                text = "QUICK INTENT PROMPTS:",
                style = MaterialTheme.typography.labelSmall,
                color = TextDimmed,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val suggestions = listOf(
                    "Emergency Enclave Lockout & Revoke Master Key" to 10.0f,
                    "Quarantine Rogue Sub-Agent #04 on Red Notice" to 9.5f,
                    "Class 3 StrongBox Biometric Attestation Challenge" to 8.0f,
                    "4-Pass Hardware Memory Zeroization Scrub" to 7.0f,
                    "Batch Export 10k ECTT Differential Telemetry Packets" to 6.0f,
                    "Inspect Latency Mesh on Synchronic Nodes" to 3.0f
                )
                items(suggestions) { (suggestion, urgency) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(SpaceCobaltSurface)
                            .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(6.dp))
                            .clickable {
                                customActionInput = suggestion
                                urgencySliderValue = urgency
                            }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("chip_suggestion_${suggestion.take(10)}")
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.labelSmall,
                            color = AmbientWhiteMuted,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time Live Scoring Preview Box (Reacts as user types!)
            AnimatedVisibility(
                visible = previewTask != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (previewTask != null) {
                    val categoryColor = Color(android.graphics.Color.parseColor(previewTask.category.hexColor))
                    val tierColor = when (previewTask.priorityTier) {
                        PriorityTier.TIER_P1_CRITICAL -> ContainmentCrimson
                        PriorityTier.TIER_P2_HIGH -> SolarAmber
                        PriorityTier.TIER_P3_MEDIUM -> PhotonicCyan
                        PriorityTier.TIER_P4_LOW -> OperationalEmerald
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SpaceCobaltSurface)
                            .border(1.dp, tierColor.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(categoryColor)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "PREDICTED CATEGORY: ${previewTask.category.title.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = categoryColor,
                                    fontSize = 10.sp
                                )
                            }

                            PhotonicBadge(
                                text = "${previewTask.priorityScore}/100 • ${previewTask.priorityTier.badgeLabel}",
                                signalColor = tierColor
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Target node & scoring formula preview
                        Text(
                            text = "Routing Target: ${previewTask.targetNode} • Confidence: ${(previewTask.scoringBreakdown.intentConfidence * 100).toInt()}% • Urgency: ${String.format(Locale.US, "%.1f", previewTask.scoringBreakdown.urgencyWeight)}/10",
                            style = MaterialTheme.typography.bodySmall,
                            color = AmbientWhiteMuted,
                            fontSize = 10.sp
                        )

                        if (previewTask.scoringBreakdown.enclaveRequirement) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🔒 Requires Hardware Enclave (Bonus +10 pts applied)",
                                style = MaterialTheme.typography.labelSmall,
                                color = QuantumVioletLight,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Urgency Adjustment Slider
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OPERATOR URGENCY WEIGHT:",
                    style = MaterialTheme.typography.labelSmall,
                    color = SolarAmber,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )
                Text(
                    text = "${String.format(Locale.US, "%.1f", urgencySliderValue)} / 10.0",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = SolarAmber
                )
            }

            Slider(
                value = urgencySliderValue,
                onValueChange = { urgencySliderValue = it },
                valueRange = 1.0f..10.0f,
                steps = 18,
                colors = SliderDefaults.colors(
                    thumbColor = SolarAmber,
                    activeTrackColor = SolarAmber,
                    inactiveTrackColor = SpaceCobaltSurface
                ),
                modifier = Modifier.fillMaxWidth().testTag("slider_urgency_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Volumetric Submit Button
            QuantumVolumetricButton(
                text = if (isEvaluating) "SCORING & CATEGORIZING INTENT..." else "ROUTE & PRIORITIZE ACTION",
                icon = if (isEvaluating) Icons.Default.HourglassTop else Icons.Default.Send,
                primaryColor = PhotonicCyan,
                secondaryColor = OperationalEmerald,
                containerColor = SpaceCobaltSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_submit_action"),
                onClick = {
                    val prompt = customActionInput.ifBlank { "Emergency Enclave Lockdown & Revoke Master Key" }
                    onSubmitAction(prompt, urgencySliderValue)
                    customActionInput = ""
                }
            )
        }

        // Real-Time Canvas Node Connection Graph for Neural Intent Routing
        var showNodeConnectionGraph by remember { mutableStateOf(true) }

        QuantumGlassCard(
            borderColor = PhotonicCyan.copy(alpha = 0.5f),
            backgroundColor = SpaceCobaltGlassElevated
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showNodeConnectionGraph = !showNodeConnectionGraph }
                    .testTag("toggle_node_connection_graph"),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = "Node Connection Graph",
                        tint = PhotonicCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "NEURAL INTENT TRAFFIC GRAPH",
                            style = MaterialTheme.typography.labelSmall,
                            color = PhotonicCyan,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Live Canvas Traffic Between System Components",
                            style = MaterialTheme.typography.titleSmall,
                            color = AmbientWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PhotonicBadge(
                        text = if (showNodeConnectionGraph) "CANVAS LIVE" else "COLLAPSED",
                        signalColor = if (showNodeConnectionGraph) OperationalEmerald else TextDimmed
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = if (showNodeConnectionGraph) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = "Toggle Node Graph",
                        tint = AmbientWhiteMuted
                    )
                }
            }

            AnimatedVisibility(visible = showNodeConnectionGraph) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))
                    NeuralIntentNodeConnectionGraph(
                        activeTasks = prioritizedTasks,
                        onDispatchRouteIntent = { routeId ->
                            val simulatedAction = when (routeId) {
                                "ROUTE_ZERO_TRUST_ATTEST" -> "Initiate Class 3 Biometric StrongBox Attestation Challenge"
                                "ROUTE_SUB_AGENT_DISPATCH" -> "Dispatch parallel sub-agent swarm on neural threads"
                                "ROUTE_CROSS_DOMAIN_MUTATION" -> "Execute Cross-Domain Gate with Kyber-1024 seal"
                                "ROUTE_PERIMETER_LEAK_PROOF" -> "Run mathematical zero-leak perimeter proof"
                                "ROUTE_TELEMETRY_PII_PURGE" -> "Purge raw PII with Laplace differential privacy noise"
                                "ROUTE_ENCLAVE_READ" -> "Read post-quantum hardware enclave keymaster memory"
                                else -> "Route neural intent directive through system components"
                            }
                            onSubmitAction(simulatedAction, 8.5f)
                        }
                    )
                }
            }
        }

        // Category Filter Chips & Search Bar
        QuantumGlassCard(
            borderColor = SpaceCobaltGlassBorder,
            backgroundColor = SpaceCobaltGlass
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRIORITIZED TASK LEDGER",
                    style = MaterialTheme.typography.labelMedium,
                    color = PhotonicCyan,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${filteredTasks.size} tasks visible",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDimmed,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Search Filter
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Filter tasks by keyword, ID, or target node...", color = AmbientWhiteSubtle, fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth().testTag("input_search_tasks"),
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AmbientWhiteMuted, modifier = Modifier.size(16.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = null, tint = AmbientWhiteMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PhotonicCyan,
                    unfocusedBorderColor = SpaceCobaltGlassBorder,
                    focusedTextColor = AmbientWhite,
                    unfocusedTextColor = AmbientWhite,
                    focusedContainerColor = SpaceCobaltDark,
                    unfocusedContainerColor = SpaceCobaltDark
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Category Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val filters = listOf(
                    "ALL" to "All Categories",
                    "CRITICAL_SECURITY" to "Critical Security",
                    "IDENTITY_ATTESTATION" to "Identity",
                    "DATA_TRANSMISSION" to "ECTT Telemetry",
                    "SYSTEM_OPTIMIZATION" to "Optimization",
                    "ROUTINE_MONITORING" to "Monitoring"
                )
                items(filters) { (key, label) ->
                    val isSelected = selectedCategoryFilter == key
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) PhotonicCyan.copy(alpha = 0.25f) else SpaceCobaltSurface)
                            .border(
                                1.dp,
                                if (isSelected) PhotonicCyan else SpaceCobaltGlassBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onSelectFilter(key) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("filter_category_$key")
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) PhotonicCyan else TextDimmed,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Dynamically Prioritized Task Cards (Ordered by Intent-Score Descending)
        if (filteredTasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterListOff,
                        contentDescription = null,
                        tint = AmbientWhiteSubtle,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No tasks match current filter or search criteria.",
                        style = MaterialTheme.typography.bodySmall,
                        color = AmbientWhiteMuted
                    )
                }
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                filteredTasks.forEach { task ->
                    PrioritizedTaskCard(
                        task = task,
                        isExpanded = showBreakdownId == task.id,
                        onToggleExpand = {
                            showBreakdownId = if (showBreakdownId == task.id) null else task.id
                        },
                        onExecute = { onExecuteTask(task.id) },
                        onPurge = { onPurgeTask(task.id) },
                        onUrgencyChange = { newUrgency -> onAdjustUrgency(task.id, newUrgency) }
                    )
                }
            }
        }
    }
}

/**
 * Individual Prioritized Task Card with intent-scoring breakdown and real-time urgency control.
 */
@Composable
fun PrioritizedTaskCard(
    task: PrioritizedNeuralTask,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onExecute: () -> Unit,
    onPurge: () -> Unit,
    onUrgencyChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val tierColor = when (task.priorityTier) {
        PriorityTier.TIER_P1_CRITICAL -> ContainmentCrimson
        PriorityTier.TIER_P2_HIGH -> SolarAmber
        PriorityTier.TIER_P3_MEDIUM -> PhotonicCyan
        PriorityTier.TIER_P4_LOW -> OperationalEmerald
    }

    val categoryColor = Color(android.graphics.Color.parseColor(task.category.hexColor))

    val isCompleted = task.status == NeuralTaskStatus.COMPLETED
    val isExecuting = task.status == NeuralTaskStatus.EXECUTING

    val timeStr = remember(task.timestamp) {
        SimpleDateFormat("HH:mm:ss", Locale.US).format(Date(task.timestamp))
    }

    QuantumGlassCard(
        borderColor = if (isCompleted) OperationalEmerald.copy(alpha = 0.5f) else tierColor.copy(alpha = 0.55f),
        backgroundColor = if (isCompleted) SpaceCobaltDark.copy(alpha = 0.85f) else SpaceCobaltGlassElevated,
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_task_${task.id}")
    ) {
        // Top Header: Priority Badge, Task ID, Score Gauge & Timestamp
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Circular Priority Tier Indicator
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(tierColor)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = task.priorityTier.badgeLabel,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = tierColor
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "• ${task.id}",
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = AmbientWhiteSubtle,
                    fontSize = 11.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Priority Score Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(tierColor.copy(alpha = 0.2f))
                        .border(1.dp, tierColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "Score: ${task.priorityScore}/100",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = tierColor,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = timeStr,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextDimmed,
                    fontSize = 10.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Action Title
        Text(
            text = task.actionTitle,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = if (isCompleted) AmbientWhiteMuted else AmbientWhite
        )

        // Raw input description
        Text(
            text = "\"${task.rawActionDescription}\"",
            style = MaterialTheme.typography.bodySmall,
            color = AmbientWhiteMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Category & Target Node Badges
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(categoryColor.copy(alpha = 0.15f))
                    .border(1.dp, categoryColor.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = task.category.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = categoryColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Target Node Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SpaceCobaltSurface)
                    .border(1.dp, SpaceCobaltGlassBorder.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Hub,
                        contentDescription = null,
                        tint = PhotonicCyan,
                        modifier = Modifier.size(10.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = task.targetNode,
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = FontFamily.Monospace,
                        color = PhotonicCyanLight,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Expand / Collapse Details Button
            Text(
                text = if (isExpanded) "Hide Breakdown ▲" else "Score Breakdown ▼",
                style = MaterialTheme.typography.labelSmall,
                color = PhotonicCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable { onToggleExpand() }
                    .padding(4.dp)
                    .testTag("btn_toggle_breakdown_${task.id}")
            )
        }

        // Expandable Intent-Scoring Breakdown Drawer
        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltDark)
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(8.dp))
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "INTENT-SCORING BREAKDOWN MATRIX",
                    style = MaterialTheme.typography.labelSmall,
                    color = PhotonicCyan,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp
                )

                // Multi-variable scoring factors
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ScoreFactorBar(
                        label = "CONFIDENCE",
                        value = "${(task.scoringBreakdown.intentConfidence * 100).toInt()}%",
                        progress = task.scoringBreakdown.intentConfidence,
                        color = OperationalEmerald,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ScoreFactorBar(
                        label = "URGENCY",
                        value = "${String.format(Locale.US, "%.1f", task.scoringBreakdown.urgencyWeight)}/10",
                        progress = task.scoringBreakdown.urgencyWeight / 10f,
                        color = SolarAmber,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    ScoreFactorBar(
                        label = "SECURITY",
                        value = "${String.format(Locale.US, "%.1f", task.scoringBreakdown.securityImpact)}/10",
                        progress = task.scoringBreakdown.securityImpact / 10f,
                        color = ContainmentCrimson,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (task.scoringBreakdown.enclaveRequirement) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = QuantumVioletLight,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Hardware Enclave Gated: +10 Base Bonus Applied",
                            style = MaterialTheme.typography.bodySmall,
                            color = QuantumVioletLight,
                            fontSize = 10.sp
                        )
                    }
                }

                // Interactive Dynamic Urgency Slider to reprioritize this task in real-time!
                if (!isCompleted) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "DYNAMIC REPRIORITIZE (ADJUST URGENCY):",
                                style = MaterialTheme.typography.labelSmall,
                                color = SolarAmber,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${String.format(Locale.US, "%.1f", task.scoringBreakdown.urgencyWeight)}",
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = SolarAmber,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = task.scoringBreakdown.urgencyWeight,
                            onValueChange = { onUrgencyChange(it) },
                            valueRange = 1.0f..10.0f,
                            steps = 18,
                            colors = SliderDefaults.colors(
                                thumbColor = SolarAmber,
                                activeTrackColor = SolarAmber,
                                inactiveTrackColor = SpaceCobaltSurface
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // Execution Log snippet if completed
        if (task.executionLog != null) {
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(OperationalEmeraldDark.copy(alpha = 0.2f))
                    .border(1.dp, OperationalEmerald.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = OperationalEmeraldLight,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = task.executionLog,
                        style = MaterialTheme.typography.bodySmall,
                        fontFamily = FontFamily.Monospace,
                        color = OperationalEmeraldLight,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Card Action Footer: Status & Execution Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isExecuting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = PhotonicCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }
                Text(
                    text = task.status.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = when (task.status) {
                        NeuralTaskStatus.QUEUED -> SolarAmber
                        NeuralTaskStatus.EVALUATING_INTENT -> PhotonicCyan
                        NeuralTaskStatus.EXECUTING -> PhotonicCyanLight
                        NeuralTaskStatus.COMPLETED -> OperationalEmeraldLight
                        NeuralTaskStatus.CANCELLED -> ContainmentCrimson
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                if (!isCompleted && !isExecuting) {
                    OutlinedButton(
                        onClick = onPurge,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = ContainmentCrimson),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(ContainmentCrimson.copy(alpha = 0.5f))
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp).testTag("btn_purge_${task.id}")
                    ) {
                        Text("PURGE", fontSize = 10.sp)
                    }

                    Button(
                        onClick = onExecute,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = tierColor,
                            contentColor = SpaceCobaltDark
                        ),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier.height(32.dp).testTag("btn_execute_${task.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("EXECUTE", fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                } else if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(OperationalEmerald.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "SEALED",
                            style = MaterialTheme.typography.labelSmall,
                            color = OperationalEmeraldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact Factor Bar for breakdown scoring
 */
@Composable
private fun ScoreFactorBar(
    label: String,
    value: String,
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
            Text(text = value, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        LinearProgressIndicator(
            progress = { progress.coerceIn(0f, 1f) },
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp)),
            color = color,
            trackColor = SpaceCobaltSurface
        )
    }
}
