# Friendly Guide
The plugin introduces a friendly guide in the Lumbridge Castle as well as in the Grand Exchange
![image](https://github.com/user-attachments/assets/8724a0a3-96a4-4df8-8622-ee2334c65cb4)

You can talk with him and ask him for some help on what to try next.
![image](https://github.com/user-attachments/assets/e40c3c10-32ed-40c7-94be-86f5787f0995)

The guide is smart enough to give you only content which you have immediate access to, based on your levels, quests, and areas explored, whether or not you are a member, or even an ironman.

![image](https://github.com/user-attachments/assets/6e9cff59-8b27-4225-b391-f6fdf65c47c9)
![image](https://github.com/user-attachments/assets/c4694ede-2766-4ce5-823b-6c83b8ad8af7)
![image](https://github.com/user-attachments/assets/04a1a021-ef38-400e-a9ba-41c7c0f61274)
![image](https://github.com/user-attachments/assets/8f18f0fe-7a14-4bb8-b4a5-6e9a386c0312)

The friendly guide pulls dialogs from the https://github.com/NicholasDenaro/osrs-friendly-guide-responses and so can be updated without the need for updating the plugin. This also provides a collaborative environment for adding new dialogs or removing them for better options. The content is cached within the user's config so they don't need to download every time.

Dialog are yaml so they are easy to read and construct. The general format looks like this:
```
type: <Combat|Explore|Money|Item|Quest>
<properties>
requirements: # any combination of requirements is fine. Below are all example requirements.
  - type: Members # case-insensitive
  - type: Music
    track: <track name>
    negate: true # optional. negate=true means not unlocked. Good for having a requirement of an area not being visited.
  - type: Skill
    name: <skill name> # Name can also be "Combat" for the player's combat level
    level: <minimum level>
    levelMax: <maximum level> # Good for restricting dialogs to lower levels only
    if: ironman # can add conditions here even with negations, like "not ironman" so that the requirement only applies in some circumstances. This makes it so you don't have to have duplicate dialogs
  - type: NotIronMan # case-insensitive
  - type: Quest
    name: <quest name>
    status: <not started|in progress|complete> # need status OR minimum. Status takes precedence over minimum
    minimum: <not started|in progress|complete>
messages:
  - raw string message
  - text: object string message, useful for adding condition to include/exclude the dialog for ironman or members or possibly other conditions
    if: not ironman
```

This is the dialog from the images above:
```
type: Explore
requirements:
  - type: Members
  - type: Music
    track: 7th Realm
    negate: true
  - type: Music
    track: High Seas
  - type: Skill
    name: woodcutting
    level: 10
messages:
  - There is a dungeon on the island of Brimhaven.
  - There is a man at the entrance standing guard and to enter you must pay a fee of 875 gp.
  - Bring an axe so you can get past the large roots that grow down there. 
  - You can find strong monsters down there. Be careful.
```
