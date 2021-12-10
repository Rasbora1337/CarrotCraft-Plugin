# CarrotCraft-Plugin

Plugin that powered all custom functionality of the CarrotCraft Minecraft server, including:

- Track amount of fully grown carrot blocks harvested for each individual player
- Integrate Economy and Permissions through Vault
- Safely store player data in a remote database with automatic backups at a specified interval
- Securely link Discord and Minecraft accounts
- Automatically give new ranks and permissiosn upon reaching a certain number of carrots harvested in both Minecraft and Discord
- Automatically broadcast messages on Discord and Minecraft when a player reaches certain milestones
- Full donation HTTP API integration
- Custom cosmetics system and GUI to manage it
  - Entity & Particle Trails
  - Character Glow
  - Arrow Trails
  
This plugin requires a configuration file on the server it is running on called config.yml to support Discord integration.

A hikari.properties file is required to integrate a mySQL database.

The SQL schema is located in carrotcraft.sql.

All configuration files are located in the config folder.
