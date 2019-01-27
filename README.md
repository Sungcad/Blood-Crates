# Blood Crates
Rewards for PvP

## Description
A blood crate is a chest with a hologram above it that when clicked runs a set of commands to reward the player. When a player is killed they have a chance to drop a blood crate. The crate replaces the block where the killed player was then puts back the old block when it is claimed or expires. There is a cool down period before the player can drop another crate. Blood crates can't spawn over doors, beds, banners, brewing stands, signs, or chests to prevent glitches from replacing them.

## Installation
Requires Holographic Displays. Put the jar file into you plugins folder and restart. Then edit the config and reload it with /bloodcrates reload.

## Commands
/bloodcrates - base command aliases /bloodcrate /bcrates /bcrate
/bloodcrates help - show a list of commands
/bloodcrates config - show values from the config
/bloodcrates reload - reload the config
/bloodcrates location - show locations of active blood crates
/bloodcrates cooldowns - show players on cooldown / set or remove a cooldown
/bloodcrates items - show menu to edit item rewards
/bloodcratesdebug - debug command aliases /bcdebug /bcdb
/bloodcratesdebug force - force a crate to spawn at your location
/bloodcratesdebug chance - chance to spawn a crate at your location

## Permissions
bloodcrates.admin - access to all commands and sub-commands
bloodcrates.debug - access to command /bloodcratesdebug
bloodcrates.items - access to sub-command /bloodcrates items
bloodcrates.check - access to sub-command /bloodcrates check
bloodcrates.reload - access to sub-command /bloodcrates reload
bloodcrates.version - access to sub-command /bloodcrates version
bloodcrates.locations - access to sub-command /bloodcrates locations
bloodcrates.cooldowns - access to sub-command /bloodcrates cooldowns
bloodcrates.cooldowns.set - access to sub-command /bloodcrates cooldowns set
bloodcrates.cooldowns.remove - access to sub-command /bloodcrates cooldowns remove 
