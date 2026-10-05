// main.kt
package pavise

// ---------- Resource Definitions ----------
interface Resource {
    val id: String
}

data class Texture(
    override val id: String,
    val width: Int,
    val height: Int
) : Resource

data class Sound(
    override val id: String,
    val durationMs: Int
) : Resource

// ---------- Generic Resource Cache ----------
private class ResourceCache<T : Resource> {
    private val cache = mutableMapOf<String, T>()
    private val refCount = mutableMapOf<String, Int>()

    fun load(resource: T) {
        cache[resource.id] = resource
        refCount[resource.id] = (refCount[resource.id] ?: 0) + 1
    }

    fun acquire(id: String): T? {
        val res = cache[id] ?: return null
        refCount[id] = (refCount[id] ?: 0) + 1
        return res
    }

    fun release(id: String) {
        val count = (refCount[id] ?: 0) - 1
        if (count <= 0) {
            cache.remove(id)
            refCount.remove(id)
        } else {
            refCount[id] = count
        }
    }

    fun getLoadedIds(): Set<String> = cache.keys
}

// ---------- Game Resource Manager ----------
class GameResourceManager {
    private val textureCache = ResourceCache<Texture>()
    private val soundCache = ResourceCache<Sound>()

    fun loadTexture(id: String, width: Int, height: Int) {
        textureCache.load(Texture(id, width, height))
    }

    fun getTexture(id: String): Texture? = textureCache.acquire(id)

    fun releaseTexture(id: String) = textureCache.release(id)

    fun loadSound(id: String, durationMs: Int) {
        soundCache.load(Sound(id, durationMs))
    }

    fun getSound(id: String): Sound? = soundCache.acquire(id)

    fun releaseSound(id: String) = soundCache.release(id)

    fun activeResources(): Set<String> = textureCache.getLoadedIds() + soundCache.getLoadedIds()
}

// ---------- Background Task Management ----------
class BackgroundTask(val name: String, var isRunning: Boolean = true) {
    fun stop() { isRunning = false }
    fun start() { isRunning = true }
}

// Suppresses (pauses) background tasks without OS interaction.
class ProcessSuppressor {
    private val tasks = mutableMapOf<String, BackgroundTask>()

    fun register(task: BackgroundTask) {
        tasks[task.name] = task
    }

    fun suppress(name: String) {
        tasks[name]?.stop()
    }

    fun resume(name: String) {
        tasks[name]?.start()
    }

    fun isRunning(name: String): Boolean = tasks[name]?.isRunning ?: false

    fun activeTaskNames(): Set<String> = tasks.filter { it.value.isRunning }.keys
}

// ---------- Unit Tests ----------
private fun assertTrue(condition: Boolean, message: String = "Assertion failed") {
    if (!condition) throw AssertionError(message)
}

private fun assertEquals(expected: Any?, actual: Any?, message: String = "Assertion failed") {
    if (expected != actual) throw AssertionError("$message: expected <$expected>, actual <$actual>")
}

fun main() {
    // Resource manager tests
    val rm = GameResourceManager()
    rm.loadTexture("hero", 64, 64)
    rm.loadTexture("enemy", 128, 128)
    rm.loadSound("explosion", 500)

    // Acquire resources
    val heroTex = rm.getTexture("hero")
    assertTrue(heroTex != null && heroTex!!.width == 64, "Hero texture loaded incorrectly")
    val enemyTex = rm.getTexture("enemy")
    assertTrue(enemyTex != null && enemyTex!!.height == 128, "Enemy texture loaded incorrectly")
    val explosionSound = rm.getSound("explosion")
    assertTrue(explosionSound != null && explosionSound!!.durationMs == 500, "Sound loaded incorrectly")

    // Reference counting: acquire same resource multiple times
    repeat(3) { rm.getTexture("hero") }
    // Release once
    rm.releaseTexture("hero")
    // Still present because refCount > 0
    assertTrue(rm.activeResources().contains("hero"), "Hero texture should still be active")

    // Release all references
    repeat(3) { rm.releaseTexture("hero") }
    assertTrue(!rm.activeResources().contains("hero"), "Hero texture should be unloaded")

    // Unload remaining resources
    rm.releaseTexture("enemy")
    rm.releaseSound("explosion")
    assertTrue(rm.activeResources().isEmpty(), "All resources should be unloaded")

    // Process suppressor tests
    val suppressor = ProcessSuppressor()
    val taskA = BackgroundTask("AI")
    val taskB = BackgroundTask("Physics")
    suppressor.register(taskA)
    suppressor.register(taskB)

    // Initial state
    assertTrue(suppressor.isRunning("AI"), "Task AI should be running")
    assertTrue(suppressor.isRunning("Physics"), "Task Physics should be running")
    assertEquals(setOf("AI", "Physics"), suppressor.activeTaskNames(), "Active tasks mismatch")

    // Suppress AI
    suppressor.suppress("AI")
    assertTrue(!suppressor.isRunning("AI"), "Task AI should be stopped")
    assertTrue(suppressor.isRunning("Physics"), "Task Physics should remain running")
    assertEquals(setOf("Physics"), suppressor.activeTaskNames(), "Active tasks after suppress mismatch")

    // Resume AI
    suppressor.resume("AI")
    assertTrue(suppressor.isRunning("AI"), "Task AI should be running again")
    assertEquals(setOf("AI", "Physics"), suppressor.activeTaskNames(), "Active tasks after resume mismatch")

    // Suppress non‑existent task (no exception)
    suppressor.suppress("Audio")
    assertTrue(!suppressor.isRunning("Audio"), "Non‑existent task should be reported as not running")

    // All assertions passed
    println("All tests passed.")
}
