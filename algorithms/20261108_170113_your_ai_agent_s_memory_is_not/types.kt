package main

data class DoublyNode<K, V>(
    val key: K,
    var value: V,
    var prev: DoublyNode<K, V>? = null,
    var next: DoublyNode<K, V>? = null
)