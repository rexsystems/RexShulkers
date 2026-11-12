---
title: Placeholders
description: PlaceholderAPI values for an open shulker and the cooldown.
---

These register when PlaceholderAPI is installed at enable. They are per player and live in memory. A restart clears them.

| Placeholder | Value |
|---|---|
| `%rexshulkers_open%` | `yes` when that player has a RexShulkers GUI open, otherwise `no` |
| `%rexshulkers_cooldown%` | Whole seconds left before they can open again, or `0` |

Inside `config.yml`, message lines can also contain PlaceholderAPI tokens. RexShulkers applies them for players before it reads `{amount}`, `{player}`, and the other tokens listed on the [messages](/rexshulkers/configuration/messages/) page.
