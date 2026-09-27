package com.example

import com.example.model.PriorityTier
import com.example.model.UserActionCategory
import com.example.service.NeuralIntentRouterEngine
import org.junit.Assert.*
import org.junit.Test

class NeuralIntentRouterTest {

    @Test
    fun testCategorizesCriticalSecurityAction() {
        val task = NeuralIntentRouterEngine.analyzeAndScoreIntent("Emergency Enclave Lockdown & Revoke All Master Keys")
        assertEquals(UserActionCategory.CRITICAL_SECURITY, task.category)
        assertTrue("Critical task should score >= 85", task.priorityScore >= 85)
        assertEquals(PriorityTier.TIER_P1_CRITICAL, task.priorityTier)
        assertTrue(task.scoringBreakdown.enclaveRequirement)
    }

    @Test
    fun testCategorizesIdentityAttestationAction() {
        val task = NeuralIntentRouterEngine.analyzeAndScoreIntent("Initiate Class 3 Biometric StrongBox Attestation Challenge")
        assertEquals(UserActionCategory.IDENTITY_ATTESTATION, task.category)
        assertTrue(task.priorityScore in 65..95)
        assertTrue(task.scoringBreakdown.enclaveRequirement)
    }

    @Test
    fun testCategorizesDataTransmissionAction() {
        val task = NeuralIntentRouterEngine.analyzeAndScoreIntent("Export ECTT Differential Privacy Telemetry Packet Stream")
        assertEquals(UserActionCategory.DATA_TRANSMISSION, task.category)
    }

    @Test
    fun testCategorizesSystemOptimizationAction() {
        val task = NeuralIntentRouterEngine.analyzeAndScoreIntent("Run 4-Pass Memory Zeroization Scrub on Coprocessor")
        assertEquals(UserActionCategory.SYSTEM_OPTIMIZATION, task.category)
    }

    @Test
    fun testCategorizesRoutineMonitoringAction() {
        val task = NeuralIntentRouterEngine.analyzeAndScoreIntent("Check mesh node latency and status heartbeat")
        assertEquals(UserActionCategory.ROUTINE_MONITORING, task.category)
        assertTrue("Routine monitoring score should be lower than critical", task.priorityScore < 70)
    }

    @Test
    fun testDynamicTaskPrioritizationSorting() {
        val initialQueue = NeuralIntentRouterEngine.getInitialPrioritizedTasks()
        assertTrue("Queue must have tasks", initialQueue.isNotEmpty())

        // Verify sorted descending by priorityScore
        for (i in 0 until initialQueue.size - 1) {
            assertTrue(
                "Task at $i (${initialQueue[i].priorityScore}) must have score >= task at ${i+1} (${initialQueue[i+1].priorityScore})",
                initialQueue[i].priorityScore >= initialQueue[i+1].priorityScore
            )
        }
    }

    @Test
    fun testDynamicUrgencyAdjustmentReprioritization() {
        val baseTask = NeuralIntentRouterEngine.analyzeAndScoreIntent("Routine mesh heartbeat", manualUrgencyOverride = 2.0f)
        val boostedTask = NeuralIntentRouterEngine.analyzeAndScoreIntent("Routine mesh heartbeat", manualUrgencyOverride = 10.0f)

        assertTrue(
            "Boosted urgency should yield a strictly higher priority score",
            boostedTask.priorityScore > baseTask.priorityScore
        )
    }
}
