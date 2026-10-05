package symbolicregression

import kotlin.math.abs
import kotlin.math.pow
import kotlin.random.Random

data class Symbol(val name: String) {
    override fun toString(): String = name
}

data class Expression(
    val type: ExprType,
    val left: Expression? = null,
    val right: Expression? = null,
    val symbol: Symbol? = null,
    val value: Double? = null
) {
    enum class ExprType {
        VARIABLE,
        CONSTANT,
        ADD,
        SUB,
        MUL,
        DIV,
        POW
    }

    fun evaluate(x: Double): Double {
        return when (type) {
            ExprType.VARIABLE -> x
            ExprType.CONSTANT -> value ?: 0.0
            ExprType.ADD -> (left?.evaluate(x) ?: 0.0) + (right?.evaluate(x) ?: 0.0)
            ExprType.SUB -> (left?.evaluate(x) ?: 0.0) - (right?.evaluate(x) ?: 0.0)
            ExprType.MUL -> (left?.evaluate(x) ?: 0.0) * (right?.evaluate(x) ?: 0.0)
            ExprType.DIV -> {
                val denom = right?.evaluate(x) ?: 0.0
                if (abs(denom) < 1e-10) 0.0 else (left?.evaluate(x) ?: 0.0) / denom
            }
            ExprType.POW -> {
                val base = left?.evaluate(x) ?: 0.0
                val exp = right?.evaluate(x) ?: 0.0
                if (base < 0 && exp != exp.toLong().toDouble()) 0.0 else base.pow(exp)
            }
        }
    }

    fun toInfix(): String {
        return when (type) {
            ExprType.VARIABLE -> symbol?.name ?: "x"
            ExprType.CONSTANT -> value?.toString() ?: "0.0"
            ExprType.ADD -> "(${left?.toInfix()} + ${right?.toInfix()})"
            ExprType.SUB -> "(${left?.toInfix()} - ${right?.toInfix()})"
            ExprType.MUL -> "(${left?.toInfix()} * ${right?.toInfix()})"
            ExprType.DIV -> "(${left?.toInfix()} / ${right?.toInfix()})"
            ExprType.POW -> "(${left?.toInfix()} ^ ${right?.toInfix()})"
        }
    }

    fun complexity(): Int {
        return when (type) {
            ExprType.VARIABLE, ExprType.CONSTANT -> 1
            else -> 1 + (left?.complexity() ?: 0) + (right?.complexity() ?: 0)
        }
    }

    companion object {
        fun variable(name: String = "x"): Expression =
            Expression(ExprType.VARIABLE, symbol = Symbol(name))

        fun constant(value: Double): Expression =
            Expression(ExprType.CONSTANT, value = value)

        fun add(left: Expression, right: Expression): Expression =
            Expression(ExprType.ADD, left = left, right = right)

        fun sub(left: Expression, right: Expression): Expression =
            Expression(ExprType.SUB, left = left, right = right)

        fun mul(left: Expression, right: Expression): Expression =
            Expression(ExprType.MUL, left = left, right = right)

        fun div(left: Expression, right: Expression): Expression =
            Expression(ExprType.DIV, left = left, right = right)

        fun pow(left: Expression, right: Expression): Expression =
            Expression(ExprType.POW, left = left, right = right)
    }
}

data class FitnessResult(
    val meanSquaredError: Double,
    val maxAbsoluteError: Double,
    val expression: Expression
)

class SymbolicRegressionEngine(
    private val maxDepth: Int = 5,
    private val populationSize: Int = 100,
    private val mutationRate: Double = 0.2,
    private val crossoverRate: Double = 0.7,
    private val random: Random = Random.Default
) {
    private val operators = listOf(
        { l: Expression, r: Expression -> Expression.add(l, r) },
        { l: Expression, r: Expression -> Expression.sub(l, r) },
        { l: Expression, r: Expression -> Expression.mul(l, r) },
        { l: Expression, r: Expression -> Expression.div(l, r) },
        { l: Expression, r: Expression -> Expression.pow(l, r) }
    )

    private val constants = listOf(0.0, 1.0, 2.0, 3.0, -1.0, 0.5)

    fun generateRandomExpression(depth: Int = 0): Expression {
        if (depth >= maxDepth) {
            return if (random.nextBoolean()) {
                Expression.variable()
            } else {
                Expression.constant(constants[random.nextInt(constants.size)])
            }
        }

        val useOperator = random.nextDouble() < 0.7
        return if (useOperator) {
            val op = operators[random.nextInt(operators.size)]
            val left = generateRandomExpression(depth + 1)
            val right = generateRandomExpression(depth + 1)
            op(left, right)
        } else {
            if (random.nextBoolean()) {
                Expression.variable()
            } else {
                Expression.constant(constants[random.nextInt(constants.size)])
            }
        }
    }

    fun evaluateFitness(expression: Expression, xs: List<Double>, ys: List<Double>): FitnessResult {
        var mse = 0.0
        var maxAbsErr = 0.0
        for (i in xs.indices) {
            val predicted = expression.evaluate(xs[i])
            val error = predicted - ys[i]
            mse += error * error
            val absErr = abs(error)
            if (absErr > maxAbsErr) maxAbsErr = absErr
        }
        mse /= xs.size
        return FitnessResult(mse, maxAbsErr, expression)
    }

    fun mutate(expression: Expression): Expression {
        if (random.nextDouble() > mutationRate) return expression
        return mutateNode(expression, 0)
    }

    private fun mutateNode(node: Expression, depth: Int): Expression {
        if (depth >= maxDepth) return node
        if (node.type == Expression.ExprType.VARIABLE || node.type == Expression.ExprType.CONSTANT) {
            return if (random.nextBoolean()) {
                Expression.variable()
            } else {
                Expression.constant(constants[random.nextInt(constants.size)])
            }
        }
        val newLeft = mutateNode(node.left ?: Expression.variable(), depth + 1)
        val newRight = mutateNode(node.right ?: Expression.variable(), depth + 1)
        return when (node.type) {
            Expression.ExprType.ADD -> Expression.add(newLeft, newRight)
            Expression.ExprType.SUB -> Expression.sub(newLeft, newRight)
            Expression.ExprType.MUL -> Expression.mul(newLeft, newRight)
            Expression.ExprType.DIV -> Expression.div(newLeft, newRight)
            Expression.ExprType.POW -> Expression.pow(newLeft, newRight)
            else -> node
        }
    }

    fun crossover(parent1: Expression, parent2: Expression): Expression {
        if (random.nextDouble() > crossoverRate) return parent1
        return crossoverNode(parent1, parent2, 0)
    }

    private fun crossoverNode(node1: Expression, node2: Expression, depth: Int): Expression {
        if (depth >= maxDepth) return node1
        if (node1.type == Expression.ExprType.VARIABLE || node1.type == Expression.ExprType.CONSTANT) {
            return if (random.nextBoolean()) node1 else node2
        }
        val swapLeft = random.nextBoolean()
        val swapRight = random.nextBoolean()
        val newLeft = if (swapLeft) {
            crossoverNode(node1.left ?: Expression.variable(), node2.left ?: Expression.variable(), depth + 1)
        } else {
            node1.left ?: Expression.variable()
        }
        val newRight = if (swapRight) {
            crossoverNode(node1.right ?: Expression.variable(), node2.right ?: Expression.variable(), depth + 1)
        } else {
            node1.right ?: Expression.variable()
        }
        return when (node1.type) {
            Expression.ExprType.ADD -> Expression.add(newLeft, newRight)
            Expression.ExprType.SUB -> Expression.sub(newLeft, newRight)
            Expression.ExprType.MUL -> Expression.mul(newLeft, newRight)
            Expression.ExprType.DIV -> Expression.div(newLeft, newRight)
            Expression.ExprType.POW -> Expression.pow(newLeft, newRight)
            else -> node1
        }
    }

    fun evolve(
        xs: List<Double>,
        ys: List<Double>,
        generations: Int = 100,
        eliteSize: Int = 5
    ): FitnessResult {
        var population = List(populationSize) { generateRandomExpression() }
        var bestFitness = evaluateFitness(population[0], xs, ys)
        var bestExpression = population[0]

        for (gen in 0 until generations) {
            val fitnesses = population.map { evaluateFitness(it, xs, ys) }
            val sorted = fitnesses.withIndex().sortedBy { it.value.meanSquaredError }
            val currentBest = sorted.first().value
            if (currentBest.meanSquaredError < bestFitness.meanSquaredError) {
                bestFitness = currentBest
                bestExpression = currentBest.expression
            }

            val elites = sorted.take(eliteSize).map { it.value.expression }
            val newPopulation = ArrayList<Expression>(populationSize)
            newPopulation.addAll(elites)

            while (newPopulation.size < populationSize) {
                val parent1 = sorted[random.nextInt(sorted.size)].value.expression
                val parent2 = sorted[random.nextInt(sorted.size)].value.expression
                val child = crossover(parent1, parent2)
                val mutated = mutate(child)
                newPopulation.add(mutated)
            }
            population = newPopulation
        }

        return FitnessResult(bestFitness.meanSquaredError, bestFitness.maxAbsoluteError, bestExpression)
    }
}
