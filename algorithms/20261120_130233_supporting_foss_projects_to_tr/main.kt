fun main() {
    // Test 1: Linear dependencies A -> B -> C (A depends on B, B depends on C)
    val pA = Project("A")
    val pB = Project("B")
    val pC = Project("C")
    val projects1 = listOf(pA, pB, pC)
    val deps1 = listOf(
        Dependency("A", "B"),
        Dependency("B", "C")
    )
    val order1 = computeMigrationOrder(projects1, deps1)
    check(order1.size == 3) { "Expected 3 projects in order, got ${order1.size}" }
    check(order1[0].name == "C") { "First project should be C, got ${order1[0].name}" }
    check(order1[1].name == "B") { "Second project should be B, got ${order1[1].name}" }
    check(order1[2].name == "A") { "Third project should be A, got ${order1[2].name}" }

    // Test 2: Independent projects (no dependencies)
    val pX = Project("X")
    val pY = Project("Y")
    val pZ = Project("Z")
    val projects2 = listOf(pX, pY, pZ)
    val deps2 = emptyList<Dependency>()
    val order2 = computeMigrationOrder(projects2, deps2)
    check(order2.size == 3) { "All independent projects should appear" }
    val names2 = order2.map { it.name }.toSet()
    check(names2 == setOf("X", "Y", "Z")) { "Unexpected set of project names: $names2" }

    // Test 3: Complex DAG
    val p1 = Project("1")
    val p2 = Project("2")
    val p3 = Project("3")
    val p4 = Project("4")
    val p5 = Project("5")
    val projects3 = listOf(p1, p2, p3, p4, p5)
    val deps3 = listOf(
        Dependency("2", "1"),
        Dependency("3", "1"),
        Dependency("4", "2"),
        Dependency("4", "3"),
        Dependency("5", "4")
    )
    val order3 = computeMigrationOrder(projects3, deps3)
    check(order3.size == 5) { "Complex DAG should yield 5 ordered projects" }
    val position = order3.mapIndexed { idx, proj -> proj.name to idx }.toMap()
    for (dep in deps3) {
        val before = position[dep.to]!!
        val after = position[dep.from]!!
        check(before < after) {
            "Dependency violation: ${dep.from} depends on ${dep.to} but appears earlier"
        }
    }

    // Test 4: Cycle detection (U -> V -> W -> U)
    val pU = Project("U")
    val pV = Project("V")
    val pW = Project("W")
    val projects4 = listOf(pU, pV, pW)
    val deps4 = listOf(
        Dependency("U", "V"),
        Dependency("V", "W"),
        Dependency("W", "U")
    )
    val order4 = computeMigrationOrder(projects4, deps4)
    check(order4.isEmpty()) { "Cycle should result in empty migration order" }

    println("All tests passed.")
}
