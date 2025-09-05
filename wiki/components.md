---
description: Guide to components
---

# Components

The **components** section in a menu file is used to configure sections of lore used in some templates. The section the component is defined is the name of the component. The `lore` key of a component defines the lines of lore that will be inserted in place of its corresponding component lore with the same name in template. The `lore` list works like the lore of any item or template, so formatting and PlaceholderAPI placeholders can be used. The `context` key states what the context type its corresponding template uses and should not be changed.

The reason components exist are to allow sections of lore that might not always be shown by a plugin to work more conveniently. If a component is hidden, no empty lines are created in the lore (the list entry in the template lore containing the component will be ignored). Thus, the `lore` list in the main template can be more clear, with each component on a different line.
