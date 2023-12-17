package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.alphabet
import me.reckter.aoc.solution
import me.reckter.aoc.solve

class Day15 : Day {
    override val day = 15

    fun hash(input: String): Int {
        return input.fold(0) { acc, c ->
            ((acc + c.code) * 17) % 256
        }
    }

    override fun solvePart1() {
        loadInput()
            .first()
            .split(",")
            .sumOf(::hash)
            .solution(1)
    }

    data class Lens(val label: String, val focalLength: Int)

    override fun solvePart2() {
        val boxes = mutableMapOf<Int, MutableList<Lens>>()

        loadInput()
            .first()
            .split(",")
            .forEach { line ->
                val label = line.takeWhile { it in alphabet }
                val hash = hash(label)
                val isTakeAway = line.contains("-")
                if (isTakeAway) {
                    boxes.getOrPut(hash) { mutableListOf() }
                        .removeIf { it.label == label }
                } else {
                    val focalLength =
                        line.split("=")
                            .last()
                            .toInt()

                    val lenses = boxes.getOrPut(hash) { mutableListOf() }
                    if (lenses.any { it.label == label }) {
                        lenses.replaceAll {
                            if (it.label == label) {
                                Lens(label, focalLength)
                            } else {
                                it
                            }
                        }
                    } else {
                        lenses.add(Lens(label, focalLength))
                    }
                }
            }

        boxes
            .entries
            .map { it ->
                it.value
                    .mapIndexed { index, lens ->
                        lens.focalLength * (index + 1) * (it.key + 1)
                    }
            }
            .flatten()
            .sum()
            .solution(2)
    }
}

fun main() = solve<Day15>()
