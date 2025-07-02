# Friendly Guide
The plugin introduces a friendly guide in the Lumbridge Castle as well as in the Grand Exchange
![image](https://github.com/user-attachments/assets/8724a0a3-96a4-4df8-8622-ee2334c65cb4)

You can talk with him and ask him for some help on what to try next.
![image](https://github.com/user-attachments/assets/e40c3c10-32ed-40c7-94be-86f5787f0995)

The guide is smart enough to give you only content which you have immediate access to, based on your levels, quests,
and areas explored, whether you are a member, or even an ironman.

![image](https://github.com/user-attachments/assets/6e9cff59-8b27-4225-b391-f6fdf65c47c9)
![image](https://github.com/user-attachments/assets/c4694ede-2766-4ce5-823b-6c83b8ad8af7)
![image](https://github.com/user-attachments/assets/04a1a021-ef38-400e-a9ba-41c7c0f61274)
![image](https://github.com/user-attachments/assets/8f18f0fe-7a14-4bb8-b4a5-6e9a386c0312)

The friendly guide pulls dialogs from the https://github.com/NicholasDenaro/osrs-friendly-guide-responses and so can be
updated without the need for updating the plugin. This also provides a collaborative environment for adding new dialogs
or removing them for better options. The content is cached within the user's config so they don't need to download every time.

Dialog are toml so they are relatively easy to read and construct. The general format looks like this:
```
type = "Combat|Quest|Money|Skill|Item|Explore"

# Item Dialog Properties
itemType = "Weapon|Armor|Potion|Teleport|Food"

# Quest Dialog Properties
quest = "Quest name"

# Skill Dialog Properties
skillGroup = "Gather|Refine|Combat|Other"

[[messages]]
text = "message 1"
if = "condition" # optional

[[messages]]
text = "message 2"

[[messages]]
text = "message 3"

[[requirements]]
type = "Members"

[[requirements]]
type = "Music"
track = "track name"
negate = true # optional. negate=true means not unlocked. Good for having a requirement of an area not being visited.

[[requirements]]
type = "Skill"
name = "skill name" # Name can also be "Combat" for the player's combat level
level = <number minimum level>
levelMax = <number maximum level> # Optional. Good for restricting dialogs to lower levels only
if = "ironman" # can add conditions here even with negations, like "not ironman" so that the requirement only applies in some circumstances. This makes it so you don't have to have duplicate dialogs

[[requirements]]
type = "NotIronMan" # case-insensitive

[[requirements]]
type = "Quest"
name = "quest name"
status = "not started|in progress|complete" # need status OR minimum. Status takes precedence over minimum
minimum = "not started|in progress|complete"

```

Requirements and messages can both have a single condition, and it is added via the `if` property. The list of valid conditions are:
```
"ironman"
"members"
```

Conditions can be negated with the word `"not "`:
```
"not ironman"
"not members"
```

This is the dialog toml from the images above:
```
type = "Explore"

[[messages]]
text = "There is a dungeon on the island of Brimhaven."

[[messages]]
text = "There is a man at the entrance standing guard and to enter you must pay a fee of 875 gp."

[[messages]]
text = "Bring an axe so you can get past the large roots that grow down there."

[[messages]]
text = "You can find strong monsters down there. Be careful."

[[requirements]]
type = "Members"

[[requirements]]
type = "Music"
track = "7th Realm"
negate = true

[[requirements]]
type = "Music"
track = "High Seas"

[[requirements]]
type = "Skill"
name = "woodcutting"
level = 10
```

This dialog requires that the player has at least level 10 woodcutting, and has visited Brimhaven, but has not been to
the Brimhaven dungeon to unlock the music track. Since the Brimhaven dungeon is in the members area, that is also listed
under the requirements.
