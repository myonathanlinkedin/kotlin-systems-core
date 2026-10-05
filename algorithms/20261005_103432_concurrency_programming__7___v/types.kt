package concurrency

data class Counter(@Volatile var value: Int = 0)

interface Worker {
    fun start()
}
