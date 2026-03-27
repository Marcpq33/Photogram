# UI State and Navigation Rule

Apply this rule when building screens, routes, or ViewModels.

- Use unidirectional data flow.
- Each meaningful screen should define UiState, UiAction, optional UiEvent, and a ViewModel or equivalent state holder.
- UI must consume state and emit actions; it must not perform repository or network work directly.
- Navigation must follow the project navigation structure and avoid one-off paradigms.
- Do not use local composable state as a hidden replacement for real screen state.
