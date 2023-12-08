package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import kotlin.streams.toList

class Day8 : Day {
    override val day = 8

    enum class Instruction(public val accessor: (pair: Pair<String, String>) -> String) {
        Left({ it.first }),
        Right({ it.second }),
    }

    val instructions by lazy {
        loadInput()
            .first()
            .map {
                when (it) {
                    'R' -> Instruction.Right
                    'L' -> Instruction.Left
                    else -> error("invalid instruction")
                }
            }
    }
    val map by lazy {
        loadInput(trim = false)
            .drop(2)
            .parseWithRegex("(.{3}) = \\((.{3}), (.{3})\\)")
            .associate { (current, left, right) -> current to (left to right) }
    }

    override fun solvePart1() {
        generateSequence { instructions }
            .flatten()
            .runningFold("AAA") { acc, instruction ->
                map[acc]?.let { instruction.accessor(it) } ?: error("invalid position $acc")
            }
            .takeWhile { it != "ZZZ" }
            .count()
            .solution(1)
    }

    data class Loop(val offset: Long, val length: Long, val end: Long)

    override fun solvePart2() {
        val loops =
            map.keys.filter { it.endsWith("A") }
                .stream()
                .parallel()
                .map { start ->
                    val instructionSequence = generateSequence { instructions }.flatten()
                    val loop =
                        instructionSequence
                            .runningFold(listOf(start)) { acc, instruction ->
                                acc + (map[acc.last()]?.let { instruction.accessor(it) } ?: error("invalid position $acc"))
                            }
                            .drop(1)
                            .dropWhile {
                                val loopsEndWith = it.last()
                                val loopsStartAt = it.indexOf(loopsEndWith)
                                val loopLength = it.size - loopsStartAt - 1

                                it.distinct().size == it.size || loopLength == 0 || loopLength % instructions.size != 0
                            }
                            .first()

                    val loopsEndWith = loop.last()
                    val loopsStartAt = loop.indexOf(loopsEndWith)
                    val loopLength = loop.size - loopsStartAt - 1

                    val endsOn =
                        loop.mapIndexed { index, s -> s to index }
                            .filter { it.first.endsWith("Z") }
                            .filter { it.second > loopsStartAt }
                            .map { it.second.toLong() }
                            .map { it - loopsStartAt }
                            .toList()
// 					.last()
                            .single()

                    Loop(loopsStartAt.toLong(), loopLength.toLong(), endsOn)
                }
                .toList()

        val firstLoop = loops.maxBy { it.length }
        generateSequence(firstLoop.offset + firstLoop.end) { it + firstLoop.length }
            .find { number ->
                loops.all { loop ->
                    (number - loop.offset) % loop.length == loop.end
                }
            }
            .solution(2)
    }
}

fun main() = solve<Day8>()
