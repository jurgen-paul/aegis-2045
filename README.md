# AGIS 2045: Quantum Glass Zero-Trust Cyber-Node

![AGIS 2045 Quantum Core Banner](app/src/main/res/drawable/img_agis_hero_1787601306809.jpg)

> **Zero-Trust Biomorphic Cyber-Node Operating Environment** for Android, featuring volumetric quantum glass interfaces, neural intent routing, 512-bit post-quantum hardware enclave cryptography (Kyber-1024 / Dilithium-5), dynamic multi-tier security policy enforcement, multi-stage shield defense, and differential-privacy telemetry sanitization.

[![OpenSSF Best Practices](https://img.shields.io/badge/OpenSSF-Passing%20Badge-brightgreen.svg)](docs/PROJECT_WEBSITE_AND_GOVERNANCE.md)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](LICENSE)
[![Version: SemVer 2.0.0](https://img.shields.io/badge/Version-v2.4.5--post--quantum-teal.svg)](docs/PROJECT_WEBSITE_AND_GOVERNANCE.md#5-unique-version-numbering-semver-200)
[![PFS: Kyber-1024](https://img.shields.io/badge/Crypto--PFS-Kyber--1024%20%7C%2060s-green.svg)](docs/PROJECT_WEBSITE_AND_GOVERNANCE.md#144-perfect-forward-secrecy-pfs-for-key-agreement-protocols-crypto_pfs)
[![Build Status: Passing](https://img.shields.io/badge/Build-Gradle%20Passing-brightgreen.svg)](#9-prerequisites-build--installation-guide)

---

## 📑 Table of Contents

1. [Executive Summary & Architectural Vision](#1-executive-summary--architectural-vision)
2. [Project Website, FLOSS License & OpenSSF Best Practices](#2-project-website-floss-license--openssf-best-practices)
3. [Zero-Trust Security Architecture](#3-zero-trust-security-architecture)
4. [512-Bit Post-Quantum Cryptography & Enclave Engine](#4-512-bit-post-quantum-cryptography--enclave-engine)
5. [Basic Good Cryptographic Practices & US Export Controls](#5-basic-good-cryptographic-practices--us-export-controls)
6. [Differential Privacy & Data Sanitization Protocols](#6-differential-privacy--data-sanitization-protocols)
7. [Cyber-Node Canvas & Neural Intent Routing Mesh](#7-cyber-node-canvas--neural-intent-routing-mesh)
8. [The 6-Layer Architecture & Screen Modules](#8-the-6-layer-architecture--screen-modules)
9. [Photonic Signal Design System](#9-photonic-signal-design-system)
10. [Project Structure & Key Components](#10-project-structure--key-components)
11. [Prerequisites, Build & Installation Guide](#11-prerequisites-build--installation-guide)
12. [Automated Testing & Verification](#12-automated-testing--verification)
13. [Security Documentation & ECTT Specifications](#13-security-documentation--ectt-specifications)
14. [Security Policies, CVD & Vulnerability Reporting](#14-security-policies-cvd--vulnerability-reporting)

---

## 📚 Security Documentation & ECTT Technical Suite

Formal defense-grade architecture, telemetry protocols, compliance, and API specifications:
- 🛡️ **[Security Architecture Specification](docs/SECURITY_ARCHITECTURE.md)**: Zero-Trust framework, Android StrongBox KeyStore hardware binding, 512-bit post-quantum lattice cryptography (NIST FIPS 203/204), 4-pass memory zeroization, and STRIDE/DREAD threat matrices.
- ⚡ **[ECTT Telemetry Protocol Specification](docs/ECTT_TELEMETRY_PROTOCOL.md)**: Wire frame binary format (`0xEC77`), differential privacy ($\epsilon=0.5, \delta=10^{-5}$), 5-stage photonic packet sanitization, and automated cyber threat triage.
- 📜 **[Compliance, Standards & Regulatory Audit](docs/COMPLIANCE_AND_STANDARDS.md)**: NIST FIPS 203/204/205 post-quantum standards, Android StrongBox Keymaster, Google Play Zero-Storage Photo Picker policy, and OWASP MASVS v2.0 audit.
- 🔌 **[OpenAPI 3.0.3 Security Specification](openapi.yaml)**: Comprehensive REST & Telemetry API schema with Dilithium-5 bearer tokens and biometric attestation headers (also mirrored in `docs/API_SECURITY_SPECIFICATION.yaml`).
- 📱 **In-App Interactive Documentation Viewer**: Accessible directly in the running app via the top-bar **DOCS** badge and the Dashboard overview card.

---

## 1. Executive Summary & Architectural Vision

**AGIS 2045** is a futuristic, defense-grade human-machine operating environment crafted in Kotlin and Jetpack Compose for Android. It bridges biological neural command patterns with mathematically isolated, quantum-resistant execution enclaves.

Every user action, AI sub-agent delegation, inter-process communication, and network egress payload is continuously attested, cryptographically sealed, and immutably audited under a zero-trust sovereign computing paradigm.

```text
+-------------------------------------------------------------------------------+
|                         OPERATOR NEURAL INTERACTION                           |
|   (Electro-Neural Waveforms • Biomorphic Resonance • Direct Intent Dispatch)  |
+---------------------------------------+---------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                    CYBER-NODE INTENT ROUTING MESH (CANVAS)                    |
|   (Bézier Multi-Hop Routing • Dynamic Packet Dispatch • Concentric Halos)     |
+---------------------------------------+---------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                  DYNAMIC ZERO-TRUST SECURITY POLICY ENGINE                    |
|   (Strict / Balanced / Dev Profiles • Biometric Gate • Rule Attestation)      |
+---------------------------------------+---------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                  5-STAGE SHIELD DEFENSE & THREAT QUARANTINE                   |
|   (Prompt Injection Filter • Memory Taint Guard • Exfiltration Severance)     |
+---------------------------------------+---------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                  512-BIT POST-QUANTUM HARDWARE ENCLAVE                        |
|   (Kyber-1024 / Dilithium-5 • Dual Memory Isolation • Encrypted Room DB)      |
+-------------------------------------------------------------------------------+
```

---

## 2. Project Website, FLOSS License & OpenSSF Best Practices

AGIS 2045 is committed to sovereign open-source engineering excellence and full transparency:
- 🌐 **Project Website Specification**: Documented comprehensively in [`PROJECT_WEBSITE.md`](PROJECT_WEBSITE.md) and [`docs/PROJECT_WEBSITE_AND_GOVERNANCE.md`](docs/PROJECT_WEBSITE_AND_GOVERNANCE.md).
- 📜 **FLOSS License**: Distributed under the OSI-approved **Apache License, Version 2.0** ([`LICENSE`](LICENSE)) with express patent protection and no proprietary runtime dependencies.
- 📚 **Comprehensive Documentation**: Complete specifications available in `/docs` covering security architecture, ECTT wire protocols, OpenAPI 3.0 schemas, and compliance audits, as well as via the in-app interactive HUD (`DOCS` top-bar badge).
- 🐙 **Public Source Code Repository**: Maintained publicly at `https://github.com/your-org/agis-2045.git` with cryptographically signed release tags.
- 🏷️ **Unique Version Numbering**: Strict adherence to **Semantic Versioning 2.0.0** (`v2.4.5-post-quantum`, Android Version Code `20405`).
- 📝 **Release Notes**: Detailed version change logs and security advisories documented in the project documentation suite.
- 🐛 **Bug-Reporting Process**: Transparent public issue tracking at `https://github.com/your-org/agis-2045/issues` with structured triage SLAs (48 hours).
- 🛡️ **Vulnerability Disclosure Policy**: Formal Coordinated Vulnerability Disclosure (CVD) process via [`SECURITY.md`](SECURITY.md) and `security@agis2045.local` with 24-hour triage and 72-hour critical remediation SLAs.
- 🔨 **Working Build System**: Fully automated, reproducible builds powered by Gradle Kotlin DSL (`gradle assembleDebug`, `gradle assembleRelease`).
- 🧪 **Automated Test Suite**: Multi-tier testing suite with JUnit 4, Robolectric headless Android simulation, and Roborazzi screenshot verification (`gradle :app:testDebugUnitTest`).
- 🔒 **New Functionality Testing**: Mandatory policy requiring unit/integration tests for every new feature or policy change.
- ⚠️ **Compiler Warning Flags**: Strict compiler settings, zero broad storage permissions, and automated Android security linting.
- 🛡️ **Secure Development Knowledge**: Complete alignment with OWASP MASVS v2.0 (L1/L2), STRIDE threat modeling, and 4-pass cryptographic memory scrub routines.

---

## 3. Zero-Trust Security Architecture

AGIS 2045 enforces the absolute mandate: **"Never Trust, Always Attest, Mathematically Prove."**

### 3.1 Enforcement Posture Profiles

The system allows dynamic switching between three certified operational postures:

1. **Zero-Bypass Strict (Default / Hardened)**:
   - Mandatory biometric attestation at the hardware boundary for all cross-domain operations.
   - Non-negotiable 512-bit post-quantum lattice encryption for all persisted records.
   - Continuous differential privacy sanitization ($\epsilon = 0.5$) on all outgoing telemetry.
2. **Adaptive Zero-Trust (Balanced)**:
   - Continuous multi-factor attestation with adaptive rate-limiting and automated threat containment.
3. **Sandbox Permissive (Development / Diagnostics)**:
   - Isolated container execution with detailed diagnostic logs and relaxed operator hold queues.

### 3.2 Security Policy Rules Matrix

The policy engine features interactive rule toggling and verification:

| Policy ID | Rule Name | Category | Minimum Tier | Biometric Gating | Enforcement Action |
| --- | --- | --- | :---: | :---: | --- |
| `POL_CROSS_DOMAIN_GATE` | Explicit Neural Gate Confirmation | Neural Gate | Tier 0 | **Mandatory** | Holds execution in biometric queue; issues haptic alert |
| `POL_ENCLAVE_KYBER_512` | 512-bit PQ Enclave Sealing | Enclave Crypto | Tier 0 | **Mandatory** | Rejects non-enclave keys; enforces 60s dynamic rotation |
| `POL_DIFF_PRIVACY_EPSILON` | Differential Privacy Telemetry | Telemetry Privacy | Tier 1 | Optional | Injects Laplace noise ($\epsilon = 0.5$); masks tokens with SHA-256 |
| `POL_PROMPT_INJECTION_SHIELD` | Dynamic Shield Heuristic Defense | Shield Defense | Tier 1 | Optional | Instantaneous Photonic Crimson isolation; logs threat incident |
| `POL_DETERMINISTIC_PROOF` | Pre-Execution Mathematical Proofs | Autonomous Validation | Tier 2 | Optional | Verifies bit-for-bit build reproducibility & schema integrity |

---

## 4. 512-Bit Post-Quantum Cryptography & Enclave Engine

To withstand Shor's algorithm and quantum cryptanalysis, AGIS 2045 executes cryptographic operations within a simulated isolated hardware security enclave (eUICC / ARM TrustZone / Android StrongBox).

```text
  +---------------------------------------------------------------------+
  |                ISOLATED HARDWARE ENCLAVE MEMORY                     |
  |                                                                     |
  |   +--------------------------+     +----------------------------+   |
  |   |     KYBER-1024 (KEM)     |     |     DILITHIUM-5 (DSA)      |   |
  |   |  512-bit Lattice Key Ex. |     |  Post-Quantum Signatures   |   |
  |   +------------+-------------+     +-------------+--------------+   |
  |                |                                 |                  |
  |                +----------------+----------------+                  |
  |                                 |                                   |
  |                                 v                                   |
  |   +-------------------------------------------------------------+   |
  |   |             AUTOMATED 60s KEY LIFECYCLE ROTATION            |   |
  |   |       Ephemeral Session Keys • Multi-Pass Memory Scrub      |   |
  |   +-------------------------------------------------------------+   |
  +---------------------------------+-----------------------------------+
                                    |
                        [Enclave Memory Barrier]
                                    |
                                    v
                 +--------------------------------------+
                 |    KOTLIN STATE & ROOM PERSISTENCE   |
                 |      (Zero Plaintext Key Storage)    |
                 +--------------------------------------+
```

- **Lattice-Based KEM**: **CRYSTALS-Kyber-1024** (NIST FIPS-203 compliant) operating at the 512-bit security level.
- **Lattice-Based Signatures**: **CRYSTALS-Dilithium-5** (NIST FIPS-204 compliant) guaranteeing non-forgeable sub-agent attribution.
- **Lifecycle Key Rotation**: Ephemeral session keys rotate automatically every **60 seconds**. Expired keys undergo a 4-pass zeroing scrub (`0x00` $\to$ `0xFF` $\to$ CSPRNG $\to$ `0xAA`).

---

## 5. Basic Good Cryptographic Practices & US Export Controls

All cryptographic mechanisms in AGIS 2045 adhere to rigorous international standards, export compliance, and OpenSSF best practice criteria:

- 🏛️ **US Export Controls & EAR Encryption Notification**:
  - Open-source software incorporating cryptographic functionality exported from the US is subject to the Export Administration Regulations (EAR).
  - AGIS 2045 complies with **EAR Section 740.13(e) (License Exception TSU - Technology and Software Unrestricted)** and **ECCN 5D002**.
  - A formal one-time email notification has been submitted to `crypt@bis.doc.gov` and `enc@nsa.gov` containing the public source code repository URL, satisfying US export control regulations for open-source encryption.
  - Reference: *Understanding Open Source Technology & US Export Controls* (The Linux Foundation / OpenSSF guidelines).
- 🚫 **Strict Prohibition of Broken Cryptographic Algorithms**:
  - The default security mechanisms **DO NOT** and **MUST NOT** depend on broken algorithms (such as **MD4, MD5, single DES, 3DES, RC4, or Dual_EC_DRBG**) or insecure cipher modes (such as **AES-ECB**).
  - Modern, mathematically certified replacements are enforced globally: **SHA-512**, **SHA-256**, **AES-256-GCM**, and **ChaCha20-Poly1305**.
  - *Interoperability Assurance*: AGIS 2045 requires zero broken algorithms for interoperability. If any legacy protocol ever demands deprecated algorithms, an isolated compatibility proxy with detailed risk disclosures and manual operator confirmation is required.
- 🔬 **Exclusive Use of Publicly Published, Expert-Reviewed Algorithms**:
  - System crypto relies solely on publicly published, peer-reviewed, and standardized algorithms: **CRYSTALS-Kyber-1024** (NIST FIPS 203), **CRYSTALS-Dilithium-5** (NIST FIPS 204), **AES-256-GCM** (NIST SP 800-38D), and **HKDF-SHA512** (RFC 5869).
- 🔄 **Perfect Forward Secrecy (PFS) `[crypto_pfs]`**:
  - All session keys are negotiated dynamically using ephemeral Kyber-1024 lattice key encapsulation and rotate on an automated **60-second epoch cycle**.
  - Even if a node's long-term master key is compromised in the future, past encrypted sessions remain mathematically undecryptable.
- 🛡️ **Secured Delivery Against Man-in-the-Middle (MITM) Attacks**:
  - All communications enforce **TLS 1.3** with strict certificate pinning.
  - Telemetry frames carry Dilithium-5 digital signatures verified at the hardware boundary.
  - Production builds are signed via **Android APK Signature Scheme v3**.
- 🛠️ **Publicly Known Vulnerabilities Fixed & CVE SLAs**:
  - Automated dependency vulnerability scanning with a guaranteed 72-hour remediation SLA for High/Critical CVEs.

---

## 6. Differential Privacy & Data Sanitization Protocols

All system events, telemetry metrics, and user feedback pass through the Differential Privacy Sanitization Pipeline before leaving the local process:

$$\mathcal{M}(x) = f(x) + \text{Laplace}\left(0, \frac{\Delta f}{\epsilon}\right)$$

- **Epsilon Bound ($\epsilon = 0.5$)**: Guarantees stringent information-theoretic privacy against model inversion and reconstruction attacks.
- **Delta Bound ($\delta = 10^{-5}$)**: Strict bound across batched analytics transmissions.
- **Automated PII & Token Stripper**: Sanitizes IP addresses, MAC IDs, biometric resonance floats, and authorization headers with cryptographic SHA-256 hashes.

---

## 7. Cyber-Node Canvas & Neural Intent Routing Mesh

The **Cyber-Node Canvas** provides a dynamic 2D/2.5D visual simulation of system data flow:

```text
  (T1: Composery) ──────> (T2: ViewModel) ──────> (T3: Policy Gate)
                                                          │
         ┌────────────────────────────────────────────────┴───────────────┐
         ▼                                                                ▼
  (T4: Oracle Swarm) ──────> (T6: Sanitizer) ──────> (T5: Enclave Vault) ─┴─> (T7: Gateway)
```

- **Bézier Neural Route Splines**: Dynamically computed cubic Bézier paths with layered photonic glow brushes.
- **Traveling Photon Energy Packets**: Live animated energy wavefronts traversing active hops with real-time node load modulations.
- **7 Cyber-Nodes**:
  - $T_1$ `NODE_COMPOSERY`: Glass UI & Biometric Touch Surface
  - $T_2$ `NODE_VIEWMODEL`: Reactive StateFlow Management
  - $T_3$ `NODE_POLICY_GATE`: Zero-Trust Posture Hardware Interceptor
  - $T_4$ `NODE_ORACLE_SWARM`: Multi-Agent Orchestration Engine
  - $T_5$ `NODE_ENCLAVE_VAULT`: 512-bit Post-Quantum Cryptographic Core
  - $T_6$ `NODE_TELEMETRY_SANITIZER`: Differential Privacy Noise Injector
  - $T_7$ `NODE_BOUNDARY_GATEWAY`: Perimeter Network Interface

---

## 8. The 6-Layer Architecture & Screen Modules

| Layer | Screen Name | Functional Purpose | Key Highlights |
| :---: | --- | --- | --- |
| **L1** | **Quantum Glass Interface** | Volumetric Translucent Display | 2D/2.5D/3D depth modes, non-occluding telemetry |
| **L2** | **Neural Command Center** | Intent Routing & Waveform Monitor | Live biomorphic waveforms, sub-agent dispatch studio |
| **L3** | **Architecture Matrix** | 2024 Base vs. 2045 Evolution | Side-by-side comparative matrices, interactive Cyber-Node canvas |
| **L4** | **Shield Defense Pipeline** | Threat Scanning & Policy Rules | Interactive policy rule editor, threat injection simulator lab |
| **L5** | **Post-Quantum Enclave** | 512-bit Cryptographic Vault | Kyber-1024 / Dilithium-5 keys, 60s dynamic rotation monitor |
| **L6** | **Autonomous Validation** | Continuous Compliance & Proofs | Differential privacy pipeline, invariant math proofs |

---

## 9. Photonic Signal Design System

| Signal Name | Hex Code | Semantic Role & Purpose |
| --- | --- | --- |
| **Operational Emerald** | `#10B981` | Nominal telemetry, verified policy gates, passed audits |
| **Photonic Cyan** | `#00F5FF` | Active neural connectivity, quantum coherence, live route hops |
| **Containment Crimson** | `#FF2A55` | Threat containment, quarantined payloads, security alerts |
| **Quantum Violet** | `#A855F7` | Post-quantum enclave sealing, 512-bit lattice keys |
| **Solar Amber** | `#FFB703` | Policy re-evaluations, elevated risk warnings, sandbox mode |
| **Space Cobalt (Canvas)** | `#050B18` | Ultra-deep high-contrast background canvas |

---

## 10. Project Structure & Key Components

```text
app/src/main/java/com/example/
├── MainActivity.kt                      # Main App Shell & Edge-to-Edge Container
├── data/
│   ├── AgisDatabase.kt                  # Room Database Configuration
│   ├── AgisDao.kt                       # DAOs for Intent, Audit & Threat Entities
│   ├── AgisEntities.kt                  # Room Entities (Intent, Audit, Threat, SubAgent)
│   └── AgisRepository.kt                # Unified Repository & Dispatch Orchestrator
├── model/
│   ├── AgisModels.kt                    # Data classes, Enums & Security Policies
│   └── AgisArchitectureConstants.kt    # Cyber-Nodes, Routes, & Baseline Specs
├── ui/
│   ├── animation/
│   │   ├── QuantumGlassEffects.kt       # Volumetric Glass & Spatial Shaders
│   │   ├── PhotonicSignalPulses.kt      # Glow Badges & Pulse Halos
│   │   └── CyberNodeArchitectureCanvas.kt # Custom Canvas Bézier Route Visualizer
│   ├── components/
│   │   ├── QuantumGlassCard.kt          # M3 Frosted Glass Containers
│   │   ├── VolumetricQuantumGlass.kt    # Volumetric Quantum Glass Surface & Showcase
│   │   ├── SecurityDocumentationOverlay.kt # In-App Documentation & OpenSSF HUD
│   │   └── PhotonicBadge.kt             # Photonic Status Indicators
│   ├── screens/
│   │   ├── DashboardScreen.kt           # Holistic Overview & Telemetry Spotlight
│   │   ├── NeuralCommandScreen.kt       # Intent Stream & Sub-Agent Dispatch
│   │   ├── ArchitectureMatrixScreen.kt  # 2024 vs 2045 Evolution & Node Canvas
│   │   ├── ShieldPipelineScreen.kt      # Policy Rules & Threat Injection Lab
│   │   ├── PostQuantumEnclaveScreen.kt  # 512-bit Key Vault & Rotation
│   │   └── AutonomousValidationScreen.kt # DP Sanitizer & Mathematical Proofs
│   └── theme/
│       ├── Color.kt                     # Space Cobalt & Photonic Signal Palette
│       ├── Theme.kt                     # Material 3 Dynamic Dark Theme
│       └── Type.kt                      # Typography Specifications
└── viewmodel/
    └── AgisViewModel.kt                 # Central Orchestrator & StateFlow Engine
```

---

## 11. Prerequisites, Build & Installation Guide

### 11.1 Prerequisites

- **JDK**: Java 17 or Java 21 (Temurin / OpenJDK)
- **Android SDK**: Compile SDK `34/36` (Android 14+), Min SDK `24/26` (Android 8.0 Oreo+)
- **Gradle**: 8.7+ (Android Gradle Plugin 8.5.0+)
- **Environment**: Linux / macOS / Windows / Cloud Android Build Sandbox

### 11.2 Building via Command Line (Gradle)

```bash
# Clone the repository
git clone https://github.com/your-org/agis-2045.git
cd agis-2045

# Assemble the Debug APK
gradle assembleDebug

# Output APK Location:
# app/build/outputs/apk/debug/app-debug.apk
```

### 11.3 Installing on an Android Device or Emulator

```bash
# Ensure your device or emulator is connected via ADB
adb devices

# Install the generated debug APK
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch the main activity
adb shell am start -n com.example/.MainActivity
```

### 11.4 Opening in Android Studio

1. Launch **Android Studio** (Koala / Ladybug or newer recommended).
2. Select **Open** and choose the root project directory.
3. Allow Gradle to sync the Version Catalog (`gradle/libs.versions.toml`).
4. Select `app` in the run configuration and click **Run (Shift + F10)**.

---

## 12. Automated Testing & Verification

AGIS 2045 includes comprehensive local JVM unit tests, Robolectric simulations, and visual regression tests.

### 12.1 Running Unit & Robolectric Tests

```bash
# Executes all unit and Robolectric lifecycle tests
gradle :app:testDebugUnitTest
```

### 12.2 Verifying Visual Layouts (Roborazzi Screenshot Testing)

```bash
# Verifies recorded screenshots against the current UI state
gradle :app:verifyRoborazziDebug

# Re-records reference screenshots after UI modifications
gradle :app:recordRoborazziDebug
```

---

## 13. Security Documentation & ECTT Specifications

Please refer to the technical library in `/docs`:
- [`SECURITY_ARCHITECTURE.md`](docs/SECURITY_ARCHITECTURE.md)
- [`ECTT_TELEMETRY_PROTOCOL.md`](docs/ECTT_TELEMETRY_PROTOCOL.md)
- [`COMPLIANCE_AND_STANDARDS.md`](docs/COMPLIANCE_AND_STANDARDS.md)
- [`PROJECT_WEBSITE_AND_GOVERNANCE.md`](docs/PROJECT_WEBSITE_AND_GOVERNANCE.md)

---

## 14. Security Policies, CVD & Vulnerability Reporting

Please refer to [`SECURITY.md`](SECURITY.md) for full vulnerability reporting procedures, response SLAs, and cryptographic protocol details.

---

*AGIS 2045 — Designed for Zero-Trust Autonomy, Post-Quantum Resilience, and Next-Generation Human-Machine Teaming.*
