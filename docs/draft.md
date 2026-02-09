## Theme

> Hunt the monsters the market demands and deliver the loot faster than anyone else.

A derivative of "Miner's Market", an ore collection competition mod. The game system is fundamentally the same.

## Settings and Basic Rules
- The world contains a "market" where players can sell their loot
- Various loot items have fixed purchase prices; selling adds (loot quantity × purchase price) to the player's sales total
- The first player to reach a sales total of 10,000 wins the game
- Mod name: Hunter's Market
- Mod ID: huntersmarket

## Gameplay Overview
- In multiplayer, players compete to be the first to finish
- In singleplayer, it can be played as a speedrun (RTA) to minimize completion time

## Market Structure
- One structure per world, generated once at the initial spawn point
	- ID: huntersmarket:market
- Generated only once in a new world; not generated in existing worlds
	- Can be placed manually with the command `/place structure huntersmarket:market`
- Players initially spawn at a designated location near the market
- This means the initial spawn point is fixed at the same location for all players
- On death, players respawn at this initial spawn point
- However, players can set a different respawn point by using a bed

## Market Loot Purchase System

### Loot Purchasing
- For the MVP, the purchase price list is fixed
- However, price fluctuation events may occur (may be excluded from MVP scope)
- The proceeds from selling only function as a sales total and are not represented as an in-game item (unlike emeralds used in vanilla villager trading)

### Sales Total
- The player's own sales total is always displayed on screen as a HUD element
- The sales total is represented with a gold coin icon and a number
- Players cannot see other players' sales totals

### Market Merchant NPC
- The market structure contains a fixed villager-model mob called the "Merchant"
	- ID: huntersmarket:merchant
- Players can sell loot by interacting with the merchant mob
- Not affected by vanilla trading system limitations (daily trade limits, price fluctuations)
	- As mentioned above, price fluctuation events may be provided, but as a system independent of vanilla trading
- Items can be sold by using them on the merchant
	- The purchase price list is displayed within the structure using item frames with attached items and signs
	- Loot items are consumed and sold by using them on the merchant mob

## Sellable Items and Purchase Price List
1. Rotten Flesh: 10
2. Bone: 10
3. Arrow: 5
4. String: 5
5. Spider Eye: 15
6. Gunpowder: 15
7. Ender Pearl: 20
8. Glowstone Dust: 20
9. Trident: 100

## Mobs and Drops

Some vanilla hostile mobs have their drop rates for sellable items increased as follows, creating a state where defeating them earns money.

- Zombie, Zombie Villager, Husk:
	- Rotten Flesh: 100%
- Skeleton, Stray:
	- Bone: 100%
	- Arrow: 50%
- Creeper:
	- Gunpowder: 100%
- Spider, Cave Spider:
	- String: 100%
	- Spider Eye: 50%
- Enderman:
	- Ender Pearl: 70%
- Witch:
	- Glowstone Dust: 100%
- Drowned:
	- Rotten Flesh: 100%
	- Trident: 10%

### Notes
- Items not on the above list cannot be sold, even if they are mob drops
- Items on the above list can be sold regardless of how they were obtained (acquisition method does not matter)

## Purchase Price Fluctuation Events
- Excluded from MVP scope
- Price fluctuation events occur once every 20 minutes
	- Prices increase or decrease by 10-30% (random)
- Events are active for only 5 minutes
- Messages are displayed on all players' screens when an event starts or ends

## Game Start and End
- Start
	- A block is provided to declare the game start
		- ID: huntersmarket:game_start_block
		- Name: EN: Game Start Block / JA: Game Start Block
	- The Game Start Block is placed within the market structure
	- Right-clicking the Game Start Block initiates a countdown; when it reaches zero, the game enters the "In Progress" state
	- The Game Start Block can only be used when the game is in the "Not Started" state
	- Play time is counted until the game ends and is displayed on screen as a HUD element
- End
	- When the first player reaches the sales total target, the game enters the "Ended" state
		- Even if multiple players reach the target amount in the same tick, the first one detected is considered the winner
	- A message is displayed on all players' screens along with the winner's player name
	- The play time counter stops
- Reset
	- The reset operation returns the game to the "Not Started" state
	- A block is provided to reset the game
		- ID: huntersmarket:game_reset_block
		- Name: EN: Game Reset Block / JA: Game Reset Block
	- The Game Reset Block is placed within the market structure
	- Right-clicking the Game Reset Block prompts whether to reset
		- If this mechanism is difficult to implement, the reset operation itself may be excluded from the MVP
	- The play time counter and sales totals are cleared

## Game States
- Not Started
	- Internal State
		- Sales total: 0
		- Play time counter: 0:00 (0 min 0 sec)
	- Operations
		- Selling: Disabled
		- Game Start Block: Usable
		- Game Reset Block: Not usable
- In Progress
	- Internal State
		- Sales total: Changes based on sales activity
		- Play time counter: Time elapsed since entering the "In Progress" state (displayed as min:sec)
	- Operations
		- Selling: Enabled
		- Game Start Block: Not usable
		- Game Reset Block: Usable
- Ended
	- Internal State
		- Sales total: Changes based on sales activity (remaining players can continue selling)
		- Play time counter: Continues counting
	- Operations
		- Selling: Enabled (remaining players can continue to reach the goal)
		- Game Start Block: Not usable
		- Game Reset Block: Usable

## Provided Items and Effects
- Players receive the following items upon initial spawn:
	- Iron Sword
	- Shield
	- Bread: 1 stack
- Players are automatically granted Night Vision with unlimited duration

## HUD Display
- Sales Total
	- Position: Top-right of screen, right-aligned
	- Format: 💰 3,250 / 10,000
		- For "💰" emoji part, gui/coin.png texture should be used.
- Play Time (elapsed time since start)
	- Position: Top-right of screen, right-aligned, below the sales total
	- Format: 00:00 (mm:ss)

## Architecture

- Minecraft mod using Architectury
- Compatible with both Fabric and NeoForge
- Initially implemented for Minecraft version 1.21.1, then 1.20.1
- The project will be configured with Gradle as a multi-project setup

## Directory structure

- common-shared
    - Common code without loader dependencies or version dependencies. Not a Gradle subproject, but incorporated as one of the srcDirs from each version-specific subproject
- common-1.20.1
    - Common code for Minecraft 1.20.1 without loader dependencies. Gradle subproject.
- common-1.21.1
    - Common code for Minecraft 1.21.1 without loader dependencies. Gradle subproject.
- fabric-base
    - Code for Fabric without Minecraft version dependencies. Gradle subproject.
- fabric-1.20.1
    - Code for Fabric and Minecraft 1.20.1. Gradle subproject. Depends on fabric-base.
- fabric-1.21.1
    - Code for Fabric and Minecraft 1.21.1. Gradle subproject. Depends on fabric-base.
- neoforge-base
    - Code for NeoForge without Minecraft version dependencies. Gradle subproject.
- neoforge-1.21.1
    - Code for NeoForge and Minecraft 1.21.1. Gradle subproject. Depends on neoforge-base.
- forge-base
    - Code for Forge without Minecraft version dependencies. Gradle subproject.
- forge-1.20.1
    - Code for Forge and Minecraft 1.20.1. Gradle subproject. Depends on forge-base.

## License

- LGPL-3.0-only
