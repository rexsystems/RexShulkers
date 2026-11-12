---
title: RexShulkers
description: Open a shulker from your hand. Contents stay in SQLite.
---

RexShulkers opens a shulker box from the item in your hand. The items you put inside are stored in `plugins/RexShulkers/shulkers.db`. The item only keeps a UUID under the persistent data key `rexshulkers:id`.

The vanilla block inventory on that item stays empty when `settings.obfuscate` is true, so the tooltip does not list the contents.

A second item with the same UUID does not open. While that UUID is open, the item cannot be dropped, placed, click-moved, or swapped. Closing the GUI, dying, leaving, or stopping the server writes the GUI back to SQLite and then unlocks the UUID.

## Files

```
plugins/RexShulkers/
  config.yml
  shulkers.db
```

A reload that changes the file writes `config.yml.bak.<timestamp>` first, then adds any keys that were missing. Values you already set stay as they are. Reload does not delete `shulkers.db`.

## Next

- [Installation](/rexshulkers/getting-started/installation/)
- [Filled shulkers in crates](/rexshulkers/guides/giving-filled-shulkers/)
- [Settings](/rexshulkers/configuration/overview/)
- [Commands](/rexshulkers/reference/commands/)
