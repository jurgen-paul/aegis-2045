package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.AgisArchitectureConstants
import com.example.model.CyberNode
import com.example.model.CyberNodeRoute
import com.example.model.IntentRiskLevel
import com.example.model.UserActionCategory
import com.example.service.NeuralIntentRouterEngine
import com.example.ui.components.SystemComponentConnection
import com.example.viewmodel.AgisViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class NeuralIntentNodeConnectionGraphTest {

    @Test
    fun testSevenSystemComponentsTopologyIntegrity() {
        val nodes = AgisArchitectureConstants.CYBER_NODES
        assertEquals("Topology must include exactly 7 primary system components", 7, nodes.size)

        val nodeIds = nodes.map { it.id }.toSet()
        val expectedNodes = setOf(
            "NODE_COMPOSERY",
            "NODE_VIEWMODEL",
            "NODE_POLICY_GATE",
            "NODE_ORACLE_SWARM",
            "NODE_ENCLAVE_VAULT",
            "NODE_TELEMETRY_SANITIZER",
            "NODE_BOUNDARY_GATEWAY"
        )
        assertEquals("All expected system component nodes must be present", expectedNodes, nodeIds)

        // Verify coordinate bounds within normalized screen space [0f..1f]
        nodes.forEach { node ->
            assertTrue("Node ${node.id} normalizedX (${node.normalizedX}) must be between 0.0 and 1.0", node.normalizedX in 0f..1f)
            assertTrue("Node ${node.id} normalizedY (${node.normalizedY}) must be between 0.0 and 1.0", node.normalizedY in 0f..1f)
            assertTrue("Tier number must be >= 1", node.tierNumber >= 1)
            assertTrue("Node latency must be positive", node.latencyNs > 0)
        }

        // Hardware enclave assertion
        val enclaveNode = nodes.first { it.id == "NODE_ENCLAVE_VAULT" }
        assertTrue("Enclave node must be flagged as hardware enclave", enclaveNode.isHardwareEnclave)
        assertTrue("Enclave security protocol must mention Kyber or NIST", enclaveNode.securityProtocol.contains("Kyber", ignoreCase = true) || enclaveNode.securityProtocol.contains("NIST", ignoreCase = true))
    }

    @Test
    fun testNeuralIntentRoutesHopValidity() {
        val nodes = AgisArchitectureConstants.CYBER_NODES.associateBy { it.id }
        val routes = AgisArchitectureConstants.STANDARD_NEURAL_ROUTES

        assertTrue("Neural routes list must not be empty", routes.isNotEmpty())

        routes.forEach { route ->
            assertTrue("Route ${route.id} must have at least 2 hops", route.nodeHops.size >= 2)
            route.nodeHops.forEach { hopNodeId ->
                assertTrue("Hop '$hopNodeId' in route ${route.id} must correspond to an existing system component", nodes.containsKey(hopNodeId))
            }
            assertTrue("Cryptographic digest must be present", route.cryptographicDigest.isNotBlank())
            assertTrue("Latency must be positive", route.latencyMs > 0)
        }
    }

    @Test
    fun testSystemComponentConnectionsStructure() {
        val nodes = AgisArchitectureConstants.CYBER_NODES.associateBy { it.id }
        val testConnections = listOf(
            SystemComponentConnection("CONN_1", "NODE_COMPOSERY", "NODE_VIEWMODEL", "Intent Stream Bus", 40.0f, 1),
            SystemComponentConnection("CONN_2", "NODE_COMPOSERY", "NODE_POLICY_GATE", "Hardware Gate Bus", 60.0f, 2),
            SystemComponentConnection("CONN_3", "NODE_VIEWMODEL", "NODE_POLICY_GATE", "State Mutex Lock", 80.0f, 2),
            SystemComponentConnection("CONN_4", "NODE_POLICY_GATE", "NODE_ORACLE_SWARM", "Swarm Dispatch Pipe", 120.0f, 3),
            SystemComponentConnection("CONN_6", "NODE_POLICY_GATE", "NODE_ENCLAVE_VAULT", "Kyber-1024 Lattice Bus", 25.0f, 4),
            SystemComponentConnection("CONN_10", "NODE_TELEMETRY_SANITIZER", "NODE_BOUNDARY_GATEWAY", "DP Perturbed Egress", 75.0f, 4)
        )

        testConnections.forEach { conn ->
            assertTrue("Source node ${conn.sourceNodeId} must exist", nodes.containsKey(conn.sourceNodeId))
            assertTrue("Target node ${conn.targetNodeId} must exist", nodes.containsKey(conn.targetNodeId))
            assertTrue("Bandwidth must be positive", conn.bandwidthGbps > 0f)
            assertTrue("Security tier must be >= 1", conn.securityTier >= 1)
        }
    }

    @Test
    fun testNeuralIntentTaskRoutingTargetMapping() {
        val criticalTask = NeuralIntentRouterEngine.analyzeAndScoreIntent("Emergency Enclave Lockdown & Revoke All Master Keys")
        assertEquals(UserActionCategory.CRITICAL_SECURITY, criticalTask.category)
        assertTrue("Critical task should route to Hardware Enclave", criticalTask.targetNode.contains("Hardware Enclave", ignoreCase = true) || criticalTask.targetNode.contains("eUICC", ignoreCase = true))

        val attestTask = NeuralIntentRouterEngine.analyzeAndScoreIntent("Initiate Biometric StrongBox Attestation")
        assertEquals(UserActionCategory.IDENTITY_ATTESTATION, attestTask.category)
        assertTrue("Attestation task should target StrongBox Keymaster", attestTask.targetNode.contains("StrongBox", ignoreCase = true) || attestTask.targetNode.contains("Keymaster", ignoreCase = true))

        val telemetryTask = NeuralIntentRouterEngine.analyzeAndScoreIntent("Export ECTT Differential Privacy Telemetry Batch")
        assertEquals(UserActionCategory.DATA_TRANSMISSION, telemetryTask.category)
        assertTrue("Telemetry task should target Sanitizer", telemetryTask.targetNode.contains("Sanitizer", ignoreCase = true) || telemetryTask.targetNode.contains("ECTT", ignoreCase = true))
    }

    @Test
    fun testViewModelRouteSelectionAndPacketDispatch() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AgisViewModel(context)

        val initialRoutes = viewModel.activeNeuralRoutes.value
        assertTrue(initialRoutes.isNotEmpty())

        val targetRoute = initialRoutes.first()
        viewModel.selectCyberRoute(targetRoute.id)
        assertEquals(targetRoute.id, viewModel.selectedCyberRouteId.value)

        // Select cyber node inspection
        viewModel.selectCyberNode("NODE_ENCLAVE_VAULT")
        assertEquals("NODE_ENCLAVE_VAULT", viewModel.selectedCyberNodeId.value)

        // Dispatch packet
        viewModel.dispatchNeuralRoutePacket(targetRoute.id)
        assertTrue(viewModel.isRouteSimulationRunning.value)
    }
}
