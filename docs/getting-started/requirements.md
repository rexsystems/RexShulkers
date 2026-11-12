---
title: Requirements
description: Java 21 and Paper, Purpur, or Folia 1.21 or newer.
sidebar:
  order: 2
---

- Java 21
- Paper, Purpur, or Folia 1.21 or newer
- PlaceholderAPI is optional

The plugin shades `sqlite-jdbc` into the jar. The driver classes live under `me.rexsystems.rexShulkers.libs.sqlite`. The native library still looks up `org.sqlite.core.NativeDB` and the callback types it was built with, so that package stays on its original name. You do not install SQLite yourself.

Folia is supported. Opening, closing, dying, and quitting save that player's shulker on the player thread. The SQLite write is synchronous and does not read another player's inventory. Shutdown saves each open GUI from the snapshot taken on the player thread, then unlocks it.
