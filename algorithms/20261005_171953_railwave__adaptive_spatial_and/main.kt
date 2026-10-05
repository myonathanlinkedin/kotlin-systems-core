package railwave

fun main() {
    // Define experts
    val experts = listOf(
        Expert(id = 1, capacity = 1),
        Expert(id = 2, capacity = 1),
        Expert(id = 3, capacity = 1)
    )

    // Define tasks with varying durations and expert requirements
    val tasks = listOf(
        Task(id = 101, duration = 5L, requiredExpertIds = listOf(1, 2)),
        Task(id = 102, duration = 3L, requiredExpertIds = listOf(2)),
        Task(id = 103, duration = 4L, requiredExpertIds = listOf(3)),
        Task(id = 104, duration = 2L, requiredExpertIds = listOf(1, 3)),
        Task(id = 105, duration = 6L, requiredExpertIds = listOf(1, 2, 3))
    )

    // Number of spatial rails
    val railCount = 2

    // Run scheduler
    val schedule = RailWaveScheduler.schedule(tasks, experts, railCount)

    // Verify that every task has exactly one assignment
    check(schedule.assignments.size == tasks.size) {
        "Expected ${tasks.size} assignments, got ${schedule.assignments.size}"
    }

    // Build lookup maps for validation
    val assignmentsByRail = schedule.assignments.groupBy { it.rail }
    val assignmentsByExpert = mutableMapOf<Int, MutableList<Pair<Long, Long>>>()
    for (task in tasks) {
        val assignment = schedule.assignments.first { it.taskId == task.id }
        for (eid in task.requiredExpertIds) {
            assignmentsByExpert.computeIfAbsent(eid) { mutableListOf() }
                .add(Pair(assignment.startTime, assignment.endTime))
        }
    }

    // 1. No overlapping assignments on the same rail
    for ((railIdx, list) in assignmentsByRail) {
        for (i in list.indices) {
            for (j in i + 1 until list.size) {
                val a = list[i]
                val b = list[j]
                val overlap = a.startTime < b.endTime && b.startTime < a.endTime
                check(!overlap) {
                    "Rail $railIdx has overlapping tasks ${a.taskId} and ${b.taskId}"
                }
            }
        }
    }

    // 2. Expert capacity constraints respected
    for ((eid, intervals) in assignmentsByExpert) {
        val capacity = experts.first { it.id == eid }.capacity
        // Check pairwise overlaps count does not exceed capacity
        for (i in intervals.indices) {
            var concurrent = 1
            for (j in intervals.indices) {
                if (i == j) continue
                val (s1, e1) = intervals[i]
                val (s2, e2) = intervals[j]
                if (s1 < e2 && s2 < e1) concurrent++
            }
            check(concurrent <= capacity) {
                "Expert $eid exceeds capacity $capacity at time ${intervals[i].first}"
            }
        }
    }

    // 3. All start times are non‑negative and end = start + duration
    for (assignment in schedule.assignments) {
        val task = tasks.first { it.id == assignment.taskId }
        check(assignment.startTime >= 0L) { "Negative start time for task ${task.id}" }
        check(assignment.endTime == assignment.startTime + task.duration) {
            "Incorrect end time for task ${task.id}"
        }
    }

    // Demo output
    println("Scheduling successful. Assignments:")
    for (a in schedule.assignments.sortedBy { it.startTime }) {
        println("Task ${a.taskId} -> Rail ${a.rail}, [${a.startTime}, ${a.endTime})")
    }
}
