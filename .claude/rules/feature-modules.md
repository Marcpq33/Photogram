# Feature Module Boundaries Rule

Apply this rule when adding or modifying modules or cross-feature behavior.

- Respect existing feature and core boundaries.
- Prefer extending shared systems over one-off local implementations.
- Do not allow one feature to reach into another feature internals without a defined contract.
- Do not create dumping-ground modules.
- If ownership or placement is unclear, stop and define it before implementation.
