# Photogram Domain Model

## Core product objects
The following entities must be defined clearly where relevant:
- User
- UserProfile
- PrivacySettings
- Album
- AlbumMember
- AlbumInvite
- Photo
- MediaAsset
- Story
- StoryReply
- Recap
- Event
- EventGuest
- EventTimelineItem
- Chat
- Message
- Notification

## Entity definition rule
For each entity, define:
- identity
- ownership
- source of truth
- local persistence needs
- sync rules
- visibility rules
- relationships to other entities

If these are not defined, do not over-implement UI around them.

## Product domain definitions
### Album
Album is the primary product unit.
An album is a structured collaborative memory space that may contain:
- media
- members
- permissions
- recap association
- event association
- conversation context
- privacy rules

### Recap
Recap is a first-class product object.
It may be personal, album-based, event-based, or derived from curated memory selections.
Generation, playback, ownership, and visibility must be modeled explicitly.

### Event
An event is a structured experience object with identity, schedule, participation, associated media, and potentially linked album, chat, and recap behavior.

### Story
A story is an immersive media presentation unit with temporal viewing behavior and lightweight interaction such as reactions or replies.

### Chat
Chat must be modeled explicitly by conversation type.
Possible types may include:
- direct
- album-linked
- event-linked
- group
