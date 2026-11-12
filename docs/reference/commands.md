---
title: Commands
description: /rexshulkers, /rsh, and /shulker.
---

The command is `/rexshulkers`. Aliases are `/rsh` and `/shulker`.

| Command | Permission | What it does |
|---|---|---|
| `/rexshulkers help` | everyone | Sends the help lines from `config.yml` |
| `/rexshulkers open` | `rexshulkers.use` | Opens the RexShulkers shulker in your main hand, or your off hand if the main hand is not one |
| `/rexshulkers give <player> [amount]` | `rexshulkers.give` | Creates that many new empty shulkers, each with its own UUID, and adds them to an online player's inventory |
| `/rexshulkers clear` | `rexshulkers.clear` | Removes the UUID from the held shulker and deletes that database row |
| `/rexshulkers reload` | `rexshulkers.reload` | Reloads `config.yml`. Does not touch `shulkers.db` |

`give` takes an exact online name. `amount` is optional and defaults to 1. The maximum is 64. Items that do not fit are dropped at that player's feet.

Right-clicking a RexShulkers shulker in the air or against a block does the same thing as `/rexshulkers open`. The item is not placed.

`open` and `clear` are player commands. `give`, `reload`, and `help` can be run from the console. `give` still adds the items on the target player's thread.
