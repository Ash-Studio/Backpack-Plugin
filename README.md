# BackpackPlugin
## Description
BackpackPlugin is a customizable Minecraft plugin that provides players with personal storage backpacks. It allows players to store and access their items on-the-go, with different backpack sizes available based on permissions. The plugin features an intuitive command system with tab completion and supports various operations like opening, clearing, and viewing help information.
## Features
- Personal storage backpacks for each player
- Multiple backpack sizes (small: 3 rows, medium: 4 rows, large: 6 rows)
- Simple command interface with tab completion
- Permission-based access to different backpack sizes and commands
- Data persistence across server restarts

## Commands
- `/backpack` or `/bp` - Main command
    - `open` - Opens your backpack
    - `clear` - Clears all items from your backpack
    - `help` - Shows available commands and usage information

## Permissions
- `backpack.use` - Basic access to backpack functionality (default: true)
- `backpack.command.clear` - Allows clearing backpack contents (default: true)
- `backpack.size.small` - Access to small backpacks with 3 rows (default: true)
- `backpack.size.medium` - Access to medium backpacks with 4 rows (default: op)
- `backpack.size.large` - Access to large backpacks with 6 rows (default: op)
- `backpack.admin` - Full administrative access (default: op)

## Requirements
- Minecraft server running Bukkit/Spigot/Paper
- API version: 1.20

This plugin is perfect for survival servers, RPG-themed servers, or any Minecraft server where additional inventory space and organization would enhance the player experience.
