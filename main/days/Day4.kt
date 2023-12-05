package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.print
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.toIntegers
import kotlin.math.pow

class Day4 : Day {
	override val day = 4

	data class Card(
		val cardNumber: Int,
		val winning: List<Int>,
		val have: List<Int>,
	)
	val cards by lazy {
		loadInput()
			.parseWithRegex("Card *?(\\d*): (.*?) \\| (.*?)$")
			.map { (cardNumberStr, winningStr, haveStr) ->
				val winningList = winningStr
					.split(" ")
					.filter { it.isNotEmpty() }
					.toIntegers()
				val haveList = haveStr
					.split(" ")
					.filter { it.isNotEmpty() }
					.toIntegers()
				Card(
					cardNumberStr.toInt(),
					winningList,
					haveList,)
			}
	}

	override fun solvePart1() {
		cards
			.map { it.winning.intersect(it.have) }
			.map { it.size }
			.filter { it > 0 }
			.map { 2.0.pow(it.toDouble() - 1).toInt() }
			.sum()
			.solution(1)
	}

	override fun solvePart2() {
		val cardMap = cards.associateBy { it.cardNumber }
		val copies = cardMap.mapValues { 1 }.toMutableMap()

		cards
			.forEach {card ->
				val wins = card.winning.intersect(card.have).size
				if(wins == 0) return@forEach
				val multiplier = copies[card.cardNumber]!!
				((card.cardNumber + 1 )..(card.cardNumber + wins)).forEach {
					copies[it] = copies[it]!! + multiplier
				}
			}

		copies.values
			.sum()
			.solution(2)

	}
}

fun main() = solve<Day4>()
