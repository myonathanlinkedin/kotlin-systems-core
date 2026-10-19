package main

import core.Task
import core.findOptimalSchedule
import kotlin.math.max

fun assert(condition: Boolean, message: String = "Assertion failed") {
    if (!condition) throw AssertionError(message)
}

fun testSingleTask() {
    val task = Task(intArrayOf(2, 3, 1))
    val result = findOptimalSchedule(listOf(task))
    assert(result.peakMemory == 3, "Single task peak should be 3")
    assert(result.startTimes == listOf(0), "Single task should start at 0")
}

fun testTwoTasks() {
    val t1 = Task(intArrayOf(2, 2))
    val t2 = Task(intArrayOf(3, 1))
    val result = findOptimalSchedule(listOf(t1, t2))
    // Optimal: t1 at 0, t2 at 2 -> peak 3
    assert(result.peakMemory == 3, "Two tasks optimal peak should be 3")
    assert(result.startTimes == listOf(0, 2), "Expected starts [0,2]")
}

fun testThreeTasks() {
    val a = Task(intArrayOf(4))
    val b = Task(intArrayOf(2, 2))
    val c = Task(intArrayOf(1, 3, 1))
    val result = findOptimalSchedule(listOf(a, b, c))
    // One optimal schedule: a@0, b@1, c@3 -> cumulative max = 4
    assert(result.peakMemory == 4, "Three tasks optimal peak should be 4")
    // Verify that the schedule indeed yields peak 4
    val starts = result.startTimes
    val timeline = mutableListOf<Int>()
    for (i in listOf(a, b, c).indices) {
        val task = listOf(a, b, c)[i]
        val s = starts[i]
        val needed = s + task.memoryProfile.size
        while (timeline.size < needed) timeline.add(0)
        for (j in task.memoryProfile.indices) {
            timeline[s + j] += task.memoryProfile[j]
        }
    }
    val computedPeak = timeline.maxOrNull() ?: 0
    assert(computedPeak == result.peakMemory, "Computed peak must match reported peak")
}

fun testEmpty() {
    val result = findOptimalSchedule(emptyList())
    assert(result.peakMemory == 0, "Empty list peak should be 0")
    assert(result.startTimes.isEmpty(), "Empty list startTimes should be empty")
}

fun main() {
    testEmpty()
    testSingleTask()
    testTwoTasks()
    testThreeTasks()
    println("All tests passed.")
}