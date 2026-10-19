package core

import kotlin.math.max

data class Task(val memoryProfile: IntArray)

data class ScheduleResult(val startTimes: List<Int>, val peakMemory: Int)

fun findOptimalSchedule(tasks: List<Task>): ScheduleResult {
    val n = tasks.size
    if (n == 0) return ScheduleResult(emptyList(), 0)

    var bestPeak = Int.MAX_VALUE
    var bestStarts: List<Int> = List(n) { 0 }

    val starts = IntArray(n)

    fun backtrack(index: Int, cumulative: MutableList<Int>) {
        if (index == n) {
            val peak = cumulative.maxOrNull() ?: 0
            if (peak < bestPeak) {
                bestPeak = peak
                bestStarts = starts.toList()
            }
            return
        }

        val task = tasks[index]
        // The start can be anywhere from 0 up to current schedule length (inclusive)
        val maxStart = cumulative.size
        for (s in 0..maxStart) {
            // Ensure cumulative list is long enough
            val neededLength = s + task.memoryProfile.size
            while (cumulative.size < neededLength) cumulative.add(0)

            // Apply task memory to cumulative and compute new peak
            var localPeak = 0
            for (i in task.memoryProfile.indices) {
                val idx = s + i
                cumulative[idx] = cumulative[idx] + task.memoryProfile[i]
                if (cumulative[idx] > localPeak) localPeak = cumulative[idx]
            }

            val currentPeak = max(localPeak, cumulative.maxOrNull() ?: 0)
            if (currentPeak < bestPeak) {
                starts[index] = s
                backtrack(index + 1, cumulative)
            }

            // Revert changes
            for (i in task.memoryProfile.indices) {
                val idx = s + i
                cumulative[idx] = cumulative[idx] - task.memoryProfile[i]
            }
            // Trim trailing zeros to keep list compact
            while (cumulative.isNotEmpty() && cumulative.last() == 0) {
                cumulative.removeAt(cumulative.size - 1)
            }
        }
    }

    backtrack(0, mutableListOf())
    return ScheduleResult(bestStarts, bestPeak)
}