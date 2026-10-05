package railwave

object RailWaveScheduler {

    /**
     * Schedules the given tasks onto rails respecting expert capacities and temporal constraints.
     *
     * @param tasks list of tasks to schedule
     * @param experts list of available experts
     * @param railCount number of parallel spatial rails
     * @return a Schedule containing all assignments
     */
    fun schedule(
        tasks: List<Task>,
        experts: List<Expert>,
        railCount: Int
    ): Schedule {
        require(railCount > 0) { "There must be at least one rail." }
        val expertMap = experts.associateBy { it.id }
        // Validate that all required experts exist
        tasks.forEach { task ->
            task.requiredExpertIds.forEach { eid ->
                require(expertMap.containsKey(eid)) { "Task ${task.id} requires unknown expert $eid." }
            }
        }

        // Mutable state tracking usage intervals
        val expertUsage = mutableMapOf<Int, MutableList<Pair<Long, Long>>>()
        val railUsage = mutableMapOf<Int, MutableList<Pair<Long, Long>>>()
        repeat(railCount) { railIdx -> railUsage[railIdx] = mutableListOf() }

        val assignments = mutableListOf<Assignment>()

        // Sort tasks by descending duration to improve packing
        val sortedTasks = tasks.sortedByDescending { it.duration }

        for (task in sortedTasks) {
            var scheduled = false
            var candidateStart = 0L

            // Search for earliest feasible start time
            while (!scheduled) {
                // Find a rail that is free for the whole interval
                val feasibleRail = (0 until railCount).firstOrNull { railIdx ->
                    isIntervalFree(railUsage[railIdx]!!, candidateStart, candidateStart + task.duration)
                }

                if (feasibleRail != null && expertsAvailable(
                        task.requiredExpertIds,
                        candidateStart,
                        candidateStart + task.duration,
                        expertMap,
                        expertUsage
                    )
                ) {
                    // Allocate intervals
                    task.requiredExpertIds.forEach { eid ->
                        expertUsage.computeIfAbsent(eid) { mutableListOf() }
                            .add(Pair(candidateStart, candidateStart + task.duration))
                    }
                    railUsage[feasibleRail]!!.add(Pair(candidateStart, candidateStart + task.duration))
                    assignments.add(
                        Assignment(
                            taskId = task.id,
                            startTime = candidateStart,
                            endTime = candidateStart + task.duration,
                            rail = feasibleRail
                        )
                    )
                    scheduled = true
                } else {
                    // Increment time and try again
                    candidateStart += 1L
                }
            }
        }

        return Schedule(assignments = assignments)
    }

    private fun isIntervalFree(
        intervals: List<Pair<Long, Long>>,
        start: Long,
        end: Long
    ): Boolean {
        return intervals.none { (s, e) -> intervalsOverlap(s, e, start, end) }
    }

    private fun intervalsOverlap(
        aStart: Long,
        aEnd: Long,
        bStart: Long,
        bEnd: Long
    ): Boolean {
        return aStart < bEnd && bStart < aEnd
    }

    private fun expertsAvailable(
        expertIds: List<Int>,
        start: Long,
        end: Long,
        expertMap: Map<Int, Expert>,
        usage: MutableMap<Int, MutableList<Pair<Long, Long>>>
    ): Boolean {
        for (eid in expertIds) {
            val capacity = expertMap[eid]?.capacity ?: 1
            val currentIntervals = usage.getOrDefault(eid, mutableListOf())
            // Count overlapping intervals
            val overlapping = currentIntervals.count { (s, e) -> intervalsOverlap(s, e, start, end) }
            if (overlapping >= capacity) return false
        }
        return true
    }
}
