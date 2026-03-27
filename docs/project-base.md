# Photogram Product Base

## Product identity
Photogram is a premium Android-native product for personal and collaborative memory management.
It combines albums, gallery, media upload, stories, chat, recaps, events, notifications, profile, settings, and hierarchical privacy.

It is not a simple gallery, not a social clone, and not a narrow single-feature app.
It is a modular memory platform with a strong visual identity and media-heavy workflows.

## Primary goal
Build Photogram as a robust, scalable, maintainable Android product with production-oriented architecture from the start.

## Product principles
- Albums are the primary product unit.
- Privacy must exist as product logic, not only as UI toggles.
- Media-heavy flows must be designed around success, failure, retry, and visibility of state.
- UI must feel premium, dark-first, editorial, emotional, and visually coherent.
- Long-term maintainability takes priority over superficial implementation speed.
- Emulator success is useful but insufficient for high-risk flows.

## Functional scope
Photogram includes:
- authentication and onboarding
- home and featured memories
- albums and album collaboration
- global gallery and temporal archive
- upload and media handling
- stories and reactions
- chat and messaging
- recaps
- events
- profile
- notifications
- settings
- privacy and access control

## Scope rules
- Do not shrink the product into a small MVP unless explicitly instructed.
- Do not silently drop high-risk or difficult areas like media retry, privacy hierarchy, sync, or device validation.
- When a feature depends on undefined contracts, pause and define the contract before over-implementing UI.
