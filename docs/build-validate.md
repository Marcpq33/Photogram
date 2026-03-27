# Build and Validation

## Platform constraint
All generated code must be Android Studio-compatible and structured as a real Android project.
Do not generate pseudo-Android code, HTML-first mental models, or abstractions that cannot be built, synced, and run in Android Studio.

## Validation workflow
Claude Code generates and refactors code.
Android Studio validates reality.

Use Android Studio to confirm:
- Gradle sync
- dependency resolution
- build success
- resource correctness
- navigation wiring
- previews where useful
- emulator execution
- device execution for critical flows
- logs and crash behavior
- performance profiling where relevant

## Completion criteria
A feature is not meaningfully complete unless:
- its data model is defined sufficiently
- its architecture placement is correct
- its state model is coherent
- its error states are represented
- its validation path is known
- its testing expectations are defined
- it compiles and integrates cleanly

## Device validation discipline
Emulator is required.
Real-device validation is required for high-risk flows.

High-risk flows include, when relevant:
- media capture and upload
- permissions
- storage and file handling
- performance-sensitive screens
- long-running background work
- notification behavior
- network edge cases
- camera and gallery interactions
