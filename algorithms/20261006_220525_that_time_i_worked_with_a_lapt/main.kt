fun testMostStolenLaptop() {
    val tracker = LaptopTheftTracker()
    check(tracker.getMostStolenLaptop() == null)

    tracker.recordEvent(5, "DellXPS")
    tracker.recordEvent(10, "MacBookPro")
    tracker.recordEvent(15, "DellXPS")
    tracker.recordEvent(20, "ThinkPad")
    tracker.recordEvent(25, "DellXPS")

    val most = tracker.getMostStolenLaptop()
    check(most == "DellXPS")
}

fun testLongestStreak() {
    val tracker = LaptopTheftTracker()
    check(tracker.getLongestStreak() == null)

    // Non‑consecutive timestamps
    tracker.recordEvent(1, "A")
    tracker.recordEvent(3, "B")
    tracker.recordEvent(4, "C")
    tracker.recordEvent(6, "D")
    tracker.recordEvent(7, "E")
    tracker.recordEvent(8, "F")

    val streak1 = tracker.getLongestStreak()
    // longest consecutive run is 6,7,8 => length 3
    check(streak1 == Pair(6, 8))

    // Add a longer streak
    tracker.recordEvent(10, "G")
    tracker.recordEvent(11, "H")
    tracker.recordEvent(12, "I")
    tracker.recordEvent(13, "J")
    tracker.recordEvent(14, "K")

    val streak2 = tracker.getLongestStreak()
    // longest run now 10‑14
    check(streak2 == Pair(10, 14))
}

fun testEdgeCases() {
    val tracker = LaptopTheftTracker()
    // Single event
    tracker.recordEvent(0, "Solo")
    check(tracker.getMostStolenLaptop() == "Solo")
    check(tracker.getLongestStreak() == Pair(0, 0))

    // Multiple laptops with same count
    tracker.recordEvent(1, "Alpha")
    tracker.recordEvent(2, "Beta")
    // Both Alpha and Beta have count 1, but Alpha was first inserted
    val most = tracker.getMostStolenLaptop()
    check(most == "Solo" || most == "Alpha" || most == "Beta")
}

fun main() {
    testMostStolenLaptop()
    testLongestStreak()
    testEdgeCases()
    println("All tests passed")
}
