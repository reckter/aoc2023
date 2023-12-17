package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import kotlin.math.abs

class Day11 : Day {
    override val day = 11

    fun List<String>.flipXY(): List<String> {
        return this
            .mapIndexed { y, line ->
                line.mapIndexed { x, c ->
                    Cord2D(x, y) to c
                }
            }
            .flatten()
            .groupBy { it.first.x }
            .entries
            .sortedBy { it.key }
            .map { it.value.map { it.second }.joinToString("") }
    }

    val galaxies by lazy {
        loadInput()
            .mapIndexed { y, line ->
                line.mapIndexed { x, c ->
                    Cord2D(x.toLong(), y.toLong()) to c
                }
            }
            .flatten()
            .filter { it.second == '#' }
            .map { it.first }
    }

    private fun shiftGalaxies(by: Long): List<Cord2D<Long>> {
        val minX = galaxies.minOf { it.x }.toLong()
        val maxX = galaxies.maxOf { it.x }.toLong()
        val minY = galaxies.minOf { it.y }.toLong()
        val maxY = galaxies.maxOf { it.y }.toLong()

        val missingX =
            (minX..maxX)
                .filter { x -> !galaxies.any { it.x == x } }

        val missingY =
            (minY..maxY)
                .filter { y -> !galaxies.any { it.y == y } }

        val shifted =
            galaxies
                .map { galaxy ->
                    val xShift = missingX.count { it < galaxy.x }
                    val yShift = missingY.count { it < galaxy.y }

                    Cord2D(
                        galaxy.x + xShift * (by - 1L),
                        galaxy.y + yShift * (by - 1L),
                    )
                }
        return shifted
    }

    fun List<Cord2D<Long>>.sumOfManhatenDistances(): Long {
        return this
            .mapIndexed { index, first ->
                this
                    .drop(index + 1)
                    .sumOf { second ->
                        if (first == second) {
                            0
                        } else {
                            abs(first.x - second.x) + abs(first.y - second.y)
                        }
                    }
            }
            .sum()
    }

    override fun solvePart1() {
        val shifted = shiftGalaxies(2L)

        shifted
            .sumOfManhatenDistances()
            .solution(1)
    }

    override fun solvePart2() {
        val shifted = shiftGalaxies(1000000L)

        shifted
            .sumOfManhatenDistances()
            .solution(2)
    }
}

fun main() = solve<Day11>()
