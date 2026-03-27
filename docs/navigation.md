# Photogram Navigation

## Navigation rule
Navigation must be designed intentionally before feature sprawl begins.
Do not create ad hoc navigation patterns per feature.
Do not introduce multiple inconsistent bottom navigation paradigms.
Do not couple navigation logic to visual components beyond route triggering.

## Navigation structure
The app must distinguish clearly between:
- auth flow
- main application flow
- top-level destinations
- detail destinations
- full-screen immersive flows such as story viewer
- modal or bottom-sheet flows such as media selection where appropriate

## UI state/navigation coupling rules
- Use unidirectional data flow.
- State flows downward.
- Actions flow upward.
- Each screen should define UiState, UiAction, optional UiEvent, and a ViewModel or equivalent state holder.
- Composables should be as stateless as practical.
- Use remember and rememberSaveable correctly.
- Do not use local composable state as a hidden replacement for real screen state.
