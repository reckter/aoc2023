package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import kotlin.math.pow
import kotlin.streams.toList

class Day7 : Day {
	override val day = 7

	fun scoreHand(hand: String, enableJoker: Boolean): Double {
		val cards = hand.chars().toList()
		val differentCards = cards.groupBy { it }.mapValues { it.value.size }
		val jokers = differentCards.getOrDefault('J'.code, 0)
		val mostCards = if (enableJoker) (differentCards.filter { it.key != 'J'.code }.values.maxOrNull() ?: 0) + jokers else differentCards.values.max()
		val differentCardsCount = if (enableJoker) {
			differentCards.size - if (jokers > 0) 1 else 0
		} else differentCards.size

		val type =
			when {
				mostCards == 5 -> 8
				mostCards == 4 -> 7
				mostCards == 3 && differentCardsCount == 2 -> 6
				mostCards == 3 && differentCardsCount == 3 -> 5
				mostCards == 2 && differentCardsCount == 3 -> 4
				mostCards == 2 && differentCardsCount == 4 -> 3
				mostCards == 1 -> 2
				else -> error("invalid hand")
			}

		val highestCard = hand.toCharArray().toList().reversed().mapIndexed { index, it ->
			val value = when (it) {
				'A' -> 14
				'K' -> 13
				'Q' -> 12
				'J' -> if (enableJoker) 1 else 11
				'T' -> 10
				else -> it.code - 48
			}

			value * 100.0.pow(index)
		}
			.sum()

		return type * 10000000000 + highestCard
	}

	override fun solvePart1() {
		loadInput()
			.map {
				val (hand, bidStr) = it.split(" ")
				hand to bidStr.toInt()
			}
			.sortedBy {scoreHand(it.first, false) }
			.mapIndexed { index, (hand, bid) ->
				bid * (index + 1)
			}
			.sum()
			.solution(1)
	}

	override fun solvePart2() {
		loadInput()
			.map {
				val (hand, bidStr) = it.split(" ")
				hand to bidStr.toInt()
			}
			.sortedBy {scoreHand(it.first, true) }
			.mapIndexed { index, (hand, bid) ->
				bid * (index + 1)
			}
			.sum()
			.solution(1)

	}
}

fun main() = solve<Day7>()
