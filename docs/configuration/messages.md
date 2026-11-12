---
title: Messages
description: Every player-facing string in config.yml.
---

Each string under `messages` accepts MiniMessage, legacy `&` codes, `&#RRGGBB`, and `#RRGGBB`. A blank string is not sent.

`messages.prefix` is inserted wherever a line contains `{prefix}`.

| Placeholder | Used by | Meaning |
|---|---|---|
| `{prefix}` | most replies | The prefix line |
| `{player}` | `given` | The target's name, inserted as literal text |
| `{amount}` | `given`, `received` | How many shulkers were created |
| `{dropped}` | `dropped` | How many did not fit and were dropped at their feet |
| `{seconds}` | `cooldown` | Whole seconds left before another open |
| `{saved}` | `slots-too-small` | Slot count already stored for that UUID |
| `{slots}` | `slots-too-small` | Slot count of the current GUI |

`messages.help` is a list. Each line is sent on its own. The header is `messages.help-header`.

PlaceholderAPI placeholders in these strings are applied for players when that plugin is installed. They are left as written for the console.
