package me.reckter.aoc.cords.d2

import java.lang.Integer.max
import kotlin.math.absoluteValue
import kotlin.math.sign

/**
 *           2D
 */

data class Cord2D<T : Number>(
    val x: T,
    val y: T
)

operator fun Cord2D<Int>.plus(other: Cord2D<Int>): Cord2D<Int> {
    return Cord2D(
        this.x + other.x,
        this.y + other.y
    )
}

fun Cord2D<Int>.getNeighbors(noEdges: Boolean = false): List<Cord2D<Int>> {
    if (noEdges) {
        return listOf(
            0 to -1,
            0 to 1,
            -1 to 0,
            1 to 0
        )
            .map {
                this + Cord2D(it.first, it.second)
            }
    }

    return (-1..1).flatMap { xOffset ->
        (-1..1).map { yOffset ->
            this + Cord2D(xOffset, yOffset)
        }
    }
        .filter { it != this }
}

fun Cord2D<Int>.lineTo(end: Cord2D<Int>): List<Cord2D<Int>> {
    val result = mutableListOf<Cord2D<Int>>()
    val xDiff = end.x - this.x
    val yDiff = end.y - this.y
    val xDir = xDiff.sign
    val yDir = yDiff.sign
    val xSteps = xDiff.absoluteValue
    val ySteps = yDiff.absoluteValue
    val steps = max(xSteps, ySteps)
    for (i in 0..steps) {
        val x = this.x + (i * xDir * xSteps / steps)
        val y = this.y + (i * yDir * ySteps / steps)
        result.add(Cord2D(x, y))
    }
    return result
}

fun Cord2D<Int>.manhattenDistance(to: Cord2D<Int>): Int {
    return Math.abs(this.x - to.x) + Math.abs(this.y - to.y)
}
