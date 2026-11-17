# RexShulkers

Open a shulker from your hand on Paper, Purpur, or Folia. The contents stay in SQLite. The item only keeps a UUID, so the tooltip does not list what is inside.

**Java 21. Paper, Purpur, or Folia 1.21+.** PlaceholderAPI is optional.

## Features

- Right-click or `/rexshulkers open` opens the box. The item is not placed
- Each `/rexshulkers give` writes a new UUID, so those items do not share a box
- A copied item with the same UUID is refused while that box is open
- Drop, place, click-move, and hand swap of that item are blocked until the GUI closes
- Closing, dying, leaving, and server shutdown save the GUI before the UUID unlocks
- Cooldown, world lists, and a material blacklist
- `/rexshulkers clear` removes the UUID and deletes that database row before you put the item in a crate

## Commands

| Command | Description | Permission |
|---|---|---|
| `/rexshulkers help` | Show help | everyone |
| `/rexshulkers open` | Open the shulker in your hand | `rexshulkers.use` |
| `/rexshulkers give <player> [amount]` | Give a new empty shulker | `rexshulkers.give` |
| `/rexshulkers clear` | Strip the storage id from the held shulker | `rexshulkers.clear` |
| `/rexshulkers reload` | Reload `config.yml` | `rexshulkers.reload` |

Aliases: `/rsh`, `/shulker`

## Permissions

| Permission | Default | Description |
|---|---|---|
| `rexshulkers.use` | true | Open a shulker from your hand |
| `rexshulkers.give` | op | Give a new empty shulker |
| `rexshulkers.clear` | op | Strip the storage id from the held shulker |
| `rexshulkers.reload` | op | Reload the configuration |
| `rexshulkers.admin` | op | Open a shulker without the cooldown |

Documentation: https://docs.rexsystems.me/rexshulkers/
