# Photogram Design System

## Design goals
Photogram requires a reusable design system.
Do not build every screen from isolated local styles.

UI identity must remain:
- premium
- dark-first
- editorial
- emotional
- polished

## Design system ownership
The design system must define and own:
- colors
- typography
- spacing
- corner radii
- shadows
- surfaces
- glass treatment patterns
- standard buttons
- chips and pills
- cards
- headers
- navigation bars
- avatars and rings
- loading, empty, and error states

## Design rules
- Avoid visual inconsistency between features.
- Avoid duplicating almost-identical components.
- Prefer reusable Compose components over local one-off styling.
- Material 3 may be used as technical base, but Photogram visual identity is custom.

## Resource rules
- Use Android resources appropriately.
- Do not hardcode user-facing text when resources are appropriate.
- Use stable naming for drawables, strings, dimensions, and themes.
- Internationalization must be supported by design.
