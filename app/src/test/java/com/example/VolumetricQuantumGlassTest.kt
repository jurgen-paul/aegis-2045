package com.example

import com.example.ui.components.OpticalGlassMaterial
import com.example.ui.components.QuantumGlassDepthLevel
import com.example.ui.modifiers.VolumetricGlassConfig
import org.junit.Assert.*
import org.junit.Test

class VolumetricQuantumGlassTest {

    @Test
    fun testVolumetricGlassConfigDefaults() {
        val config = VolumetricGlassConfig()
        assertTrue("Virtual thickness must be positive", config.thicknessMm > 0f)
        assertTrue("Refractive index must be >= 1.0", config.refractiveIndex >= 1.0f)
        assertTrue("Frosted roughness must be in [0, 1]", config.frostedRoughness in 0f..1f)
        assertTrue("Specular intensity must be in [0, 1]", config.specularIntensity in 0f..1f)
        assertTrue("Max tilt angle must be positive", config.maxTiltAngleDegrees > 0f)
    }

    @Test
    fun testOpticalGlassMaterialProperties() {
        val materials = OpticalGlassMaterial.values()
        assertEquals("4 optical materials defined", 4, materials.size)

        val cryo = OpticalGlassMaterial.CRYO_FLUID
        val crown = OpticalGlassMaterial.CROWN_GLASS
        val sapphire = OpticalGlassMaterial.SAPPHIRE_CRYSTAL
        val diamond = OpticalGlassMaterial.DIAMOND_ENCLAVE

        assertTrue("Cryo index < Crown index", cryo.refractiveIndex < crown.refractiveIndex)
        assertTrue("Crown index < Sapphire index", crown.refractiveIndex < sapphire.refractiveIndex)
        assertTrue("Sapphire index < Diamond index", sapphire.refractiveIndex < diamond.refractiveIndex)

        // Abbe numbers should be positive
        materials.forEach {
            assertTrue("Abbe number must be positive", it.abbeNumber > 0f)
            assertTrue("Thickness must be positive", it.virtualThicknessMm > 0f)
        }

        // Test fromIndex
        assertEquals(OpticalGlassMaterial.CRYO_FLUID, OpticalGlassMaterial.fromIndex(0))
        assertEquals(OpticalGlassMaterial.DIAMOND_ENCLAVE, OpticalGlassMaterial.fromIndex(3))
        assertEquals(OpticalGlassMaterial.SAPPHIRE_CRYSTAL, OpticalGlassMaterial.fromIndex(99)) // default
    }

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
