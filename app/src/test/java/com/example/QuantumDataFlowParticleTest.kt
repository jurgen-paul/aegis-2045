package com.example

import com.example.model.ThreatSeverity
import com.example.ui.components.QuantumDataFlowConfig
import com.example.ui.components.QuantumDataFlowState
import org.junit.Assert.*
import org.junit.Test

class QuantumDataFlowParticleTest {

    @Test
    fun testDefaultConfigParameters() {
        val config = QuantumDataFlowConfig()
        assertTrue("Particle count must be positive", config.particleCount > 0)
        assertTrue("Speed multiplier must be positive", config.speedMultiplier > 0f)
        assertTrue("Base particle alpha must be between 0 and 1", config.baseParticleAlpha in 0f..1f)
        assertTrue("Filament max alpha must be between 0 and 1", config.filamentMaxAlpha in 0f..1f)
        assertTrue("Synaptic filaments must be enabled by default", config.enableSynapticFilaments)
        assertTrue("Waveguides must be enabled by default", config.enableWaveguides)
    }

    @Test
    fun testParticleInitialization() {
        val count = 30
        val config = QuantumDataFlowConfig(particleCount = count)
        val state = QuantumDataFlowState(config)

        assertEquals("Particle array size matches config", count, state.particles.size)

        var hasNexus = false
        var hasDeepTier = false
        var hasMidTier = false
        var hasSurfaceTier = false

        for (p in state.particles) {
            assertTrue("Normalized X within [0, 1]", p.normX in 0f..1f)
            assertTrue("Normalized Y within [0, 1]", p.normY in 0f..1f)
            assertTrue("Base radius positive", p.baseRadiusDp > 0f)
            assertTrue("Depth tier within [0, 2]", p.depthTier in 0..2)

            if (p.isNexusNode) hasNexus = true
            when (p.depthTier) {
                0 -> hasDeepTier = true
                1 -> hasMidTier = true
                2 -> hasSurfaceTier = true
            }
        }

        assertTrue("Should contain nexus node", hasNexus)
        assertTrue("Should contain deep tier particles", hasDeepTier)
        assertTrue("Should contain mid tier particles", hasMidTier)
        assertTrue("Should contain surface tier particles", hasSurfaceTier)
    }

    @Test
    fun testThreatStateReaction() {
        val state = QuantumDataFlowState()
        assertEquals(ThreatSeverity.LOW, state.activeThreatSeverity)

        state.activeThreatSeverity = ThreatSeverity.CRITICAL
        assertEquals(ThreatSeverity.CRITICAL, state.activeThreatSeverity)
    }
}
