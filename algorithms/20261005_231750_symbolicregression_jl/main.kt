package symbolicregression

import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

fun main() {
    println("=== Symbolic Regression Engine - Unit Tests & Benchmarks ===")

    // Test 1: Expression evaluation
    println("\n[Test 1] Expression Evaluation")
    val expr1 = Expression.add(Expression.variable(), Expression.constant(1.0))
    check(abs(expr1.evaluate(2.0) - 3.0) < 1e-10) { "Expected 3.0, got ${expr1.evaluate(2.0)}" }
    println("  PASS: x + 1 at x=2 equals 3.0")

    val expr2 = Expression.mul(Expression.variable(), Expression.variable())
    check(abs(expr2.evaluate(3.0) - 9.0) < 1e-10) { "Expected 9.0, got ${expr2.evaluate(3.0)}" }
    println("  PASS: x * x at x=3 equals 9.0")

    val expr3 = Expression.pow(Expression.variable(), Expression.constant(2.0))
    check(abs(expr3.evaluate(4.0) - 16.0) < 1e-10) { "Expected 16.0, got ${expr3.evaluate(4.0)}" }
    println("  PASS: x^2 at x=4 equals 16.0")

    // Test 2: Expression complexity
    println("\n[Test 2] Expression Complexity")
    val simpleExpr = Expression.variable()
    check(simpleExpr.complexity() == 1) { "Expected complexity 1, got ${simpleExpr.complexity()}" }
    val complexExpr = Expression.add(Expression.variable(), Expression.constant(1.0))
    check(complexExpr.complexity() == 3) { "Expected complexity 3, got ${complexExpr.complexity()}" }
    println("  PASS: Complexity calculation correct")

    // Test 3: Expression to infix string
    println("\n[Test 3] Infix String Representation")
    val testExpr = Expression.mul(Expression.variable(), Expression.constant(2.0))
    val infixStr = testExpr.toInfix()
    check(infixStr.contains("x")) { "Infix should contain 'x'" }
    check(infixStr.contains("2.0")) { "Infix should contain '2.0'" }
    println("  PASS: Infix representation: $infixStr")

    // Test 4: Fitness evaluation
    println("\n[Test 4] Fitness Evaluation")
    val engine = SymbolicRegressionEngine(maxDepth = 3, populationSize = 50, random = Random(42))
    val xs = listOf(1.0, 2.0, 3.0, 4.0, 5.0)
    val ys = listOf(2.0, 4.0, 6.0, 8.0, 10.0) // y = 2x
    val linearExpr = Expression.mul(Expression.constant(2.0), Expression.variable())
    val fitness = engine.evaluateFitness(linearExpr, xs, ys)
    check(abs(fitness.meanSquaredError) < 1e-10) { "Expected MSE ~0, got ${fitness.meanSquaredError}" }
    println("  PASS: Perfect fit has MSE ~0")

    val badExpr = Expression.constant(1.0)
    val badFitness = engine.evaluateFitness(badExpr, xs, ys)
    check(badFitness.meanSquaredError > 1.0) { "Expected high MSE for bad fit" }
    println("  PASS: Bad fit has high MSE: ${badFitness.meanSquaredError}")

    // Test 5: Mutation
    println("\n[Test 5] Mutation")
    val original = Expression.add(Expression.variable(), Expression.constant(1.0))
    val mutated = engine.mutate(original)
    check(mutated != null) { "Mutated expression should not be null" }
    println("  PASS: Mutation produces valid expression")

    // Test 6: Crossover
    println("\n[Test 6] Crossover")
    val parent1 = Expression.add(Expression.variable(), Expression.constant(1.0))
    val parent2 = Expression.mul(Expression.variable(), Expression.constant(2.0))
    val child = engine.crossover(parent1, parent2)
    check(child != null) { "Crossover should produce valid expression" }
    println("  PASS: Crossover produces valid expression")

    // Test 7: Evolution - learn y = x^2
    println("\n[Test 7] Evolution: Learning y = x^2")
    val xs2 = (1..10).map { it.toDouble() }
    val ys2 = xs2.map { it * it }
    val engine2 = SymbolicRegressionEngine(maxDepth = 4, populationSize = 80, random = Random(123))
    val result2 = engine2.evolve(xs2, ys2, generations = 50, eliteSize = 5)
    println("  Best expression: ${result2.expression.toInfix()}")
    println("  MSE: ${result2.meanSquaredError}")
    check(result2.meanSquaredError < 100.0) { "Should find reasonable fit for x^2" }
    println("  PASS: Evolution finds fit for y = x^2")

    // Test 8: Evolution - learn y = 2x + 1
    println("\n[Test 8] Evolution: Learning y = 2x + 1")
    val xs3 = (1..10).map { it.toDouble() }
    val ys3 = xs3.map { 2.0 * it + 1.0 }
    val engine3 = SymbolicRegressionEngine(maxDepth = 4, populationSize = 80, random = Random(456))
    val result3 = engine3.evolve(xs3, ys3, generations = 50, eliteSize = 5)
    println("  Best expression: ${result3.expression.toInfix()}")
    println("  MSE: ${result3.meanSquaredError}")
    check(result3.meanSquaredError < 50.0) { "Should find reasonable fit for 2x+1" }
    println("  PASS: Evolution finds fit for y = 2x + 1")

    // Test 9: Division by zero safety
    println("\n[Test 9] Division by Zero Safety")
    val divExpr = Expression.div(Expression.constant(1.0), Expression.constant(0.0))
    val divResult = divExpr.evaluate(5.0)
    check(divResult == 0.0) { "Division by zero should return 0.0, got $divResult" }
    println("  PASS: Division by zero safely returns 0.0")

    // Test 10: Negative base with fractional exponent
    println("\n[Test 10] Negative Base Safety")
    val negPowExpr = Expression.pow(Expression.constant(-1.0), Expression.constant(0.5))
    val negPowResult = negPowExpr.evaluate(0.0)
    check(negPowResult == 0.0) { "Negative base with fractional exp should return 0.0" }
    println("  PASS: Negative base with fractional exponent safely returns 0.0")

    // Benchmark
    println("\n=== Benchmark ===")
    val benchXs = (1..50).map { it.toDouble() }
    val benchYs = benchXs.map { it * it + 3.0 * it + 2.0 }
    val benchEngine = SymbolicRegressionEngine(maxDepth = 5, populationSize = 100, random = Random(789))
    val startTime = System.nanoTime()
}
