package dj

import kotlin.math.sqrt
import kotlin.math.abs

/**
 * Apply a Hadamard gate to the specified qubit (0 = least‑significant bit) of the quantum state.
 *
 * @param state mutable array of amplitudes; will be updated in‑place.
 * @param qubit index of the qubit to transform.
 * @param totalQubits total number of qubits in the register.
 */
fun applyHadamard(state: Array<Complex>, qubit: Int, totalQubits: Int) {
    val mask = 1 shl qubit
    val invSqrt2 = 1.0 / sqrt(2.0)
    val visited = BooleanArray(state.size)

    for (i in state.indices) {
        if (visited[i]) continue
        val j = i xor mask
        val ampI = state[i]
        val ampJ = state[j]

        // |0> -> (|0>+|1>)/√2, |1> -> (|0>-|1>)/√2
        state[i] = (ampI + ampJ) * invSqrt2
        state[j] = (ampI - ampJ) * invSqrt2

        visited[i] = true
        visited[j] = true
    }
}

/**
 * Apply the oracle U_f for a Boolean function f: {0,1}^n → {0,1}.
 * The oracle maps |x, y⟩ → |x, y ⊕ f(x)⟩.
 *
 * @param state mutable array of amplitudes; will be updated in‑place.
 * @param n number of input qubits.
 * @param f function mapping integer representation of x to 0 or 1.
 */
fun applyOracle(state: Array<Complex>, n: Int, f: (Int) -> Int) {
    val totalQubits = n + 1
    val size = 1 shl totalQubits
    val newState = zeroState(size)

    for (idx in 0 until size) {
        val x = idx shr 1               // input bits
        val y = idx and 1               // output bit
        val newY = y xor f(x)           // flip according to f(x)
        val newIdx = (x shl 1) or newY
        newState[newIdx] = newState[newIdx] + state[idx]
    }

    // copy back
    for (i in 0 until size) {
        state[i] = newState[i]
    }
}

/**
 * Simulate the Deutsch‑Jozsa algorithm for a given function f.
 *
 * @param n number of input qubits (≥1).
 * @param f Boolean function promised to be either constant or balanced.
 * @return true if f is constant, false if f is balanced.
 */
fun deutschJozsa(n: Int, f: (Int) -> Int): Boolean {
    require(n >= 1) { "Number of input qubits must be at least 1" }

    val totalQubits = n + 1
    val size = 1 shl totalQubits
    val state = zeroState(size)

    // Initialise to |0…0,1⟩
    val initIdx = 1                     // input bits 0, output bit 1
    state[initIdx] = Complex(1.0, 0.0)

    // Apply Hadamard to all qubits
    for (q in 0 until totalQubits) {
        applyHadamard(state, q, totalQubits)
    }

    // Apply oracle U_f
    applyOracle(state, n, f)

    // Apply Hadamard to the first n input qubits (skip output qubit 0)
    for (q in 1..n) {
        applyHadamard(state, q, totalQubits)
    }

    // Measure: sum probabilities of states where all input bits are 0
    var probZeroInput = 0.0
    for (outputBit in 0..1) {
        val idx = outputBit               // input bits zero → high bits zero
        probZeroInput += state[idx].magnitudeSquared()
    }

    val epsilon = 1e-9
    return abs(probZeroInput - 1.0) < epsilon
}