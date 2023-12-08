package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAtEmptyLine
import me.reckter.aoc.toLongs
import kotlin.math.min

class Day5 : Day {
    override val day = 5

    val areas by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
    }

    val seeds by lazy {
        areas.first()
            .first()
            .removePrefix("seeds: ")
            .split(" ")
            .toLongs()
    }

    val maps by lazy {
        areas.drop(1)
            .associate {
                val (from, to) =
                    it.first()
                        .removeSuffix(" map:")
                        .split("-to-")

                val entries =
                    it.drop(1)
                        .parseWithRegex("(\\d+) (\\d+) (\\d+)")
                        .map { (dstStr, srcStr, rangeStr) -> Triple(dstStr.toLong(), srcStr.toLong(), rangeStr.toLong()) }
                        .map { (dst, src, range) ->
                            (src..src + range - 1) to (dst - src)
                        }
                        .sortedBy { it.first.first }

                val withStart = if (entries.first().first.first > 0L) entries + (0L..0L to 0L) else entries
                val ret =
                    withStart
                        .sortedBy { it.first.first }
                        .windowed(2, 1)
                        .flatMap { (start, end) ->
                            val between = (start.first.last + 1)..(end.first.first - 1)
                            if (between.isEmpty()) {
                                listOf(start, end)
                            } else {
                                listOf(start, between to 0L, end)
                            }
                        }
                        .distinct()

                (from to to) to ret
            }
    }

    fun getMap(from: String): Pair<String, List<Pair<LongRange, Long>>> {
        val entries = maps.entries.first { it.key.first == from }
        return entries.key.second to entries.value
    }

    fun getLocationFromSeed(seed: Long): Long {
        var current = "seed"
        var value = seed
        while (current != "location") {
            val (to, map) = getMap(current)
            val rule =
                map.find { (range, _) -> range.contains(value) }?.second
                    ?: 0
            value += rule
            current = to
        }
        return value
    }

    fun transposeRange(
        range: LongRange,
        map: List<Pair<LongRange, Long>>,
    ): List<LongRange> {
        val result = mutableListOf<LongRange>()
        var current = range
        while (true) {
            val (to, rule) =
                map.find { (range, _) -> range.contains(current.first) }
                    ?: (current to 0L)
            val rest = (to.last + 1..current.last)
            val mapped = (current.first + rule)..min(to.last + rule, current.last + rule)
            result.add(mapped)
            if (rest.isEmpty()) {
                return result
            }
            current = rest
        }
    }

    fun getLocationsFromSeedRange(range: LongRange): List<LongRange> {
        var current = "seed"
        var value = listOf(range)
        while (current != "location") {
            val (to, map) = getMap(current)
            value = value.flatMap { transposeRange(it, map) }
            current = to
        }
        return value
    }

    override fun solvePart1() {
        seeds
            .minOfOrNull { getLocationFromSeed(it) }
            .solution(1)
    }

    override fun solvePart2() {
        seeds.windowed(2, 2)
// 		listOf(82L to 0L)
            .map { (start, length) ->
                (start..start + length)
            }
            .map { it to getLocationsFromSeedRange(it) }
            .map { it.second }
            .minOfOrNull { it.minOf { it.first } }
            .solution(2)
    }
}

fun main() = solve<Day5>()
