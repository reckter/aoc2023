package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.days.Day18.Tile.Center
import me.reckter.aoc.days.Day18.Tile.Edge
import me.reckter.aoc.days.Day18.Tile.Empty
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class Day18 : Day {
    override val day = 18

    sealed class Tile {
        data object Empty : Tile()

        data class Edge(val color: String) : Tile()

        data object Center : Tile()
    }

    enum class Direction(val vector: Cord2D<Long>) {
        Up(Cord2D(0L, -1L)),
        Down(Cord2D(0L, 1L)),
        Left(Cord2D(-1L, 0L)),
        Right(Cord2D(1L, 0L)),
    }

    data class Segment(val direction: Direction, val start: Cord2D<Long>, val end: Cord2D<Long>)

    fun printMap(
        map: Map<Cord2D<Long>, Tile>,
        segments: List<Segment>,
    ) {
        val minX = map.keys.minOf { it.x } // + 10
        val maxX = map.keys.maxOf { it.x }
        val minY = map.keys.minOf { it.y }
        val maxY = map.keys.maxOf { it.y }

        (minY..maxY).forEach { y ->
            (minX..maxX).forEach { x ->
                val cord = Cord2D(x, y)
                val isOnSegment =
                    segments
                        .any {
                            val inX =
                                it.start.x == x.toLong() && min(it.start.y, it.end.y) <= y.toLong() && max(
                                    it.start.y,
                                    it.end.y,
                                ) >= y.toLong()
                            val inY =
                                it.start.y == y.toLong() && min(it.start.x, it.end.x) <= x.toLong() && max(
                                    it.start.x,
                                    it.end.x,
                                ) >= x.toLong()
                            inX || inY
                        }

                val tile = map[cord]
                if (isOnSegment) {
                    if (tile is Tile.Edge) {
                        print("#")
                    } else {
                        print('X')
                    }
                } else {
                    print(
                        when (map[Cord2D(x, y)]) {
                            Tile.Empty -> '.'
                            is Tile.Edge -> 'B'
                            Tile.Center -> '.'
                            else -> ' '
                        },
                    )
                }
            }
            println()
        }
    }

    private fun countInside(segments: List<Segment>): Long {
        val nullSegment = Segment(Direction.Down, Cord2D(0, 0), Cord2D(0, 0))

        val minX = segments.minOf { min(it.start.x, it.end.x) }
        val segmentsWithPadding = segments + listOf(nullSegment)

        val segmentsWithChange =
            segmentsWithPadding
                .windowed(3, 1)
                .map { (before, it, after) ->
                    val sattle = before.direction != after.direction

                    it to sattle
                }
        val maxX = segments.maxOf { max(it.start.x, it.end.x) }

        var lastSegments = listOf(nullSegment to false)
        var lastCount = 0L
        return (minX..maxX)
            .map { x ->
                val segmentsAtX =
                    segmentsWithChange.filter { (it, _) ->
                        min(it.start.x, it.end.x) <= x && max(
                            it.start.x,
                            it.end.x,
                        ) >= x
                    }
                        .sortedBy { (it, _) ->
                            listOf(it.start.y, it.end.y)
                                .sorted()
                                .fold(0L) { acc, cur -> acc * 1000000L + cur }
                        }
                if (lastSegments == segmentsAtX) {
                    return@map lastCount
                }

                val includedInX =
                    segmentsAtX
                        .windowed(2, 1)
                        .fold((true to null as Direction?) to 0L) { (insideData, acc), (startSegment, endSegment) ->
                            val (inside, insideDirection) = insideData

                            val startIsSattleOrLedge = startSegment.first.start.x == startSegment.first.end.x
                            val newInside =
                                if (endSegment.first.start.x == endSegment.first.end.x) {
                                    // the end is a sattle/ledge
                                    inside
                                } else if (endSegment.first.start.x == x || endSegment.first.end.x == x) {
                                    // end is neighbour of a sattle/ledge
                                    if (startIsSattleOrLedge) {
                                        insideDirection == endSegment.first.direction
                                    } else {
                                        // the next segment after end is a sattle/ledge
                                        // so we are inside
                                        true
                                    }
                                } else {
                                    !inside
                                }
                            val newAcc =
                                if (inside) {
                                    acc +
                                        max(
                                            endSegment.first.start.y,
                                            endSegment.first.end.y,
                                        ) - max(startSegment.first.start.y, startSegment.first.end.y) + if (!newInside) 1 else 0
                                } else {
                                    acc
                                }
                            val newInsideDirection =
                                if (inside && insideDirection == null) {
                                    startSegment.first.direction
                                } else if (!newInside) {
                                    null
                                } else {
                                    insideDirection
                                }
                            (newInside to newInsideDirection) to newAcc
                        }.second

                lastSegments = segmentsAtX
                lastCount = includedInX

                includedInX
            }
            .sum()
    }

    private fun floodFill(map: MutableMap<Cord2D<Long>, Tile>) {
        val minY = map.keys.minOf { it.y }
        val maxY = map.keys.maxOf { it.y }

        val emptyPlace =
            Cord2D(
                1,
                (minY..maxY).find {
                    map[Cord2D(0, it)] is Edge && (map[Cord2D(1L, it)] == null || map[Cord2D(1, it)] is Empty)
                } ?: error("could not find start point for flood"),
            )

        val queue = ArrayDeque<Cord2D<Long>>(listOf(emptyPlace))

        while (queue.isNotEmpty()) {
            val next = queue.removeFirst()
            val neighbors =
                next.getNeighbors()
                    .filter { it !in map }

            neighbors.forEach {
                map[it] = Center
            }
            queue.addAll(neighbors)
        }
    }

    override fun solvePart1() {
        val nullSegment = Segment(Direction.Down, Cord2D(0, 0), Cord2D(0, 0))
        val segments =
            loadInput()
                .parseWithRegex("(.) (\\d+) \\(#(......)\\)")
                .runningFold(nullSegment) { acc, cur ->
                    val (direction, lengthStr, _) = cur
                    val length = lengthStr.toLong(10)

                    val directionToTravel =
                        when (direction) {
                            "U" -> Direction.Up
                            "D" -> Direction.Down
                            "L" -> Direction.Left
                            "R" -> Direction.Right
                            else -> error("unknown direction $direction")
                        }

                    val newCord = acc.end + directionToTravel.vector * length

                    Segment(directionToTravel, acc.end, newCord)
                }

        countInside(segments)
            .solution(1)
    }

    override fun solvePart2() {
        val nullSegment = Segment(Direction.Down, Cord2D(0, 0), Cord2D(0, 0))
        val segments =
            loadInput()
                .parseWithRegex("(.) (\\d+) \\(#(......)\\)")
                .runningFold(nullSegment) { acc, cur ->
                    val (_, _, color) = cur
                    val length = color.dropLast(1).toLong(16)

                    val directionToTravel =
                        when (val direction = color.last()) {
                            '3' -> Direction.Up
                            '1' -> Direction.Down
                            '2' -> Direction.Left
                            '0' -> Direction.Right
                            else -> error("unknown direction $direction")
                        }
                    val newCord = acc.end + directionToTravel.vector * length
                    val (direction, lengthStr, _) = cur

                    Segment(directionToTravel, acc.end, newCord)
                }

        (segments + segments.first())
            .map { it.end }
            .windowed(2, 1)
            .sumOf { (i, i1) ->
                (i.y + i1.y) * (i.x - i1.x)
            }
            .let {
                it / 2 + segments.sumOf {
                    val length = (it.end - it.start)
                    abs(length.x + length.y)
                } / 2 + 1
            }
            .solution(2)

        countInside(segments)
            .solution(2)
    }

    operator fun Cord2D<Long>.times(length: Long): Cord2D<Long> {
        return Cord2D(this.x * length, this.y * length)
    }

    private operator fun Cord2D<Long>.plus(cord2D: Cord2D<Long>): Cord2D<Long> {
        return Cord2D(this.x + cord2D.x, this.y + cord2D.y)
    }

    private operator fun Cord2D<Long>.minus(cord2D: Cord2D<Long>): Cord2D<Long> {
        return Cord2D(this.x - cord2D.x, this.y - cord2D.y)
    }

    fun Cord2D<Long>.getNeighbors(noEdges: Boolean = false): List<Cord2D<Long>> {
        if (noEdges) {
            return listOf(
                0L to -1L,
                0L to 1L,
                -1L to 0L,
                1L to 0L,
            )
                .map {
                    this + Cord2D(it.first, it.second)
                }
        }

        return (-1L..1L).flatMap { xOffset ->
            (-1L..1L).map { yOffset ->
                this + Cord2D(xOffset, yOffset)
            }
        }
            .filter { it != this }
    }
}

fun main() = solve<Day18>()
