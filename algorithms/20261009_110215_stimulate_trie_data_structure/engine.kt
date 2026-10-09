class Trie {
    private val root = TrieNode()

    /** Inserts the given word into the trie. */
    fun insert(word: String) {
        var current = root
        for (ch in word) {
            current = current.children.getOrPut(ch) { TrieNode() }
        }
        current.isEndOfWord = true
    }

    /** Returns true if the exact word exists in the trie. */
    fun contains(word: String): Boolean {
        val node = navigateToNode(word) ?: return false
        return node.isEndOfWord
    }

    /** Returns true if there is any word in the trie that starts with the given prefix. */
    fun startsWith(prefix: String): Boolean {
        return navigateToNode(prefix) != null
    }

    /** Deletes the word from the trie.
     *  Returns true if the word was present and removed, false otherwise.
     */
    fun delete(word: String): Boolean {
        if (word.isEmpty()) {
            // Special handling for empty string
            if (root.isEndOfWord) {
                root.isEndOfWord = false
                return true
            }
            return false
        }
        return deleteRecursive(root, word, 0)
    }

    /** Helper: navigate to the node representing the last character of the given key.
     *  Returns null if any character path is missing.
     */
    private fun navigateToNode(key: String): TrieNode? {
        var current = root
        for (ch in key) {
            val next = current.children[ch] ?: return null
            current = next
        }
        return current
    }

    /** Recursive deletion helper.
     *  Returns true if the caller should delete the current node because it became unnecessary.
     */
    private fun deleteRecursive(node: TrieNode, word: String, index: Int): Boolean {
        if (index == word.length) {
            // Reached the node representing the word
            if (!node.isEndOfWord) {
                return false // word not present
            }
            node.isEndOfWord = false
            // If node has no children, inform parent to delete this node
            return node.children.isEmpty()
        }

        val ch = word[index]
        val child = node.children[ch] ?: return false // word not present

        val shouldDeleteChild = deleteRecursive(child, word, index + 1)

        if (shouldDeleteChild) {
            node.children.remove(ch)
            // Delete this node if it's not end of another word and has no other children
            return !node.isEndOfWord && node.children.isEmpty()
        }
        return false
    }
}
