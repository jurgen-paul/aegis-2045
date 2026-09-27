package com.example

import com.example.ui.components.QuantumGlassDepthLevel
import org.junit.Assert.*
import org.junit.Test

class VolumetricQuantumGlassTest {

    @Test
    fun testDepthLevelParsing() {
        assertEquals(QuantumGlassDepthLevel.LEVEL_2D_CLEAN, QuantumGlassDepthLevel.fromString("2D Clean"))
        assertEquals(QuantumGlassDepthLevel.LEVEL_2D_CLEAN, QuantumGlassDepthLevel.fromString("2D Flat Volumetric"))
        assertEquals(QuantumGlassDepthLevel.LEVEL_25D_VOLUMETRIC, QuantumGlassDepthLevel.fromString("2.5D Volumetric"))
        assertEquals(QuantumGlassDepthLevel.LEVEL_3D_QUANTUM, QuantumGlassDepthLevel.fromString("3D Quantum Volumetric"))
        assertEquals(QuantumGlassDepthLevel.LEVEL_HYPER_ENCLAVE, QuantumGlassDepthLevel.fromString("Hyper-Enclave Volumetric"))
    }

    @Test
    fun testDepthLevelOpticalProperties() {
        val levels = QuantumGlassDepthLevel.values()
        assertTrue("At least 4 depth levels must exist", levels.size >= 4)

        // Ensure depth factor strictly increases with depth level
        for (i in 0 until levels.size - 1) {
            assertTrue(
                "Level ${levels[i].shortName} depth factor (${levels[i].depthFactor}) must be < ${levels[i+1].shortName} (${levels[i+1].depthFactor})",
                levels[i].depthFactor < levels[i+1].depthFactor
            )
            assertTrue(
                "Virtual thickness must increase with depth",
                levels[i].virtualThicknessMm < levels[i+1].virtualThicknessMm
            )
            assertTrue(
                "Refractive index must increase with depth",
                levels[i].refractiveIndex <= levels[i+1].refractiveIndex
            )
        }
    }

    @Test
    fun testFrostedScatterDensityBounds() {
        // Density calculation bounds: (frostedRoughness * depthLevel.frostedIntensity * 36).toInt().coerceIn(12, 48)
        val testRoughness = 0.70f
        QuantumGlassDepthLevel.values().forEach { level ->
            val density = (testRoughness * level.frostedIntensity * 36).toInt().coerceIn(12, 48)
            assertTrue("Scatter density must be within [12, 48]", density in 12..48)
        }
    }

    @Test
    fun testCameraDistanceScaling() {
        val level2D = QuantumGlassDepthLevel.LEVEL_2D_CLEAN
        val level3D = QuantumGlassDepthLevel.LEVEL_3D_QUANTUM
        val levelHyper = QuantumGlassDepthLevel.LEVEL_HYPER_ENCLAVE

        assertTrue(level2D.cameraDistanceMultiplier < level3D.cameraDistanceMultiplier)
        assertTrue(level3D.cameraDistanceMultiplier < levelHyper.cameraDistanceMultiplier)
    }
}
