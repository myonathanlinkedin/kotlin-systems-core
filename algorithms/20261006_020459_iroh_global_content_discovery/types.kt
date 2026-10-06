package iroh

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Represents a unique identifier for a content blob.
 * In Iroh, this is typically a SHA-256 hash.
 */
data class ContentId(
    val bytes: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContentId) return false
        return bytes.contentEquals(other.bytes)
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }

    override fun toString(): String {
        return bytes.joinToString("") { "%02x".format(it) }
    }

    companion object {
        fun random(rng: Random): ContentId {
            val bytes = ByteArray(32)
            rng.nextBytes(bytes)
            return ContentId(bytes)
        }
    }
}

/**
 * Represents a node in the Iroh network.
 * Each node has a unique ID and a position in the DHT (Distributed Hash Table).
 */
data class Node(
    val id: String,
    val address: String,
    val port: Int
) {
    override fun toString(): String {
        return "Node($id, $address:$port)"
    }
}

/**
 * Represents a content entry in the DHT.
 */
data class ContentEntry(
    val id: ContentId,
    val size: Long,
    val providers: List<Node>
)

/**
 * Represents a query for content discovery.
 */
data class DiscoveryQuery(
    val targetId: ContentId,
    val maxResults: Int = 10
)

/**
 * Represents a result from a discovery query.
 */
data class DiscoveryResult(
    val query: DiscoveryQuery,
    val entries: List<ContentEntry>,
    val latencyMs: Long
)

/**
 * Interface for the DHT storage backend.
 */
interface DhtStorage {
    fun put(entry: ContentEntry)
    fun get(id: ContentId): ContentEntry?
    fun remove(id: ContentId)
    fun queryNearby(targetId: ContentId, radius: Double): List<ContentEntry>
    fun getAll(): List<ContentEntry>
    fun size(): Int
}

/**
 * Interface for the discovery engine.
 */
interface DiscoveryEngine {
    fun registerNode(node: Node)
    fun unregisterNode(nodeId: String)
    fun publish(entry: ContentEntry)
    fun unpublish(id: ContentId)
    fun discover(query: DiscoveryQuery): DiscoveryResult
    fun getNodes(): List<Node>
    fun getStats(): DiscoveryStats
}

/**
 * Statistics about the discovery engine.
 */
data class DiscoveryStats(
    val nodeCount: Int,
    val entryCount: Int,
    val queryCount: Long,
    val avgLatencyMs: Double
)