package dj

fun main() {
    // Helper to generate constant functions
    fun constantFunction(value: Int): (Int) -> Int = { _ -> value }

    // Balanced parity function: f(x) = parity of bits of x
    val parityFunction: (Int) -> Int = { x ->
        var count = 0
        var v = x
        while (v != 0) {
            count += v and 1
            v = v shr 1
        }
        count and 1
    }

    // Balanced threshold function: first half 0, second half 1
    fun thresholdFunction(n: Int): (Int) -> Int = { x ->
        if (x < (1 shl (n - 1))) 0 else 1
    }

    // Test cases for n = 1, 2, 3
    for (n in 1..3) {
        // Constant zero
        val constZero = constantFunction(0)
        val resultZero = deutschJozsa(n, constZero)
        assert(resultZero) { "Failed: constant zero should be constant for n=$n" }

        // Constant one
        val constOne = constantFunction(1)
        val resultOne = deutschJozsa(n, constOne)
        assert(resultOne) { "Failed: constant one should be constant for n=$n" }

        // Balanced parity
        val resultParity = deutschJozsa(n, parityFunction)
        assert(!resultParity) { "Failed: parity function should be balanced for n=$n" }

        // Balanced threshold
        val thresh = thresholdFunction(n)
        val resultThresh = deutschJozsa(n, thresh)
        assert(!resultThresh) { "Failed: threshold function should be balanced for n=$n" }
    }

    // Edge case: n = 1 with explicit balanced function f(x)=x
    val identityFunction: (Int) -> Int = { x -> x and 1 }
    val resultIdentity = deutschJozsa(1, identityFunction)
    assert(!resultIdentity) { "Failed: identity function should be balanced for n=1" }

    // All tests passed
    println("All Deutsch‑Jozsa tests passed.")
}