# Data, Domain, and Privacy Rule

Apply this rule when defining entities, repositories, use cases, sync, media, or privacy logic.

- Never propose implementation details if the data model is underdefined.
- Never invent backend behavior casually; state assumptions clearly when contracts are missing.
- Keep domain rules out of composables.
- Keep DTOs out of UI state.
- Treat privacy as domain behavior and access logic, not only settings UI.
- Do not introduce media or sync flows without ownership, failure states, retries, and visible progress or state.
- Do not consider a feature done without a minimum testing and validation strategy.
