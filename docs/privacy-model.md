# Photogram Privacy Model

## Privacy model
Privacy is hierarchical and must be modeled explicitly across:
- profile
- album
- photo or media
- event
- recap

Higher-order privacy rules may constrain lower-order visibility.
Do not implement privacy only as UI toggles.
Privacy must exist in domain rules and access logic.

## Privacy decision checklist
Every privacy decision must answer:
- who can see this
- who can contribute to this
- who can invite others
- who can access through direct membership
- who can access through shared link
- whether approval is required
- whether parent-level privacy overrides child-level settings

## Privacy scope rules
- Do not ship privacy features whose semantics are unclear.
- Do not treat public/private as sufficient if the product requires hierarchical access control.
- Privacy must be represented in domain and backend contract assumptions, not only in settings screens.
