import kotlin.math.*

data class AllocationHandle(val id: Int)

enum class MemoryError {
    INVALID_HANDLE,
    DOUBLE_FREE,
    OUT_OF_BOUNDS,
    ZERO_ALLOCATION
}
