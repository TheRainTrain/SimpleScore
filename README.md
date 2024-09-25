[![SimpleScore Logo]][GitHub]

[![Latest Release](https://img.shields.io/github/v/release/r4g3baby/SimpleScore)](https://github.com/r4g3baby/SimpleScore/releases/latest)
[![Spigot Downloads](https://img.shields.io/spiget/downloads/23243)][SpigotMC]
[![Discord](https://img.shields.io/discord/217018114083127296)][Discord]
[![bStats Servers](https://img.shields.io/bstats/servers/644)][bStats]
[![bStats Players](https://img.shields.io/bstats/players/644)][bStats]
[![License](https://img.shields.io/github/license/r4g3baby/SimpleScore)](https://github.com/r4g3baby/SimpleScore/blob/main/LICENSE)

# Information
[SimpleScore][GitHub] is a plugin for Minecraft servers that allows server owners to display various information to their players using Minecraft's built-in scoreboard system.

Supports selecting and showing specific scoreboards to players based on their permissions, certain conditions, the current world, or even the [WorldGuard][WorldGuard] region they are in with a very simple and intuitive configuration system.

Also comes with support for both [PlaceholderAPI][PlaceholderAPI] and [MVdWPlaceholderAPI][MVdWPlaceholderAPI], allowing you to hook into thousands of other plugins to retrieve and display their information in real time, or create a set of conditions that the player must meet to be able to see a particular scoreboard.

## Main Features
- A lag-free, fully animated scoreboard with no flickering
- Full RGB colour support on 1.16 servers or newer
- Supports all major Minecraft versions from 1.8.x to 1.21.x
- Can display scoreboards based on permissions, conditions, worlds and regions
- No character limit on 1.13 servers or newer (limited to 32 characters on older versions)*
- Fully compatible with [mcMMO][mcMMO] and other plugins that temporarily change the scoreboard*
- Any message sent by the plugin can be modified either by the built-in translation system or by providing a custom message file

*This feature requires [ProtocolLib][ProtocolLib]. Adding [ProtocolLib][ProtocolLib] to your server will improve plugin compatibility and also reduce the number of packets sent to players, thus improving server/client performance and reducing network bandwidth usage.

## Known Issues
- Using an up-to-date version of ProtocolLib (>=5.1.0) *may* cause issues with 1.8-1.20. To circumvent this, temporarily downgrade to ProtocolLib 5.0.0 until this is resolved (possible issue but untested, **ONLY NECESSARY IF YOU USE <=1.20**)

## More Information
### [Installation](https://github.com/r4g3baby/SimpleScore/wiki/Installation) - [Configuration](https://github.com/r4g3baby/SimpleScore/wiki/Configuration) - [Commands](https://github.com/r4g3baby/SimpleScore/wiki/Commands)

### Download Links
[GitHub](https://github.com/r4g3baby/SimpleScore/releases/latest) - [SpigotMC][SpigotMC] - [PaperMC][PaperMC] - [Modrinth][Modrinth]

### Quick Links
[Discord][Discord] - [Issues](https://github.com/r4g3baby/SimpleScore/issues) - [Wiki](https://github.com/r4g3baby/SimpleScore/wiki) - [bStats][bStats]

## Support
If you find [SimpleScore][GitHub] useful and would like to support its development, please consider following, rating or reviewing the project on its respective platform, such as [SpigotMC][SpigotMC], [PaperMC][PaperMC] or [Modrinth][Modrinth].

Starring the project on [GitHub][GitHub] or [following me](https://github.com/r4g3baby) is an easy way to show your support. It helps increase the visibility of [SimpleScore][GitHub] and encourages others to check it out.

I also accept financial support via [GitHub Sponsors][Sponsors] or a one-time donation via [PayPal][PayPal] to show your appreciation for the project.


[SimpleScore Logo]: https://raw.githubusercontent.com/r4g3baby/SimpleScore/main/.github/SimpleScore.png

[GitHub]: https://github.com/r4g3baby/SimpleScore
[Modrinth]: https://modrinth.com/plugin/simplescore
[SpigotMC]: https://www.spigotmc.org/resources/23243/
[PaperMC]: https://hangar.papermc.io/r4g3baby/SimpleScore

[Discord]: https://discord.gg/cJnzTDGphE
[bStats]: https://bstats.org/plugin/bukkit/SimpleScore/644

[Sponsors]: https://github.com/sponsors/r4g3baby
[PayPal]: https://paypal.me/RageBaby

[mcMMO]: https://www.spigotmc.org/resources/64348/
[WorldGuard]: https://dev.bukkit.org/projects/worldguard
[PlaceholderAPI]: https://www.spigotmc.org/resources/6245/
[MVdWPlaceholderAPI]: https://www.spigotmc.org/resources/11182/
[ProtocolLib]: https://www.spigotmc.org/resources/1997/