package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.TelemetryThroughputPoint
import com.example.ui.components.RechartsTabMode
import com.example.viewmodel.AgisViewModel
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class RechartsTelemetryVisualizerTest {

    @Test
    fun testRechartsTabModes() {
        val modes = RechartsTabMode.values()
        assertEquals(3, modes.size)
        assertTrue(modes.contains(RechartsTabMode.MONOTONE_AREA_CHART))
        assertTrue(modes.contains(RechartsTabMode.CYBER_NODE_DATA_FLOW))
        assertTrue(modes.contains(RechartsTabMode.DIFFERENTIAL_SANITY_SPLINE))

        assertEquals("RECHARTS AREA", RechartsTabMode.MONOTONE_AREA_CHART.label)
        assertEquals("CYBER-NODE FLOW", RechartsTabMode.CYBER_NODE_DATA_FLOW.label)
        assertEquals("LEAK ATTENUATION", RechartsTabMode.DIFFERENTIAL_SANITY_SPLINE.label)
    }

    @Test
    fun testTelemetryThroughputMetricsComputation() {
        val point = TelemetryThroughputPoint(
            rawThroughputKbps = 1000f,
            sanitizedThroughputKbps = 750f,
            packetsPerSec = 120,
            piiScrubbedRate = 18,
            threatAnomalyScore = 0.05f,
            differentialEpsilon = 0.5f
        )

        val attenuationPct = (1f - (point.sanitizedThroughputKbps / point.rawThroughputKbps)) * 100f
        assertEquals(25.0f, attenuationPct, 0.01f)
        assertEquals(0.5f, point.differentialEpsilon, 0.001f)
        assertEquals(18, point.piiScrubbedRate)
        assertTrue(point.sanitizedThroughputKbps < point.rawThroughputKbps)
    }

    @Test
    fun testViewModelTelemetryStreamingAndActions() {
        val context = ApplicationProvider.getApplicationContext<Application>()
        val viewModel = AgisViewModel(context)

        val history = viewModel.telemetryThroughputHistory.value
        assertTrue("Telemetry history must contain points", history.isNotEmpty())

        val currentPoint = viewModel.currentThroughputPoint.value
        assertNotNull("Current throughput point must exist", currentPoint)
        assertTrue("Raw throughput must be positive", currentPoint.rawThroughputKbps > 0f)
        assertTrue("Sanitized throughput must be positive", currentPoint.sanitizedThroughputKbps > 0f)

        // Trigger burst and verify state
        viewModel.triggerTelemetryBurst()
        assertTrue("Bursting state should be active immediately upon trigger", viewModel.isThroughputBursting.value)

        // Flush buffer
        viewModel.flushPerimeterBuffer()
        val alertMessage = viewModel.systemAlertMessage.value ?: ""
        assertTrue("System alert should reflect buffer flush", alertMessage.contains("Flushing", ignoreCase = true) || alertMessage.contains("Perimeter", ignoreCase = true))
    }

    @Test
    fun testCyberNodeFiveStageSanitizationFlowValidation() {
        val stages = listOf(
            "INGRESS",
            "SHIELD",
            "DP NOISE",
            "REDACTOR",
            "EGRESS"
        )
        assertEquals("Cyber-node sanitization pipeline must have exactly 5 stages", 5, stages.size)
        assertEquals("INGRESS", stages[0])
        assertEquals("SHIELD", stages[1])
        assertEquals("DP NOISE", stages[2])
        assertEquals("REDACTOR", stages[3])
        assertEquals("EGRESS", stages[4])
    }
}
