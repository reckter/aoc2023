package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day2 : Day {
    override val day = 2

    val games by lazy {
        loadInput()
            .parseWithRegex("Game (\\d*): (.*)")
            .map { (gameNumber, states) ->
                gameNumber.toInt() to
                    states.split(";")
                        .map {
                            it.split(",")
                                .map { it.trim() }
                                .parseWithRegex("(\\d+) (.*)")
                                .map { (numberStr, color) -> color to numberStr.toInt() }
                        }
            }
    }

    override fun solvePart1() {
        games
            .filter { (_, states) ->
                states.all {
                    it.all { (color, amount) ->
                        when (color) {
                            "red" -> amount <= 12
                            "green" -> amount <= 13
                            "blue" -> amount <= 14
                            else -> error("unknown color $color")
                        }
                    }
                }
            }
            .sumOf { it.first }
            .solution(1)
    }

    override fun solvePart2() {
        games
            .sumOf { (_, states) ->
                states.flatten()
                    .groupBy { it.first }
                    .mapValues { it.value.maxOf { it.second } }
                    .values
                    .reduce(Int::times)
            }
            .solution(2)
    }
}

fun main() = solve<Day2>()
