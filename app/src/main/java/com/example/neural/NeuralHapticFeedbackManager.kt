package com.example.neural

import android.content.Context
import android.os.Build
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

/**
 * Aesthetic acoustic-tactile profiles for the AGIS 2045 Quantum Glass Haptics.
 */
enum class HapticAestheticProfile(
    val title: String,
    val description: String
) {
    CYBERNETIC_TICK("Cybernetic Precision", "Sharp, concise micro-pulses with ultra-fast attack"),
    FLUIDIC_PHOTONIC("Fluidic Photonic", "Smooth continuous sine envelopes mimicking wave propagation"),
    TACTILE_MECHANICAL("Tactile Quantum Detent", "Crisp multi-stage mechanical latch feedback"),
    MINIMAL_WHISPER("Minimal Whisper", "Ultra-subtle sub-perceptual micro-ticks")
}

/**
 * Concrete high-fidelity vibration pattern specification.
 */
data class HapticPatternSpec(
    val timingsMs: LongArray,
    val amplitudes: IntArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as HapticPatternSpec
        if (!timingsMs.contentEquals(other.timingsMs)) return false
        return amplitudes.contentEquals(other.amplitudes)
    }

    override fun hashCode(): Int {
        var result = timingsMs.contentHashCode()
        result = 31 * result + amplitudes.contentHashCode()
        return result
    }
}

/**
 * High-Fidelity Haptic Feedback Manager for the AGIS 2045 Neural Intent Routing system.
 *
 * Employs hardware-accelerated vibration effects:
 * - Android 12+ (API 31+) VibratorManager & VibrationEffect.Composition primitives
 * - Android 8.0+ (API 26+) Waveform amplitude modulation with custom envelope curves
 * - Graceful fallback for legacy or limited-hardware devices.
 */
class NeuralHapticFeedbackManager(
    private val context: Context
) {

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    private val _isHapticsEnabled = MutableStateFlow(true)
    val isHapticsEnabled: StateFlow<Boolean> = _isHapticsEnabled.asStateFlow()

    private val _hapticIntensity = MutableStateFlow(0.85f) // 0.0 to 1.0 scale
    val hapticIntensity: StateFlow<Float> = _hapticIntensity.asStateFlow()

    private val _activeProfile = MutableStateFlow(HapticAestheticProfile.CYBERNETIC_TICK)
    val activeProfile: StateFlow<HapticAestheticProfile> = _activeProfile.asStateFlow()

    private val _lastTriggeredPattern = MutableStateFlow<String?>(null)
    val lastTriggeredPattern: StateFlow<String?> = _lastTriggeredPattern.asStateFlow()

    fun setHapticsEnabled(enabled: Boolean) {
        _isHapticsEnabled.value = enabled
    }

    fun setHapticIntensity(intensity: Float) {
        _hapticIntensity.value = intensity.coerceIn(0.0f, 1.0f)
    }

    fun setActiveProfile(profile: HapticAestheticProfile) {
        _activeProfile.value = profile
    }

    /**
     * Triggers a subtle high-fidelity vibration pattern when the neural router maps
     * a gesture to a data pathway.
     */
    fun triggerPathwayHaptic(pathway: NeuralPathwayType, confidence: Float = 0.90f) {
        if (!_isHapticsEnabled.value) return

        val pattern = buildPatternForPathway(pathway, _activeProfile.value, confidence)
        _lastTriggeredPattern.value = "${pathway.pathwayCode} (${_activeProfile.value.title})"
        executePattern(pattern)
    }

    /**
     * Subtle micro-tick when a gesture passes the neural activation threshold.
     */
    fun triggerThresholdTick() {
        if (!_isHapticsEnabled.value) return

        val scale = _hapticIntensity.value
        val amp = (60 * scale).roundToInt().coerceIn(1, 255)
        val pattern = HapticPatternSpec(
            timingsMs = longArrayOf(0, 8),
            amplitudes = intArrayOf(0, amp)
        )
        _lastTriggeredPattern.value = "SYNAPTIC_THRESHOLD_TICK"
        executePattern(pattern)
    }

    /**
     * Resonance ramp while operator performs a sustained long-press charge gesture.
     */
    fun triggerChargeResonance(chargeRatio: Float) {
        if (!_isHapticsEnabled.value) return

        val scale = _hapticIntensity.value
        val amp = (chargeRatio * 180 * scale).roundToInt().coerceIn(1, 255)
        val pattern = HapticPatternSpec(
            timingsMs = longArrayOf(0, 10),
            amplitudes = intArrayOf(0, amp)
        )
        _lastTriggeredPattern.value = "CHARGE_RESONANCE_${(chargeRatio * 100).toInt()}%"
        executePattern(pattern)
    }

    /**
     * Rejection buzz when a gesture does not meet neural firing threshold.
     */
    fun triggerRejectionBuzz() {
        if (!_isHapticsEnabled.value) return

        val scale = _hapticIntensity.value
        val amp = (110 * scale).roundToInt().coerceIn(1, 255)
        val pattern = HapticPatternSpec(
            timingsMs = longArrayOf(0, 18, 25, 18),
            amplitudes = intArrayOf(0, amp, 0, amp / 2)
        )
        _lastTriggeredPattern.value = "NEURAL_REJECTION_BUZZ"
        executePattern(pattern)
    }

    /**
     * Generates tailored waveform patterns according to pathway semantics and selected aesthetic profile.
     */
    fun buildPatternForPathway(
        pathway: NeuralPathwayType,
        profile: HapticAestheticProfile,
        confidence: Float
    ): HapticPatternSpec {
        val intensity = _hapticIntensity.value * (0.8f + confidence * 0.2f)

        return when (profile) {
            HapticAestheticProfile.CYBERNETIC_TICK -> buildCyberneticPattern(pathway, intensity)
            HapticAestheticProfile.FLUIDIC_PHOTONIC -> buildFluidicPattern(pathway, intensity)
            HapticAestheticProfile.TACTILE_MECHANICAL -> buildMechanicalPattern(pathway, intensity)
            HapticAestheticProfile.MINIMAL_WHISPER -> buildWhisperPattern(pathway, intensity)
        }
    }

    private fun buildCyberneticPattern(pathway: NeuralPathwayType, scale: Float): HapticPatternSpec {
        fun a(base: Int): Int = (base * scale).roundToInt().coerceIn(1, 255)

        return when (pathway) {
            NeuralPathwayType.PROPAGATE_FORWARD -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 10, 14, 14),
                amplitudes = intArrayOf(0, a(70), 0, a(150))
            )
            NeuralPathwayType.PROPAGATE_BACKWARD -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 14, 14, 10),
                amplitudes = intArrayOf(0, a(150), 0, a(70))
            )
            NeuralPathwayType.ELEVATE_ENCLAVE -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 10, 12, 12, 12, 22),
                amplitudes = intArrayOf(0, a(90), 0, a(160), 0, a(250))
            )
            NeuralPathwayType.TELEMETRY_PURGE -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 8, 8, 8, 8, 12),
                amplitudes = intArrayOf(0, a(180), 0, a(130), 0, a(70))
            )
            NeuralPathwayType.MACRO_OVERVIEW -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 18, 10, 18),
                amplitudes = intArrayOf(0, a(110), 0, a(60))
            )
            NeuralPathwayType.DEEP_NEURAL_STREAM -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 8, 10, 10, 10, 14, 10, 18),
                amplitudes = intArrayOf(0, a(50), 0, a(90), 0, a(140), 0, a(210))
            )
            NeuralPathwayType.AUTONOMOUS_VALIDATION -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 12, 16, 20),
                amplitudes = intArrayOf(0, a(140), 0, a(230))
            )
            NeuralPathwayType.ENTROPY_RESEED -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 6, 8, 6, 8, 10, 8, 12),
                amplitudes = intArrayOf(0, a(190), 0, a(120), 0, a(220), 0, a(90))
            )
        }
    }

    private fun buildFluidicPattern(pathway: NeuralPathwayType, scale: Float): HapticPatternSpec {
        fun a(base: Int): Int = (base * scale).roundToInt().coerceIn(1, 255)

        return when (pathway) {
            NeuralPathwayType.PROPAGATE_FORWARD, NeuralPathwayType.PROPAGATE_BACKWARD -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 12, 8, 16, 8, 20),
                amplitudes = intArrayOf(0, a(40), 0, a(90), 0, a(140))
            )
            NeuralPathwayType.ELEVATE_ENCLAVE -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 15, 10, 25, 10, 30),
                amplitudes = intArrayOf(0, a(70), 0, a(150), 0, a(220))
            )
            NeuralPathwayType.TELEMETRY_PURGE -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 20, 10, 16, 10, 12),
                amplitudes = intArrayOf(0, a(160), 0, a(110), 0, a(50))
            )
            NeuralPathwayType.MACRO_OVERVIEW -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 24, 12, 20),
                amplitudes = intArrayOf(0, a(80), 0, a(50))
            )
            NeuralPathwayType.DEEP_NEURAL_STREAM -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 10, 8, 14, 8, 18, 8, 24),
                amplitudes = intArrayOf(0, a(40), 0, a(75), 0, a(120), 0, a(180))
            )
            NeuralPathwayType.AUTONOMOUS_VALIDATION -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 16, 14, 26),
                amplitudes = intArrayOf(0, a(110), 0, a(190))
            )
            NeuralPathwayType.ENTROPY_RESEED -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 10, 10, 12, 10, 14),
                amplitudes = intArrayOf(0, a(130), 0, a(180), 0, a(120))
            )
        }
    }

    private fun buildMechanicalPattern(pathway: NeuralPathwayType, scale: Float): HapticPatternSpec {
        fun a(base: Int): Int = (base * scale).roundToInt().coerceIn(1, 255)

        return when (pathway) {
            NeuralPathwayType.ELEVATE_ENCLAVE -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 14, 18, 26),
                amplitudes = intArrayOf(0, a(210), 0, a(255))
            )
            NeuralPathwayType.TELEMETRY_PURGE -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 12, 10, 12, 10, 14),
                amplitudes = intArrayOf(0, a(200), 0, a(160), 0, a(120))
            )
            NeuralPathwayType.AUTONOMOUS_VALIDATION -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 16, 20, 22),
                amplitudes = intArrayOf(0, a(180), 0, a(240))
            )
            else -> HapticPatternSpec(
                timingsMs = longArrayOf(0, 12, 14, 16),
                amplitudes = intArrayOf(0, a(120), 0, a(190))
            )
        }
    }

    private fun buildWhisperPattern(pathway: NeuralPathwayType, scale: Float): HapticPatternSpec {
        fun a(base: Int): Int = (base * scale * 0.45f).roundToInt().coerceIn(1, 100)

        return HapticPatternSpec(
            timingsMs = longArrayOf(0, 8, 12, 10),
            amplitudes = intArrayOf(0, a(70), 0, a(90))
        )
    }

    private fun executePattern(spec: HapticPatternSpec) {
        val vib = vibrator ?: return
        try {
            if (!vib.hasVibrator()) return

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Amplitude control check
                val effect = if (vib.hasAmplitudeControl()) {
                    VibrationEffect.createWaveform(spec.timingsMs, spec.amplitudes, -1)
                } else {
                    // Fallback to timing on/off pulses without amplitude scaling
                    VibrationEffect.createWaveform(spec.timingsMs, -1)
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val attributes = VibrationAttributes.Builder()
                        .setUsage(VibrationAttributes.USAGE_TOUCH)
                        .build()
                    vib.vibrate(effect, attributes)
                } else {
                    vib.vibrate(effect)
                }
            } else {
                @Suppress("DEPRECATION")
                vib.vibrate(spec.timingsMs, -1)
            }
        } catch (_: Exception) {
            // Silently absorb in environments without physical vibrators
        }
    }
}
