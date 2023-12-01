package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import kotlin.math.max
import kotlin.math.min

class Day1 : Day {
	override val day = 1

	override fun solvePart1() {
		loadInput()
			.map { it.filter { it.isDigit() } }
			.map { it.first().toString() + it.last().toString() }
			.map { it.toInt() }
			.sum()
			.solution(1)
	}

	override fun solvePart2() {

		val digits = listOf(
			"one" to "1",
			"two" to "2",
			"three" to "3",
			"four" to "4",
			"five" to "5",
			"six" to "6",
			"seven" to "7",
			"eight" to "8",
			"nine" to "9",
			"zero" to "0"
		)
		loadInput()
			.map { str ->
				val first = digits.minBy {
					val strIndex = str.indexOf(it.first)
					val digitIndex = str.indexOf(it.second)
					min(
						if (strIndex == -1) Int.MAX_VALUE else strIndex,
						if (digitIndex == -1) Int.MAX_VALUE else digitIndex
					)
				}
					.second
					.toString()
				val last = digits.maxBy {
					val strIndex = str.lastIndexOf(it.first)
					val digitIndex = str.lastIndexOf(it.second)
					max(
						if (strIndex == -1) Int.MIN_VALUE else strIndex,
						if (digitIndex == -1) Int.MIN_VALUE else digitIndex
					)
				}
					.second
					.toString()
				first + last
			}
			.print("2")
			.map { it.toInt() }
			.sum()
			.solution(2)
	}
}

fun main() = solve<Day1>()
