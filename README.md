# RexShulkers

Open a shulker from your hand. The items live in SQLite, not in the item, so the tooltip stays empty.

## Features

- Right-click, or `/rexshulkers open`, opens a 27-slot GUI
- The item stores a UUID in its persistent data. The contents are rows in `shulkers.db`
- A second item with that same UUID does not open
- While that UUID is open, the item cannot be dropped, placed, click-moved, or swapped
- Close, death, quit, and shutdown write the GUI back to SQLite before the UUID unlocks
- Cooldown, world allow and deny lists, and a material blacklist
- `/rexshulkers clear` strips the UUID so a crate copy cannot share storage
- MiniMessage, legacy `&` codes, `&#RRGGBB`, and `#RRGGBB`
- PlaceholderAPI is optional

## Requirements

- Java 21
- Paper, Purpur, or Folia 1.21+
- PlaceholderAPI is optional

## Install

1. Put the jar in `plugins/`
2. Restart
3. Edit `plugins/RexShulkers/config.yml`
4. Run `/rexshulkers reload`

Reload rewrites missing config keys. It does not delete `plugins/RexShulkers/shulkers.db`.

## Commands

| Command | Permission |
|---|---|
| `/rexshulkers help` | everyone |
| `/rexshulkers open` | `rexshulkers.use` |
| `/rexshulkers give <player> [amount]` | `rexshulkers.give` |
| `/rexshulkers clear` | `rexshulkers.clear` |
| `/rexshulkers reload` | `rexshulkers.reload` |

Aliases: `/rsh`, `/shulker`

`rexshulkers.admin` skips the open cooldown.

## Documentation

The pages in [docs/](docs/index.md) drop into a Starlight site at `src/content/docs/rexshulkers/`. Paste `docs/sidebar.mjs` into the site sidebar.

Full docs: [https://docs.rexsystems.me/rexshulkers/](https://docs.rexsystems.me/rexshulkers/)

## License

Licensed under the [GNU Affero General Public License v3.0](LICENSE).
