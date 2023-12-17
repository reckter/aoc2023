package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.days.Day12.Tile.Unknown
import me.reckter.aoc.nullIfEmpty
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAt
import me.reckter.aoc.toIntegers

class Day12 : Day {
    override val day = 12

    enum class Tile {
        Operational,
        Broken,
        Unknown,
    }

    fun getAllPossibleSpringsForDeterminedSpring(
        springs: List<Tile>,
        first: Tile,
        nextRuns: List<Int>,
        currentRun: Int?,
    ): List<List<Tile>>? {
        if (first == Tile.Unknown) {
            error("got unknown tile")
        }
        if (currentRun == null) {
            if (first == Tile.Broken) {
                if (nextRuns.isEmpty()) return null
                return getAllPossibleSprings(springs.drop(1), nextRuns.drop(1), nextRuns.first() - 1)
                    ?.map { listOf(first) + it }
            } else {
                return getAllPossibleSprings(springs.drop(1), nextRuns, null)
                    ?.map { listOf(first) + it }
            }
        } else {
            if (first == Tile.Operational) {
                if (currentRun > 0) return null
                return getAllPossibleSprings(springs.drop(1), nextRuns, null)
                    ?.map { listOf(first) + it }
            } else {
                return getAllPossibleSprings(springs.drop(1), nextRuns, currentRun - 1)
                    ?.map { listOf(first) + it }
            }
        }
    }

    fun getAllPossibleSprings(
        springs: List<Tile>,
        nextRuns: List<Int>,
        currentRun: Int?,
    ): List<List<Tile>>? {
        if (springs.isEmpty()) {
            if ((currentRun != null && currentRun != 0) || nextRuns.isNotEmpty()) return null
            return listOf(emptyList())
        }
        val first = springs.first()
        if (first != Tile.Unknown) {
            return getAllPossibleSpringsForDeterminedSpring(springs, first, nextRuns, currentRun)
        }

        return listOf(
            Tile.Operational,
            Tile.Broken,
        )
            .mapNotNull { replaceFirst ->
                getAllPossibleSpringsForDeterminedSpring(springs, replaceFirst, nextRuns, currentRun)
            }
            .flatten()
            .nullIfEmpty()
    }

    fun countBrokenRuns(springs: List<Tile>): List<Int> {
        return springs
            .splitAt { it == Tile.Operational }
            .map { it.count() }
            .filter { it > 0 }
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

                getAllPossibleSprings(springs, numbers, null)
                    ?.count()
                    ?: error("no solution found")
            }
            .sum()
            .solution(1)
    }

    fun getAllPermutations(list: List<Tile>): List<List<Tile>> {
        if (list.isEmpty()) return listOf(emptyList())
        val first = list.first()
        val prefix =
            if (first == Tile.Unknown) {
                listOf(Tile.Operational, Tile.Broken)
            } else {
                listOf(first)
            }
        val restPermutations = getAllPermutations(list.drop(1))

        return prefix
            .flatMap { pre ->
                restPermutations.map {
                    listOf(pre) + it
                }
            }
    }

    override fun solvePart2() {
        loadInput(1)
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
                springs to numbers
            }
            .map { (springs, numbers) ->
                val withoutConnection =
                    getAllPossibleSprings(springs, numbers, null)
                        ?.map { countBrokenRuns(it) }
                        ?.count { it == numbers } ?: error("no solution found")

                val connections =
                    getAllPermutations(
                        listOf(
                            Tile.Unknown,
                            Tile.Unknown,
                            Tile.Unknown,
                            Tile.Unknown,
                        ),
                    )
                val ret =
                    connections
                        .map {
                            val wholeSprings = it.map { springs + listOf(it) }.flatten() + springs
                            val wholeNumbers = it.map { numbers }.flatten() + numbers
                            getAllPossibleSprings(wholeSprings, wholeNumbers, null)
                                ?.count() ?: 0
                        }
                println("$ret $withoutConnection")

                ret.sum()
            }
            .sum()
            .solution(2)
    }
}

fun main() = solve<Day12>()
