---
description: Guide to view and click conditions
---

# Conditions

**Conditions** are used to add requirements for a player to view or click an item. There are two ways a condition can be used:

* [View conditions](conditions.md#view-conditions)
* [Click conditions](conditions.md#click-conditions)

For both ways, there are the following types of conditions available:

* [Permission condition](conditions.md#permission)
* [Placeholder condition](conditions.md#placeholder)

## Scopes

### View conditions

View conditions will hide the item if not all conditions are met. Add the `view_conditions` key to an item to add view conditions. The syntax of this option is a map list of condtion types, see below.

### Click conditions

Click conditions will prevent click actions or built-in click behavior from running if not all conditions are met. Click conditions are added using the `on_click_conditions` key, or for a specific button trigger like `on_right_click_conditions`. The syntax of these options is a map list of condition types, see below.

## Types

### Permission

Permission conditions check if the player has a permission. Uses `type: permission` , though this is optional due to auto type detection. Keys:

* `permission` - The permission node to check (required).
* `value` - An optional boolean key to check the permission value against (defaults to true).

An example permission condition required to click an item:

```yaml
some_item:
  on_click_conditions:
    - permission: auraskills.skill.farming
```

### Placeholder

Placeholder conditions compare two values that can contain PlaceholderAPI placeholders. Uses `type: placeholder`, though this is optional due to auto type detection. Keys:

* `placeholder` - A string used as the left side value to be compared (required).
* `value` - A string used as the right side value to be compared (required).
* `compare` - An optional string that specifies the type of comparison to perform (defaults to equals). The following values are valid:
  * `equals` - checks for numerical or string equality
  * `greater_than` - checks if `placeholder` is strictly greater than `value`
  * `greater_than_or_equals` - checks if `placeholder` is greater than or equal to `value`
  * `less_than` - checks if `placeholder` is strictly less than `value`
  * `less_than_or_equals` - checks if `placeholder` is less than or equal to `value`

Any `compare` other than `equals` requires both `placeholder` and `value` to be evaluated to doubles.

An example placeholder condition that requires Fighting 100 to view an item:

```yaml
some_item:
  view_conditions:
    - placeholder: '%auraskills_fighting%'
      value: '100'
```

An example placeholder condition that requires at least skill average 20 to click an item:

```yaml
some_item:
  on_click_conditions:
    - placeholder: '%auraskills_average%'
      value: '20'
      compare: greater_than_or_equals
```
