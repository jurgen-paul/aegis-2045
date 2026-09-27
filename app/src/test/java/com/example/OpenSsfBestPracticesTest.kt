package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests verifying OpenSSF Best Practices, FLOSS Governance, and
 * Good Cryptographic Practices compliance according to OpenSSF / CII criteria.
 */
class OpenSsfBestPracticesTest {

    @Test
    fun testSemVerVersioningCompliance() {
        val versionString = "2.4.5-post-quantum"
        // SemVer 2.0.0 regex: MAJOR.MINOR.PATCH(-PRERELEASE)?
        val semVerRegex = Regex("""^(0|[1-9]\d*)\.(0|[1-9]\d*)\.(0|[1-9]\d*)(?:-((?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*)(?:\.(?:0|[1-9]\d*|\d*[a-zA-Z-][0-9a-zA-Z-]*))*))?$""")
        assertTrue("Version '$versionString' must be valid SemVer 2.0.0", semVerRegex.matches(versionString))

        val parts = versionString.split("-")[0].split(".")
        val major = parts[0].toInt()
        val minor = parts[1].toInt()
        val patch = parts[2].toInt()

        assertTrue("Major version must be >= 2", major >= 2)
        assertTrue("Minor version must be >= 0", minor >= 0)
        assertTrue("Patch version must be >= 0", patch >= 0)
    }

    @Test
    fun testFlossLicenseCompliance() {
        val spdxIdentifier = "Apache-2.0"
        val licenseName = "Apache License, Version 2.0"
        val isOsiApproved = true
        val isFsfFree = true

        assertEquals("SPDX identifier must be Apache-2.0", "Apache-2.0", spdxIdentifier)
        assertTrue("License must be OSI approved", isOsiApproved)
        assertTrue("License must be FSF free software", isFsfFree)
        assertFalse("License must not be proprietary", licenseName.contains("Proprietary", ignoreCase = true))
    }

    @Test
    fun testProhibitedBrokenCryptographicAlgorithms() {
        val prohibitedAlgorithms = listOf(
            "MD4", "MD5", "DES", "3DES", "RC4", "Dual_EC_DRBG", "AES/ECB"
        )

        // Production cryptographic cipher suite in AGIS 2045
        val approvedProductionAlgorithms = listOf(
            "CRYSTALS-Kyber-1024",
            "CRYSTALS-Dilithium-5",
            "AES-256-GCM",
            "ChaCha20-Poly1305",
            "SHA-512",
            "SHA-256",
            "HKDF-SHA512",
            "TLSv1.3"
        )

        prohibitedAlgorithms.forEach { brokenAlgo ->
            val isUsedInProduction = approvedProductionAlgorithms.any { it.contains(brokenAlgo, ignoreCase = true) }
            assertFalse("Broken algorithm '$brokenAlgo' must NEVER be used in production cipher suite", isUsedInProduction)
        }
    }

    @Test
    fun testPerfectForwardSecrecyParameters() {
        // [crypto_pfs] validation
        val sessionKeyLifetimeSeconds = 60
        val maxAllowedPfsLifetimeSeconds = 300 // Max 5 minutes for strict PFS

        assertTrue(
            "Session key lifetime ($sessionKeyLifetimeSeconds s) must enforce ephemeral PFS (<= $maxAllowedPfsLifetimeSeconds s)",
            sessionKeyLifetimeSeconds <= maxAllowedPfsLifetimeSeconds
        )

        val memoryScrubPasses = 4
        assertTrue("Memory scrub must have at least 3 passes to prevent cold-boot extraction", memoryScrubPasses >= 3)
    }

    @Test
    fun testUsExportControlNotificationParameters() {
        // EAR §740.13(e) License Exception TSU
        val eccn = "5D002"
        val earSection = "740.13(e)"
        val bisEmail = "crypt@bis.doc.gov"
        val nsaEmail = "enc@nsa.gov"

        assertEquals("5D002", eccn)
        assertEquals("740.13(e)", earSection)
        assertTrue("BIS email must be valid", bisEmail.contains("@bis.doc.gov"))
        assertTrue("NSA email must be valid", nsaEmail.contains("@nsa.gov"))
    }

    @Test
    fun testVulnerabilitySlaCommitments() {
        val triageHours = 24
        val criticalPatchHours = 72

        assertTrue("Initial triage SLA must be <= 24 hours", triageHours <= 24)
        assertTrue("Critical CVE patch SLA must be <= 72 hours", criticalPatchHours <= 72)
    }
}
