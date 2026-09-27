package com.example.model

enum class AgentStatus(val label: String) {
    ACTIVE("Active / Harmonized"),
    SECURING("Enclave Securing"),
    ISOLATED("Hardware Isolated"),
    SCANNING("Deep Threat Scan")
}

enum class ThreatSeverity(val label: String) {
    CRITICAL("CRITICAL TIER-0"),
    HIGH("HIGH TIER-1"),
    MEDIUM("MEDIUM TIER-2"),
    LOW("LOW TIER-3")
}

enum class ThreatStatus(val label: String) {
    CONTAINED("Photonic Contained"),
    ISOLATING("Enclave Isolating"),
    MITIGATED("Zero-Trust Purged")
}

data class SubAgentThread(
    val id: String,
    val name: String,
    val role: String,
    val status: AgentStatus,
    val neuralLoad: Float, // 0.0f to 1.0f
    val latencyMs: Int,
    val handledTasks: Int,
    val memoryAllocation: String,
    val cryptographicSignature: String
)

data class BiometricState(
    val neuralPulseBpm: Int = 78,
    val galvanicConductance: Float = 4.2f, // uS
    val neuralSyncRatio: Float = 0.984f, // 98.4%
    val enclaveIsolation: String = "Dual Isolation (HW Mem + Sandboxing)",
    val entropyRateKbps: Float = 512.4f,
    val sessionTokenHash: String = "0x9F4C...B82A"
)

data class ArchitectureComparison(
    val layerName: String,
    val year2024Base: String,
    val year2045Quantum: String,
    val securityBenefit: String
)

data class AgisLayerData(
    val layerId: Int,
    val name: String,
    val timestamp: String,
    val subtitle: String,
    val coreRule: String,
    val signals: List<Pair<String, String>>,
    val components: List<String>,
    val specs: List<Pair<String, String>>
)

data class ValidationProof(
    val id: String,
    val name: String,
    val description: String,
    val status: String,
    val verificationDigest: String,
    val isPassing: Boolean = true
)

enum class EnclaveLockState(val label: String) {
    LOCKED("Hardware Locked (Biometric Auth Required)"),
    AUTHENTICATING("Verifying Credential Manager Token..."),
    UNLOCKED("Decrypted & Biometrically Attested"),
    DENIED("Authentication Challenge Failed")
}

data class BiometricAttestationDetails(
    val credentialType: String = "Passkey / FIDO2 Biometric",
    val attestationToken: String = "0x8F92...D04E",
    val biometricStrength: String = "Class 3 (Strong Hardware Biometrics)",
    val hardwareSecurityModule: String = "ARM TrustZone / StrongBox Keymaster",
    val verifiedTimestamp: Long = System.currentTimeMillis()
)

data class EnclaveKeyInfo(
    val keyId: String,
    val algorithm: String = "Kyber-1024 / Dilithium-5 (512-bit)",
    val hardwareSlot: String = "eUICC Enclave Core #04",
    val memoryAddress: String = "0x7FFF_8000_9000_PQE",
    val rotationRemainingSec: Int = 42,
    val activeState: String = "SEALED_HARDWARE_BOUND",
    val lockState: EnclaveLockState = EnclaveLockState.LOCKED,
    val attestationDetails: BiometricAttestationDetails? = null
)

enum class IntentRiskLevel(val label: String) {
    SAFE("Low Risk / Autonomous"),
    ELEVATED("Elevated / Sandboxed"),
    RESTRICTED("Restricted / Gated"),
    ISOLATED("Quarantine Contained")
}

data class NeuralIntentPattern(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val sourceNode: String,
    val targetNode: String,
    val intentType: String,
    val classification: String,
    val confidenceScore: Float, // 0.0 to 1.0
    val entropyDelta: Float,
    val latencyMs: Int,
    val riskLevel: IntentRiskLevel,
    val synchronicHash: String,
    val activeState: String = "ROUTED_VERIFIED"
)

data class NeuralTopologyNode(
    val nodeId: String,
    val label: String,
    val role: String,
    val normalizedX: Float, // 0.0 to 1.0 on canvas
    val normalizedY: Float, // 0.0 to 1.0 on canvas
    val activeTrafficRate: Float, // ops/sec
    val isPrimaryCore: Boolean = false
)

data class CyberNode(
    val id: String,
    val name: String,
    val shortLabel: String,
    val tierNumber: Int,
    val tierLabel: String,
    val description: String,
    val normalizedX: Float,
    val normalizedY: Float,
    val securityProtocol: String,
    val latencyNs: Int,
    val activeLoad: Float, // 0.0f to 1.0f
    val isHardwareEnclave: Boolean = false,
    val activePackets: Int = 0
)

data class CyberNodeRoute(
    val id: String,
    val name: String,
    val intentType: String,
    val description: String,
    val nodeHops: List<String>,
    val riskLevel: IntentRiskLevel,
    val latencyMs: Int,
    val cryptographicDigest: String,
    val isSimulated: Boolean = false
)

enum class PolicyEnforcementLevel(val label: String) {
    STRICT("Zero-Bypass Strict (Hardware Enclave Sealed)"),
    BALANCED("Adaptive Zero-Trust (Attestation Monitored)"),
    DEVELOPMENT("Sandbox Permissive (Full Telemetry Logged)")
}

data class SecurityPolicyRule(
    val id: String,
    val name: String,
    val category: String, // "NEURAL_GATE", "ENCLAVE_CRYPTO", "TELEMETRY_PRIVACY", "CROSS_DOMAIN"
    val description: String,
    val isEnabled: Boolean = true,
    val minimumTier: Int = 1,
    val requiresBiometricConfirmation: Boolean = false,
    val enforcementAction: String
)

data class TelemetryThroughputPoint(
    val timestamp: Long = System.currentTimeMillis(),
    val rawThroughputKbps: Float,
    val sanitizedThroughputKbps: Float,
    val packetsPerSec: Int,
    val piiScrubbedRate: Int,
    val threatAnomalyScore: Float, // 0.0 to 1.0
    val differentialEpsilon: Float = 0.5f
)

data class ThreatCategoryMetric(
    val categoryName: String,
    val shortCode: String,
    val incidentCount: Int,
    val severityLevel: ThreatSeverity,
    val riskRatio: Float, // 0.0 to 1.0
    val accentColorHex: String = "#10B981"
)

enum class RadarThreatLevel(val label: String) {
    FRIENDLY("Friendly / Authorized"),
    NEUTRAL("Neutral / Civil"),
    HOSTILE("Hostile / Red Alert"),
    UNKNOWN("Unverified / Taint Risk")
}

enum class TargetClassification(val label: String) {
    OPERATIVE("Autonomous Operative"),
    INTRUDER("Perimeter Intruder"),
    SYNTHETIC_DRONE("Autonomous Micro-Drone"),
    UNKNOWN_ENTITY("Sub-Surface Entity"),
    GHOST_SIGNATURE("Electromagnetic Ghost")
}

data class RadarTarget(
    val id: String,
    val codeName: String,
    val classification: TargetClassification,
    val threatLevel: RadarThreatLevel,
    val rangeMeters: Float,
    val bearingDegrees: Float,
    val velocityKmh: Float,
    val headingDegrees: Float,
    val altitudeMeters: Float = 1.8f,
    val isTraceLocked: Boolean = false,
    val matchedSubjectId: String? = null,
    val confidence: Float = 0.95f,
    val signalStrengthDbm: Float = -48.2f,
    val trajectoryHistory: List<Pair<Float, Float>> = emptyList() // Polar (range, bearing) history
)

data class SubjectIdentity(
    val id: String,
    val operativeCode: String,
    val fullName: String,
    val clearanceLevel: String, // "TIER-6 ENCLAVE MASTER", "TIER-4 SENTINEL", "TIER-1 VISITOR", "RED_FLAG QUARANTINE"
    val affiliation: String,
    val threatRating: ThreatSeverity,
    val isRedNotice: Boolean = false,
    val biometricHash: String,
    val facialConfidence: Float = 0.98f,
    val voiceConfidence: Float = 0.94f,
    val shadowSilhouetteScore: Float = 0.91f,
    val footstepsGaitScore: Float = 0.89f,
    val lastKnownCoordinates: String = "GRID-44.209, 12.871",
    val primaryThreatVector: String = "None Detected",
    val profileStatus: String = "ACTIVE_SURVEILLANCE"
)

data class FacialRecognitionScan(
    val subjectId: String,
    val subjectName: String,
    val matchConfidence: Float, // 0.0 to 1.0
    val livenessScore: Float, // 0.0 to 1.0
    val landmarkCount: Int = 68,
    val pupillaryDistanceMm: Float = 63.5f,
    val headPoseRollPitchYaw: Triple<Float, Float, Float> = Triple(0.2f, -1.4f, 2.1f),
    val antiSpoofAttestation: Boolean = true,
    val microExpressionIndex: Float = 0.12f,
    val biometricVectorDigest: String
)

data class VoiceprintRecognitionScan(
    val subjectId: String,
    val subjectName: String,
    val matchConfidence: Float,
    val pitchHz: Float = 142.6f,
    val formantF1Hz: Float = 520f,
    val formantF2Hz: Float = 1840f,
    val formantF3Hz: Float = 2650f,
    val deepfakeSyntheticScore: Float = 0.02f, // low = authentic human
    val speakerDiarizationId: String = "SPK_ALPHA_01",
    val spectralBandEnergies: List<Float> = emptyList()
)

data class ShadowSilhouetteScan(
    val subjectId: String,
    val subjectName: String,
    val matchConfidence: Float,
    val estimatedHeightCm: Float = 181.4f,
    val shoulderToHipRatio: Float = 1.38f,
    val volumetricGaitSymmetry: Float = 0.95f,
    val ambientOcclusionLux: Float = 120.5f,
    val silhouetteProfileDigest: String
)

data class FootstepsGaitScan(
    val subjectId: String,
    val subjectName: String,
    val matchConfidence: Float,
    val cadenceSpm: Int = 114, // steps per min
    val groundForceNewtons: Float = 780.5f,
    val heelToePressureRatio: Float = 1.15f,
    val seismicSensorId: String = "GEOPHONE_NODE_04",
    val gaitResonanceHz: Float = 1.85f,
    val groundImpulseWaveform: List<Float> = emptyList()
)

enum class TelemetryAnomalyType(val label: String, val shortCode: String, val defaultSeverity: ThreatSeverity) {
    UNMASKED_PII_LEAK("Unmasked PII & Egress Leak", "PII_LEAK", ThreatSeverity.CRITICAL),
    PROMPT_INJECTION_PAYLOAD("Adversarial Prompt Injection Taint", "PROMPT_INJECT", ThreatSeverity.CRITICAL),
    DIFFERENTIAL_PRIVACY_VIOLATION("Differential Privacy Epsilon Collapse", "EPSILON_COLLAPSE", ThreatSeverity.HIGH),
    MEMORY_REGISTER_EXFIL("Enclave Memory Exfiltration Probe", "MEM_EXFIL", ThreatSeverity.CRITICAL),
    RETINAL_BIOMETRIC_EXPOSURE("Unredacted Biometric Retinal / EEG Stream", "BIO_EXPOSURE", ThreatSeverity.HIGH),
    SURGE_PACKET_ANOMALY("High-Rate Entropy Taint Surge", "SURGE_ANOMALY", ThreatSeverity.HIGH)
}

data class TelemetryAnomalyAlert(
    val id: String,
    val timestamp: Long = System.currentTimeMillis(),
    val anomalyType: TelemetryAnomalyType,
    val severity: ThreatSeverity,
    val riskScore: Float, // 0.0 to 1.0 (e.g. 0.98 = 98%)
    val title: String,
    val description: String,
    val detectedPayloadSnippet: String,
    val redactionRuleApplied: String,
    val affectedDomainOrNode: String = "ENCLAVE_BOUNDARY_GATE",
    val isMitigated: Boolean = false,
    val mitigationActionTaken: String? = null,
    val cryptographicFingerprint: String
)

enum class SanitizationScanPhase(val label: String, val stepNumber: Int) {
    IDLE("Idle / Ready", 0),
    INGRESS_HEADER_INSPECTION("Stage 1/5: Ingress Packet Header Inspection", 1),
    PII_AND_TOKEN_SCRUBBING("Stage 2/5: Differential Privacy & PII Scrubbing", 2),
    POST_QUANTUM_LATTICE_VERIFICATION("Stage 3/5: Kyber-1024 Lattice Attestation", 3),
    HARDWARE_ENCLAVE_ISOLATION_CHECK("Stage 4/5: Hardware Enclave Memory Check", 4),
    COMPLETED_AND_SEALED("Stage 5/5: Zero-Leak Cryptographic Seal Complete", 5)
}

data class SanitizationScanFeedback(
    val scanId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isEnclaveSealed: Boolean = true,
    val enclaveKeyAlgorithm: String = "Kyber-1024 / Dilithium-5 (512-bit)",
    val hardwareSlot: String = "eUICC Enclave Core #04",
    val memoryAddress: String = "0x7FFF_8000_9000_PQE",
    val latticeStatus: String = "NIST FIPS 203/204 MATRICES VERIFIED",
    val quantumResistanceScore: Int = 100,
    val packetsScanned: Int = 142,
    val piiTokensScrubbed: Int = 18,
    val threatsNeutralizedCount: Int = 0,
    val differentialEpsilon: Float = 0.5f,
    val dilithiumProofDigest: String,
    val isolationStatus: String = "HARDWARE_MEMORY_BARRIER_INTACT",
    val summary: String
)

// ==========================================
// NEURAL INTENT ROUTER & DYNAMIC TASK PRIORITIZATION
// ==========================================

enum class UserActionCategory(
    val title: String,
    val subtitle: String,
    val baseImpactWeight: Int,
    val hexColor: String
) {
    CRITICAL_SECURITY(
        title = "Critical Security",
        subtitle = "Hardware enclave locking, key revocation, threat quarantine",
        baseImpactWeight = 40,
        hexColor = "#F43F5E"
    ),
    IDENTITY_ATTESTATION(
        title = "Identity Attestation",
        subtitle = "Biometric challenge, passkey tokens, StrongBox hardware attestation",
        baseImpactWeight = 30,
        hexColor = "#8B5CF6"
    ),
    DATA_TRANSMISSION(
        title = "ECTT Telemetry Egress",
        subtitle = "Differential privacy packet stream, audit log sync, cross-domain bridge",
        baseImpactWeight = 25,
        hexColor = "#06B6D4"
    ),
    SYSTEM_OPTIMIZATION(
        title = "System Optimization",
        subtitle = "Memory zeroization scrub, lattice integrity check, latency recalibration",
        baseImpactWeight = 20,
        hexColor = "#10B981"
    ),
    ROUTINE_MONITORING(
        title = "Routine Monitoring",
        subtitle = "Node health checks, mesh topology telemetry, ambient status queries",
        baseImpactWeight = 10,
        hexColor = "#F59E0B"
    )
}

enum class PriorityTier(val label: String, val badgeLabel: String, val minScore: Int) {
    TIER_P1_CRITICAL("Tier-1 Critical", "P1 • CRITICAL", 85),
    TIER_P2_HIGH("Tier-2 High", "P2 • HIGH", 65),
    TIER_P3_MEDIUM("Tier-3 Medium", "P3 • MEDIUM", 40),
    TIER_P4_LOW("Tier-4 Low", "P4 • LOW", 0)
}

enum class NeuralTaskStatus(val label: String) {
    QUEUED("In Queue"),
    EVALUATING_INTENT("Scoring Intent..."),
    EXECUTING("Executing Route..."),
    COMPLETED("Successfully Routed"),
    CANCELLED("Purged by Operator")
}

data class IntentScoringBreakdown(
    val intentConfidence: Float, // 0.0 to 1.0 (e.g. 0.94 = 94%)
    val urgencyWeight: Float, // 1.0 to 10.0
    val securityImpact: Float, // 1.0 to 10.0
    val enclaveRequirement: Boolean,
    val finalPriorityScore: Int, // 1 to 100
    val computedTier: PriorityTier
)

data class PrioritizedNeuralTask(
    val id: String,
    val actionTitle: String,
    val rawActionDescription: String,
    val category: UserActionCategory,
    val scoringBreakdown: IntentScoringBreakdown,
    val priorityScore: Int,
    val priorityTier: PriorityTier,
    val status: NeuralTaskStatus = NeuralTaskStatus.QUEUED,
    val targetNode: String,
    val timestamp: Long = System.currentTimeMillis(),
    val estimatedLatencyMs: Int = 18,
    val executionLog: String? = null
)


