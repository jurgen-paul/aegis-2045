package com.example

import androidx.compose.ui.geometry.Offset
import com.example.neural.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NeuralGestureIntentHandlerTest {

    private var navigatedTab: Int? = null
    private var navigatedDelta: Int? = null
    private var telemetryPurged = false
    private var validationTriggered = false
    private var entropyReseeded = false
    private var lastAlertMessage: String? = null

    @Before
    fun setUp() {
        navigatedTab = null
        navigatedDelta = null
        telemetryPurged = false
        validationTriggered = false
        entropyReseeded = false
        lastAlertMessage = null
    }

    private fun createHandler(scope: CoroutineScope): NeuralGestureIntentHandler {
        return NeuralGestureIntentHandler(
            coroutineScope = scope,
            onNavigateToTab = { navigatedTab = it },
            onNavigateRelative = { navigatedDelta = it },
            onPurgeTelemetry = { telemetryPurged = true },
            onTriggerValidation = { validationTriggered = true },
            onReseedEntropy = { entropyReseeded = true },
            onAlert = { lastAlertMessage = it }
        )
    }

    @Test
    fun testSwipeLeftPropagatesForward() = runTest {
        val handler = createHandler(this)
        val vector = GestureVectorData(
            startOffset = Offset(400f, 300f),
            endOffset = Offset(100f, 300f),
            deltaX = -300f,
            deltaY = 0f,
            velocityX = -1200f,
            velocityY = 0f,
            distancePx = 300f,
            durationMs = 150L
        )

        handler.processGestureVector(vector)
        advanceUntilIdle()

        val active = handler.activePathway.value
        assertNotNull("Active pathway must not be null", active)
        assertEquals(NeuralPathwayType.PROPAGATE_FORWARD, active?.pathwayType)
        assertEquals(GesturePhysicalType.SWIPE_LEFT, active?.triggerGesture)
        assertEquals(1, navigatedDelta)
        assertTrue(active!!.firingConfidence >= 0.70f)
        assertTrue(active.membranePotentialMv > -50f)
        assertEquals(4, active.nodes.size)
        assertNotNull(lastAlertMessage)
    }

    @Test
    fun testSwipeRightPropagatesBackward() = runTest {
        val handler = createHandler(this)
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

        val active = handler.activePathway.value
        assertNotNull(active)
        assertEquals(NeuralPathwayType.PROPAGATE_BACKWARD, active?.pathwayType)
        assertEquals(GesturePhysicalType.SWIPE_RIGHT, active?.triggerGesture)
        assertEquals(-1, navigatedDelta)
    }

    @Test
    fun testSwipeUpElevatesToEnclaveVault() = runTest {
        val handler = createHandler(this)
        val vector = GestureVectorData(
            startOffset = Offset(200f, 600f),
            endOffset = Offset(200f, 150f),
            deltaX = 0f,
            deltaY = -450f,
            velocityX = 0f,
            velocityY = -1500f,
            distancePx = 450f,
            durationMs = 140L
        )

        handler.processGestureVector(vector)
        advanceUntilIdle()

        val active = handler.activePathway.value
        assertNotNull(active)
        assertEquals(NeuralPathwayType.ELEVATE_ENCLAVE, active?.pathwayType)
        assertEquals(GesturePhysicalType.SWIPE_UP, active?.triggerGesture)
        assertEquals(5, navigatedTab)
    }

    @Test
    fun testSwipeDownTriggersTelemetryPurge() = runTest {
        val handler = createHandler(this)
        val vector = GestureVectorData(
            startOffset = Offset(200f, 100f),
            endOffset = Offset(200f, 500f),
            deltaX = 0f,
            deltaY = 400f,
            velocityX = 0f,
            velocityY = 1400f,
            distancePx = 400f,
            durationMs = 160L
        )

        handler.processGestureVector(vector)
        advanceUntilIdle()

        val active = handler.activePathway.value
        assertNotNull(active)
        assertEquals(NeuralPathwayType.TELEMETRY_PURGE, active?.pathwayType)
        assertEquals(GesturePhysicalType.SWIPE_DOWN, active?.triggerGesture)
        assertEquals(4, navigatedTab)
        assertTrue("Telemetry must be purged", telemetryPurged)
    }

    @Test
    fun testLongPressTriggersAutonomousValidation() = runTest {
        val handler = createHandler(this)
        val vector = GestureVectorData(
            startOffset = Offset(200f, 200f),
            endOffset = Offset(205f, 205f),
            deltaX = 5f,
            deltaY = 5f,
            velocityX = 0f,
            velocityY = 0f,
            distancePx = 7f,
            durationMs = 600L
        )

        handler.processGestureVector(vector)
        advanceUntilIdle()

        val active = handler.activePathway.value
        assertNotNull(active)
        assertEquals(NeuralPathwayType.AUTONOMOUS_VALIDATION, active?.pathwayType)
        assertEquals(GesturePhysicalType.LONG_PRESS_CHARGE, active?.triggerGesture)
        assertEquals(6, navigatedTab)
        assertTrue("Validation must be triggered", validationTriggered)
    }

    @Test
    fun testTapBurstTriggersEntropyReseed() = runTest {
        val handler = createHandler(this)
        val vector = GestureVectorData(
            startOffset = Offset(150f, 150f),
            endOffset = Offset(150f, 150f),
            deltaX = 0f,
            deltaY = 0f,
            velocityX = 0f,
            velocityY = 0f,
            distancePx = 0f,
            durationMs = 100L,
            contactCount = 1
        )

        handler.processGestureVector(vector)
        advanceUntilIdle()

        val active = handler.activePathway.value
        assertNotNull(active)
        assertEquals(NeuralPathwayType.ENTROPY_RESEED, active?.pathwayType)
        assertEquals(GesturePhysicalType.DOUBLE_TAP_PULSE, active?.triggerGesture)
        assertTrue("Entropy reseed must be executed", entropyReseeded)
    }

    @Test
    fun testPinchInNavigatesToMacroOverview() = runTest {
        val handler = createHandler(this)
        val vector = GestureVectorData(
            startOffset = Offset(100f, 100f),
            endOffset = Offset(150f, 150f),
            deltaX = 50f,
            deltaY = 50f,
            velocityX = 0f,
            velocityY = 0f,
            distancePx = 70f,
            durationMs = 250L,
            contactCount = 2,
            scaleFactor = 0.65f // Pinch in
        )

        handler.processGestureVector(vector)
        advanceUntilIdle()

        val active = handler.activePathway.value
        assertNotNull(active)
        assertEquals(NeuralPathwayType.MACRO_OVERVIEW, active?.pathwayType)
        assertEquals(0, navigatedTab)
    }

    @Test
    fun testPinchOutNavigatesToDeepNeuralStream() = runTest {
        val handler = createHandler(this)
        val vector = GestureVectorData(
            startOffset = Offset(100f, 100f),
            endOffset = Offset(200f, 200f),
            deltaX = 100f,
            deltaY = 100f,
            velocityX = 0f,
            velocityY = 0f,
            distancePx = 140f,
            durationMs = 250L,
            contactCount = 2,
            scaleFactor = 1.45f // Pinch out
        )

        handler.processGestureVector(vector)
        advanceUntilIdle()

        val active = handler.activePathway.value
        assertNotNull(active)
        assertEquals(NeuralPathwayType.DEEP_NEURAL_STREAM, active?.pathwayType)
        assertEquals(3, navigatedTab)
    }

    @Test
    fun testAutoNavigationSuppression() = runTest {
        val handler = createHandler(this)
        handler.setAutoNavigationEnabled(false)
        assertFalse(handler.isAutoNavigationEnabled.value)

        val vector = GestureVectorData(
            startOffset = Offset(200f, 600f),
            endOffset = Offset(200f, 150f),
            deltaX = 0f,
            deltaY = -450f,
            velocityX = 0f,
            velocityY = -1500f,
            distancePx = 450f,
            durationMs = 140L
        )

        handler.processGestureVector(vector)
        advanceUntilIdle()

        // Pathway still decodes and fires
        assertNotNull(handler.activePathway.value)
        assertEquals(NeuralPathwayType.ELEVATE_ENCLAVE, handler.activePathway.value?.pathwayType)
        // But navigation callback is suppressed
        assertNull(navigatedTab)
    }

    @Test
    fun testTriggerSimulatedPathwayDirectly() = runTest {
        val handler = createHandler(this)
        handler.triggerSimulatedPathway(NeuralPathwayType.AUTONOMOUS_VALIDATION)
        advanceUntilIdle()

        val active = handler.activePathway.value
        assertNotNull(active)
        assertEquals(NeuralPathwayType.AUTONOMOUS_VALIDATION, active?.pathwayType)
        assertTrue(validationTriggered)
        assertEquals(6, navigatedTab)

        val history = handler.pathwayHistory.value
        assertFalse(history.isEmpty())
        assertEquals(NeuralPathwayType.AUTONOMOUS_VALIDATION, history.first().pathwayType)
    }

    @Test
    fun testSensitivityCalibration() = runTest {
        val handler = createHandler(this)
        handler.setSensitivityMultiplier(1.85f)
        assertEquals(1.85f, handler.sensitivityMultiplier.value, 0.001f)

        // Clamping check
        handler.setSensitivityMultiplier(10.0f)
        assertEquals(2.5f, handler.sensitivityMultiplier.value, 0.001f)

        handler.setSensitivityMultiplier(0.1f)
        assertEquals(0.5f, handler.sensitivityMultiplier.value, 0.001f)
    }
}
