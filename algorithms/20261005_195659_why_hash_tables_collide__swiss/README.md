# Why Hash Tables Collide: Swiss Tables, Robin Hood Hashing, and CPU Cache Lines

Core **Kotlin** implementation for **Why Hash Tables Collide: Swiss Tables, Robin Hood Hashing, and CPU Cache Lines**, structured for computational clarity, explicit data structures, and deterministic unit test coverage.

### Core Highlights
* **Language & Standard**: Modern `Kotlin` standard library conventions.
* **Architecture Pattern**: Designed for `Algorithmic Engineering` using `Standard Memory Primitives`.
* **Runtime Overhead**: Zero external heap dependencies; designed as a pure in-memory algorithmic component.
* **Concurrency & Safety**: State consistency is verified after mutations through assertion test coverage.

---

### Complexity Analysis

| Dimension | Bound |
| :--- | :--- |
| **Time (Best Case)** | `O(1)` |
| **Time (Worst Case)** | `O(N log N)` |
| **Auxiliary Space** | `O(N)` |

---

### Test Suite Execution

Self-contained verification drivers are embedded directly in `main.kt` to validate happy paths, boundary inputs, and invariant preservation.

```bash
kotlin main.kt
```

---

*Reference implementation verified by [@myonathanlinkedin](https://github.com/myonathanlinkedin)*
