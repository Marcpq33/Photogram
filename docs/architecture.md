# Photogram Architecture

## Architecture model
Use modular architecture with clear feature and core boundaries.
Prefer stable boundaries over convenience.

## Recommended project modules
- app
- core-common
- core-model
- core-ui
- core-designsystem
- core-navigation
- core-network
- core-database
- core-datastore
- core-media
- core-notifications
- feature-auth
- feature-home
- feature-albums
- feature-gallery
- feature-upload
- feature-story
- feature-chat
- feature-recaps
- feature-events
- feature-profile
- feature-notifications
- feature-settings
- feature-privacy

## Layer model
Use clear separation between:
- presentation
- domain
- data

### Presentation responsibilities
- composables
- route/state wiring
- screen rendering
- user actions
- ViewModels or equivalent state holders

### Domain responsibilities
- business rules
- reusable use cases when logic is complex or cross-feature
- policy enforcement that should not live in UI or repositories

### Data responsibilities
- repositories
- local data sources
- remote data sources
- persistence mapping
- sync coordination

## Source-of-truth rules
Each important data type must have a clear source of truth.
Ownership must be explicit.
Do not duplicate authority across layers.

Ownership categories:
- UI state is owned by ViewModel/state holder
- local entities are owned by Room-backed repositories
- remote entities are owned by backend contracts and exposed through repositories
- preferences are owned by DataStore
- background task execution is owned by WorkManager orchestration

If ownership is unclear, stop and define it before implementation.

## Prohibited patterns
- no repository calls directly from composables
- no database logic in UI modules
- no network DTO leakage into UI state
- no feature reaching into another feature internals without a defined contract
- no dumping-ground shared misc module
- no architecture drift across features
