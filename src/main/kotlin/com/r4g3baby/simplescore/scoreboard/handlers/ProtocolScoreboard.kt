package com.r4g3baby.simplescore.scoreboard.handlers

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.InternalStructure
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.utility.MinecraftVersion
import com.comphenix.protocol.wrappers.EnumWrappers
import com.comphenix.protocol.wrappers.WrappedChatComponent.fromLegacyText
import com.comphenix.protocol.wrappers.WrappedChatComponent.fromText
import com.comphenix.protocol.wrappers.WrappedTeamParameters
import com.r4g3baby.simplescore.scoreboard.models.PlayerBoard
import org.bukkit.entity.Player
import org.bukkit.scoreboard.DisplaySlot
import java.util.*
import java.util.concurrent.ConcurrentHashMap

// todo: update all applicable packets to latest ProtocolLib
class ProtocolScoreboard : ScoreboardHandler() {
    private val protocolManager = ProtocolLibrary.getProtocolManager()

    // Don't use ProtocolLib version enums, so we can support older plugin versions
    private val afterAquaticUpdate = MinecraftVersion("1.13").atOrAbove()
    private val afterCavesAndCliffsUpdate = MinecraftVersion("1.17").atOrAbove()
    private val afterTrailsAndTailsDot2Update = MinecraftVersion("1.20.2").atOrAbove()
    private val afterTrailsAndTailsDot4Update = MinecraftVersion("1.20.4").atOrAbove()
    private val afterTrickyTrialsUpdate = MinecraftVersion("1.21").atOrAbove()

    private val playerBoards = ConcurrentHashMap(HashMap<UUID, PlayerBoard>())

    override fun createScoreboard(player: Player) {
        playerBoards.computeIfAbsent(player.uniqueId) {
            var packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)
            if (!afterTrickyTrialsUpdate) {
                // Defaults mess with NumberFormat after 1.21
                packet.modifier.writeDefaults()
            }
            packet.strings.write(0, getPlayerIdentifier(player)) // Objective Name
            packet.integers.write(0, 0) // Mode 0: Created Scoreboard
            if (afterAquaticUpdate) {
                packet.chatComponents.write(0, fromText("")) // Display Name
            } else packet.strings.write(1, "") // Display Name
            protocolManager.sendServerPacket(player, packet)

            packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_DISPLAY_OBJECTIVE)
            packet.modifier.writeDefaults()
            if (afterTrailsAndTailsDot2Update) {
                packet.getEnumModifier(DisplaySlot::class.java, 0).write(0, DisplaySlot.SIDEBAR)
            } else packet.integers.write(0, 1) // Position 1: Sidebar
            packet.strings.write(0, getPlayerIdentifier(player)) // Objective Name
            protocolManager.sendServerPacket(player, packet)

            return@computeIfAbsent PlayerBoard("", emptyMap())
        }
    }

    override fun removeScoreboard(player: Player) {
        playerBoards.remove(player.uniqueId)?.also { playerBoard ->
            var packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)
            if (!afterTrickyTrialsUpdate) {
                packet.modifier.writeDefaults()
            }
            packet.strings.write(0, getPlayerIdentifier(player)) // Objective Name
            packet.integers.write(0, 1) // Mode 1: Remove Scoreboard
            protocolManager.sendServerPacket(player, packet)

            playerBoard.scores.forEach { (score, _) ->
                packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM)
                packet.modifier.writeDefaults()
                packet.strings.write(0, scoreToName(score)) // Team Name
                if (afterAquaticUpdate) {
                    packet.integers.write(0, 1) // Mode - remove team
                } else packet.integers.write(1, 1) // Mode - remove team
                protocolManager.sendServerPacket(player, packet)
            }
        }
    }

    override fun clearScoreboard(player: Player) {
        playerBoards[player.uniqueId]?.also { playerBoard ->
            if (afterTrailsAndTailsDot4Update) {
                val packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)
                if (!afterTrickyTrialsUpdate) {
                    packet.modifier.writeDefaults()
                }
                packet.strings.write(0, getPlayerIdentifier(player)) // Objective Name
                packet.integers.write(0, 2) // Mode 2: Update Display Name
                if (afterAquaticUpdate) {
                    packet.chatComponents.write(0, fromLegacyText("")) // Display Name
                } else packet.strings.write(1, "") // Display Name
                protocolManager.sendServerPacket(player, packet)

                playerBoard.title = ""
            }

            playerBoard.scores.forEach { (score, _) ->
                val scoreName = scoreToName(score)

                var packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM)
                packet.modifier.writeDefaults()
                packet.strings.write(0, scoreName) // Team Name
                if (afterAquaticUpdate) {
                    packet.integers.write(0, 1) // Mode - remove team
                } else packet.integers.write(1, 1) // Mode - remove team
                protocolManager.sendServerPacket(player, packet)

                if (afterTrickyTrialsUpdate) {
                    packet = PacketContainer(PacketType.Play.Server.RESET_SCORE)
                    packet.strings.write(0, scoreName) // Score Name
                    packet.strings.write(1, getPlayerIdentifier(player)) // Objective Name
                } else if (afterTrailsAndTailsDot4Update) {
                    // todo: temporary 1.20.4 compatibility fix
                    packet = PacketContainer(
                        PacketType.findCurrent(
                            PacketType.Protocol.PLAY, PacketType.Sender.SERVER, 0x42
                        )
                    )
                    packet.modifier.writeDefaults()
                    packet.strings.write(0, scoreName) // Score Name
                    packet.strings.write(1, getPlayerIdentifier(player)) // Objective Name
                } else {
                    packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE)
                    packet.modifier.writeDefaults()
                    packet.strings.write(0, scoreName) // Score Name
                    packet.scoreboardActions.write(0, EnumWrappers.ScoreboardAction.REMOVE) // Action
                    packet.strings.write(1, getPlayerIdentifier(player)) // Objective Name
                }
                protocolManager.sendServerPacket(player, packet)
            }
            playerBoard.scores = emptyMap()
        }
    }

    override fun updateScoreboard(title: String?, scores: Map<Int, String?>, player: Player) {
        playerBoards[player.uniqueId]?.also { playerBoard ->
            if (title != null && playerBoard.title != title) {
                val packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_OBJECTIVE)
                if (!afterTrickyTrialsUpdate) {
                    packet.modifier.writeDefaults()
                }
                packet.strings.write(0, getPlayerIdentifier(player)) // Objective Name
                packet.integers.write(0, 2) // Mode 2: Update Display Name
                if (afterAquaticUpdate) {
                    packet.chatComponents.write(0, fromLegacyText(title)) // Display Name
                } else {
                    val displayTitle = if (title.length > 32) title.substring(0, 32) else title
                    packet.strings.write(1, displayTitle) // Display Name
                }
                protocolManager.sendServerPacket(player, packet)
            }

            scores.forEach { (score, value) ->
                if (value == null) return@forEach

                val boardScore = playerBoard.getScore(value)
                if (boardScore == score) return@forEach

                val scoreName = scoreToName(score)

                var packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM)

                packet.modifier.writeDefaults()
                packet.strings.write(0, scoreName) // Team Name

                // Always split at 16 to improve version compatibility (players on 1.12 and older)
                val splitText = splitScoreLine(value, 16, !afterAquaticUpdate)
                if (afterTrickyTrialsUpdate) {
                    packet.optionalTeamParameters.write(0, Optional.of(
                        WrappedTeamParameters.newBuilder()
                            .displayName(fromText(scoreName))
                            .prefix(fromLegacyText(splitText.first))
                            .suffix(fromLegacyText(splitText.second))
                            .nametagVisibility("never")
                            .collisionRule("never")
                            .color(EnumWrappers.ChatFormatting.RESET)
                            .build()
                    ))
                } else if (afterCavesAndCliffsUpdate) {
                    val optStruct: Optional<InternalStructure> = packet.optionalStructures.read(0)
                    if (optStruct.isPresent) {
                        val struct = optStruct.get()
                        struct.chatComponents.write(0, fromText(scoreName)) // Display Name
                        struct.chatComponents.write(1, fromLegacyText(splitText.first)) // Prefix
                        struct.chatComponents.write(2, fromLegacyText(splitText.second)) // Suffix

                        packet.optionalStructures.write(0, Optional.of(struct))
                    }
                } else if (afterAquaticUpdate) {
                    packet.chatComponents.write(0, fromText(scoreName)) // Display Name
                    packet.chatComponents.write(1, fromLegacyText(splitText.first)) // Prefix
                    packet.chatComponents.write(2, fromLegacyText(splitText.second)) // Suffix
                } else {
                    packet.strings.write(1, scoreName) // Display Name
                    packet.strings.write(2, splitText.first) // Prefix
                    packet.strings.write(3, splitText.second) // Suffix
                }

                // there's no need to create the team again if this line already exists
                if (playerBoard.scores.containsKey(score)) {
                    if (afterAquaticUpdate) {
                        packet.integers.write(0, 2) // Mode - update team info
                    } else packet.integers.write(1, 2) // Mode - update team info
                    protocolManager.sendServerPacket(player, packet)
                    return@forEach
                }

                if (afterAquaticUpdate) {
                    packet.integers.write(0, 0) // Mode - create team
                } else packet.integers.write(1, 0) // Mode - create team
                packet.getSpecificModifier(Collection::class.java).write(0, listOf(scoreName)) // Entities
                protocolManager.sendServerPacket(player, packet)

                packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE)
                if (!afterTrickyTrialsUpdate) {
                    // Defaults mess with nms.chat.Component
                    packet.modifier.writeDefaults()
                }
                packet.strings.write(0, scoreName) // Score Name
                packet.scoreboardActions.write(0, EnumWrappers.ScoreboardAction.CHANGE) // Action
                packet.strings.write(1, getPlayerIdentifier(player)) // Objective Name
                packet.integers.write(0, score) // Score Value
                protocolManager.sendServerPacket(player, packet)
            }

            playerBoard.scores.filter { !scores.containsKey(it.key) }.forEach { (score, _) ->
                val scoreName = scoreToName(score)

                var packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_TEAM)
                packet.modifier.writeDefaults()
                packet.strings.write(0, scoreName) // Team Name
                if (afterAquaticUpdate) {
                    packet.integers.write(0, 1) // Mode - remove team
                } else packet.integers.write(1, 1) // Mode - remove team
                protocolManager.sendServerPacket(player, packet)

                packet = PacketContainer(PacketType.Play.Server.SCOREBOARD_SCORE)
                if (!afterTrickyTrialsUpdate) {
                    // Defaults mess with nms.chat.Component
                    packet.modifier.writeDefaults()
                }
                packet.strings.write(0, scoreName) // Score Name
                packet.scoreboardActions.write(0, EnumWrappers.ScoreboardAction.REMOVE) // Action
                packet.strings.write(1, getPlayerIdentifier(player)) // Objective Name
                protocolManager.sendServerPacket(player, packet)
            }

            playerBoard.apply {
                this.title = title ?: this.title
                this.scores = scores.mapValues { score ->
                    if (score.value == null) {
                        this.scores[score.key] ?: ""
                    } else score.value!!
                }
            }
        }
    }

    override fun hasScoreboard(player: Player): Boolean {
        return playerBoards.containsKey(player.uniqueId)
    }

    override fun hasScores(player: Player): Boolean {
        return playerBoards[player.uniqueId]?.scores?.isNotEmpty() ?: false
    }
}