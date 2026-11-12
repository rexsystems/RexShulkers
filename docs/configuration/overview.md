---
title: Settings
description: Cooldown, worlds, blacklist, and tooltip obfuscation.
---

`plugins/RexShulkers/config.yml` is the only file reload reads. `shulkers.db` is left in place.

Messages accept MiniMessage, legacy `&` codes, `&#RRGGBB`, and `#RRGGBB`. Names inserted with `{player}` are literal, so a name cannot add color tags.

## Cooldown

`settings.cooldown-seconds` is the wait after a player closes a shulker before they can open one again. The default is 3. Set it to 0 to turn the wait off.

The wait is per player. `rexshulkers.admin` skips it. The message uses `{seconds}` for the remaining whole seconds.

The timer is memory only. A restart clears it.

## Worlds

```yaml
worlds:
  enabled: []
  disabled: []
```

If `enabled` is not empty, shulkers open only in those worlds. Otherwise they open everywhere except the worlds in `disabled`. Names are compared without case. Give and clear still work in a world that cannot open shulkers.

## Blacklist

`blacklist` is a list of material names, such as `BEDROCK` or `SHULKER_BOX`. A click that would put one of those materials into the shulker GUI is cancelled, and the player gets `messages.blacklisted`.

The cancel applies to that click only. A click in a chest, a click that takes an item out of the shulker, or a click on an ordinary item is not cancelled by the blacklist.

Shulker boxes are listed by default so a portable shulker cannot be nested inside another one. Remove a line if you want that material to be allowed.

## Obfuscation

`settings.obfuscate` defaults to true. On give and on open, RexShulkers clears the vanilla block inventory stored on the item. That is the inventory the client reads for the tooltip. The SQLite contents are never written onto the item, so the tooltip stays empty.

Set it to false to leave an existing block inventory alone. The portable contents still come from `shulkers.db` only.

## GUI

`gui.title` is the inventory title. `gui.rows` is the number of rows, from 1 to 6. The default is 3, which is 27 slots, the size of a shulker.

If you lower `gui.rows` after a shulker was saved with more slots, that shulker refuses to open until you raise the row count again. The saved row is not trimmed.

## Sounds

`sounds.open` and `sounds.close` take a namespaced sound such as `minecraft:block.shulker_box.open`, plus volume and pitch. An empty `sound`, or `enabled: false`, plays nothing.

The close sound plays when the player closes the GUI, dies, or quits. Shutdown does not play it.

## Item

`item.name` and `item.lore` are written onto items from `/rexshulkers give`. `/rexshulkers clear` does not remove the name or the lore. It only removes the UUID.
