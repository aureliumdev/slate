---
description: Guide to custom actions for menus
---

# Actions

**Actions** are used to define custom functionality purely through the menu file when a certain trigger occurs, including when an item is clicked or when the menu is opened. Action types includes executing a command, closing the menu, opening another menu, or playing a sound.

## Triggers

### Item triggers

These triggers apply to individual items and templates (key in parenthesis):

* Any click (`on_click`)
* Left click (`on_left_click`)
* Right click (`on_right_click`)
* Middle click (`on_middle_click`)
* Drop key (`on_drop`)

The trigger key is used to name the section that contains the click actions for that particular trigger. For example, all the actions under an `on_click` section will be activated when the player clicks that item with any mouse button.

### Menu triggers

These triggers apply to the entire menu and are defined at the top-level of the menu file.

* Menu open (`on_open`)
* Menu close (`on_close`)
  * This will run even if the player changes to viewing another menu immediately

## Types

Once you have created a section under the item with a trigger name, click actions are defined underneath using a map list.

Each action should have a `type` defined, either `command` (executing a command),`menu` (menu navigation), or `sound`. However, Slate can automatically detect the type when the `command`, `menu`, or `sound` keys are present, making the type uncessary in those cases.

### Command

For command actions, an `executor` (either `console` or `player`) and the `command` itself must be defined. Commands with a `console` executor will be run through console, while a `player` executor will be run as if the player had typed it (accounts for player permissions too).&#x20;

The `command` text should not include the beginning slash. It supports PlaceholderAPI placeholders and a built in placeholder of `{player}` (player username).

An example command action using an on\_click trigger:

```yaml
some_item:
  on_click:
    - type: command
      executor: console
      command: say hi
```

### Menu

Menu actions are for anything related to menu navigation. You must define the specific action type using the `action` value, which can be as follows:

* `open` - Opens another menu (must be within the Aurelium Skills plugin)
* `close` - Closes the current menu
* `next_page` - Moves to the next page of the menu, if applicable
* `previous_page` - Returns to the previous page of the menu, if applicable

The `open` action type also requires a `menu` value, which is the internal name of the menu to open (this is usually the lowercase name with spaces replaced with underscores).

The `open`, `next_page`, and `previous_page` action types also take an optional `properties` section that is used to define menu behavior, though this option is hard to use unless you dig through the source code.

An example menu action activated on right click only that closes the menu:

```yaml
some_item:
  on_right_click:
    - type: menu
      action: close
```

### Sound

Sound actions play a sound for the player. The following options configure a sound action:

* `sound` - The name of the sound to play. This uses the vanilla sound names that are valid in the /playsound command. This option is required. (ex: entity.villager.no)
* `category` - An optional string for the sound category to player the sound in (defaults to master).
* `volume` - An optional number for the volume to play the sound at (defaults to 1).
* `pitch` - An optional number for the pitch to player the sound at (defaults to 1).

An example sound action played when the player opens the menu. This example omits the `type` key because it is auto detected by the presence of `sound`:

```yaml
on_open:
  - sound: entity.villager.no
    category: master
    volume: 0.5
    pitch: 1.2
```

## Examples

Multiple click actions under the same trigger will activate together:

```yaml
some_item:
  on_click:
    - type: command
      executor: player
      command: shop
    - type: command
      executor: console
      command: say Player opened shop
```

