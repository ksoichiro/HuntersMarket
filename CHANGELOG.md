# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.2.0] - 2026-02-22

### Added

- Minecraft 1.21.11 support (Fabric + NeoForge)
- Minecraft 1.21.10 support (Fabric + NeoForge)
- Minecraft 1.21.9 support (Fabric + NeoForge)
- Minecraft 1.21.8 support (Fabric + NeoForge)
- Minecraft 1.21.7 support (Fabric + NeoForge)
- Minecraft 1.21.6 support (Fabric + NeoForge)
- Minecraft 1.21.5 support (Fabric + NeoForge)
- Minecraft 1.21.4 support (Fabric + NeoForge)
- Minecraft 1.21.3 support (Fabric + NeoForge)
- Play anvil sound and display gold-styled title on game start
- Add bow and arrows to initial equipment chest

### Changed

- Update item prices and add Trident to price list

### Fixed

- Reset client state when leaving world to prevent stale HUD display
- Remove mobs before structure placement to prevent trapping inside market

## [0.1.0] - 2026-01-19

### Added

- Initial release
- Market structure that generates near new player spawn points
- Merchant NPC for selling ores and earning gold toward 10,000 gold goal
- Multi-loader support: Fabric and NeoForge (Minecraft 1.21.1), Fabric and Forge (Minecraft 1.20.1)
- Game state persistence using SavedData

[Unreleased]: https://github.com/ksoichiro/HuntersMarket/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/ksoichiro/HuntersMarket/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/ksoichiro/HuntersMarket/releases/tag/v0.1.0
