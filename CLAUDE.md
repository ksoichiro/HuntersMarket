# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Hunter's Market is a Minecraft mod where players compete to earn 10,000 gold by mining and selling ores to an NPC merchant. Built with **Architectury** for cross-platform support (Fabric + NeoForge/Forge), targeting Minecraft 1.21.11 (Java 21), 1.21.10 (Java 21), 1.21.9 (Java 21), 1.21.8 (Java 21), 1.21.7 (Java 21), 1.21.6 (Java 21), 1.21.5 (Java 21), 1.21.4 (Java 21), 1.21.3 (Java 21), 1.21.1 (Java 21), and 1.20.1 (Java 17).

## Build Commands

```bash
# Standard build (default target: 1.21.1)
./gradlew build -Ptarget_mc_version=1.21.1

# Build for 1.21.11
./gradlew build -Ptarget_mc_version=1.21.11

# Build for 1.21.10
./gradlew build -Ptarget_mc_version=1.21.10

# Build for 1.21.9
./gradlew build -Ptarget_mc_version=1.21.9

# Build for 1.21.8
./gradlew build -Ptarget_mc_version=1.21.8

# Build for 1.21.7
./gradlew build -Ptarget_mc_version=1.21.7

# Build for 1.21.6
./gradlew build -Ptarget_mc_version=1.21.6

# Build for 1.21.5
./gradlew build -Ptarget_mc_version=1.21.5

# Build for 1.21.4
./gradlew build -Ptarget_mc_version=1.21.4

# Build for 1.21.3
./gradlew build -Ptarget_mc_version=1.21.3

# Build for 1.20.1
./gradlew build -Ptarget_mc_version=1.20.1

# Build all supported versions
./gradlew buildAll

# Platform-specific builds
./gradlew :fabric:build -Ptarget_mc_version=1.21.11
./gradlew :neoforge:build -Ptarget_mc_version=1.21.11
./gradlew :fabric:build -Ptarget_mc_version=1.21.10
./gradlew :neoforge:build -Ptarget_mc_version=1.21.10
./gradlew :fabric:build -Ptarget_mc_version=1.21.9
./gradlew :neoforge:build -Ptarget_mc_version=1.21.9
./gradlew :fabric:build -Ptarget_mc_version=1.21.8
./gradlew :neoforge:build -Ptarget_mc_version=1.21.8
./gradlew :fabric:build -Ptarget_mc_version=1.21.7
./gradlew :neoforge:build -Ptarget_mc_version=1.21.7
./gradlew :fabric:build -Ptarget_mc_version=1.21.6
./gradlew :neoforge:build -Ptarget_mc_version=1.21.6
./gradlew :fabric:build -Ptarget_mc_version=1.21.5
./gradlew :neoforge:build -Ptarget_mc_version=1.21.5
./gradlew :fabric:build -Ptarget_mc_version=1.21.4
./gradlew :neoforge:build -Ptarget_mc_version=1.21.4
./gradlew :fabric:build -Ptarget_mc_version=1.21.3
./gradlew :neoforge:build -Ptarget_mc_version=1.21.3
./gradlew :fabric:build -Ptarget_mc_version=1.21.1
./gradlew :neoforge:build -Ptarget_mc_version=1.21.1
./gradlew :forge:build -Ptarget_mc_version=1.20.1

# Full release (clean + buildAll + collectJars → build/release/)
./gradlew release

# Collect built JARs into build/release/
./gradlew collectJars
```

Note: Tests are excluded from builds (`-x test`). The build system auto-downloads the correct JDK via Foojay toolchain resolver (JDK 21 for 1.21.11/1.21.10/1.21.9/1.21.8/1.21.7/1.21.6/1.21.5/1.21.4/1.21.3/1.21.1, JDK 17 for 1.20.1).

## Architecture

### Multi-Platform Module Structure

The project uses Architectury's pattern to share code between Fabric and NeoForge/Forge:

- **`common/shared/`** — Platform-independent shared code (no build.gradle, included as srcDir by common module). Base package: `com.huntersmarket`
- **`common/{version}/`** (×11) — Version-specific common module (Architectury common). Includes `common/shared` sources via `srcDir`. Maps to Gradle project `:common-{version}`.
- **`fabric/base/`** — Fabric platform base code (no build.gradle, included as srcDir by fabric module)
- **`fabric/{version}/`** (×11) — Fabric platform build module. Maps to Gradle project `:fabric`.
- **`neoforge/base/`** — NeoForge platform base code (no build.gradle, included as srcDir by neoforge module)
- **`neoforge/{version}/`** (×10) — NeoForge platform build module (1.21.1+). Maps to Gradle project `:neoforge`.
- **`forge/base/`** — Forge platform base code (no build.gradle, included as srcDir by forge module)
- **`forge/1.20.1/`** — Forge platform build module for MC 1.20.1. Maps to Gradle project `:forge`.

### Module Resolution in settings.gradle

Gradle project names differ from directory names. `settings.gradle` dynamically resolves modules based on `target_mc_version`:
- `:common-{version}` → `common/{version}/`
- `:fabric` → `fabric/{version}/`
- `:neoforge` → `neoforge/{version}/` (1.21.1+)
- `:forge` → `forge/{version}/` (1.20.1)

### Multi-Version Support

- Version properties in `props/<version>.properties` (`1.20.1`, `1.21.1`, `1.21.3`, `1.21.4`, `1.21.5`, `1.21.6`, `1.21.7`, `1.21.8`, `1.21.9`, `1.21.10`, `1.21.11`)
- Override target version: `-Ptarget_mc_version=<version>`
- Version-specific task aliases: `build1_20_1`, `build1_21_1`, `build1_21_3`, `build1_21_4`, `build1_21_5`, `build1_21_6`, `build1_21_7`, `build1_21_8`, `build1_21_9`, `build1_21_10`, `build1_21_11`, etc.
- 1.21.11/1.21.10/1.21.9/1.21.8/1.21.7/1.21.6/1.21.5/1.21.4/1.21.3/1.21.1 use NeoForge; 1.20.1 uses Forge (NeoForge didn't exist for 1.20.1)

### Where to Place Code

- **Shared game logic** → `common/shared/src/main/java/com/huntersmarket/`
- **Version-specific common code** → `common/<version>/src/main/java/`
- **Fabric-specific code** → `fabric/base/src/main/java/com/huntersmarket/fabric/`
- **NeoForge-specific code** → `neoforge/base/src/main/java/com/huntersmarket/neoforge/`
- **Forge-specific code** → `forge/base/src/main/java/com/huntersmarket/forge/`
- **Assets/resources** → `common/<version>/src/main/resources/`
- **Fabric resources** (fabric.mod.json) → `fabric/base/src/main/resources/` (version override in `fabric/<version>/src/main/resources/`)
- **NeoForge resources** (neoforge.mods.toml) → `neoforge/base/src/main/resources/META-INF/`
- **Forge resources** (mods.toml) → `forge/base/src/main/resources/META-INF/`

### Key Dependencies

**1.21.11**: Architectury API 19.0.1, Fabric Loader 0.18.4 / Fabric API 0.141.3+1.21.11, NeoForge 21.11.38-beta
**1.21.10**: Architectury API 18.0.8, Fabric Loader 0.18.4 / Fabric API 0.138.4+1.21.10, NeoForge 21.10.64
**1.21.9**: Architectury API 18.0.3, Fabric Loader 0.16.13 / Fabric API 0.134.1+1.21.9, NeoForge 21.9.16-beta
**1.21.8**: Architectury API 17.0.8, Fabric Loader 0.18.4 / Fabric API 0.136.0+1.21.8, NeoForge 21.8.52
**1.21.7**: Architectury API 17.0.8, Fabric Loader 0.17.3 / Fabric API 0.128.2+1.21.7, NeoForge 21.7.25-beta
**1.21.6**: Architectury API 17.0.6, Fabric Loader 0.17.3 / Fabric API 0.128.2+1.21.6, NeoForge 21.6.20-beta
**1.21.5**: Architectury API 16.1.4, Fabric Loader 0.17.3 / Fabric API 0.128.2+1.21.5, NeoForge 21.5.96
**1.21.4**: Architectury API 15.0.3, Fabric Loader 0.17.3 / Fabric API 0.119.4+1.21.4, NeoForge 21.4.156
**1.21.3**: Architectury API 14.0.4, Fabric Loader 0.17.3 / Fabric API 0.108.0+1.21.3, NeoForge 21.3.95
**1.21.1**: Architectury API 13.0.8, Fabric Loader 0.17.3 / Fabric API 0.116.7+1.21.1, NeoForge 21.1.209
**1.20.1**: Architectury API 9.2.14, Fabric Loader 0.16.10 / Fabric API 0.92.2+1.20.1, Forge 47.3.0
- Mojang official mappings

### Platform-Specific gradle.properties

Each version-specific platform module requires a `gradle.properties` with `loom.platform`:
- `fabric/{version}/gradle.properties` → `loom.platform=fabric`
- `neoforge/{version}/gradle.properties` → `loom.platform=neoforge`
- `forge/1.20.1/gradle.properties` → `loom.platform=forge`

Without this, Architectury Loom does not create platform-specific dependency configurations (e.g., `neoForge`, `forge`).

## Implementation Plan

Detailed plan in `docs/plan.md` with 11 phases. MVP = Phases 1-10. Current status: Phase 1 complete.

Package structure: `com.huntersmarket.{registry,state,entity,block,item,trade,hud,network,event,structure}`
