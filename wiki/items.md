---
description: Guide to configuring items
---

# Items

**Items** in Slate menus can be of three types: fill items, single items from the `items` section, and template items from the `templates` section. While each section is configured slightly differently, the keys used to change an item and its meta (potion data, NBT, etc) work mostly the same for all items.

## Appearance

### Material

The `material` key defines the type of the item and is required for all items and most templates. Its value must be a valid Minecraft item name, which can be found in game by hovering over an item in the inventory with advanced tooltips enabled (F3 + H). The dark gray text on the bottom after `minecraft:` is the name of the material used in menu configuration. Lowercase is recommended but capitals are also allowed.

```yaml
some_item:
  material: diamond_block
```

In templates, the material can either be defined directly in the template (which will be the default) or in a context subsection, which will only apply to the instance of the item using that context.

```yaml
some_template:
  contexts:
    farming:
      material: iron_hoe # Material for the farming context
    mining:
      material: iron_pickaxe # Material for the mining context
  material: stone_sword # The default material
```

### Pos

The `pos` key is the position or slot on the menu where the item is located. There are two possible formats. The first is in a row, column format like:

```yaml
pos: 2,3
```

This means row 2, column 3. Row can be from 0-5 and column can be from 0-8.

::: warning
Row and column numbers start at 0 from the top row and left column
:::

You can also specify a single number for the slot if you prefer, which can be from 0-53. 0 is the top-left slot and the number increases left to right, then top to bottom.

Example using a single slot number:

```yaml
pos: 32
```

If you need to duplicate the same item across multiple slots, you can specify pos as a list of values, such as:

```yaml
pos:
  - 2,0
  - 2,1
  - 3,0
  - 3,1
```

This will copy the item, including its material and meta, with all built-in placeholders properly replaced. If you instead want to copy with the same built-in placeholders but different material/meta, you can copy the entire item section and change its name to the format `some_item(1)`.

An example copied default item with different material and model data:

```yaml
items:
  skull:
    pos: 1,2
    material: player_head
    custom_model_data: 100
    display_name: '<white>{player}'
    lore:
      - '{entries[\n]}'
  skull(1):
    pos: 1,3
    material: brick
    custom_model_data: 99
    display_name: '<white>{player}'   
    lore:
      - '{entries[\n]}' 
```

The text in parenthesis can be any integer, as long as each duplicate item has a unique name. You must copy the display\_name and lore values from the original item in order to keep the same tooltip. Items duplicated in this way will also preserve the built-in click behavior of the item. You cannot duplicate templates like this.

### Display Name

The `display_name` key contains the text used for the item display name. MiniMessage, color codes, and placeholders are supported. The display name of fill items is automatically set to an empty string. Templates can have a default display name and a different display name for each context.

### Lore

> Main article: [Lore](items.md#lore)

The `lore` key is a list of strings containing the lore displayed on the item. MiniMessage, color codes, and placeholders are also supported. Templates can also have a default lore and context-specific lore. Lines of lore can also have types beyond a simple string, such as automatical wrapping lore and components.

### Enchantments

The `enchantments` key is a list of strings with each entry in the format `name level`, such as:

```yaml
enchantments:
  - sharpness 5
  - unbreaking 3
```

The name of the enchantment is the name used by the vanilla game.

### Potion Data

The `potion_data` section contains subkeys for defining data on a potion. There are three keys in the `potion_data` section:

* `type` - The name of the potion type, such as `speed` or `night_vision`. The type must be a valid Bukkit PotionType (listed [here](https://helpch.at/docs/1.18/org/bukkit/potion/PotionType.html)).
* `extended` - An optional true/false value of whether the potion should have an extended duration (defaults to false).
* `upgraded` - An optional true/false value of whether the potion should have an upgraded level (defaults to false).

An example of a potion\_data section with all three keys:

```yaml
potion_data:
  type: night_vision
  extended: true
  upgraded: true
```

### Custom Effects

The `custom_effects` section is a map list defining custom potion effects on a potion (this is different from potion data). Each object in the map list must have three keys:

* `type` - The type of potion effect to use, must be a valid Bukkit PotionEffectType (listed [here](https://helpch.at/docs/1.18/org/bukkit/potion/PotionEffectType.html)).
* `duration` - The duration of the potion effect, in ticks (20 ticks = 1 second).
* `amplifier` - The level of the potion effect, with an amplifier of 0 being level 1.

Below is an example of a `custom_effects` section with multiple custom effects (Fire Resistance I for 10 seconds and Haste II for 30 seconds):

```yaml
custom_effects:
  - type: fire_resistance
    duration: 200
    amplifier: 0
  - type: fast_digging
    duration: 600
    amplifier: 1
```

### Item Flags

The `flags` key is a list of item flags to add to the item. Item flags are a type of meta used to hide other meta on the item from being shown to the player. Below is a list of valid flags and the format used in item configuration:

```yaml
flags:
  - hide_attributes
  - hide_destroys
  - hide_dye
  - hide_enchants
  - hide_placed_on
  - hide_potion_effects
  - hide_unbreakable
```

### Glow

The `glow` key is a true/false value that adds the glowing enchantment effect to the item without showing the enchantment name if set to true. It is equivalent to adding an enchantment and a hide\_enchants item flag.

### Skull Meta

The `skull_meta` section can be used to add a custom skin texture to a player head item. The section must have one of three possible sub-keys, `uuid`, `base64`, or `url`.

The `uuid` key is the UUID of the player's skin texture to use. The `base64` key is a base64 string of the texture. The `url` is the minecraft.net URL containing the texture.\


Below is an example of using skull meta. You only need ONE of the three options.

```yaml
skull_meta:
  uuid: 6302a69e-9995-4f95-b2cd-d37b8ab875c9
  base64: eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIxMTk3ZTUzYzFhYjExMmU2ODA3OWQzYzgzZmE0NzE0Yzc3YTgzZDA2MjFhMjlkNzYyZTk5ZTlmZWFhZTRkNSJ9fX0=
  url: http://textures.minecraft.net/texture/621197e53c1ab112e68079d3c83fa4714c77a83d0621a29d762e99e9feaae4d5
```

### Durabiliy

The `durability` key sets the durability of the item from 0 to the max durability of the item (a value of 0 means the item is almost broken).

### NBT

NBT data can be added to an item using the `nbt` section. This section supports nested NBT structures and will automatically detect the type of data inputted.&#x20;

Example nbt section on an item:

```yaml
some_item:
  nbt:
    CustomNBTData: "some value"
```

::: info
Custom model data now has a dedicated option shown below which is preferred over using the `nbt` section. In 1.20.5, changes to the item format mean that custom NBT data is fully separate from vanilla components.

Slate uses a special override if you use `nbt: CustomModelData:` to preserve compatibility for upgraded servers. However, any other vanilla functionality defined with the `nbt` section will no longer work in 1.20.5+.
:::

### Custom Model Data

Custom model data can be added to an item using a `custom_model_data` key:

```yaml
some_item:
  custom_model_data: 123
```

Using the old format under the nbt section will still work, though this new format more accurately reflects how vanilla stores items in 1.20.5+.

### Registry Items

The `key` key can be used in an item section to use an item in the item registry. For AuraSkills, any item can be registered in game using `/skills item register [name]`. This is used instead of defining `material` and other meta. This can be convenient when an item has already been created in game or is too complex.

```yaml
some_item:
  key: flaming_sword # Registered in game using /skills item register flaming_sword
```

## Click Actions

> Main article: [Actions](actions.md)

Click actions are used to define custom functionality when a player clicks an item in the menu. This includes executing a command, closing the menu, opening another menu, and more.

## Conditions

> Main article: [Conditions](conditions.md)

Conditions can be added to an item to add requirements for viewing or clicking an item.

## Custom Menu Items

Completely new items can be added to menus by simply defining a new section in the `items` section with the name of your new item. The custom item name cannot be the name of an existing built-in item.&#x20;

The format of the custom item is the same as any other item (material, pos, display\_name, lore, etc). Click actions and PlaceholderAPI placeholders are also supported.
