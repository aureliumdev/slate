---
description: Guide to templates
---

# Templates

The **templates** section in a menu file is used for items with a similar layout that appear multiple times in the same menu. Each instance of a template is differentiated by its context, a data type specified by the plugin providing the menu. Since templates depend on behavior implemented through a plugin's code, custom templates cannot be created.

## Contexts

Each context has a string key that can be used to customize the appearance of specific contexts and override the default. In the `contexts` section of a template, each child section must be a valid context name. Within the context name section, keys such as `material`, `display_name`, `lore`, and `pos` can be used to override the defaults.

Here is an example showing the skill template from AuraSkills:

```yaml
skill: # The skill template section
  contexts: # The contexts section
    farming: # The context for farming
      pos: ... # Keys in this section only apply to the farming item
      material: ...
    foraging: # Another context for foraging
      pos: ...
      material: ...
  # Keys below are default values
  display_name: ... # Applies to all contexts
  lore: ... # Applies to all contexts  
```

## Groups

Instead of a fixed `pos` value, there is a group system that allows contexts to have their `pos` generated dynamically based on the other contexts within a group. Essentially, a group is a rectangular box where items are placed into. A group is created in the `groups.[name]` section, where `[name`] is the name of the group.&#x20;

```yaml
stat: # The stat template section
  groups:
    lower: # The group name
      start: 2,0 # Start pos
      end: 2,8 # End pos
      align: center # Align type
```

In the example, the group is named `lower`, with the following available keys defined:

* `start` - The starting slot position of the group, which is the top left corner of the box. Can be either in `row,column` format or a single number `slot` format.
* `end` - The ending slot position of the group, which is the bottom right corner of the box. Can be either in `row,column` format or a single number `slot` format.
* `align` - The alignment type used to determine where to place items. Can be `center`, `left`, or `right`. To determine where items are placed, the slots from start to end are ordered from left to right, then top to bottom. When align is `center`, items will be placed so that the number of empty slots is even (or close to it) at the start and end of the group. For `left` align, items will be filled from start so that the context with the lowest `order` is on the `start` slot. For `right` align, the context with the highest `order` will be on the `end` slot.

### Adding contexts to groups

Once you create a group, you have to add contexts to the group for it to work.

```yaml
stat: # The stat template section
  contexts:
    speed: # The context secton
      group: lower # The group name to put the context in
      order: 5 # The order number of the context within the group
      material: white_stained_glass_pane
  groups:
    lower:
      start: 2,0
      end: 2,8
      align: center
```

In a context section like `stat` in the example above, the following keys are used instead of specifying a `pos`:

* `group` - The name of the group to put the context in. This must be exactly the same as the name of the section in `groups` that the group was created.
* `order` - An integer that defines the order number of the context. Contexts with lower orders will be placed to the left or above contexts with higher orders in the same group. If two contexts have the same order number, the order between the two is indeterminate.

::: warning
When using setting a `group` and `order` in a context, do not specify a `pos`.
:::
