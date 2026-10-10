package com.example.neural

import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.*
import kotlin.random.Random

/**
 * ViewModel-based Neural Intent Handler.
 *
 * Intercepts physical user gestures and maps them through simulated multi-layer
 * neural pathways (Sensory Receptors -> Cortical Feature Map -> Synaptic Gating -> Executive Motor Action)
 * to drive navigation or trigger real-time zero-trust data interactions in AGIS 2045.
 */
class NeuralGestureIntentHandler(
    private val coroutineScope: CoroutineScope,
    val hapticManager: NeuralHapticFeedbackManager? = null,
    private val onNavigateToTab: (Int) -> Unit,
    private val onNavigateRelative: (Int) -> Unit,
    private val onPurgeTelemetry: () -> Unit,
    private val onTriggerValidation: () -> Unit,
    private val onReseedEntropy: () -> Unit,
    private val onAlert: (String) -> Unit
) {

    // Active firing synaptic pathway
    private val _activePathway = MutableStateFlow<SynapticRouteActivation?>(null)
    val activePathway: StateFlow<SynapticRouteActivation?> = _activePathway.asStateFlow()

    // Last successfully decoded intent
    private val _lastDecodedIntent = MutableStateFlow<DecodedNeuralGestureIntent?>(null)
    val lastDecodedIntent: StateFlow<DecodedNeuralGestureIntent?> = _lastDecodedIntent.asStateFlow()

    // History of synaptic pathway firings
    private val _pathwayHistory = MutableStateFlow<List<SynapticRouteActivation>>(emptyList())
    val pathwayHistory: StateFlow<List<SynapticRouteActivation>> = _pathwayHistory.asStateFlow()

    // Real-time gesture trail offsets for HUD rendering
    private val _gestureTrail = MutableStateFlow<List<Offset>>(emptyList())
    val gestureTrail: StateFlow<List<Offset>> = _gestureTrail.asStateFlow()

    // State tracking whether gesture interception is currently active
    private val _isGestureActive = MutableStateFlow(false)
    val isGestureActive: StateFlow<Boolean> = _isGestureActive.asStateFlow()

    // Operational configuration
    private val _isAutoNavigationEnabled = MutableStateFlow(true)
    val isAutoNavigationEnabled: StateFlow<Boolean> = _isAutoNavigationEnabled.asStateFlow()

    private val _sensitivityMultiplier = MutableStateFlow(1.0f)
    val sensitivityMultiplier: StateFlow<Float> = _sensitivityMultiplier.asStateFlow()

    // Simulated 4-layer neural network synaptic weights
    private val sensoryWeights = floatArrayOf(0.85f, 0.92f, 0.78f, 0.88f)
    private val corticalBiases = floatArrayOf(0.12f, -0.05f, 0.08f, 0.15f)

    /**
     * Intercepts a raw gesture vector and maps it through the simulated neural network.
     */
    fun processGestureVector(vector: GestureVectorData) {
        val sensitivity = _sensitivityMultiplier.value
        val effectiveVelocity = vector.totalVelocity * sensitivity
        val effectiveDistance = vector.distancePx * sensitivity

        // Classify physical gesture using vector dynamics
        val gesture = classifyGesture(vector, effectiveVelocity, effectiveDistance) ?: return

        // Propagate signal through simulated neural pathway
        coroutineScope.launch {
            val activation = simulateNeuralPathway(gesture, vector)
            _activePathway.value = activation
            _lastDecodedIntent.value = DecodedNeuralGestureIntent(
                gestureType = gesture,
                pathwayActivation = activation,
                rawVector = vector,
                statusMessage = "Synaptic threshold exceeded: ${activation.actionSummary}"
            )

            // Append to rolling history (keep latest 15)
            val updated = (listOf(activation) + _pathwayHistory.value).take(15)
            _pathwayHistory.value = updated

            // Trigger subtle, high-fidelity haptic feedback for the mapped pathway
            hapticManager?.triggerPathwayHaptic(activation.pathwayType, activation.firingConfidence)

            // Dispatch executive motor action (Navigation or Data Interaction)
            executeMotorAction(activation)
        }
    }

    /**
     * Manually triggers a specific neural pathway (useful for UI testing & direct HUD triggers).
     */
    fun triggerSimulatedPathway(pathwayType: NeuralPathwayType) {
        coroutineScope.launch {
            val gesture = when (pathwayType) {
                NeuralPathwayType.PROPAGATE_FORWARD -> GesturePhysicalType.SWIPE_LEFT
                NeuralPathwayType.PROPAGATE_BACKWARD -> GesturePhysicalType.SWIPE_RIGHT
                NeuralPathwayType.ELEVATE_ENCLAVE -> GesturePhysicalType.SWIPE_UP
                NeuralPathwayType.TELEMETRY_PURGE -> GesturePhysicalType.SWIPE_DOWN
                NeuralPathwayType.MACRO_OVERVIEW -> GesturePhysicalType.PINCH_IN
                NeuralPathwayType.DEEP_NEURAL_STREAM -> GesturePhysicalType.PINCH_OUT
                NeuralPathwayType.AUTONOMOUS_VALIDATION -> GesturePhysicalType.LONG_PRESS_CHARGE
                NeuralPathwayType.ENTROPY_RESEED -> GesturePhysicalType.DOUBLE_TAP_PULSE
            }

            val dummyVector = GestureVectorData(
                startOffset = Offset(200f, 400f),
                endOffset = Offset(450f, 400f),
                deltaX = 250f,
                deltaY = 0f,
                velocityX = 1200f,
                velocityY = 0f,
                distancePx = 250f,
                durationMs = 120L
            )

            val activation = simulateNeuralPathway(gesture, dummyVector, overridePathway = pathwayType)
            _activePathway.value = activation
            _lastDecodedIntent.value = DecodedNeuralGestureIntent(
                gestureType = gesture,
                pathwayActivation = activation,
                rawVector = dummyVector,
                statusMessage = "Manual synaptic injection: ${activation.actionSummary}"
            )

            val updated = (listOf(activation) + _pathwayHistory.value).take(15)
            _pathwayHistory.value = updated

            // Trigger haptic feedback for simulated pathway
            hapticManager?.triggerPathwayHaptic(activation.pathwayType, activation.firingConfidence)

            executeMotorAction(activation)
        }
    }

    fun updateGestureTrail(points: List<Offset>) {
        _gestureTrail.value = points
    }

    fun setGestureActive(active: Boolean) {
        _isGestureActive.value = active
        if (active) {
            hapticManager?.triggerThresholdTick()
        } else {
            _gestureTrail.value = emptyList()
        }
    }

    fun setAutoNavigationEnabled(enabled: Boolean) {
        _isAutoNavigationEnabled.value = enabled
    }

    fun setSensitivityMultiplier(multiplier: Float) {
        _sensitivityMultiplier.value = multiplier.coerceIn(0.5f, 2.5f)
    }

    fun clearActivePathway() {
        _activePathway.value = null
    }

    /**
     * Mathematical classification of the raw gesture vector into a discrete physical gesture.
     */
    private fun classifyGesture(
        v: GestureVectorData,
        velocity: Float,
        distance: Float
    ): GesturePhysicalType? {
        // Multi-touch scale gestures (Pinch In / Pinch Out)
        if (v.contactCount >= 2 || v.scaleFactor != 1.0f) {
            return if (v.scaleFactor < 0.85f) GesturePhysicalType.PINCH_IN
            else if (v.scaleFactor > 1.15f) GesturePhysicalType.PINCH_OUT
            else null
        }

        // Long Press Charge (Low movement, long contact duration)
        if (distance < 30f && v.durationMs >= 480L) {
            return GesturePhysicalType.LONG_PRESS_CHARGE
        }

        // Tap Burst
        if (distance < 25f && v.durationMs < 200L && v.contactCount == 1) {
            return GesturePhysicalType.DOUBLE_TAP_PULSE
        }

        // High-velocity or sufficient distance directional swipes
        if (distance > 45f || velocity > 300f) {
            val absX = abs(v.deltaX)
            val absY = abs(v.deltaY)

            return if (absX > absY) {
                if (v.deltaX < 0) GesturePhysicalType.SWIPE_LEFT else GesturePhysicalType.SWIPE_RIGHT
            } else {
                if (v.deltaY < 0) GesturePhysicalType.SWIPE_UP else GesturePhysicalType.SWIPE_DOWN
            }
        }

        return null
    }

    /**
     * Simulates feed-forward and lateral propagation through synthetic spiking neurons.
     */
    private fun simulateNeuralPathway(
        gesture: GesturePhysicalType,
        vector: GestureVectorData,
        overridePathway: NeuralPathwayType? = null
    ): SynapticRouteActivation {
        val pathway = overridePathway ?: mapGestureToPathway(gesture)

        // Calculate simulated membrane potential ($V_m$) and firing frequency
        val speedFactor = (vector.totalVelocity / 1500f).coerceIn(0.1f, 1.0f)
        val membranePotential = (-55f + (speedFactor * 90f)).coerceIn(-50f, 38f) // spikes to +35mV
        val spikeHz = (25f + speedFactor * 115f)
        val latencyMs = (16L + ((1f - speedFactor) * 26L).toLong()).coerceIn(12L, 45L)

        // Confidence calculation based on gesture alignment and velocity
        val baseConfidence = gesture.minimumConfidence
        val confidence = (baseConfidence + (speedFactor * 0.32f)).coerceIn(0.72f, 0.99f)

        // Generate synthetic pathway nodes for visualization
        val nodes = listOf(
            NeuralPathwayNode(
                nodeId = "IN-SENS-${gesture.name.take(3)}",
                label = "Sensory Receptor (${gesture.title})",
                layerType = "SENSORY",
                membranePotentialMv = membranePotential * 0.8f,
                synapticWeight = sensoryWeights[0],
                isSpiking = true,
                neurotransmitter = "Dopamine-S"
            ),
            NeuralPathwayNode(
                nodeId = "HID-COR-${pathway.name.take(3)}",
                label = "Cortical Feature (${pathway.corticalSubsystem})",
                layerType = "HIDDEN",
                membranePotentialMv = membranePotential * 0.95f,
                synapticWeight = sensoryWeights[1],
                isSpiking = true,
                neurotransmitter = "Acetylcholine-Q"
            ),
            NeuralPathwayNode(
                nodeId = "GATE-ZTRUST",
                label = "Invariant Zero-Trust Gate (θ > 0.65)",
                layerType = "GATING",
                membranePotentialMv = 32.5f,
                synapticWeight = sensoryWeights[2],
                isSpiking = true,
                neurotransmitter = "GABA-Photonic"
            ),
            NeuralPathwayNode(
                nodeId = "OUT-EXEC",
                label = "Executive Motor Dispatch (${pathway.pathwayCode})",
                layerType = "EXECUTIVE",
                membranePotentialMv = 36.2f,
                synapticWeight = sensoryWeights[3],
                isSpiking = true,
                neurotransmitter = "Noradrenaline-M"
            )
        )

        val actionSummary = getActionSummary(pathway)

        return SynapticRouteActivation(
            pathwayType = pathway,
            triggerGesture = gesture,
            firingConfidence = confidence,
            synapticLatencyMs = latencyMs,
            membranePotentialMv = membranePotential,
            synapticSpikeHz = spikeHz,
            nodes = nodes,
            actionSummary = actionSummary
        )
    }

    private fun mapGestureToPathway(gesture: GesturePhysicalType): NeuralPathwayType {
        return when (gesture) {
            GesturePhysicalType.SWIPE_LEFT -> NeuralPathwayType.PROPAGATE_FORWARD
            GesturePhysicalType.SWIPE_RIGHT -> NeuralPathwayType.PROPAGATE_BACKWARD
            GesturePhysicalType.SWIPE_UP -> NeuralPathwayType.ELEVATE_ENCLAVE
            GesturePhysicalType.SWIPE_DOWN -> NeuralPathwayType.TELEMETRY_PURGE
            GesturePhysicalType.PINCH_IN -> NeuralPathwayType.MACRO_OVERVIEW
            GesturePhysicalType.PINCH_OUT -> NeuralPathwayType.DEEP_NEURAL_STREAM
            GesturePhysicalType.LONG_PRESS_CHARGE -> NeuralPathwayType.AUTONOMOUS_VALIDATION
            GesturePhysicalType.DOUBLE_TAP_PULSE -> NeuralPathwayType.ENTROPY_RESEED
        }
    }

    private fun getActionSummary(pathway: NeuralPathwayType): String {
        return when (pathway) {
            NeuralPathwayType.PROPAGATE_FORWARD -> "Navigate forward to adjacent cyber-node domain"
            NeuralPathwayType.PROPAGATE_BACKWARD -> "Navigate backward to prior cyber-node domain"
            NeuralPathwayType.ELEVATE_ENCLAVE -> "Elevate clearance: Open Root Hardware Enclave Vault (#5)"
            NeuralPathwayType.TELEMETRY_PURGE -> "Purge & Sanitize Layer-4 Telemetry Pipeline (#4)"
            NeuralPathwayType.MACRO_OVERVIEW -> "Dimensional Collapse: Return to Macro Overview (#0)"
            NeuralPathwayType.DEEP_NEURAL_STREAM -> "Expand Layer-2 Neural Command & Intent Stream (#3)"
            NeuralPathwayType.AUTONOMOUS_VALIDATION -> "Trigger Zero-Knowledge Autonomous Proof Verification (#6)"
            NeuralPathwayType.ENTROPY_RESEED -> "Reseed CSPRNG Hardware Entropy Pool & Rotate PQ Key"
        }
    }

    /**
     * Executes the resulting navigation or data interaction callback.
     */
    private fun executeMotorAction(activation: SynapticRouteActivation) {
        val autoNav = _isAutoNavigationEnabled.value

        when (activation.pathwayType) {
            NeuralPathwayType.PROPAGATE_FORWARD -> {
                if (autoNav) onNavigateRelative(+1)
                onAlert("⚡ Neural Pathway Active: Forward Propagation (Tab +1)")
            }
            NeuralPathwayType.PROPAGATE_BACKWARD -> {
                if (autoNav) onNavigateRelative(-1)
                onAlert("⚡ Neural Pathway Active: Backward Propagation (Tab -1)")
            }
            NeuralPathwayType.ELEVATE_ENCLAVE -> {
                if (autoNav) onNavigateToTab(5)
                onAlert("🔒 Enclave Elevation: Switched to Enclave Vault")
            }
            NeuralPathwayType.TELEMETRY_PURGE -> {
                if (autoNav) onNavigateToTab(4)
                onPurgeTelemetry()
                onAlert("🛡️ Shield Reflex: Telemetry Pipeline Sanitized & Flushed")
            }
            NeuralPathwayType.MACRO_OVERVIEW -> {
                if (autoNav) onNavigateToTab(0)
                onAlert("🌐 Dimensional Collapse: Overview Engaged")
            }
            NeuralPathwayType.DEEP_NEURAL_STREAM -> {
                if (autoNav) onNavigateToTab(3)
                onAlert("🧠 Deep Neural Stream: Neural Command Engaged")
            }
            NeuralPathwayType.AUTONOMOUS_VALIDATION -> {
                if (autoNav) onNavigateToTab(6)
                onTriggerValidation()
                onAlert("⚖️ Entangled Validation: Autonomous Proof Lattice Verified")
            }
            NeuralPathwayType.ENTROPY_RESEED -> {
                onReseedEntropy()
                onAlert("🎲 Quantum Reseed: Hardware CSPRNG Entropy Injected")
            }
        }
    }
}
