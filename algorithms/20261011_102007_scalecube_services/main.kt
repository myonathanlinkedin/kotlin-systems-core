package scalecube

fun main() {
    // Run all unit tests. Any failed check will throw an IllegalStateException.
    testRegisterAndRetrieve()
    testDuplicateRegistration()
    testDeregister()
    testListeners()
    testListSnapshot()
    println("All tests passed.")
}

/**
 * Simple assertion helper that throws with a clear message.
 */
private fun assertTrue(condition: Boolean, message: String) {
    if (!condition) throw IllegalStateException("Assertion failed: $message")
}

/**
 * Test that a service can be registered and later retrieved.
 */
private fun testRegisterAndRetrieve() {
    val registry = ServiceRegistry()
    val svc = ServiceInfo(name = "auth", address = "10.0.0.1:8080")
    val added = registry.register(svc)
    assertTrue(added, "Service should be added")
    val fetched = registry.get("auth")
    assertTrue(fetched == svc, "Fetched service must equal the registered one")
}

/**
 * Test that registering a service with an existing name fails and does not replace the original.
 */
private fun testDuplicateRegistration() {
    val registry = ServiceRegistry()
    val first = ServiceInfo(name = "db", address = "10.0.0.2:5432")
    val second = ServiceInfo(name = "db", address = "10.0.0.3:5432")
    assertTrue(registry.register(first), "First registration must succeed")
    assertTrue(!registry.register(second), "Duplicate registration must be rejected")
    val stored = registry.get("db")
    assertTrue(stored == first, "Original service must remain unchanged")
}

/**
 * Test deregistration semantics.
 */
private fun testDeregister() {
    val registry = ServiceRegistry()
    val svc = ServiceInfo(name = "cache", address = "10.0.0.4:6379")
    registry.register(svc)
    assertTrue(registry.deregister("cache"), "Deregistration should succeed")
    assertTrue(registry.get("cache") == null, "Service must be absent after deregister")
    assertTrue(!registry.deregister("cache"), "Second deregister should report false")
}

/**
 * Verify that listeners are invoked exactly once per mutation.
 */
private fun testListeners() {
    val registry = ServiceRegistry()
    var registerCalls = 0
    var deregisterCalls = 0

    registry.addRegisterListener { registerCalls++ }
    registry.addDeregisterListener { deregisterCalls++ }

    val svc = ServiceInfo(name = "search", address = "10.0.0.5:9200")
    registry.register(svc)
    assertTrue(registerCalls == 1, "Register listener should have been called once")
    registry.deregister("search")
    assertTrue(deregisterCalls == 1, "Deregister listener should have been called once")
}

/**
 * Ensure that list() returns a snapshot that is not affected by subsequent mutations.
 */
private fun testListSnapshot() {
    val registry = ServiceRegistry()
    val a = ServiceInfo(name = "svcA", address = "1.1.1.1")
    val b = ServiceInfo(name = "svcB", address = "2.2.2.2")
    registry.register(a)
    val snapshot1 = registry.list()
    assertTrue(snapshot1.size == 1 && snapshot1[0] == a, "Snapshot after first insert must contain only svcA")
    registry.register(b)
    val snapshot2 = registry.list()
    assertTrue(snapshot2.size == 2, "Snapshot after second insert must contain two services")
    // Ensure snapshot1 is unchanged
    assertTrue(snapshot1.size == 1 && snapshot1[0] == a, "First snapshot must remain unchanged after later mutations")
}