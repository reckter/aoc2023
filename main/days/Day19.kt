package me.reckter.aoc.days

import me.reckter.aoc.Day
import me.reckter.aoc.parseWithRegex
import me.reckter.aoc.solution
import me.reckter.aoc.solve
import me.reckter.aoc.splitAtEmptyLine

typealias DataRange = Map<String, IntRange>

class Day19 : Day {
    override val day = 19

    data class Part(
        val values: Map<String, Int>,
    )

    enum class Comparison(val op: (Int, Int) -> Boolean) {
        GT({ a, b -> a > b }),
        LT({ a, b -> a < b }),
    }

    data class Rule(
        val variable: String?,
        val comparison: Comparison?,
        val threshold: Int,
        val result: String,
    ) {
        fun apply(part: Part): String? {
            if (variable == null || comparison == null) {
                return result
            }
            val value = variable.let { part.values[it] } ?: 0
            val triggers = comparison.op(value, threshold)
            return if (triggers) {
                result
            } else {
                null
            }
        }

        // apply; non-apply
        fun applyToRange(range: DataRange): Pair<DataRange?, DataRange?> {
            if (variable == null || comparison == null) {
                return range to null
            }
            val valueRange = variable.let { range[it] } ?: error("no value range found for $variable in $this")

            if (threshold in valueRange) {
                // we need to split
                when (comparison) {
                    Comparison.GT -> {
                        val left = valueRange.first..threshold
                        val right = (threshold + 1)..valueRange.last

                        return range + (variable to right) to
                            range + (variable to left)
                    }

                    Comparison.LT -> {
                        val left = valueRange.first until threshold
                        val right = (threshold)..valueRange.last

                        return range + (variable to left) to
                            range + (variable to right)
                    }
                }
            }
            when (comparison) {
                Comparison.GT -> {
                    if (valueRange.first > threshold) {
                        return range to null
                    }
                    return null to range
                }

                Comparison.LT -> {
                    if (valueRange.last < threshold) {
                        return range to null
                    }
                    return null to range
                }

                else -> {
                    error("unknown comparison $comparison")
                }
            }
        }
    }

    data class Workflow(
        val name: String,
        val rules: List<Rule>,
    )

    fun applyAllWorkflows(
        part: Part,
        workflow: Workflow,
    ): Boolean {
        val result =
            generateSequence(workflow to "") { (workflow, _) ->
                val next =
                    workflow.rules
                        .firstNotNullOf { it.apply(part) }

                val work = workflows[next]
                (work ?: workflow) to next
            }
                .dropWhile { it.second != "A" && it.second != "R" }
                .first()
                .second

        return result == "A"
    }

    fun applyRangeToAllWorkflows(
        range: DataRange,
        workflow: Workflow,
    ): List<DataRange> {
        val (empty, nextStep) =
            workflow.rules
                .fold(listOf(range) to emptyList<Pair<DataRange, String>>()) { acc, cur ->
                    val after =
                        acc.first
                            .map {
                                cur.result to cur.applyToRange(it)
                            }

                    val newResults =
                        after.map {
                            if (it.second.first == null) {
                                null
                            } else {
                                (it.second.first!! to it.first)
                            }
                        }
                            .mapNotNull { it }

                    val newRanges =
                        after.mapNotNull {
                            it.second.second
                        }

                    newRanges to (newResults + acc.second)
                }
        if (empty.isNotEmpty()) {
            error("empty should be empty")
        }

        return nextStep
            .flatMap { (range, result) ->
                when (result) {
                    "A" -> listOf(range)
                    "R" -> emptyList()
                    else -> applyRangeToAllWorkflows(range, workflows[result]!!)
                }
            }
    }

    val workflows by lazy {
        loadInput(trim = false)
            .parseWithRegex("([^{]+)\\{(.*)\\}")
            .map { (name, rules) ->
                val parsedRules =
                    rules.split(",")
                        .map {
                            if (!it.contains(":")) {
                                Rule(null, null, 0, it)
                            } else {
                                val (conditionStr, result) = it.split(":")
                                val match =
                                    Regex("(.+)([>=<])(\\d+)")
                                        .matchEntire(conditionStr)
                                val (variable, comparison, value) = match!!.destructured
                                Rule(
                                    variable = variable,
                                    comparison =
                                        when (comparison) {
                                            ">" -> Comparison.GT
                                            "<" -> Comparison.LT
                                            else -> throw Exception("unknown comparison $comparison")
                                        },
                                    threshold = value.toInt(),
                                    result = result,
                                )
                            }
                        }

                Workflow(name, parsedRules)
            }
            .associateBy { it.name }
    }

    val parts by lazy {
        loadInput(trim = false)
            .splitAtEmptyLine()
            .last()
            .toList()
            .parseWithRegex("\\{x=(\\d+),m=(\\d+),a=(\\d+),s=(\\d+)\\}")
            .map { (x, m, a, s) ->
                Part(
                    mapOf(
                        "x" to x.toInt(),
                        "m" to m.toInt(),
                        "a" to a.toInt(),
                        "s" to s.toInt(),
                    ),
                )
            }
    }

    override fun solvePart1() {
        val firstWorkflow = workflows["in"] ?: error("no workflow 'in' found")
        parts
            .filter {
                applyAllWorkflows(it, firstWorkflow)
            }
            .sumOf { it.values.values.sum() }
            .solution(1)
    }

    override fun solvePart2() {
        val firstWorkflow = workflows["in"] ?: error("no workflow 'in' found")

        val startRange = (1..4000)

        val dataRange =
            mapOf(
                "x" to startRange,
                "m" to startRange,
                "a" to startRange,
                "s" to startRange,
            )

        val acceptedRanges = applyRangeToAllWorkflows(dataRange, firstWorkflow)

        acceptedRanges
            .sumOf {
                it.values
                    .map { it.last - it.first + 1 }
                    .map { it.toLong() }
                    .reduce { a, b -> a * b }
            }
            .solution(2)
    }
}

fun main() = solve<Day19>()
