package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import androidx.test.core.app.ApplicationProvider
import com.example.service.HardwareSensorEntropyService
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowSensorManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HardwareSensorEntropyServiceTest {

    @Test
    fun testHighEntropyBytesGeneration() {
        // Generate two distinct high-entropy random byte arrays (512-bit / 64 bytes each)
        val randomBytes1 = HardwareSensorEntropyService.generateHighEntropyBytes(64)
        val randomBytes2 = HardwareSensorEntropyService.generateHighEntropyBytes(64)

        assertEquals("Should produce exactly 64 bytes (512 bits)", 64, randomBytes1.size)
        assertEquals("Should produce exactly 64 bytes (512 bits)", 64, randomBytes2.size)

        // Consecutive generations must produce different random byte sequences
        assertFalse("Subsequent random generations must differ", randomBytes1.contentEquals(randomBytes2))
    }

    @Test
    fun testSecureHexNonceGeneration() {
        val nonce32 = HardwareSensorEntropyService.generateSecureHexNonce(32)
        val nonce64 = HardwareSensorEntropyService.generateSecureHexNonce(64)

        assertEquals("Hex length should be double byte length (64 hex chars for 32 bytes)", 64, nonce32.length)
        assertEquals("Hex length should be double byte length (128 hex chars for 64 bytes)", 128, nonce64.length)
        assertNotEquals("Generated hex nonces must be distinct", nonce32, nonce64)
    }

    @Test
    fun testEntropyStateFlowInitialState() {
        val state = HardwareSensorEntropyService.entropyStateFlow.value
        assertNotNull("Entropy state flow must not be null", state)
        assertEquals("Pool entropy bits should be 512-bit", 512, state.poolEntropyBits)
        assertTrue("Estimated Shannon entropy should be close to 8.0 bits/byte", state.estimatedShannonEntropy >= 6.0)
        assertEquals("NIST SP 800-90A / HMAC-SHA512 + HKDF", state.primaryAlgorithm)
        assertTrue("Peak jitter should be positive", state.peakJitterMicroG > 0f)
        assertTrue("Noise floor should be negative in RF range", state.noiseFloorDb < 0f)
        assertEquals("Nyquist rate should be 120 Hz", 120, state.nyquistRateHz)
    }

    @Test
    fun testServiceLifecycleAndStartIntent() {
        val context: Application = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, HardwareSensorEntropyService::class.java).apply {
            action = HardwareSensorEntropyService.ACTION_START_HARVEST
        }

        val controller = Robolectric.buildService(HardwareSensorEntropyService::class.java, intent)
        val service = controller.create().startCommand(0, 0).get()

        assertNotNull("Service instance must be created", service)
        assertTrue("Service state should reflect running", HardwareSensorEntropyService.entropyStateFlow.value.isRunning)

        // Trigger manual reseed
        service.forceEntropyReseed()
        val stateAfterReseed = HardwareSensorEntropyService.entropyStateFlow.value
        assertNotNull(stateAfterReseed.latestRandomBytes64)

        controller.destroy()
    }
}
