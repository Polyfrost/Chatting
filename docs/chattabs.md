# Chatting Chat Tabs

## Syntax

The file for Chat Tabs is in `{MINECRAFT DIRECTORY}/W-OVERFLOW/Chatting/chattabs.json`. The default file will look
something like this:

```json
{
  "tabs": [
    {
      "enabled": true,
      "name": "ALL",
      "unformatted": false,
      "color": 14737632,
      "hovered_color": 16777120,
      "selected_color": 10526880,
      "prefix": ""
    },
    {
      "enabled": true,
      "name": "PARTY",
      "unformatted": false,
      "starts": [
        "§r§9Party §8> ",
        "§r§9P §8> ",
        "§eThe party was transferred to §r",
        "§eKicked §r"
      ],
      "ends": [
        "§r§ehas invited you to join their party!",
        "§r§eto the party! They have §r§c60 §r§eseconds to accept.§r",
        "§r§ehas disbanded the party!§r",
        "§r§ehas disconnected, they have §r§c5 §r§eminutes to rejoin before they are removed from the party.§r",
        " §r§ejoined the party.§r",
        " §r§ehas left the party.§r",
        " §r§ehas been removed from the party.§r",
        "§r§e because they were offline.§r"
      ],
      "equals": [
        "§cThe party was disbanded because all invites expired and the party was empty§r"
      ],
      "regex": [
        "(§r)*(§9Party §8>)+(.*)",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§einvited §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eto the party! They have §r§c60 §r§eseconds to accept\\.§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas left the party\\.§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ejoined the party\\.§r",
        "§eYou left the party\\.§r",
        "§eYou have joined §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)'s §r§eparty!§r",
        "§cThe party was disbanded because all invites expired and the party was empty§r",
        "§cYou cannot invite that player since they're not online\\.§r",
        "§eThe party leader, §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e, warped you to §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e's house\\.§r",
        "§eSkyBlock Party Warp §r§7\\([0-9]+ players?\\)§r",
        "§a. §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§f §r§awarped to your server§r",
        "§eYou summoned §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§f §r§eto your server\\.§r",
        "§eThe party leader, §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e, warped you to their house\\.§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§aenabled Private Game§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§cdisabled Private Game§r",
        "§cThe party is now muted\\. §r",
        "§aThe party is no longer muted\\.§r",
        "§cThere are no offline players to remove\\.§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas been removed from the party\\.§r",
        "§eThe party was transferred to §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eby §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e has promoted §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eto Party Leader§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e has promoted §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eto Party Moderator§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eis now a Party Moderator§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r§e has demoted §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§eto Party Member§r",
        "§cYou can't demote yourself!§r",
        "§6Party Members \\([0-9]+\\)§r",
        "§eParty Leader: §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) ?§r(?:§[a-zA-Z0-9]).§r",
        "§eParty Members: §r(?:(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r(?:§[a-zA-Z0-9]) . §r)+",
        "§eParty Moderators: §r(?:(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+)§r(?:§[a-zA-Z0-9]) . §r)+",
        "§eThe party invite to §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas expired§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§cdisabled All Invite§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§aenabled All Invite§r",
        "§cYou cannot invite that player\\.§r",
        "§cYou are not allowed to invite players\\.§r",
        "§eThe party leader, §r(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas disconnected, they have §r§c5 §r§eminutes to rejoin before the party is disbanded\\.§r",
        "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)|(?:VIP§r§6\\+)|(?:MVP)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+)|(?:MVP(?:§r)?(?:§[a-zA-Z0-9])\\+\\+)|(?:(?:§r)?§fYOUTUBE))(?:§r)?(?:(?:§[a-zA-Z0-9]))?\\] [a-zA-Z0-9_]+|§7[a-zA-Z0-9_]+) §r§ehas disconnected, they have §r§c5 §r§eminutes to rejoin before they are removed from the party.§r",
        "§cYou are not in a party right now\\.§r",
        "§cThis party is currently muted\\.§r",
        "(§r)*(§9P §8>)+(.*)"
      ],
      "color": 14737632,
      "hovered_color": 16777120,
      "selected_color": 10526880,
      "prefix": "/pc "
    },
    {
      "enabled": true,
      "name": "GUILD",
      "unformatted": true,
      "starts": [
        "Guild >",
        "G >"
      ],
      "color": 14737632,
      "hovered_color": 16777120,
      "selected_color": 10526880,
      "prefix": "/gc "
    },
    {
      "enabled": true,
      "name": "PM",
      "unformatted": true,
      "starts": [
        "To ",
        "From "
      ],
      "color": 14737632,
      "hovered_color": 16777120,
      "selected_color": 10526880,
      "prefix": "/r "
    }
  ],
  "version": 4
}
```

The `version` property stores the version number of the Chat Tabs JSON file. This should not be touched unless you have
an older Chat Tab JSON and would like Chatting to automatically migrate to the newer version. The current version is `4`
.

The `tabs` property stores all the chat tabs, in the order they should be displayed in. By default, there are 4 chat
tabs - ALL, PARTY, GUILD, and PM. ALL simply shows all messages; nothing is filtered. PARTY shows only party messages,
GUILD shows only guild messages, and PM only shows private messages. These can be modified to the user's free will.

### Tab Syntax

This is the default PARTY chat tab. We will be using this as an example, as it utilizes most of the chat tab features
that you may want to use.

```json
{
  "enabled": true,
  "name": "PARTY",
  "unformatted": false,
  "starts": [
    "§r§9Party §8> ",
    "§r§9P §8> ",
    "§eThe party was transferred to §r",
    "§eKicked §r"
  ],
  "ends": [
    "§r§ehas invited you to join their party!",
    ...
    "§r§e because they were offline.§r"
  ],
  "equals": [
    "§cThe party was disbanded because all invites expired and the party was empty§r"
  ],
  "regex": [
    "(§r)*(§9Party §8>)+(.*)",
    "(?:(?:§[a-zA-Z0-9])*\\[(...seconds to accept\\.§r",
    "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP)... §r§ehas left the party\\.§r",
    "(?:(?:§[a-zA-Z0-9])*\\[(?:(?:VIP...joined the party\\.§r",
    ...
    "(§r)*(§9P §8>)+(.*)"
  ],
  "color": 14737632,
  "hovered_color": 16777120,
  "selected_color": 10526880,
  "prefix": "/pc "
}
```

The `enabled` property allows you to disable a chat tab without removing the tab from the JSON file. Users will be able
to directly manage this property via a GUI in the future.

The `name` property allows you to customize the display name of the tab.

The `unformatted` property allows you to toggle whether the filters go through the raw message sent or the message
without color / formatting codes. For example...

BEFORE

```json
{
  "enabled": true,
  "name": "EXAMPLE",
  "unformatted": false,
  "starts": [
    "§r§9Message §8> "
  ]
}
```

AFTER

```json
{
  "enabled": true,
  "name": "EXAMPLE",
  "unformatted": true,
  "starts": [
    "Message > "
  ]
}
```

The `starts` property allows you to only allow a message if it starts with a string in the `starts` property. For
example, if a message which contents were "Hello!", it would not be allowed, as it does not start anything in
the `starts` property.

The `ends` property does a similar function, except only allowing a message if it **ends** with anything in the `ends`
property rather than if it starts with anything.

The `equals` property allows you to only allow a message if it equals with a string in the `equals` property. **_THIS IS
CASE SENSITIVE._**

The `regex` property allows you to only allow a message if it matches a regex in the `regex` property.

You can append `ignore_` to `starts`, `ends`, `equals`, or `regex` to ignore messages that match rather than allow, and
of course use both at the same time.

The `color` property allows you to change the color of the chat tab text. It is in RGBA format.

The `hovered_color` property allows you to change the color of the chat tab text while being hovered. This takes
priority over the `color` property. Like the `color` property, it is in RGBA format.

The `selected_color` property allows you to change the color of the chat tab text while selected. This takes priority
over all the other color properties. Like the other color properties, it is in RGBA format.

The `prefix` property appends the prefix to any message sent while in the specific chat tab **if it is not a command**.
This can be used to automatically send messages in a specific channel in servers, like in Hypixel.

## Chat Tabs JSON Changelogs


### Version 4
- Added color text options (`color`, `hovered_color`, and `selected_color`)
- `version` is now actually an integer

### Version 3
- Added `ignore_` options (`ignore_starts`, `ignore_ends`, `ignore_equals`, and `ignore_regex`)

### Version 2
- Added `enabled` property