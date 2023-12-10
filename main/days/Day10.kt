package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day10 : Day {
    override val day = 10

    enum class Tile(val getNeighbours: (coord: Cord2D<Int>) -> List<Cord2D<Int>>) {
        Ground({ emptyList() }),
        PipeHorizontal({ listOf(it + Cord2D(1, 0), it + Cord2D(-1, 0)) }),
        PipeVertical({ listOf(it + Cord2D(0, 1), it + Cord2D(0, -1)) }),
        PipeTopLeft({ listOf(it + Cord2D(0, -1), it + Cord2D(-1, 0)) }),
        PipeTopRight({ listOf(it + Cord2D(0, -1), it + Cord2D(1, 0)) }),
        PipeBottomLeft({ listOf(it + Cord2D(0, 1), it + Cord2D(-1, 0)) }),
        PipeBottomRight({ listOf(it + Cord2D(0, 1), it + Cord2D(1, 0)) }),
        PipeAnimal({ listOf(it + Cord2D(1, 0), it + Cord2D(-1, 0), it + Cord2D(0, 1), it + Cord2D(0, -1)) }),
    }

    val map by lazy {
        loadInput()
            .mapIndexed { y, line ->
                line.mapIndexed { x, it ->
                    val tile =
                        when (it) {
                            '.' -> Tile.Ground
                            '-' -> Tile.PipeHorizontal
                            '|' -> Tile.PipeVertical
                            'J' -> Tile.PipeTopLeft
                            'L' -> Tile.PipeTopRight
                            '7' -> Tile.PipeBottomLeft
                            'F' -> Tile.PipeBottomRight
                            'S' -> Tile.PipeAnimal
                            else -> throw IllegalArgumentException("unknown tile $it")
                        }

                    Cord2D(x, y) to tile
                }
            }
            .flatten()
            .toMap()
    }

    fun getConnectedNeighbours(coord: Cord2D<Int>): List<Cord2D<Int>> {
        return map[coord]!!.getNeighbours(coord)
            .filter { map[it] != Tile.Ground }
            .filter { map[it]?.getNeighbours?.invoke(it)?.contains(coord) ?: false }
    }

    val loop by lazy {

        val animal = map.entries.first { it.value == Tile.PipeAnimal }.key
        generateSequence(listOf(animal)) { list ->
            val neighbors = getConnectedNeighbours(list.last())
            val newNeighbour =
                neighbors.firstOrNull { it !in list }
                    ?: return@generateSequence null

            list + newNeighbour
        }
            .last()
    }

    override fun solvePart1() {
        (loop.size / 2)
            .solution(1)
    }

    override fun solvePart2() {
        val lookup = loop.toSet()
        val minX = loop.minOf { it.x }
        val maxX = loop.maxOf { it.x }
        val minY = loop.minOf { it.y }
        val maxY = loop.maxOf { it.y }

        (minY..maxY).map { y ->
            (minX..maxX)
                .map { x -> Cord2D(x, y) }
                .filter { it !in lookup }
                .filter { it ->
                    val crossings =
                        generateSequence(it) {
                            it + Cord2D(-1, -1)
                        }
                            .takeWhile { it.x >= minX && it.y >= minY }
                            .filter { it in lookup }
                            .map {
                                when (map[it]) {
                                    Tile.PipeBottomLeft -> 2
                                    Tile.PipeTopRight -> 2
                                    else -> 1
                                }
                            }
                            .sum()

                    crossings % 2 == 1
                }
        }
            .flatten()
            .size
            .solution(2)
    }
}

fun main() = solve<Day10>()
