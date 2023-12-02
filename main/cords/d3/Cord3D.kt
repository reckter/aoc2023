package me.reckter.aoc.cords.d3

import java.lang.Math.abs

/**
 *           3D
 */

data class Cord3D<T : Number>(
    val x: T,
    val y: T,
    val z: T,
)

operator fun Cord3D<Int>.plus(other: Cord3D<Int>): Cord3D<Int> {
    return Cord3D(
        this.x + other.x,
        this.y + other.y,
        this.z + other.z,
    )
}

fun Cord3D<Int>.getNeighbors(noEdges: Boolean = false): List<Cord3D<Int>> {
    if (noEdges) {
        return listOf(
            Cord3D(0, 0, -1),
            Cord3D(0, 0, 1),
            Cord3D(0, -1, 0),
            Cord3D(0, 1, 0),
            Cord3D(-1, 0, 0),
            Cord3D(1, 0, 0),
        )
            .map {
                this + it
            }
    }

    return (-1..1).flatMap { xOffset ->
        (-1..1).flatMap { yOffset ->
            (-1..1).map { zOffset ->
                this + Cord3D(xOffset, yOffset, zOffset)
            }
        }
    }
        .filter { it != this }
}

fun Cord3D<Int>.manhattenDistance(to: Cord3D<Int>): Int {
    return abs(this.x - to.x) + abs(this.y - to.y) + abs(this.z - to.z)
}
