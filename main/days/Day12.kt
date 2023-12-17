package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.days.Day12.Tile.Broken
import me.reckter.aoc.days.Day12.Tile.Operational
import me.reckter.aoc.days.Day12.Tile.Unknown
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers

class Day12 : Day {
    override val day = 12

    enum class Tile {
        Operational,
        Broken,
        Unknown,
    }

    fun countAllPossibleSpringsForDeterminedSpring(
        springs: List<Tile>,
        first: Tile,
        nextRuns: List<Int>,
        currentRun: Int?,
    ): Long {
        if (first == Tile.Unknown) {
            error("got unknown tile")
        }
        if (currentRun == null) {
            if (first == Tile.Broken) {
                if (nextRuns.isEmpty()) return 0
                return countAllPossibleSprings(springs.drop(1), nextRuns.drop(1), nextRuns.first() - 1)
            } else {
                return countAllPossibleSprings(springs.drop(1), nextRuns, null)
            }
        } else {
            if (first == Tile.Operational) {
                if (currentRun > 0) return 0
                return countAllPossibleSprings(springs.drop(1), nextRuns, null)
            } else {
                if (currentRun == 0) return 0
                return countAllPossibleSprings(springs.drop(1), nextRuns, currentRun - 1)
            }
        }
    }

    val cache = mutableMapOf<Triple<List<Tile>, List<Int>, Int?>, Long>()

    fun countAllPossibleSprings(
        springs: List<Tile>,
        nextRuns: List<Int>,
        currentRun: Int?,
    ): Long {
        if (springs.isEmpty()) {
            if ((currentRun != null && currentRun != 0) || nextRuns.isNotEmpty()) return 0
            return 1
        }

        val key = Triple(springs, nextRuns, currentRun)
        if (cache.containsKey(key)) {
            return cache[key]!!
        }
        val first = springs.first()
        if (first != Tile.Unknown) {
            val ret = countAllPossibleSpringsForDeterminedSpring(springs, first, nextRuns, currentRun)
            cache[key] = ret
            return ret
        }

        val ret =
            listOf(
                Operational,
                Broken,
            ).sumOf { replaceFirst ->
                countAllPossibleSpringsForDeterminedSpring(springs, replaceFirst, nextRuns, currentRun)
            }
        cache[key] = ret
        return ret
    }

    override fun solvePart1() {
        loadInput()
            .map {
                val (field, numbersStr) = it.split(" ")
                val numbers = numbersStr.split(",").toIntegers()
                val springs =
                    field.map {
                        when (it) {
                            '#' -> Tile.Broken
                            '.' -> Tile.Operational
                            '?' -> Tile.Unknown
                            else -> error("invalid tile $it")
                        }
                    }

                countAllPossibleSprings(springs, numbers, null)
            }
            .sum()
            .solution(1)
    }

    override fun solvePart2() {
        loadInput()
            .map {
                val (field, numbersStr) = it.split(" ")
                "$field?$field?$field?$field?$field" to "$numbersStr,$numbersStr,$numbersStr,$numbersStr,$numbersStr"
            }
            .map { (field, numbersStr) ->
                val numbers = numbersStr.split(",").toIntegers()
                val springs =
                    field.map {
                        when (it) {
                            '#' -> Tile.Broken
                            '.' -> Tile.Operational
                            '?' -> Tile.Unknown
                            else -> error("invalid tile $it")
                        }
                    }

                countAllPossibleSprings(springs, numbers, null)
            }
            .sum()
            .solution(2)
    }
}

fun main() = solve<Day12>()
