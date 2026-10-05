package concurrency

class VolatileFlag {
    @Volatile
    var isSet: Boolean = false
}

class FlagSetter(private val flag: VolatileFlag, private val delayMs: Long = 0L) : Worker {
    private lateinit var thread: Thread

    override fun start() {
        thread = Thread {
            if (delayMs > 0) Thread.sleep(delayMs)
            flag.isSet = true
        }
        thread.start()
    }

    fun join() = thread.join()
}

fun waitForFlag(flag: VolatileFlag, timeoutMs: Long = 5000L): Boolean {
    val deadline = System.nanoTime() + timeoutMs * 1_000_000
    while (System.nanoTime() < deadline) {
        if (flag.isSet) return true
        Thread.yield()
    }
    return false
}
