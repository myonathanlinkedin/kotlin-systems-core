import java.util.concurrent.Executors
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.RejectedExecutionException

class Scheduler(private val workerCount: Int = 4) {
    private val executor: ExecutorService = Executors.newFixedThreadPool(workerCount)

    fun schedule(task: () -> Unit) {
        executor.submit(task)
    }

    fun awaitTermination(timeout: Long = 5L, unit: TimeUnit = TimeUnit.SECONDS) {
        executor.shutdown()
        executor.awaitTermination(timeout, unit)
    }

    fun isShutdown(): Boolean = executor.isShutdown

    fun isTerminated(): Boolean = executor.isTerminated
}
