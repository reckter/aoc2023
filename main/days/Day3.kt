package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.cords.d2.Cord2D
import me.reckter.aoc.cords.d2.getNeighbors
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import java.io.File

class Day3 : Day {
    override val day = 3

    data class Number(
        val value: Int,
        val cord: Cord2D<Int>,
    )

    data class Symbol(
        val char: Char,
        val cord: Cord2D<Int>,
    )

    val map by lazy {
        loadInput()
            .mapIndexed { y, line ->
                var i = 0
                val numbers = kotlin.collections.mutableListOf<me.reckter.aoc.days.Day3.Number>()
                val symbols = kotlin.collections.mutableListOf<me.reckter.aoc.days.Day3.Symbol>()
                while (i < line.length) {
                    val char = line[i]
                    if (char.isDigit()) {
                        val last =
                            numbers.removeLastOrNull() ?: me.reckter.aoc.days.Day3.Number(
                                0,
                                me.reckter.aoc.cords.d2.Cord2D(i, y),
                            )
                        if (last.cord.x + last.value.toString().length >= i) {
                            numbers.add(last.copy(value = last.value * 10 + char.toString().toInt()))
                        } else {
                            numbers.add(last)
                            numbers.add(
                                me.reckter.aoc.days.Day3.Number(
                                    char.toString().toInt(),
                                    me.reckter.aoc.cords.d2.Cord2D(i, y),
                                ),
                            )
                        }
                    } else if (char != '.') {
                        symbols.add(me.reckter.aoc.days.Day3.Symbol(char, me.reckter.aoc.cords.d2.Cord2D(i, y)))
                    }
                    i++
                }

                numbers to symbols
            }
            .reduce { acc, (numbers, symbols) ->
                acc.first.addAll(numbers)
                acc.second.addAll(symbols)
                acc
            }
    }
    val numbers by lazy { map.first }
    val symbols by lazy { map.second }

    override fun solvePart1() {
        val symbolLookup = symbols.map { it.cord }.toSet()
        numbers
            .filter {
                val ret =
                    (it.cord.x until it.cord.x + it.value.toString().length)
                        .flatMap { x -> Cord2D(x, it.cord.y).getNeighbors() }
                        .distinct()
                        .any { it in symbolLookup }
                ret
            }
            .sumOf { it.value }
            .solution(1)
    }

    override fun solvePart2() {
        symbols
            .filter { it.char == '*' }
            .map { symbol ->
                symbol to
                    numbers.filter {
                        (it.cord.x until it.cord.x + it.value.toString().length)
                            .flatMap { x -> Cord2D(x, it.cord.y).getNeighbors() }
                            .distinct()
                            .any { it == symbol.cord }
                    }
            }
            .filter { it.second.size == 2 }
            .map { it.second.fold(1) { acc, number -> acc * number.value } }
            .sum()
            .solution(2)
    }

    fun printMap(
        numbers: List<Number>,
        symbols: List<Cord2D<Int>>,
    ) {
        val minX = numbers.minOf { it.cord.x }
        val maxX = numbers.maxOf { it.cord.x + it.value.toString().length }
        val minY = numbers.minOf { it.cord.y }
        val maxY = numbers.maxOf { it.cord.y }
        var str = ""
        for (y in minY..maxY) {
            var x = minX
            while (x < maxX) {
                val cord = Cord2D(x, y)
                val number = numbers.find { it.cord == cord }
                val symbol = symbols.find { it == cord }
                if (number != null) {
                    str += number.value.toString()
                    x += number.value.toString().length - 1
                } else if (symbol != null) {
                    str += "#"
                } else {
                    str += "."
                }
                x++
            }
            str += "\n"
        }
        File("map.txt").writeText(str)
        println(str)
    }
}

fun main() = solve<Day3>()
