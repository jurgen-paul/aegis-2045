package com.example.service

import com.example.model.*
import java.util.UUID
import kotlin.math.roundToInt

/**
 * Neural Intent Router Engine
 * Automatically categorizes user actions into distinct cognitive intent domains
 * and dynamically calculates priority scores to order execution queues in real-time.
 */
object NeuralIntentRouterEngine {

    /**
     * Categorizes a raw user action string and computes the intent-scoring breakdown.
     */
    fun analyzeAndScoreIntent(
        rawAction: String,
        manualUrgencyOverride: Float? = null
    ): PrioritizedNeuralTask {
        val normalized = rawAction.lowercase().trim()

        // Categorization Heuristics
        val category: UserActionCategory = when {
            // Critical Security
            normalized.contains("lock") || normalized.contains("quarantine") ||
            normalized.contains("purge") || normalized.contains("breach") ||
            normalized.contains("isolate") || normalized.contains("emergency") ||
            normalized.contains("revoke") || normalized.contains("threat") ||
            normalized.contains("kill") -> UserActionCategory.CRITICAL_SECURITY

            // Identity & Attestation
            normalized.contains("biometric") || normalized.contains("fingerprint") ||
            normalized.contains("passkey") || normalized.contains("fido") ||
            normalized.contains("strongbox") || normalized.contains("credential") ||
            normalized.contains("auth") || normalized.contains("attestation") ||
            normalized.contains("token") -> UserActionCategory.IDENTITY_ATTESTATION

            // Data Transmission & ECTT
            normalized.contains("telemetry") || normalized.contains("export") ||
            normalized.contains("egress") || normalized.contains("packet") ||
            normalized.contains("sync") || normalized.contains("stream") ||
            normalized.contains("transmit") || normalized.contains("privacy") ||
            normalized.contains("bridge") || normalized.contains("dilithium") -> UserActionCategory.DATA_TRANSMISSION

            // System Optimization & Lattice
            normalized.contains("zeroize") || normalized.contains("scrub") ||
            normalized.contains("lattice") || normalized.contains("kyber") ||
            normalized.contains("optimize") || normalized.contains("memory") ||
            normalized.contains("clean") || normalized.contains("recalibrate") ||
            normalized.contains("cache") || normalized.contains("rotate") -> UserActionCategory.SYSTEM_OPTIMIZATION

            // Default: Routine Monitoring
            else -> UserActionCategory.ROUTINE_MONITORING
        }

        // Urgency Calculation (1.0 to 10.0)
        val defaultUrgency = when (category) {
            UserActionCategory.CRITICAL_SECURITY -> {
                if (normalized.contains("emergency") || normalized.contains("immediate") || normalized.contains("now")) 10.0f
                else 9.2f
            }
            UserActionCategory.IDENTITY_ATTESTATION -> 7.8f
            UserActionCategory.DATA_TRANSMISSION -> 6.2f
            UserActionCategory.SYSTEM_OPTIMIZATION -> 5.5f
            UserActionCategory.ROUTINE_MONITORING -> 3.0f
        }
        val urgency = manualUrgencyOverride ?: defaultUrgency

        // Security Impact Calculation (1.0 to 10.0)
        val securityImpact = when (category) {
            UserActionCategory.CRITICAL_SECURITY -> 9.8f
            UserActionCategory.IDENTITY_ATTESTATION -> 8.4f
            UserActionCategory.SYSTEM_OPTIMIZATION -> 7.0f
            UserActionCategory.DATA_TRANSMISSION -> 5.8f
            UserActionCategory.ROUTINE_MONITORING -> 2.5f
        }

        // Hardware Enclave Requirement
        val requiresEnclave = category == UserActionCategory.CRITICAL_SECURITY ||
                category == UserActionCategory.IDENTITY_ATTESTATION ||
                normalized.contains("enclave") || normalized.contains("strongbox") ||
                normalized.contains("kyber")

        // Confidence estimation
        val confidence = (0.88f + ((normalized.length % 11) * 0.01f)).coerceIn(0.85f, 0.99f)

        // Mathematical Intent Scoring Formula:
        // Score = (Confidence * 20) + (Urgency * 4.5) + (SecurityImpact * 2.5) + (EnclaveBonus 10)
        val rawScore = (confidence * 20f) + (urgency * 4.5f) + (securityImpact * 2.5f) + (if (requiresEnclave) 10f else 0f)
        val finalScore = rawScore.roundToInt().coerceIn(1, 100)

        val tier = when {
            finalScore >= PriorityTier.TIER_P1_CRITICAL.minScore -> PriorityTier.TIER_P1_CRITICAL
            finalScore >= PriorityTier.TIER_P2_HIGH.minScore -> PriorityTier.TIER_P2_HIGH
            finalScore >= PriorityTier.TIER_P3_MEDIUM.minScore -> PriorityTier.TIER_P3_MEDIUM
            else -> PriorityTier.TIER_P4_LOW
        }

        val targetNode = when (category) {
            UserActionCategory.CRITICAL_SECURITY -> "eUICC-Core-04 (Hardware Enclave)"
            UserActionCategory.IDENTITY_ATTESTATION -> "StrongBox Keymaster HAL"
            UserActionCategory.DATA_TRANSMISSION -> "ECTT Photonic Sanitizer"
            UserActionCategory.SYSTEM_OPTIMIZATION -> "PQC Lattice Coprocessor"
            UserActionCategory.ROUTINE_MONITORING -> "Telemetry Sentinel Hub"
        }

        val formattedTitle = formatActionTitle(rawAction, category)

        val breakdown = IntentScoringBreakdown(
            intentConfidence = confidence,
            urgencyWeight = urgency,
            securityImpact = securityImpact,
            enclaveRequirement = requiresEnclave,
            finalPriorityScore = finalScore,
            computedTier = tier
        )

        return PrioritizedNeuralTask(
            id = "TASK-${(1000..9999).random()}",
            actionTitle = formattedTitle,
            rawActionDescription = rawAction,
            category = category,
            scoringBreakdown = breakdown,
            priorityScore = finalScore,
            priorityTier = tier,
            status = NeuralTaskStatus.QUEUED,
            targetNode = targetNode,
            estimatedLatencyMs = when (category) {
                UserActionCategory.CRITICAL_SECURITY -> 12
                UserActionCategory.IDENTITY_ATTESTATION -> 24
                UserActionCategory.SYSTEM_OPTIMIZATION -> 45
                UserActionCategory.DATA_TRANSMISSION -> 32
                UserActionCategory.ROUTINE_MONITORING -> 18
            }
        )
    }

    private fun formatActionTitle(rawAction: String, category: UserActionCategory): String {
        if (rawAction.length < 5) return "${category.title} Directive"
        val words = rawAction.trim().split(" ").take(6).joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
        return words
    }

    /**
     * Seeds the initial prioritized task queue dynamically sorted by Priority Score descending.
     */
    fun getInitialPrioritizedTasks(): List<PrioritizedNeuralTask> {
        val initialActions = listOf(
            "Emergency Enclave Lockdown & Master Key Revocation" to 10.0f,
            "Quarantine Heuristic Threat Node #03 & Isolate Sub-Agent" to 9.5f,
            "Class 3 StrongBox Biometric Attestation Challenge" to 8.2f,
            "4-Pass Hardware Memory Zeroization Scrub (0x00..0xAA)" to 7.4f,
            "Differential Privacy ε=0.5 ECTT Telemetry Packet Batch" to 6.2f,
            "Post-Quantum NIST FIPS 203 Lattice Cryptographic Audit" to 5.0f,
            "Routine Synchronic Mesh Node Health & Latency Heartbeat" to 2.8f
        )

        return initialActions.map { (action, urgency) ->
            analyzeAndScoreIntent(action, urgency)
        }.sortedByDescending { it.priorityScore }
    }
}
