package scalecube

import kotlin.collections.MutableMap
import kotlin.collections.MutableList

/**
 * Immutable description of a service instance.
 */
data class ServiceInfo(
    val name: String,
    val address: String,
    val metadata: Map<String, String> = emptyMap()
)

/**
 * Thread‑safe in‑memory service registry.
 *
 * Guarantees:
 * - All public operations are atomic.
 * - Listeners are invoked synchronously in the thread that performed the mutation.
 */
class ServiceRegistry {
    // name → ServiceInfo
    private val services: MutableMap<String, ServiceInfo> = mutableMapOf()
    private val registerListeners: MutableList<(ServiceInfo) -> Unit> = mutableListOf()
    private val deregisterListeners: MutableList<(ServiceInfo) -> Unit> = mutableListOf()

    /**
     * Registers a new service.
     * @return true if the service was added, false if a service with the same name already exists.
     */
    fun register(service: ServiceInfo): Boolean = synchronized(this) {
        if (services.containsKey(service.name)) {
            false
        } else {
            services[service.name] = service
            // invoke listeners after mutation
            registerListeners.forEach { it(service) }
            true
        }
    }

    /**
     * Removes a service by name.
     * @return true if a service was removed, false otherwise.
     */
    fun deregister(name: String): Boolean = synchronized(this) {
        val removed = services.remove(name)
        if (removed != null) {
            deregisterListeners.forEach { it(removed) }
            true
        } else {
            false
        }
    }

    /**
     * Retrieves a service by name, or null if absent.
     */
    operator fun get(name: String): ServiceInfo? = synchronized(this) {
        services[name]
    }

    /**
     * Returns an immutable snapshot of all registered services.
     */
    fun list(): List<ServiceInfo> = synchronized(this) {
        services.values.toList()
    }

    /**
     * Adds a listener that is called synchronously after a successful registration.
     */
    fun addRegisterListener(listener: (ServiceInfo) -> Unit) = synchronized(this) {
        registerListeners.add(listener)
    }

    /**
     * Adds a listener that is called synchronously after a successful deregistration.
     */
    fun addDeregisterListener(listener: (ServiceInfo) -> Unit) = synchronized(this) {
        deregisterListeners.add(listener)
    }
}