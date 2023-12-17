package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.plus
import me.reckter.aoc.days.Day16.Direction.Down
import me.reckter.aoc.days.Day16.Direction.Left
import me.reckter.aoc.days.Day16.Direction.Right
import me.reckter.aoc.days.Day16.Direction.Up
import me.reckter.aoc.days.Day16.Tile.Empty
import me.reckter.aoc.days.Day16.Tile.MirrorUpLeft
import me.reckter.aoc.days.Day16.Tile.MirrorUpRight
import me.reckter.aoc.days.Day16.Tile.SplitHorizontal
import me.reckter.aoc.days.Day16.Tile.SplitVertical
import me.reckter.aoc.parseMap
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day16 : Day {
    override val day = 16

    enum class Tile {
        Empty,
        MirrorUpLeft,
        MirrorUpRight,
        SplitVertical,
        SplitHorizontal,
    }

    enum class Direction(val movement: Cord2D<Int>) {
        Up(Cord2D(0, -1)),
        Down(Cord2D(0, 1)),
        Left(Cord2D(-1, 0)),
        Right(Cord2D(1, 0)),
    }

    fun step(
        map: Map<Cord2D<Int>, Tile>,
        head: Pair<Cord2D<Int>, Direction>,
    ): List<Pair<Cord2D<Int>, Direction>> {
        val tile =
            map[head.first + head.second.movement]
                ?: return emptyList() // end of map

        return when (tile) {
            Empty -> listOf(head.first + head.second.movement to head.second)
            MirrorUpLeft -> {
                val newDirection =
                    when (head.second) {
                        Direction.Up -> Direction.Left
                        Direction.Down -> Direction.Right
                        Direction.Left -> Direction.Up
                        Direction.Right -> Direction.Down
                    }
                listOf(head.first + head.second.movement to newDirection)
            }

            MirrorUpRight -> {
                val newDirection =
                    when (head.second) {
                        Direction.Up -> Direction.Right
                        Direction.Down -> Direction.Left
                        Direction.Left -> Direction.Down
                        Direction.Right -> Direction.Up
                    }
                listOf(head.first + head.second.movement to newDirection)
            }

            SplitVertical -> {
                if (head.second == Direction.Right || head.second == Direction.Left) {
                    listOf(
                        head.first + head.second.movement to Direction.Up,
                        head.first + head.second.movement to Direction.Down,
                    )
                } else {
                    listOf(head.first + head.second.movement to head.second)
                }
            }

            SplitHorizontal -> {
                if (head.second == Direction.Up || head.second == Direction.Down) {
                    listOf(
                        head.first + head.second.movement to Direction.Left,
                        head.first + head.second.movement to Direction.Right,
                    )
                } else {
                    listOf(head.first + head.second.movement to head.second)
                }
            }
        }
    }

    fun printMap(
        map: Map<Cord2D<Int>, Tile>,
        seenHeads: Set<Pair<Cord2D<Int>, Direction>>,
    ) {
        val minX = map.keys.minOf { it.x }
        val maxX = map.keys.maxOf { it.x }
        val minY = map.keys.minOf { it.y }
        val maxY = map.keys.maxOf { it.y }

        val heads =
            seenHeads
                .groupBy { it.first }
                .mapValues { it.value.map { it.second } }

        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                val tile = map[Cord2D(x, y)]
                val seenDirections = heads[Cord2D(x, y)]

                val char =
                    when (tile) {
                        Empty -> {
                            when {
                                seenDirections == null -> '.'
                                seenDirections.size == 1 -> {
                                    when (seenDirections.first()) {
                                        Up -> '^'
                                        Down -> 'v'
                                        Left -> '<'
                                        Right -> '>'
                                    }
                                }

                                else -> seenDirections.size
                            }
                        }

                        MirrorUpLeft -> '\\'
                        MirrorUpRight -> '/'
                        SplitVertical -> '|'
                        SplitHorizontal -> '-'
                        null -> error("no tile at $x,$y")
                    }
                print(char)
            }
            println()
        }
    }

    val map by lazy {
        loadInput()
            .parseMap {
                when (it) {
                    '.' -> Tile.Empty
                    '\\' -> Tile.MirrorUpLeft
                    '/' -> Tile.MirrorUpRight
                    '|' -> Tile.SplitVertical
                    '-' -> Tile.SplitHorizontal
                    else -> error("unknown tile $it")
                }
            }
    }

    fun simulateBeam(
        startingPosition: Cord2D<Int>,
        startingDirection: Direction,
    ): Set<Pair<Cord2D<Int>, Direction>> {
        val seenHeads: MutableSet<Pair<Cord2D<Int>, Direction>> = mutableSetOf()
        var queue = listOf(startingPosition to startingDirection)
        while (queue.isNotEmpty()) {
            val nextStep =
                queue.flatMap { step(map, it) }
                    .filter { it !in seenHeads }
            seenHeads.addAll(nextStep)
            queue = nextStep
        }
        return seenHeads
    }

    override fun solvePart1() {
        val seenHeads =
            simulateBeam(
                Cord2D(-1, 0),
                Direction.Right,
            )

        seenHeads
            .map { it.first }
            .distinct()
            .size
            .solution(1)
    }

    override fun solvePart2() {
        val minX = map.keys.minOf { it.x }
        val maxX = map.keys.maxOf { it.x }
        val minY = map.keys.minOf { it.y }
        val maxY = map.keys.maxOf { it.y }

        val upDownStart =
            (minX..maxX)
                .flatMap { x ->
                    listOf(
                        Cord2D(x, minY - 1) to Direction.Down,
                        Cord2D(x, maxY + 1) to Direction.Up,
                    )
                }

        val leftRightStart =
            (minY..maxY)
                .flatMap { y ->
                    listOf(
                        Cord2D(minX - 1, y) to Direction.Right,
                        Cord2D(maxX + 1, y) to Direction.Left,
                    )
                }

        listOf(upDownStart, leftRightStart)
            .flatten()
            .map { simulateBeam(it.first, it.second) }
            .maxOf {
                it.map { it.first }
                    .distinct()
                    .size
            }
            .solution(2)
    }
}

fun main() = solve<Day16>()
