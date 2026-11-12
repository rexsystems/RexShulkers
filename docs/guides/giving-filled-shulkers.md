---
title: Filled shulkers in crates
description: Clear the storage id before a crate copies a shulker.
---

A RexShulkers shulker keeps a UUID in its persistent data, on the key `rexshulkers:id`. The items inside are stored in `plugins/RexShulkers/shulkers.db` under that UUID. The item itself does not contain them.

`/rexshulkers give` writes a new UUID on every item. Two items from that command never share a box, and they do not stack.

A crate, a kit plugin, or anything else that copies the item also copies the UUID. Every copy then opens the same box. While one player has it open, the others get the already-open message and no GUI.

Run `/rexshulkers clear` while holding the shulker before you put it in a crate.

1. Close the shulker if it is open. Clear is refused while that UUID is open.
2. Hold the item in your main hand or off hand.
3. Run `/rexshulkers clear`.

Clear removes the UUID from that item and deletes the matching row from `shulkers.db` when the row exists. The name and lore stay. The item is no longer a RexShulkers shulker, so a right-click places it like a normal shulker box.

Copy that cleared item. The copies have no UUID, so they cannot share storage.

If you only clear one copy and leave other copies that still have the old UUID, those copies still share one box. The deleted row comes back empty the next time one of them is opened, and every uncleared copy writes to that same id. Clear the item you are about to duplicate, and do not leave an uncleared copy in circulation.

To give each player their own portable shulker, use `/rexshulkers give`, or have the crate run that command as console. Do not copy one uncleared item.

You need `rexshulkers.clear`, which defaults to op.
