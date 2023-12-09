package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toLongs

class Day9 : Day {
    override val day = 9

    fun derive(numbers: List<Long>): List<Long> {
        return numbers
            .zipWithNext { a, b -> b - a }
    }

    fun nextValue(
        derivative: List<Long>,
        values: List<Long>,
    ): Long {
        val last = values.last()
        val lastDerivative = derivative.last()
        return last + lastDerivative
    }

    override fun solvePart1() {
        loadInput()
            .map { it.split(" ").toLongs() }
            .map { generateSequence(it) { derive(it) }.takeWhile { it.any { it != 0L } }.toList() }
            .sumOf { it.sumOf { it.last() } }
            .solution(1)
    }

    override fun solvePart2() {
        loadInput()
            .map { it.split(" ").toLongs() }
            .map { it.reversed() }
            .map { generateSequence(it) { derive(it) }.takeWhile { it.any { it != 0L } }.toList() }
            .sumOf { it.sumOf { it.last() } }
            .solution(2)
    }
}

fun main() = solve<Day9>()
