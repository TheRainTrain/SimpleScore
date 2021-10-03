package com.r4g3baby.simplescore.configs

import com.r4g3baby.simplescore.scoreboard.models.BoardScore
import com.r4g3baby.simplescore.scoreboard.models.Condition
import com.r4g3baby.simplescore.scoreboard.models.ScoreLines
import com.r4g3baby.simplescore.scoreboard.models.Scoreboard
import com.r4g3baby.simplescore.utils.configs.ConfigFile
import org.bukkit.configuration.ConfigurationSection
import org.bukkit.plugin.Plugin

class ScoreboardsConfig(plugin: Plugin) : ConfigFile(plugin, "scoreboards") {
    private val conditionsConfig = ConditionsConfig(plugin)
    val conditions get() = conditionsConfig.conditions
    val scoreboards = HashMap<String, Scoreboard>()

    init {
        for (scoreboard in config.getKeys(false).filter { !scoreboards.containsKey(it.lowercase()) }) {
            if (config.isConfigurationSection(scoreboard)) {
                val scoreboardSec = config.getConfigurationSection(scoreboard)
                val updateTime = scoreboardSec.getInt("updateTime", 20)

                val titles = ScoreLines()
                when {
                    scoreboardSec.isList("titles") -> {
                        scoreboardSec.getList("titles").forEach { line ->
                            val parsed = parseLine(line, updateTime)?.also { (text, time) ->
                                titles.add(text, time)
                            }
                            if (parsed == null) plugin.logger.warning(
                                "Invalid title value for scoreboard: $scoreboard, value: $line."
                            )
                        }
                    }
                    scoreboardSec.isString("titles") -> {
                        titles.add(scoreboardSec.getString("titles"), updateTime)
                    }
                    else -> {
                        val titlesValue = scoreboardSec.get("titles")
                        plugin.logger.warning(
                            "Invalid titles value for scoreboard: $scoreboard, value: $titlesValue."
                        )
                    }
                }

                val scores = ArrayList<BoardScore>()
                if (scoreboardSec.isConfigurationSection("scores")) {
                    val scoresSec = scoreboardSec.getConfigurationSection("scores")
                    scoresSec.getKeys(false).mapNotNull { it.toIntOrNull() }.forEach { score ->
                        when {
                            scoresSec.isConfigurationSection(score.toString()) -> {
                                val scoreSec = scoresSec.getConfigurationSection(score.toString())
                                val scoreLines = ScoreLines()
                                when {
                                    scoresSec.isList("lines") -> {
                                        scoreSec.getList("lines").forEach { line ->
                                            val parsed = parseLine(line, updateTime)?.also { (text, time) ->
                                                scoreLines.add(text, time)
                                            }
                                            if (parsed == null) plugin.logger.warning(
                                                "Invalid line value for scoreboard: $scoreboard, score: $score, value: $line."
                                            )
                                        }
                                    }
                                    scoresSec.isString("lines") -> {
                                        scoreLines.add(scoresSec.getString("lines"), updateTime)
                                    }
                                    else -> {
                                        val linesValue = scoresSec.get("lines")
                                        plugin.logger.warning(
                                            "Invalid lines value for scoreboard: $scoreboard, score: $score, value: $linesValue."
                                        )
                                    }
                                }
                                BoardScore(score, scoreLines, getConditions(scoreSec))
                            }
                            scoresSec.isList(score.toString()) -> {
                                val scoreLines = ScoreLines()
                                scoresSec.getList(score.toString()).forEach { line ->
                                    val parsed = parseLine(line, updateTime)?.also { (text, time) ->
                                        scoreLines.add(text, time)
                                    }
                                    if (parsed == null) plugin.logger.warning(
                                        "Invalid line value for scoreboard: $scoreboard, score: $score, value: $line."
                                    )
                                }
                                BoardScore(score, scoreLines)
                            }
                            scoresSec.isString(score.toString()) -> {
                                BoardScore(score, ScoreLines().apply {
                                    add(scoresSec.getString(score.toString()), updateTime)
                                })
                            }
                            else -> {
                                val scoreValue = scoresSec.get(score.toString())
                                plugin.logger.warning(
                                    "Invalid score value for scoreboard: $scoreboard, score: $score, value: $scoreValue."
                                )
                                null
                            }
                        }?.also { scores.add(it) }
                    }
                }

                scoreboards[scoreboard.lowercase()] = Scoreboard(
                    scoreboard, titles, scores, getConditions(scoreboardSec)
                )
            }
        }
    }

    private fun parseLine(line: Any?, updateTime: Int): Pair<String, Int>? {
        return when (line) {
            is String -> line to updateTime
            is Map<*, *> -> line["text"] as String to line.getOrDefault("time", updateTime) as Int
            else -> null
        }
    }

    private fun getConditions(section: ConfigurationSection): List<Condition> {
        return section.getStringList("conditions").mapNotNull { conditions[it.lowercase()] }
    }
}