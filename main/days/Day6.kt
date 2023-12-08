package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.binarySearch
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers

class Day6 : Day {
    override val day = 6

    val races by lazy {
        val times =
            loadInput()
                .first()
                .removePrefix("Time:")
                .trim()
                .split(" ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toIntegers()

        val distances =
            loadInput()
                .last()
                .removePrefix("Distance:")
                .trim()
                .split(" ")
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .toIntegers()

        times.zip(distances)
    }

    override fun solvePart1() {
        races
            .map { (time, distance) ->
                (0..time)
                    .map { it * (time - it) }
                    .count { it > distance }
            }
            .reduce { acc, i -> acc * i }
            .solution(1)
    }

    override fun solvePart2() {
        val time =
            loadInput()
                .first()
                .removePrefix("Time:")
                .replace(" ", "")
                .toLong()

        val distance =
            loadInput()
                .last()
                .removePrefix("Distance:")
                .replace(" ", "")
                .toLong()

        val firstSuccess = binarySearch(0L, time / 2) { it * (time - it) > distance }
        val lastSuccess = binarySearch(time / 2, time) { it * (time - it) < distance }

        (lastSuccess - firstSuccess).solution(2)
    }
}

fun main() = solve<Day6>()
