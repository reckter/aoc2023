package me.reckter.aoc

import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.lineTo
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue
class Cord2DTest {

    @Test
    fun `should correctly calculate lineTo between straight points`() {
        fun testLine(start: Cord2D<Int>, end: Cord2D<Int>, expected: List<Cord2D<Int>>) {
            assertTrue {
                start.lineTo(end) == expected
            }
        }

        testLine(
            Cord2D(0, 0),
            Cord2D(0, 3),
            listOf(
                Cord2D(0, 0),
                Cord2D(0, 1),
                Cord2D(0, 2),
                Cord2D(0, 3)
            )
        )

        testLine(
            Cord2D(0, 0),
            Cord2D(3, 0),
            listOf(
                Cord2D(0, 0),
                Cord2D(1, 0),
                Cord2D(2, 0),
                Cord2D(3, 0)
            )
        )
    }
}
