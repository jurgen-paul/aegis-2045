package com.example.neural

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.*

/**
 * Physical classification of intercepted raw user touch gestures.
 */
enum class GesturePhysicalType(
    val title: String,
    val description: String,
    val minimumConfidence: Float
) {
    SWIPE_LEFT("Synaptic Fling Left", "High-velocity lateral drag leftward across the quantum substrate", 0.60f),
    SWIPE_RIGHT("Synaptic Fling Right", "High-velocity lateral drag rightward across the quantum substrate", 0.60f),
    SWIPE_UP("Vertical Ascend (Up)", "Upward vertical elevation swipe toward root security enclave", 0.62f),
    SWIPE_DOWN("Vertical Descend (Down)", "Downward plunge gesture triggering telemetry shield flush", 0.62f),
    PINCH_IN("Axial Pinch In", "Two-finger contraction to collapse view into macro overview", 0.65f),
    PINCH_OUT("Axial Spread Out", "Two-finger expansion into microscopic neural stream inspection", 0.65f),
    LONG_PRESS_CHARGE("Resonance Charge", "Sustained pressure contact accumulating membrane potential", 0.70f),
    DOUBLE_TAP_PULSE("Dual Qubit Burst", "Rapid double-strike injecting hardware entropy seed", 0.55f)
}

/**
 * Raw mathematical vectors captured by the gesture interceptor.
 */
data class GestureVectorData(
    val startOffset: Offset,
    val endOffset: Offset,
    val deltaX: Float,
    val deltaY: Float,
    val velocityX: Float,
    val velocityY: Float,
    val distancePx: Float,
    val durationMs: Long,
    val contactCount: Int = 1,
    val scaleFactor: Float = 1.0f
) {
    val totalVelocity: Float
        get() = kotlin.math.sqrt(velocityX * velocityX + velocityY * velocityY)

    val angleDegrees: Float
        get() = Math.toDegrees(kotlin.math.atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
}

/**
 * Simulated Neural Network Pathways driving navigation or data interaction in AGIS 2045.
 */
enum class NeuralPathwayType(
    val pathwayCode: String,
    val pathwayName: String,
    val corticalSubsystem: String,
    val defaultTargetTab: Int?,
    val defaultColor: Color
) {
    PROPAGATE_FORWARD(
        pathwayCode = "NPATH-FWD-01",
        pathwayName = "Forward Synaptic Propagation",
        corticalSubsystem = "Temporal Motor Cortex",
        defaultTargetTab = null, // Dynamically increments tab
        defaultColor = PhotonicCyan
    ),
    PROPAGATE_BACKWARD(
        pathwayCode = "NPATH-BWD-02",
        pathwayName = "Backward Synaptic Propagation",
        corticalSubsystem = "Parietal Sensory Cortex",
        defaultTargetTab = null, // Dynamically decrements tab
        defaultColor = QuantumVioletLight
    ),
    ELEVATE_ENCLAVE(
        pathwayCode = "NPATH-ENC-05",
        pathwayName = "Root Hardware Enclave Elevation",
        corticalSubsystem = "Cryptographic Thalamus",
        defaultTargetTab = 5, // Navigates to Enclave Vault
        defaultColor = SolarAmber
    ),
    TELEMETRY_PURGE(
        pathwayCode = "NPATH-PURGE-04",
        pathwayName = "Zero-Trust Telemetry Sanitize & Flush",
        corticalSubsystem = "Shield Reflex Arc",
        defaultTargetTab = 4, // Navigates to Shield Pipeline
        defaultColor = ContainmentCrimsonLight
    ),
    MACRO_OVERVIEW(
        pathwayCode = "NPATH-OVR-00",
        pathwayName = "Macro Architecture Dimensional Collapse",
        corticalSubsystem = "Prefrontal Executive",
        defaultTargetTab = 0, // Navigates to Overview
        defaultColor = PhotonicCyanLight
    ),
    DEEP_NEURAL_STREAM(
        pathwayCode = "NPATH-NEU-03",
        pathwayName = "Microscopic Intent Stream Expansion",
        corticalSubsystem = "Cognitive Sub-Layer",
        defaultTargetTab = 3, // Navigates to Neural Command
        defaultColor = PhotonicCyan
    ),
    AUTONOMOUS_VALIDATION(
        pathwayCode = "NPATH-VAL-06",
        pathwayName = "Autonomous Proof Lattice Verification",
        corticalSubsystem = "Entangled Validator Core",
        defaultTargetTab = 6, // Navigates to Validator
        defaultColor = OperationalEmerald
    ),
    ENTROPY_RESEED(
        pathwayCode = "NPATH-ENT-99",
        pathwayName = "Quantum CSPRNG Entropy Injection",
        corticalSubsystem = "Hardware Quantum Pool",
        defaultTargetTab = null, // Mutates entropy data directly
        defaultColor = OperationalEmeraldLight
    )
}

/**
 * Individual synthetic neuron node within a simulated pathway.
 */
data class NeuralPathwayNode(
    val nodeId: String,
    val label: String,
    val layerType: String, // "SENSORY", "HIDDEN", "GATING", "EXECUTIVE"
    val membranePotentialMv: Float, // Resting: -70mV, Threshold: -55mV, Spiking: +35mV
    val synapticWeight: Float, // Connection weight: -1.0 to +1.0
    val isSpiking: Boolean,
    val neurotransmitter: String = "Acetylcholine-Q"
)

/**
 * Full execution trace of a simulated neural pathway activation.
 */
data class SynapticRouteActivation(
    val pathwayType: NeuralPathwayType,
    val triggerGesture: GesturePhysicalType,
    val firingConfidence: Float,
    val synapticLatencyMs: Long,
    val membranePotentialMv: Float,
    val synapticSpikeHz: Float,
    val nodes: List<NeuralPathwayNode>,
    val actionSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Decoded result emitted by the NeuralGestureIntentHandler.
 */
data class DecodedNeuralGestureIntent(
    val gestureType: GesturePhysicalType,
    val pathwayActivation: SynapticRouteActivation,
    val rawVector: GestureVectorData,
    val statusMessage: String
)
