import kotlin.math.max

fun testInternalEvents() {
    val engine = LamportEngine(1)
    engine.internalEvent(0)
    engine.internalEvent(0)
    val p = engine.processes[0]
    check(p.timestamp == 2L) { "Internal events failed, expected 2 but got ${p.timestamp}" }
}

fun testSendReceive() {
    val engine = LamportEngine(2)
    val msg = engine.sendEvent(0, 1)
    engine.receiveEvent(1, msg)
    val p0 = engine.processes[0]
    val p1 = engine.processes[1]
    check(p0.timestamp == 1L) { "Sender timestamp incorrect, expected 1 got ${p0.timestamp}" }
    check(p1.timestamp == 2L) { "Receiver timestamp incorrect, expected 2 got ${p1.timestamp}" }
}

fun testChainSynchronization() {
    val engine = LamportEngine(3)
    // P0 internal
    engine.internalEvent(0) // t0 =1
    // P0 send to P1
    val msg01 = engine.sendEvent(0, 1) // t0=2
    engine.receiveEvent(1, msg01) // t1 = max(0,2)+1 =3
    // P1 internal
    engine.internalEvent(1) // t1=4
    // P1 send to P2
    val msg12 = engine.sendEvent(1, 2) // t1=5
    engine.receiveEvent(2, msg12) // t2 = max(0,5)+1 =6
    val p0 = engine.processes[0]
    val p1 = engine.processes[1]
    val p2 = engine.processes[2]
    check(p0.timestamp == 2L) { "P0 timestamp expected 2 got ${p0.timestamp}" }
    check(p1.timestamp == 5L) { "P1 timestamp expected 5 got ${p1.timestamp}" }
    check(p2.timestamp == 6L) { "P2 timestamp expected 6 got ${p2.timestamp}" }
}

fun testComparison() {
    val engine = LamportEngine(2)
    engine.internalEvent(0) // t0=1
    engine.internalEvent(1) // t1=1
    val p0 = engine.processes[0]
    val p1 = engine.processes[1]
    // timestamps equal, compare by id
    check(engine.compare(p0, p1) < 0) { "Comparison by id failed" }
    // increase p1
    engine.internalEvent(1) // t1=2
    check(engine.compare(p0, p1) < 0) { "Comparison by timestamp failed" }
    // increase p0
    engine.internalEvent(0) // t0=2
    engine.internalEvent(0) // t0=3
    check(engine.compare(p0, p1) > 0) { "Comparison after p0 increase failed" }
}

fun main() {
    testInternalEvents()
    testSendReceive()
    testChainSynchronization()
    testComparison()
    println("All tests passed")
}
