package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAtEmptyLine

class Day13 : Day {
    override val day = 13

    fun Iterable<String>.mirrorVertical(afterColumn: Int): List<String> {
        return this
            .map { it.take(afterColumn).reversed() }
    }

    fun Iterable<String>.mirrorHorizontal(afterRow: Int): List<String> {
        return this
            .take(afterRow)
            .reversed()
    }

    val maps by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
    }

    fun findMirror(
        map: Iterable<String>,
        allowedErrors: Int,
    ): Int {
        val horizontalMirror =
            map.first()
                .indices
                .drop(1)
                .find { x ->
                    val mirrored = map.mirrorVertical(x)
                    val original = map.map { it.drop(x) }

                    original
                        .zip(mirrored)
                        .sumOf { (a, b) -> a.zip(b).count { it.first != it.second } } == allowedErrors
                }

        val verticalMirror =
            map.toList()
                .indices
                .drop(1)
                .find { y ->
                    val mirrored = map.mirrorHorizontal(y)
                    val original = map.drop(y)

                    original.zip(mirrored)
                        .sumOf { (a, b) -> a.zip(b).count { it.first != it.second } } == allowedErrors
                }

        return horizontalMirror ?: ((verticalMirror ?: 0) * 100)
    }

    override fun solvePart1() {
        maps
            .sumOf { map ->
                findMirror(map, 0)
            }
            .solution(1)
    }

    override fun solvePart2() {
        maps
            .sumOf { map ->
                findMirror(map, 1)
            }
            .solution(2)
    }
}

fun main() = solve<Day13>()
