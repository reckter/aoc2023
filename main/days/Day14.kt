package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day14 : Day {
    override val day = 14

    enum class Tile {
        SquareRock,
        RoundedRock,
        Empty,
    }

    enum class Direction(val numToCord: (num: Int, sizeX: Int, sizeY: Int) -> Cord2D<Int>, val direction: Cord2D<Int>) {
        Up({ num, sizeX, sizeY -> Cord2D(num % sizeX, num / sizeX) }, Cord2D(0, -1)),
        Down({ num, sizeX, sizeY -> Cord2D(num % sizeX, sizeY - 1 - (num / sizeX)) }, Cord2D(0, 1)),
        Left({ num, sizeX, sizeY -> Cord2D(num / sizeY, num % sizeY) }, Cord2D(-1, 0)),
        Right({ num, sizeX, sizeY -> Cord2D(sizeX - 1 - (num / sizeY), num % sizeY) }, Cord2D(1, 0)),
    }

    fun printMap(map: Map<Cord2D<Int>, Tile>) {
        val minX = map.keys.minOf { it.x }
        val maxX = map.keys.maxOf { it.x }
        val minY = map.keys.minOf { it.y }
        val maxY = map.keys.maxOf { it.y }

        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                print(
                    when (map[Cord2D(x, y)]) {
                        Tile.Empty -> '.'
                        Tile.SquareRock -> '#'
                        Tile.RoundedRock -> 'O'
                        else -> error("unknown tile")
                    },
                )
            }
            println()
        }
    }

    fun tiltInDirection(
        map: Map<Cord2D<Int>, Tile>,
        direction: Direction,
    ): Map<Cord2D<Int>, Tile> {
        val ret = map.toMutableMap()
        val minX = map.keys.minOf { it.x }
        val maxX = map.keys.maxOf { it.x }
        val minY = map.keys.minOf { it.y }
        val maxY = map.keys.maxOf { it.y }
        val sizeX = maxX - minX + 1
        val sizeY = maxY - minY + 1

        (0..<(sizeY * sizeX))
            .forEach { num ->
                val cord = direction.numToCord(num, sizeX, sizeY)
                val current =
                    ret[cord]
                        ?: error("no tile on $cord")
                if (current == Tile.RoundedRock) {
                    val freeAbove =
                        generateSequence(cord) { it + direction.direction }
                            .drop(1)
                            .takeWhile { ret.contains(it) }
                            .takeWhile { ret[it] == Tile.Empty }
                            .lastOrNull()

                    if (freeAbove != null) {
                        ret[freeAbove] = Tile.RoundedRock
                        ret[Cord2D(cord.x, cord.y)] = Tile.Empty
                    }
                }
            }

        return ret
    }

    val startMap by lazy {
        loadInput()
            .mapIndexed { y, row ->
                row.mapIndexed { x, it ->
                    Cord2D(x, y) to
                        when (it) {
                            '.' -> Tile.Empty
                            '#' -> Tile.SquareRock
                            'O' -> Tile.RoundedRock
                            else -> error("unknown tile $it")
                        }
                }
            }
            .flatten()
            .toMap()
    }

    fun calculateTotalLoad(map: Map<Cord2D<Int>, Tile>): Int {
        val minX = map.keys.minOf { it.x }
        val maxX = map.keys.maxOf { it.x }
        val minY = map.keys.minOf { it.y }
        val maxY = map.keys.maxOf { it.y }

        return (minY..maxY).sumOf { y ->
            (minX..maxX).sumOf { x ->
                if (map[Cord2D(x, y)] == Tile.RoundedRock) {
                    maxY - (y - 1)
                } else {
                    0
                }
            }
        }
    }

    override fun solvePart1() {
        val afterRol = tiltInDirection(startMap, Direction.Up)

        calculateTotalLoad(afterRol)
            .solution(1)
    }

    fun cycle(map: Map<Cord2D<Int>, Tile>): Map<Cord2D<Int>, Tile> {
        return map
            .let { tiltInDirection(it, Direction.Up) }
            .let { tiltInDirection(it, Direction.Left) }
            .let { tiltInDirection(it, Direction.Down) }
            .let { tiltInDirection(it, Direction.Right) }
    }

    override fun solvePart2() {
        val loop =
            generateSequence((0 to startMap)to mutableMapOf(startMap to 0)) { (current, map) ->
                val next = cycle(current.second)

                if (!map.containsKey(next)) {
                    map[next] = current.first + 1
                }
                (current.first + 1 to next) to map
            }
                .first { (current, map) ->
                    map[current.second] != current.first
                }

        val loopStart = loop.second[loop.first.second]!!
        val loopEnd = loop.first.first
        val offset = (1000000000 - loopStart) % (loopEnd - loopStart)

        val endMap =
            loop.second.entries.find { it.value == (offset + loopStart) }
                ?.key ?: error("no map found")

        calculateTotalLoad(endMap)
            .solution(2)
    }
}

fun main() = solve<Day14>()
