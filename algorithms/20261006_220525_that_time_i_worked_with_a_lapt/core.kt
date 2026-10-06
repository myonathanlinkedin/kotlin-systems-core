import kotlin.math.*

data class TheftEvent(val timestamp: Int, val laptopId: String)

class LaptopTheftTracker {
    private val events = mutableListOf<TheftEvent>()
    private val countMap = mutableMapOf<String, Int>()
    private val timestampSet = mutableSetOf<Int>()

    fun recordEvent(timestamp: Int, laptopId: String) {
        require(timestamp >= 0) { "Timestamp must be non‑negative" }
        events.add(TheftEvent(timestamp, laptopId))
        countMap[laptopId] = (countMap[laptopId] ?: 0) + 1
        timestampSet.add(timestamp)
    }

    fun getMostStolenLaptop(): String? {
        if (countMap.isEmpty()) return null
        var bestLaptop: String? = null
        var bestCount = -1
        for ((id, cnt) in countMap) {
            if (cnt > bestCount) {
                bestCount = cnt
                bestLaptop = id
            }
        }
        return bestLaptop
    }

    fun getLongestStreak(): Pair<Int, Int>? {
        if (timestampSet.isEmpty()) return null
        val sorted = timestampSet.sorted()
        var bestStart = sorted[0]
        var bestEnd = sorted[0]
        var curStart = sorted[0]
        var curPrev = sorted[0]

        for (i in 1 until sorted.size) {
            val ts = sorted[i]
            if (ts == curPrev + 1) {
                // continue streak
                curPrev = ts
            } else {
                // end current streak
                if (curPrev - curStart > bestEnd - bestStart) {
                    bestStart = curStart
                    bestEnd = curPrev
                }
                // start new streak
                curStart = ts
                curPrev = ts
            }
        }
        // final check
        if (curPrev - curStart > bestEnd - bestStart) {
            bestStart = curStart
            bestEnd = curPrev
        }
        return Pair(bestStart, bestEnd)
    }
}
