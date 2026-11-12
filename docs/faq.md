---
title: FAQ
description: Duplicate UUIDs, saving on death, and the empty tooltip.
---

## A second shulker will not open

Both items have the same UUID. RexShulkers keeps that UUID in a lock map while its GUI is open. The second open is rejected, the player gets `messages.already-open`, and no GUI is created.

`/rexshulkers give` does not do this. Each item from that command gets a new UUID. Copies do this: a crate, a kit, or a middle-click in creative. Hold the item you plan to duplicate and run `/rexshulkers clear` first. See [Filled shulkers in crates](/rexshulkers/guides/giving-filled-shulkers/).

## Dying or leaving with the GUI open

The contents are written to `shulkers.db` before the UUID is unlocked. The same save runs when the player closes the GUI, quits, or is kicked. Shutdown saves each open GUI from the last snapshot taken on that player's thread, then unlocks it. Shutdown does not read other players' inventories.

The shulker item stays in the player's inventory, so a death still drops it with the rest of their items. Drop, place, click-move, and hand swap are blocked only while that UUID is open.

## The tooltip does not show the contents

That is `settings.obfuscate`, which defaults to true. The item's vanilla block inventory is cleared so the client has nothing to list. The real contents are in `shulkers.db` under `rexshulkers:id`.

## Reload emptied a shulker

Reload does not delete or rewrite `shulkers.db`. It only reloads `config.yml`, adding missing keys and keeping the values you set. A backup of the previous config is `config.yml.bak.<timestamp>`.

If the GUI row count is lower than the slot count already stored for that UUID, the shulker refuses to open and the row is left as it was. Raise `gui.rows` and open it again.

## The shulker was placed as a block

Only an item that still has `rexshulkers:id` is a portable shulker. Right-click opens it and does not place it. `/rexshulkers clear` removes that id, and the item places like a normal shulker box after that. That is what you want before a crate copies it. It is not what you want if the reward should stay portable. Use `/rexshulkers give` for a portable reward.
