# LSM-Tree MemTable and SSTable Flush Compaction Engine in Kotlin

Self-contained **LSM-Tree MemTable and SSTable Flush Compaction Engine** algorithmic primitive written in idiomatic **Kotlin**. Built from scratch using standard library constructs with zero external dependencies.

## Implementation Details

* **Category**: `Balanced Hierarchical Indexing`
* **Data Structure Foundation**: `Node Pointers & Self-Balancing Trees`
* **Allocation Pattern**: Memory allocations are kept minimal to maintain clear data locality and predictable memory bounds.
* **Invariant Integrity**: State consistency is verified after mutations through assertion test coverage.

## Performance Characteristics

* **Time**: `O(log N)` average, with `O(1)` best-case response under ideal conditions.
* **Space**: `O(N)` memory usage.

## Test Harness

To compile and execute the test assertions for this module:

```bash
kotlin main.kt
```

---

*Source code released under the MIT License • [@myonathanlinkedin](https://github.com/myonathanlinkedin)*