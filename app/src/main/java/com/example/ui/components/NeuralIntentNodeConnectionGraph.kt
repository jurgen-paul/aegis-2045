package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgisArchitectureConstants
import com.example.model.CyberNode
import com.example.model.CyberNodeRoute
import com.example.model.IntentRiskLevel
import com.example.model.PrioritizedNeuralTask
import com.example.model.UserActionCategory
import com.example.ui.animation.PhotonicSignalPulseIndicator
import com.example.ui.animation.QuantumVolumetricButton
import com.example.ui.theme.*
import kotlin.math.*

/**
 * System Component Connection Definition for Graph Rendering
 */
data class SystemComponentConnection(
    val id: String,
    val sourceNodeId: String,
    val targetNodeId: String,
    val busName: String,
    val bandwidthGbps: Float,
    val securityTier: Int,
    val isPrimaryBus: Boolean = true
)

/**
 * Real-Time Canvas Node Connection Graph
 * Animates neural intent routing traffic between system components using
 * high-performance hardware-accelerated Canvas rendering, cubic Bézier photonic paths,
 * dynamic traveling photon particles, and interactive touch node inspection.
 */
@Composable
fun NeuralIntentNodeConnectionGraph(
    cyberNodes: List<CyberNode> = AgisArchitectureConstants.CYBER_NODES,
    routes: List<CyberNodeRoute> = AgisArchitectureConstants.STANDARD_NEURAL_ROUTES,
    activeTasks: List<PrioritizedNeuralTask> = emptyList(),
    selectedRouteId: String? = null,
    selectedNodeId: String? = null,
    onSelectRoute: ((String) -> Unit)? = null,
    onSelectNode: ((String?) -> Unit)? = null,
    onDispatchRouteIntent: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var internalSelectedRouteId by remember(selectedRouteId) {
        mutableStateOf(selectedRouteId ?: routes.firstOrNull()?.id ?: "ROUTE_ZERO_TRUST_ATTEST")
    }
    var internalSelectedNodeId by remember(selectedNodeId) {
        mutableStateOf(selectedNodeId)
    }
    var isBurstTrafficActive by remember { mutableStateOf(false) }
    var burstCounter by remember { mutableIntStateOf(0) }
    var filterCategory by remember { mutableStateOf("ALL") }

    val activeRoute = remember(routes, internalSelectedRouteId) {
        routes.firstOrNull { it.id == internalSelectedRouteId } ?: routes.firstOrNull()
    }

    val selectedNode = remember(cyberNodes, internalSelectedNodeId) {
        cyberNodes.firstOrNull { it.id == internalSelectedNodeId }
    }

    // High-performance continuous animation loop for photon packets and wave resonance
    val infiniteTransition = rememberInfiniteTransition(label = "NeuralIntentTrafficAnimation")

    // Continuous 0..1 phase for standard particle flow
    val trafficPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isBurstTrafficActive) 1400 else 2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "TrafficPhase"
    )

    // Secondary phase for reverse attestation heartbeats
    val reversePhase by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "ReversePhase"
    )

    // Wave harmonic oscillation for living dynamic graph curvature
    val harmonicAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "HarmonicAngle"
    )

    // Pulse expander for active node halos
    val pulseHaloScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.45f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseHaloScale"
    )

    // Predefined high-bandwidth system component connection links
    val systemConnections = remember {
        listOf(
            SystemComponentConnection("CONN_1", "NODE_COMPOSERY", "NODE_VIEWMODEL", "Intent Stream Bus", 40.0f, 1),
            SystemComponentConnection("CONN_2", "NODE_COMPOSERY", "NODE_POLICY_GATE", "Hardware Gate Bus", 60.0f, 2),
            SystemComponentConnection("CONN_3", "NODE_VIEWMODEL", "NODE_POLICY_GATE", "State Mutex Lock", 80.0f, 2),
            SystemComponentConnection("CONN_4", "NODE_POLICY_GATE", "NODE_ORACLE_SWARM", "Swarm Dispatch Pipe", 120.0f, 3),
            SystemComponentConnection("CONN_5", "NODE_VIEWMODEL", "NODE_ORACLE_SWARM", "Agent Task Channel", 50.0f, 3),
            SystemComponentConnection("CONN_6", "NODE_POLICY_GATE", "NODE_ENCLAVE_VAULT", "Kyber-1024 Lattice Bus", 25.0f, 4),
            SystemComponentConnection("CONN_7", "NODE_ORACLE_SWARM", "NODE_ENCLAVE_VAULT", "Sub-Agent Attestation", 35.0f, 4),
            SystemComponentConnection("CONN_8", "NODE_VIEWMODEL", "NODE_TELEMETRY_SANITIZER", "Raw Telemetry Ingress", 45.0f, 3),
            SystemComponentConnection("CONN_9", "NODE_ORACLE_SWARM", "NODE_TELEMETRY_SANITIZER", "Agent Log Scrub", 30.0f, 3),
            SystemComponentConnection("CONN_10", "NODE_TELEMETRY_SANITIZER", "NODE_BOUNDARY_GATEWAY", "DP Perturbed Egress", 75.0f, 4),
            SystemComponentConnection("CONN_11", "NODE_ENCLAVE_VAULT", "NODE_BOUNDARY_GATEWAY", "PQ-Signed Proof Tunnel", 90.0f, 5)
        )
    }

    // Dynamic metrics based on active tasks & burst state
    val computedPacketsPerSec = remember(activeTasks.size, isBurstTrafficActive, burstCounter) {
        val base = 142 + (activeTasks.size * 18)
        if (isBurstTrafficActive) base * 3 else base
    }

    val activeHops = remember(activeRoute) {
        activeRoute?.nodeHops ?: emptyList()
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("neural_intent_node_connection_graph_container"),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Header & Real-Time Control Card
        QuantumGlassCard(
            borderColor = PhotonicCyan.copy(alpha = 0.55f),
            backgroundColor = SpaceCobaltGlassElevated
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PhotonicSignalPulseIndicator(
                        signalColor = if (isBurstTrafficActive) SolarAmber else OperationalEmerald,
                        size = 12.dp,
                        pulseSpeedMs = if (isBurstTrafficActive) 500 else 1100
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "REAL-TIME NODE CONNECTION GRAPH",
                                style = MaterialTheme.typography.labelSmall,
                                color = PhotonicCyan,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            if (isBurstTrafficActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• BURST ACTIVE",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SolarAmber,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp
                                )
                            }
                        }
                        Text(
                            text = "Neural Intent Routing Traffic Canvas",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AmbientWhite
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    PhotonicBadge(
                        text = "$computedPacketsPerSec PKTS/SEC",
                        signalColor = if (isBurstTrafficActive) SolarAmber else OperationalEmerald,
                        icon = Icons.Default.Speed
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Real-Time Graph Telemetry HUD
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SpaceCobaltDark.copy(alpha = 0.9f))
                    .border(1.dp, SpaceCobaltGlassBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("ACTIVE ROUTE", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 9.sp)
                    Text(
                        text = activeRoute?.name ?: "Attestation Mesh",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = when (activeRoute?.riskLevel) {
                            IntentRiskLevel.RESTRICTED -> SolarAmber
                            IntentRiskLevel.ISOLATED -> ContainmentCrimson
                            IntentRiskLevel.ELEVATED -> QuantumVioletLight
                            else -> OperationalEmeraldLight
                        },
                        maxLines = 1
                    )
                }
                Column {
                    Text("HOPS", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 9.sp)
                    Text(
                        text = "${activeHops.size} Nodes",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = PhotonicCyanLight
                    )
                }
                Column {
                    Text("EST. LATENCY", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 9.sp)
                    Text(
                        text = "${activeRoute?.latencyMs ?: 4} ms",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmbientWhite
                    )
                }
                Column {
                    Text("COHERENCE", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 9.sp)
                    Text(
                        text = "99.98%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = OperationalEmeraldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Route Quick Selector Tabs
            Text(
                text = "DISPATCH NEURAL INTENT ROUTE:",
                style = MaterialTheme.typography.labelSmall,
                color = AmbientWhiteMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                routes.forEach { route ->
                    val isSelected = route.id == activeRoute?.id
                    val routeColor = when (route.riskLevel) {
                        IntentRiskLevel.RESTRICTED -> SolarAmber
                        IntentRiskLevel.ISOLATED -> ContainmentCrimson
                        IntentRiskLevel.ELEVATED -> QuantumVioletLight
                        else -> OperationalEmeraldLight
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) routeColor.copy(alpha = 0.22f) else SpaceCobaltSurface)
                            .border(
                                1.dp,
                                if (isSelected) routeColor else SpaceCobaltGlassBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable {
                                internalSelectedRouteId = route.id
                                onSelectRoute?.invoke(route.id)
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("btn_route_${route.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(routeColor)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = route.name,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) AmbientWhite else TextDimmed,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // The High-Performance Canvas Node Connection Graph Surface
        QuantumGlassCard(
            borderColor = OperationalEmerald.copy(alpha = 0.5f),
            backgroundColor = SpaceCobaltGlass
        ) {
            // Interactive Controls Bar directly above canvas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = "Live Canvas",
                        tint = OperationalEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "SYNCHRONIC TOPOLOGY MESH",
                        style = MaterialTheme.typography.labelSmall,
                        color = OperationalEmeraldLight,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    // Trigger Intent Packet Burst
                    Button(
                        onClick = {
                            isBurstTrafficActive = !isBurstTrafficActive
                            burstCounter++
                            activeRoute?.let { onDispatchRouteIntent?.invoke(it.id) }
                        },
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("btn_trigger_traffic_burst"),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBurstTrafficActive) SolarAmberDark else SpaceCobaltCard
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Icon(
                            imageVector = if (isBurstTrafficActive) Icons.Default.Bolt else Icons.Default.PlayArrow,
                            contentDescription = "Burst",
                            tint = if (isBurstTrafficActive) SolarAmber else PhotonicCyanLight,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isBurstTrafficActive) "BURST ON" else "PULSE BURST",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isBurstTrafficActive) SolarAmber else PhotonicCyanLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp
                        )
                    }

                    // Reset Selection / Clear
                    if (internalSelectedNodeId != null) {
                        OutlinedButton(
                            onClick = {
                                internalSelectedNodeId = null
                                onSelectNode?.invoke(null)
                            },
                            modifier = Modifier.height(28.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AmbientWhiteMuted)
                        ) {
                            Text("CLEAR", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // The Interactive Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(SpaceCobaltDark.copy(alpha = 0.95f))
                    .border(1.dp, SpaceCobaltGlassBorder, RoundedCornerShape(14.dp))
                    .testTag("canvas_neural_intent_node_graph")
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(cyberNodes) {
                            detectTapGestures { tapOffset ->
                                val width = size.width
                                val height = size.height

                                // Check which node was tapped within hit-test radius
                                var hitNodeId: String? = null
                                for (node in cyberNodes) {
                                    val nodeX = node.normalizedX * width
                                    val nodeY = node.normalizedY * height
                                    val distance = sqrt((tapOffset.x - nodeX).pow(2) + (tapOffset.y - nodeY).pow(2))
                                    if (distance <= 32.dp.toPx()) {
                                        hitNodeId = node.id
                                        break
                                    }
                                }

                                internalSelectedNodeId = if (internalSelectedNodeId == hitNodeId) null else hitNodeId
                                onSelectNode?.invoke(internalSelectedNodeId)
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height

                    // 1. Draw subtle background Cartesian grid lines
                    val gridSpacing = 28.dp.toPx()
                    val gridPaintColor = SpaceCobaltGlassBorder.copy(alpha = 0.25f)
                    var x = 0f
                    while (x < width) {
                        drawLine(
                            color = gridPaintColor,
                            start = Offset(x, 0f),
                            end = Offset(x, height),
                            strokeWidth = 0.6.dp.toPx()
                        )
                        x += gridSpacing
                    }
                    var y = 0f
                    while (y < height) {
                        drawLine(
                            color = gridPaintColor,
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 0.6.dp.toPx()
                        )
                        y += gridSpacing
                    }

                    // Map all CyberNodes to screen pixel Offsets
                    val nodeCoordinates = cyberNodes.associate { node ->
                        node.id to Offset(node.normalizedX * width, node.normalizedY * height)
                    }

                    // 2. Draw Bus Connections (Bézier Curves between connected components)
                    systemConnections.forEach { conn ->
                        val p1 = nodeCoordinates[conn.sourceNodeId]
                        val p2 = nodeCoordinates[conn.targetNodeId]

                        if (p1 != null && p2 != null) {
                            // Check if this connection is part of the currently active neural intent route
                            val isRouteSegment = activeRoute?.nodeHops?.let { hops ->
                                val srcIndex = hops.indexOf(conn.sourceNodeId)
                                val tgtIndex = hops.indexOf(conn.targetNodeId)
                                (srcIndex != -1 && tgtIndex != -1 && abs(srcIndex - tgtIndex) == 1)
                            } ?: false

                            // Check if connected to selected node
                            val isConnectedToSelected = internalSelectedNodeId != null &&
                                    (conn.sourceNodeId == internalSelectedNodeId || conn.targetNodeId == internalSelectedNodeId)

                            // Dynamic Bézier curvature calculation
                            val midX = (p1.x + p2.x) / 2f
                            val midY = (p1.y + p2.y) / 2f
                            val dx = p2.x - p1.x
                            val dy = p2.y - p1.y
                            val dist = sqrt(dx * dx + dy * dy)
                            val normalX = -dy / dist
                            val normalY = dx / dist

                            // Subtle breathing oscillation on the curve
                            val waveOffset = sin(harmonicAngle + conn.id.hashCode() % 5) * 8.dp.toPx()
                            val controlPoint = Offset(midX + normalX * (16.dp.toPx() + waveOffset), midY + normalY * (16.dp.toPx() + waveOffset))

                            val curvePath = Path().apply {
                                moveTo(p1.x, p1.y)
                                quadraticTo(controlPoint.x, controlPoint.y, p2.x, p2.y)
                            }

                            // Edge colors
                            val edgeColor = when {
                                isRouteSegment -> when (activeRoute?.riskLevel) {
                                    IntentRiskLevel.RESTRICTED -> SolarAmber
                                    IntentRiskLevel.ISOLATED -> ContainmentCrimson
                                    IntentRiskLevel.ELEVATED -> QuantumVioletLight
                                    else -> OperationalEmeraldLight
                                }
                                isConnectedToSelected -> PhotonicCyan
                                else -> SpaceCobaltGlassBorder.copy(alpha = 0.45f)
                            }

                            // Glow halo for active route segments
                            if (isRouteSegment || isConnectedToSelected) {
                                drawPath(
                                    path = curvePath,
                                    color = edgeColor.copy(alpha = 0.28f),
                                    style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            // Core edge stroke
                            drawPath(
                                path = curvePath,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        edgeColor.copy(alpha = if (isRouteSegment) 0.95f else 0.4f),
                                        edgeColor.copy(alpha = if (isRouteSegment) 0.75f else 0.25f)
                                    ),
                                    start = p1,
                                    end = p2
                                ),
                                style = Stroke(
                                    width = if (isRouteSegment) 2.6.dp.toPx() else 1.2.dp.toPx(),
                                    cap = StrokeCap.Round,
                                    pathEffect = if (!isRouteSegment) PathEffect.dashPathEffect(floatArrayOf(8f, 6f), trafficPhase * 20f) else null
                                )
                            )

                            // 3. Traveling Photon Traffic Packets along the Bézier curve
                            if (isRouteSegment || isConnectedToSelected || conn.isPrimaryBus) {
                                val packetCount = if (isBurstTrafficActive) 3 else 2
                                for (pIndex in 0 until packetCount) {
                                    val offsetFraction = (trafficPhase + (pIndex.toFloat() / packetCount.toFloat()) + (conn.id.hashCode() % 10) * 0.1f) % 1.0f

                                    // Quadratic Bézier Point: B(t) = (1-t)^2 P0 + 2(1-t)t P1 + t^2 P2
                                    val t = offsetFraction
                                    val px = (1 - t) * (1 - t) * p1.x + 2 * (1 - t) * t * controlPoint.x + t * t * p2.x
                                    val py = (1 - t) * (1 - t) * p1.y + 2 * (1 - t) * t * controlPoint.y + t * t * p2.y
                                    val packetCenter = Offset(px, py)

                                    // Dynamic packet color shifts based on node transit
                                    val packetColor = when {
                                        isRouteSegment -> when (activeRoute?.riskLevel) {
                                            IntentRiskLevel.RESTRICTED -> SolarAmber
                                            IntentRiskLevel.ISOLATED -> ContainmentCrimson
                                            IntentRiskLevel.ELEVATED -> QuantumVioletLight
                                            else -> OperationalEmeraldLight
                                        }
                                        conn.sourceNodeId == "NODE_COMPOSERY" -> PhotonicCyan
                                        conn.targetNodeId == "NODE_ENCLAVE_VAULT" -> QuantumVioletLight
                                        conn.targetNodeId == "NODE_TELEMETRY_SANITIZER" -> OperationalEmerald
                                        else -> PhotonicCyanLight
                                    }

                                    // Packet outer glow
                                    drawCircle(
                                        color = packetColor.copy(alpha = 0.45f),
                                        radius = (if (isBurstTrafficActive) 7.dp else 5.dp).toPx(),
                                        center = packetCenter
                                    )

                                    // Specular core dot
                                    drawCircle(
                                        color = AmbientWhite,
                                        radius = (if (isBurstTrafficActive) 3.2.dp else 2.4.dp).toPx(),
                                        center = packetCenter
                                    )

                                    // Comet-tail particle effect
                                    val tailT = (t - 0.04f).coerceAtLeast(0f)
                                    val tx = (1 - tailT) * (1 - tailT) * p1.x + 2 * (1 - tailT) * tailT * controlPoint.x + tailT * tailT * p2.x
                                    val ty = (1 - tailT) * (1 - tailT) * p1.y + 2 * (1 - tailT) * tailT * controlPoint.y + tailT * tailT * p2.y
                                    drawCircle(
                                        color = packetColor.copy(alpha = 0.25f),
                                        radius = 2.dp.toPx(),
                                        center = Offset(tx, ty)
                                    )
                                }
                            }
                        }
                    }

                    // 4. Draw System Component Nodes
                    cyberNodes.forEach { node ->
                        val center = nodeCoordinates[node.id] ?: return@forEach
                        val isSelected = node.id == internalSelectedNodeId
                        val isInActiveRoute = activeHops.contains(node.id)
                        val hopIndex = activeHops.indexOf(node.id)

                        // Base node accent color
                        val nodeColor = when {
                            node.isHardwareEnclave -> QuantumVioletLight
                            node.id == "NODE_COMPOSERY" -> PhotonicCyan
                            node.id == "NODE_POLICY_GATE" -> if (activeRoute?.riskLevel == IntentRiskLevel.RESTRICTED) SolarAmber else ContainmentCrimsonLight
                            node.id == "NODE_TELEMETRY_SANITIZER" -> OperationalEmerald
                            node.id == "NODE_ORACLE_SWARM" -> PhotonicCyanLight
                            else -> OperationalEmeraldLight
                        }

                        // Radius sizes
                        val nodeRadius = (if (isSelected) 17.dp else if (isInActiveRoute) 15.dp else 13.dp).toPx()

                        // A. Radiating pulse wave halo when selected or active in route
                        if (isSelected || isInActiveRoute) {
                            val haloRadius = nodeRadius * pulseHaloScale
                            val haloAlpha = ((1.45f - pulseHaloScale) / 0.45f).coerceIn(0f, 1f) * 0.45f
                            drawCircle(
                                color = nodeColor.copy(alpha = haloAlpha),
                                radius = haloRadius,
                                center = center
                            )
                        }

                        // B. Node dark cobalt background fill
                        drawCircle(
                            color = SpaceCobaltSurface,
                            radius = nodeRadius,
                            center = center
                        )

                        // C. Tier level or load radial indicator ring
                        val loadSweepAngle = node.activeLoad * 360f
                        drawArc(
                            color = nodeColor,
                            startAngle = -90f,
                            sweepAngle = loadSweepAngle,
                            useCenter = false,
                            topLeft = Offset(center.x - nodeRadius, center.y - nodeRadius),
                            size = androidx.compose.ui.geometry.Size(nodeRadius * 2, nodeRadius * 2),
                            style = Stroke(width = (if (isSelected) 3.dp else 2.dp).toPx())
                        )

                        // D. Unfilled ring background
                        drawCircle(
                            color = SpaceCobaltGlassBorder.copy(alpha = 0.5f),
                            radius = nodeRadius,
                            center = center,
                            style = Stroke(width = 1.dp.toPx())
                        )

                        // E. Specular Core Center Dot
                        drawCircle(
                            color = if (isSelected) AmbientWhite else nodeColor,
                            radius = (if (isSelected) 5.dp else 4.dp).toPx(),
                            center = center
                        )

                        // F. Hop order badge if in active route
                        if (isInActiveRoute && hopIndex != -1) {
                            val badgeOffset = Offset(center.x + nodeRadius * 0.7f, center.y - nodeRadius * 0.7f)
                            drawCircle(
                                color = nodeColor,
                                radius = 6.dp.toPx(),
                                center = badgeOffset
                            )
                            drawCircle(
                                color = SpaceCobaltDark,
                                radius = 4.dp.toPx(),
                                center = badgeOffset
                            )
                        }
                    }
                }

                // 5. Compose Text Label Overlays (Positioned relative to nodes)
                cyberNodes.forEach { node ->
                    val isSelected = node.id == internalSelectedNodeId
                    val isInActiveRoute = activeHops.contains(node.id)

                    val labelColor = when {
                        isSelected -> AmbientWhite
                        node.isHardwareEnclave -> QuantumVioletLight
                        node.id == "NODE_COMPOSERY" -> PhotonicCyanLight
                        isInActiveRoute -> OperationalEmeraldLight
                        else -> AmbientWhiteMuted
                    }

                    // Strategic label placement (left, center, or right)
                    val alignHoriz = when {
                        node.normalizedX < 0.35f -> Alignment.Start
                        node.normalizedX > 0.65f -> Alignment.End
                        else -> Alignment.CenterHorizontally
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = (node.normalizedX * 260).dp.coerceIn(6.dp, 220.dp),
                                top = (node.normalizedY * 200).dp.coerceIn(8.dp, 240.dp)
                            )
                    ) {
                        Column(
                            horizontalAlignment = alignHoriz,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(SpaceCobaltDark.copy(alpha = 0.85f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = node.shortLabel,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected || isInActiveRoute) FontWeight.ExtraBold else FontWeight.SemiBold,
                                color = labelColor,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                            Text(
                                text = "T${node.tierNumber} • ${(node.activeLoad * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextDimmed,
                                fontSize = 8.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Active Route Sequence Hop Breadcrumbs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "HOP PATH:",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextDimmed,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )

                activeHops.forEachIndexed { index, hopNodeId ->
                    val matchingNode = cyberNodes.firstOrNull { it.id == hopNodeId }
                    val label = matchingNode?.shortLabel ?: hopNodeId.removePrefix("NODE_")
                    val isSelectedNode = hopNodeId == internalSelectedNodeId

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelectedNode) PhotonicCyan.copy(alpha = 0.25f) else SpaceCobaltSurface)
                            .border(1.dp, if (isSelectedNode) PhotonicCyan else SpaceCobaltGlassBorder, RoundedCornerShape(4.dp))
                            .clickable {
                                internalSelectedNodeId = hopNodeId
                                onSelectNode?.invoke(hopNodeId)
                            }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${index + 1}. $label",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelectedNode) PhotonicCyanLight else AmbientWhite,
                            fontWeight = if (isSelectedNode) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 9.sp
                        )
                    }

                    if (index < activeHops.size - 1) {
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Next Hop",
                            tint = OperationalEmerald.copy(alpha = 0.8f),
                            modifier = Modifier.size(10.dp)
                        )
                    }
                }
            }
        }

        // Selected Node Telemetry Inspector Card (Revealed on node click)
        AnimatedVisibility(
            visible = selectedNode != null,
            enter = fadeIn() + androidx.compose.animation.expandVertically(),
            exit = fadeOut() + androidx.compose.animation.shrinkVertically()
        ) {
            selectedNode?.let { node ->
                QuantumGlassCard(
                    borderColor = if (node.isHardwareEnclave) QuantumVioletLight else PhotonicCyan,
                    backgroundColor = SpaceCobaltGlassElevated
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (node.isHardwareEnclave) QuantumVioletDark else SpaceCobaltSurface)
                                    .border(1.dp, if (node.isHardwareEnclave) QuantumVioletLight else PhotonicCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (node.isHardwareEnclave) Icons.Default.Shield else Icons.Default.Hub,
                                    contentDescription = "Node Tier",
                                    tint = if (node.isHardwareEnclave) QuantumVioletLight else PhotonicCyanLight,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "TIER ${node.tierNumber}: ${node.tierLabel.uppercase()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (node.isHardwareEnclave) QuantumVioletLight else PhotonicCyan,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = node.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = AmbientWhite
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                internalSelectedNodeId = null
                                onSelectNode?.invoke(null)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Inspector",
                                tint = TextDimmed,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = node.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = AmbientWhiteMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Inspection Metrics Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SpaceCobaltDark)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("SECURITY PROTOCOL", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                            Text(
                                text = node.securityProtocol,
                                style = MaterialTheme.typography.labelSmall,
                                color = AmbientWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                        Column {
                            Text("LATENCY", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                            Text(
                                text = "${node.latencyNs} ns",
                                style = MaterialTheme.typography.labelSmall,
                                color = OperationalEmeraldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                        Column {
                            Text("ACTIVE LOAD", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                            Text(
                                text = "${(node.activeLoad * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (node.activeLoad > 0.7f) SolarAmber else OperationalEmeraldLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                        Column {
                            Text("HARDWARE ENCLAVE", style = MaterialTheme.typography.labelSmall, color = TextDimmed, fontSize = 8.sp)
                            Text(
                                text = if (node.isHardwareEnclave) "SEALED eUICC" else "LOGICAL SANDBOX",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (node.isHardwareEnclave) QuantumVioletLight else AmbientWhiteMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
