# AGIS 2045: Official Project Website & OpenSSF Governance

> **Welcome to the official project website and OpenSSF Best Practices Badge attestation for AGIS 2045: Zero-Trust Cyber-Node Operating Environment.**

For the full OpenSSF Best Practices criteria, licensing, cryptographic policies, and developer guides, please see:
👉 **[docs/PROJECT_WEBSITE_AND_GOVERNANCE.md](docs/PROJECT_WEBSITE_AND_GOVERNANCE.md)**

---

## ⚡ Quick Links & Core Summary

- 🎯 **What AGIS 2045 Does**: Zero-Trust Biomorphic Cyber-Node environment featuring volumetric quantum glass UI, neural intent routing, 512-bit post-quantum hardware enclave storage (Kyber-1024 / Dilithium-5), differential privacy telemetry sanitization, and 5-stage shield defense.
- 📜 **FLOSS License**: [Apache License 2.0](LICENSE) (OSI-approved, FSF-free, patent grant included).
- 📚 **Documentation**:
  - [Security Architecture Specification](docs/SECURITY_ARCHITECTURE.md)
  - [ECTT Telemetry Protocol Specification](docs/ECTT_TELEMETRY_PROTOCOL.md)
  - [Compliance, Standards & Regulatory Audit](docs/COMPLIANCE_AND_STANDARDS.md)
  - [OpenAPI 3.0.3 Security Specification](openapi.yaml)
  - [Project Website & Governance](docs/PROJECT_WEBSITE_AND_GOVERNANCE.md)
- 🌐 **Public Source Code Repository**: [https://github.com/your-org/agis-2045.git](https://github.com/your-org/agis-2045.git)
- 🏷️ **Unique Version Numbering**: Semantic Versioning 2.0.0 (`v2.4.5-post-quantum`, Version Code: `20405`).
- 📝 **Release Notes**: Maintained in [docs/PROJECT_WEBSITE_AND_GOVERNANCE.md#6-release-notes--change-history](docs/PROJECT_WEBSITE_AND_GOVERNANCE.md#6-release-notes--change-history).
- 🐛 **Bug-Reporting Process**: Public Issue Tracker at [https://github.com/your-org/agis-2045/issues](https://github.com/your-org/agis-2045/issues).
- 🛡️ **Vulnerability Disclosure Policy**: Coordinated Vulnerability Disclosure via [SECURITY.md](SECURITY.md) and `security@agis2045.local` with 24h response / 72h remediation SLAs.
- 🔨 **Working Build System**: Gradle Kotlin DSL (`gradle assembleDebug`, `gradle assembleRelease`).
- 🧪 **Automated Test Suite**: JUnit, Robolectric, and Roborazzi (`gradle :app:testDebugUnitTest`).
- ✅ **New Functionality Testing**: Mandatory automated unit and integration tests required for all PRs.
- ⚠️ **Compiler Warning Flags**: Strict compiler warning flags, Android Lint zero-storage enforcement.
- 🔐 **Secure Development Knowledge**: OWASP MASVS v2.0, STRIDE/DREAD threat matrices, 4-pass memory zeroization.
- 🛡️ **Cryptographic Practices**:
  - **US Export Controls Notice**: EAR §740.13(e) / ECCN 5D002 notification submitted to `crypt@bis.doc.gov` & `enc@nsa.gov`.
  - **No Broken Algorithms**: Zero MD4, MD5, single DES, RC4, Dual_EC_DRBG, or ECB modes.
  - **Expert-Reviewed Algorithms**: NIST FIPS 203 (Kyber-1024), NIST FIPS 204 (Dilithium-5), AES-256-GCM, HKDF-SHA512, TLS 1.3.
  - **Perfect Forward Secrecy (`[crypto_pfs]`)**: Automated 60-second ephemeral session key rotation; long-term key compromise does not compromise past sessions.
  - **MITM Attack Defense**: TLS 1.3, strict certificate pinning, Dilithium-5 packet signature verification, Android APK Signature Scheme v3.
  - **Vulnerabilities Fixed**: Automated CVE dependency scanning, 72h critical remediation turnaround.
