package quantum

import kotlin.system.measureNanoTime

fun main() {
    // Test MetricTensor and inner product
    val metricMatrix = Matrix(
        arrayOf(
            doubleArrayOf(1.0, 0.0),
            doubleArrayOf(0.0, 1.0)
        )
    )
    val metric = MetricTensor(metricMatrix)
    val v1 = Vector(doubleArrayOf(1.0, 2.0))
    val v2 = Vector(doubleArrayOf(3.0, 4.0))
    val ip = metric.innerProduct(v1, v2)
    assert(ip == 11.0) { "Inner product failed: expected 11.0, got $ip" }

    // Test QuantumState normalization
    val stateVec = Vector(doubleArrayOf(3.0, 4.0))
    val state = QuantumState(stateVec)
    val normState = state.normalize(metric)
    val norm = metric.innerProduct(normState.vector, normState.vector)
    assert(kotlin.math.abs(norm - 1.0) < 1e-9) { "Normalization failed: norm = $norm" }

    // Test Hamiltonian evolution step
    val hMatrix = Matrix(
        arrayOf(
            doubleArrayOf(0.0, -1.0),
            doubleArrayOf(1.0, 0.0)
        )
    )
    val hamiltonian = Hamiltonian(hMatrix)
    val dt = 0.1
    val evolved = evolve(state, hamiltonian, dt)
    val expected = Vector(doubleArrayOf(3.0, 4.0) + doubleArrayOf(0.0, -1.0, 1.0, 0.0).map { it * -dt }.toDoubleArray())
    // Since we use real numbers, we just check approximate equality
    assert(kotlin.math.abs(evolved.vector.components[0] - expected.components[0]) < 1e-6)
    assert(kotlin.math.abs(evolved.vector.components[1] - expected.components[1]) < 1e-6)

    // Test simulation over multiple steps
    val steps = 10
    val trajectory = simulate(state, hamiltonian, metric, steps, dt)
    assert(trajectory.size == steps + 1) { "Trajectory size mismatch" }
    // Check that each state remains normalized
    trajectory.forEach { s ->
        val n = metric.innerProduct(s.vector, s.vector)
        assert(kotlin.math.abs(n - 1.0) < 1e-6) { "State not normalized during simulation" }
    }

    // Simple benchmark
    val benchmarkSteps = 1000
    val timeNs = measureNanoTime {
        simulate(state, hamiltonian, metric, benchmarkSteps, dt)
    }
    println("Benchmark: $benchmarkSteps steps took ${timeNs / 1_000_000.0} ms")

    println("All tests passed.")
}
