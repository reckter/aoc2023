package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.cords.d2.minus
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.dijkstraInt
import me.reckter.aoc.parseMap
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day17 : Day {
    override val day = 17

    val map by lazy {
        loadInput()
            .parseMap { it.digitToInt() }
    }

    fun printMap(path: List<Cord2D<Int>>) {
        val minX = map.keys.minOf { it.x }
        val maxX = map.keys.maxOf { it.x }
        val minY = map.keys.minOf { it.y }
        val maxY = map.keys.maxOf { it.y }

        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                print(
                    when (Cord2D(x, y)) {
                        Cord2D(0, 0) -> 'S'
                        Cord2D(maxX, maxY) -> 'E'
                        in path -> '.'
                        else -> map[Cord2D(x, y)]
                    },
                )
            }
            println()
        }
    }

    fun getNeighbours(
        history: List<Pair<Cord2D<Int>, Pair<Int, Cord2D<Int>>>>,
        minimumStraight: Int,
        maximumStraight: Int,
    ): List<Pair<Cord2D<Int>, Pair<Int, Cord2D<Int>>>> {
        val previous =
            history
                .dropLast(1)
                .lastOrNull()

        val last = history.last()
        val neighbors =
            if (last.second.first < minimumStraight) {
                listOf(last.second.second + last.first)
            } else {
                last.first.getNeighbors(noEdges = true)
            }
        val ret =
            neighbors
                .filter { it in map }
                .filter { it != previous?.first }
                .map {
                    val direction = it - last.first
                    if (last.second.second == direction) {
                        it to ((last.second.first + 1) to direction)
                    } else {
                        it to (1 to direction)
                    }
                }
                .filter {
                    it.second.first <= maximumStraight
                }
        return ret
    }

    override fun solvePart1() {
        val maxX = map.keys.maxOf { it.x }
        val maxY = map.keys.maxOf { it.y }

        val end = Cord2D(maxX, maxY)
        val (path, weight) =
            dijkstraInt(
                start = Cord2D(0, 0) to (0 to Cord2D(0, 1)),
                isEnd = { it.first == end },
                getNeighbors = { history ->
                    getNeighbours(history, 0, 3)
                },
                getWeightBetweenNodes = { _, b ->
                    map[b.first] ?: error("no tile at $b")
                },
            )

        weight.solution(1)
    }

    override fun solvePart2() {
        val maxX = map.keys.maxOf { it.x }
        val maxY = map.keys.maxOf { it.y }

        val end = Cord2D(maxX, maxY)
        val (path, weight) =
            dijkstraInt(
                start = Cord2D(0, 0) to (10 to Cord2D(-1, -1)),
                isEnd = { it.first == end },
                getNeighbors = { history ->
                    getNeighbours(history, 4, 10)
                },
                getWeightBetweenNodes = { _, b ->
                    map[b.first] ?: error("no tile at $b")
                },
            )

        weight.solution(2)
    }
}

fun main() = solve<Day17>()
