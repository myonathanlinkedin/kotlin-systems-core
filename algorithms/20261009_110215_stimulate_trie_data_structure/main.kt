fun main() {
    val trie = Trie()

    // Basic insertion and search
    trie.insert("apple")
    check(trie.contains("apple")) { "Failed: 'apple' should be found after insertion." }
    check(!trie.contains("app")) { "Failed: 'app' should not be found as a complete word." }
    check(trie.startsWith("app")) { "Failed: prefix 'app' should be recognized." }

    // Insert a word that is a prefix of an existing word
    trie.insert("app")
    check(trie.contains("app")) { "Failed: 'app' should be found after insertion." }
    check(trie.startsWith("ap")) { "Failed: prefix 'ap' should be recognized." }

    // Edge case: empty string handling
    trie.insert("")
    check(trie.contains("")) { "Failed: empty string should be recognized after insertion." }
    check(trie.startsWith("")) { "Failed: empty prefix should always be true." }

    // Deletion tests
    check(trie.delete("apple")) { "Failed: 'apple' should be deletable." }
    check(!trie.contains("apple")) { "Failed: 'apple' should no longer be present after deletion." }
    check(trie.contains("app")) { "Failed: 'app' should remain after deleting 'apple'." }

    // Deleting a non‑existent word
    check(!trie.delete("banana")) { "Failed: deleting non‑existent word should return false." }

    // Deleting a word that is a prefix of another word
    trie.insert("banana")
    trie.insert("band")
    check(trie.contains("banana")) { "Failed: 'banana' should be present." }
    check(trie.contains("band")) { "Failed: 'band' should be present." }
    check(trie.delete("banana")) { "Failed: 'banana' should be deletable." }
    check(!trie.contains("banana")) { "Failed: 'banana' should be gone after deletion." }
    check(trie.contains("band")) { "Failed: 'band' should still exist after deleting 'banana'." }

    // Deleting the empty string
    check(trie.delete("")) { "Failed: empty string should be deletable when present." }
    check(!trie.contains("")) { "Failed: empty string should no longer be present after deletion." }

    // Stress test: insert many words and verify
    val words = listOf("cat", "cater", "catering", "dog", "dove", "dot")
    for (w in words) {
        trie.insert(w)
    }
    for (w in words) {
        check(trie.contains(w)) { "Failed: word '$w' should be present after bulk insertion." }
    }
    check(trie.startsWith("cat")) { "Failed: prefix 'cat' should be recognized." }
    check(trie.startsWith("do")) { "Failed: prefix 'do' should be recognized." }
    check(!trie.startsWith("z")) { "Failed: prefix 'z' should not be recognized." }

    // Clean up: delete all bulk words
    for (w in words) {
        check(trie.delete(w)) { "Failed: word '$w' should be deletable." }
        check(!trie.contains(w)) { "Failed: word '$w' should be absent after deletion." }
    }

    // Final sanity check: trie should only contain 'app' and 'band' from earlier tests
    check(trie.contains("app")) { "Failed: 'app' should still be present at end." }
    check(trie.contains("band")) { "Failed: 'band' should still be present at end." }
    check(!trie.contains("dog")) { "Failed: 'dog' should have been removed." }

    println("All Trie unit tests passed successfully.")
}
