# xInventories
## Background
xInventories was created in 2016 as a lightweight inventory management plugin for Minecraft server owners. It was sold for a while as a premium resource on [SpigotMC](https://www.spigotmc.org/resources/xinventories.657/) but is no longer undergoing active development.

The code for this project is now open source (in sore need of refactoring) but I'm happy to accept PRs for folks that want continue using / maintaining this project. If you are updating xInventories to work with new versions of Minecraft, I kindly ask you to put up PRs with those changes so that other users can benefit from them.

Below is the original plugin description on SpigotMC:

## About xInventories
This plugin is very similar to Multiverse-Inventories, with the following benefits
- you are not restricted to using Multiverse, xInventories is world-management-plugin agnostic
- xInventories is lightweight and simple to use

## Installation and Configuration
Installation is extremely easy and can be done in just a few minutes. After you purchase the resource, download the jar and upload it to your server's plugins folder. Once you reload/restart your server, a config will be generated automatically and all of the worlds on your server will be loaded into the config. Below you can find a copy of the config file on a fresh server:
```
groups:
- default
worlds:
    world: default
    world_nether: default
    world_the_end: default
respect-gamemode: false
save-async: false
```
First, add your groups. A group is essentially an instance of someone's inventory. For example, if you wanted to have a creative world, you would add a creative group. Each group is isolated from all other groups, meaning nothing is shared between the groups. Define a group for each different set of inventories you want to have on your server.

Once you have added your groups, it's time to move on to the worlds section. In the worlds section, you are assigning a group to a world. You can assign the same group to multiple worlds, and that would make those worlds share inventories. An example configuration can be seen below:
```
groups:
- survival
- creative
- game
worlds:
    world: survival
    world_nether: survival
    world_the_end: survival
    plotworld: creative
    game: game
    freebuild:creative
respect-gamemode: true
save-async: false
```

The above config is sharing the same inventory for `world`, `world_nether`, and `world_the_end.` The `game` world has it's own inventory, and worlds `plotworld` and `freebuild` share an inventory. All groups will have seperate sub-groups based on the user's gamemode.

The option to save inventories asynchronously (save-async) is also available as of version 2.4. However, enabling this option may lead to inventory corruption in rare circumstances. It is highly recommended to leave this feature disabled unless you know what you're doing.