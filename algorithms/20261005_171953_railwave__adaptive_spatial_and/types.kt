package railwave

data class Expert(
    val id: Int,
    val capacity: Int = 1 // maximum concurrent tasks this expert can handle
)

data class Task(
    val id: Int,
    val duration: Long, // duration in time units
    val requiredExpertIds: List<Int>
)

data class Assignment(
    val taskId: Int,
    val startTime: Long,
    val endTime: Long,
    val rail: Int
)

data class Schedule(
    val assignments: List<Assignment>
)
