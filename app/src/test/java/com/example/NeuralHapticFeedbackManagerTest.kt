package com.example

import android.content.Context
import androidx.compose.ui.geometry.Offset
import androidx.test.core.app.ApplicationProvider
import com.example.neural.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class NeuralHapticFeedbackManagerTest {

    private lateinit var context: Context
    private lateinit var hapticManager: NeuralHapticFeedbackManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        hapticManager = NeuralHapticFeedbackManager(context)
    }

    @Test
    fun testDefaultState() {
        assertTrue("Haptics should be enabled by default", hapticManager.isHapticsEnabled.value)
        assertEquals("Default intensity 0.85", 0.85f, hapticManager.hapticIntensity.value, 0.001f)
        assertEquals(
            "Default profile CYBERNETIC_TICK",
            HapticAestheticProfile.CYBERNETIC_TICK,
            hapticManager.activeProfile.value
        )
        assertNull("Initially no pattern triggered", hapticManager.lastTriggeredPattern.value)
    }

    @Test
    fun testIntensityClamping() {
        hapticManager.setHapticIntensity(0.50f)
        assertEquals(0.50f, hapticManager.hapticIntensity.value, 0.001f)

        // Upper clamp
        hapticManager.setHapticIntensity(1.85f)
        assertEquals(1.0f, hapticManager.hapticIntensity.value, 0.001f)

        // Lower clamp
        hapticManager.setHapticIntensity(-0.35f)
        assertEquals(0.0f, hapticManager.hapticIntensity.value, 0.001f)
    }

    @Test
    fun testProfileSwitching() {
        HapticAestheticProfile.values().forEach { profile ->
            hapticManager.setActiveProfile(profile)
            assertEquals(profile, hapticManager.activeProfile.value)
        }
    }

    @Test
    fun testPatternGenerationCybernetic() {
        NeuralPathwayType.values().forEach { pathway ->
            val spec = hapticManager.buildPatternForPathway(
                pathway = pathway,
                profile = HapticAestheticProfile.CYBERNETIC_TICK,
                confidence = 0.95f
            )

            assertEquals(
                "Timings and amplitudes array length must match",
                spec.timingsMs.size,
                spec.amplitudes.size
            )
            assertTrue("Pattern must have at least 2 steps", spec.timingsMs.size >= 2)
            spec.amplitudes.forEach { amp ->
                assertTrue("Amplitude in [0, 255]", amp in 0..255)
            }
        }
    }

    @Test
    fun testPatternGenerationFluidic() {
        val spec = hapticManager.buildPatternForPathway(
            pathway = NeuralPathwayType.DEEP_NEURAL_STREAM,
            profile = HapticAestheticProfile.FLUIDIC_PHOTONIC,
            confidence = 0.88f
        )
        assertEquals(spec.timingsMs.size, spec.amplitudes.size)
        assertTrue(spec.timingsMs.isNotEmpty())
    }

    @Test
    fun testPatternGenerationMechanical() {
        val spec = hapticManager.buildPatternForPathway(
            pathway = NeuralPathwayType.ELEVATE_ENCLAVE,
            profile = HapticAestheticProfile.TACTILE_MECHANICAL,
            confidence = 0.99f
        )
        assertEquals(spec.timingsMs.size, spec.amplitudes.size)
        // Mechanical enclave elevation should end with high-impact amplitude
        assertTrue("Mechanical latch detent amplitude", spec.amplitudes.last() > 180)
    }

    @Test
    fun testPatternGenerationWhisper() {
        val spec = hapticManager.buildPatternForPathway(
            pathway = NeuralPathwayType.PROPAGATE_FORWARD,
            profile = HapticAestheticProfile.MINIMAL_WHISPER,
            confidence = 0.75f
        )
        assertEquals(spec.timingsMs.size, spec.amplitudes.size)
        spec.amplitudes.forEach { amp ->
            assertTrue("Whisper amplitudes should be subtle (<= 100)", amp <= 100)
        }
    }

    @Test
    fun testTriggerThresholdTick() {
        hapticManager.triggerThresholdTick()
        assertEquals("SYNAPTIC_THRESHOLD_TICK", hapticManager.lastTriggeredPattern.value)
    }

    @Test
    fun testTriggerPathwayHaptic() {
        hapticManager.triggerPathwayHaptic(NeuralPathwayType.PROPAGATE_FORWARD, 0.95f)
        val pattern = hapticManager.lastTriggeredPattern.value
        assertNotNull("Pattern should be recorded", pattern)
        assertTrue(pattern!!.contains("NPATH-FWD-01"))
        assertTrue(pattern.contains("Cybernetic Precision"))
    }

    @Test
    fun testDisabledHapticsSuppression() {
        hapticManager.setHapticsEnabled(false)
        hapticManager.triggerPathwayHaptic(NeuralPathwayType.ELEVATE_ENCLAVE, 0.90f)
        assertNull("Should not record pattern when disabled", hapticManager.lastTriggeredPattern.value)

        hapticManager.triggerThresholdTick()
        assertNull("Should not record threshold tick when disabled", hapticManager.lastTriggeredPattern.value)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun testHandlerIntegrationTriggersHaptic() = runTest {
        val handler = NeuralGestureIntentHandler(
            coroutineScope = this,
            hapticManager = hapticManager,
            onNavigateToTab = {},
            onNavigateRelative = {},
            onPurgeTelemetry = {},
            onTriggerValidation = {},
            onReseedEntropy = {},
            onAlert = {}
        )

        val vector = GestureVectorData(
            startOffset = Offset(100f, 300f),
            endOffset = Offset(400f, 300f),
            deltaX = 300f,
            deltaY = 0f,
            velocityX = 1200f,
            velocityY = 0f,
            distancePx = 300f,
            durationMs = 150L
        )

        handler.processGestureVector(vector)
        advanceUntilIdle()

        val lastPattern = hapticManager.lastTriggeredPattern.value
        assertNotNull("Handler must trigger haptic feedback on successful mapping", lastPattern)
        assertTrue("Pattern matches backward pathway code", lastPattern!!.contains("NPATH-BWD-02"))
    }
}
