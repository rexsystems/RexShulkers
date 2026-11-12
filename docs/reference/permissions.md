---
title: Permissions
description: Who can open, give, clear, reload, and skip the cooldown.
---

| Permission | Default | What it allows |
|---|---|---|
| `rexshulkers.use` | true | Right-click or `/rexshulkers open` |
| `rexshulkers.give` | op | `/rexshulkers give` |
| `rexshulkers.clear` | op | `/rexshulkers clear` |
| `rexshulkers.reload` | op | `/rexshulkers reload` |
| `rexshulkers.admin` | op | Open a shulker without waiting for `settings.cooldown-seconds` |

A player without `rexshulkers.use` who right-clicks a RexShulkers shulker gets the no-permission message. The item is still not placed.

`rexshulkers.admin` does not bypass the world list, the blacklist, or the rule that one UUID can only be open once.
