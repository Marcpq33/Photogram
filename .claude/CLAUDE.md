# Photogram Project Base

Photogram is a large-scale Android app for capturing, organizing, sharing, and reliving memories through albums, media, events, stories, chat, recaps, notifications, and privacy-first collaboration.

Claude Code is the primary code generation and iteration environment.
Android Studio is the source of truth for Gradle sync, build validation, dependency resolution, runtime verification, debugging, profiling, and emulator/device testing.

## Non-negotiable stack
- Kotlin
- Jetpack Compose
- Material 3 only as technical base, with a custom design system on top
- Navigation Compose
- MVVM-style presentation state holders
- Hilt
- Coroutines + Flow
- Room
- DataStore
- WorkManager
- Coil
- Gradle Kotlin DSL

## Non-negotiable rules
- Build real Android Studio-compatible code only.
- Albums are the core product unit.
- Privacy is a first-class product rule.
- UI must not perform repository, database, or network work directly.
- Do not mix presentation, domain, and data responsibilities.
- Do not invent backend behavior casually; state assumptions explicitly when contracts are missing.
- Do not treat emulator validation as equivalent to real-device validation.
- Do not mark work as production-ready without explicit validation steps.
- Do not optimize for generation speed over architectural integrity.
- Do not reduce product scope or collapse Photogram into a smaller MVP unless explicitly instructed.

## Build and validation reality
No feature is meaningfully complete unless:
- Gradle sync succeeds
- the project compiles
- navigation wiring works
- state is coherent
- critical flows run in emulator
- high-risk flows are verified on real device when relevant

## Order of construction
1. Core architecture
2. Design system
3. Authentication
4. Home
5. Albums
6. Gallery
7. Upload/media foundations
8. Profile
9. Notifications
10. Chat
11. Stories
12. Events
13. Recaps
14. Privacy hardening
15. Performance and release hardening

## Imported project documents
@docs/project-base.md
@docs/architecture.md
@docs/design-system.md
@docs/domain-model.md
@docs/navigation.md
@docs/privacy-model.md
@docs/build-validate.md
