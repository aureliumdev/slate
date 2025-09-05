---
description: Guide to lore and lore types
---

# Lore

**Lore** is used to display lines of text below an item's name. In Slate, lore is defined as a list of strings or objects with the `lore` key on [items](items.md), [templates](templates.md), or template [context sections](templates.md#contexts).

## Simple Lore

Use a list of strings to directly define lore on an item.

```yaml
some_item:
  lore:
    - 'Line 1 of lore'
    - '<red>This lore is red'
    - '<#99ccff>This is a hex color'
    - 'Gamemode: %player_gamemode% using PlaceholderAPI placeholders'
    - '&7Using a legacy color code'
```

As shown above, each entry of the list is a line in the lore. The value is a simple string, which should be wrapped in single quotes to ensure it is recognized as a string in Yaml.

### Colors and Formatting

Colors are supported with [MiniMessage](https://docs.advntr.dev/minimessage/format.html), which using tags defined with angle brackets (\<tag>). Standard Minecraft color names and formats are defined with their name such as `<gray>` and `<bold>`. MiniMessage also supports hex colors and other modern text component features.

If you wish, legacy Bukkit color codes like &7 can also be used, though this is not recommended.

### Placeholders

[PlaceholderAPI](https://github.com/PlaceholderAPI/PlaceholderAPI) placeholders are supported in all lore, though you must have the PlaceholderAPI plugin installed as well as any necessary ecloud extensions you are using. Many items also have placeholders defined with curly braces like `{this}`, which are internally replaced by the plugin providing the menus. You can only create placeholders with curly braces through the Slate API in code.

## Complex Lore

Line of lore can also be defined as an object with multiple key-value pairs. This is used to add more complex behavior such as lore wrapping and inserting components into lore. There are two types of lore specified using the `type` key: text and component.

### Text Lore

Text lore is defined using `type: text`. At minumum, a text lore must define a `text` key which is the string value of the lore.

```yaml
lore:
  - 'this is a line'
  - type: text
    text: 'this is a line'
  - text: 'this is a line'
```

All three entries of the list above will produce the same line of text.

::: info
The `type` key is optional since the type of lore is inferred with the presence of the `text` key.
:::

Why use this more complex way to define lore? Because the following keys can be added with custom behavior:

* `text` - The string content of the line
* `wrap` - When true, Slate will automatically insert newlines on text over 40 characters long. Colors and formatting are not included in the count.
* `style` - A string with color codes or formatting that is inserting before the `text` and at the beginning of each new line inserting by lore wrapping. If `smart_wrap` is enabled (which is true by default), the style might not apply to a new line (see `smart_wrap` below).
* `styles` - Contains key-value pairs mapping a number to a style. This number inside angle brackets can be used in the `text` of the line (and any placeholders inside). This is mainly used to keep the styling of a placeholder within the menu file, so that modifying a messages file containing the placeholder is not needed. A style number of 0 is treated as the default style to be inserted in front of the text and used to wrap lines by default. Example:

```yaml
styles:
  0: '<gray>' # 0 indicates a default style, same as the style key
  1: '<yellow>' # Can be used in the text with <1>some text</1>
```

* `wrap_indent` - Like `style`, it is inserted before the text and at the beginning of each new line created by wrapping.
* `smart_wrap` - When true, which it is by default, lore wrapping will detect the last MiniMessage tag in the previous line to use as the style to begin the next line. If the previous line doesn't have any tags, it will use the previously detected tag in the entire string or the default style if not found. This will override the `style` or the number 0 in `styles`.

Full example of a text lore with multiple keys:

```yaml
- text: '{desc}'
  wrap: true
  wrap_indent: '  '
  styles:
    0: '<gray>'
    1: '<yellow>'
```

### Component Lore

The other type of complex lore is component lore, used to insert [components](components.md).

```yaml
lore:
  - component: ability_unlock
```

The above example will insert the component defined at `components.ability_unlock` into the lore. Components are useful because when a plugin decides that a component should be hidden, Slate will ignore the line with the component, preventing an empty line from being created. A `type` is not needed since the presence of the `component` key infers that the lore is a component lore.
