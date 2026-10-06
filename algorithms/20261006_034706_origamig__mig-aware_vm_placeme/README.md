# OrigaMIG: MIG-Aware VM Placement with a Neighborhood-Restricted BILP and Live Migration (Kotlin)

> A clean, dependency-free **Kotlin** reference implementation of **OrigaMIG: MIG-Aware VM Placement with a Neighborhood-Restricted BILP and Live Migration**, focused on core algorithmic mechanics, clear memory layout, and test verification.

## Overview & Mechanics

The implementation focuses on the core mathematical properties of **OrigaMIG: MIG-Aware VM Placement with a Neighborhood-Restricted BILP and Live Migration**:
* **Data Organization**: Built upon `Standard Memory Primitives` to ensure predictable traversal and storage overhead.
* **Safety Invariants**: Contiguous memory layouts and standard collections are favored for straightforward iteration and access.
* **Execution Guarantees**: State transitions follow clear ordering guarantees with explicit validation at each phase.

## Complexity Profile

* **Time Complexity**:
  * Fast Path (Best): `O(1)`
  * Generalized (Avg / Worst): `O(N)`
* **Space Footprint**: `O(N)` resident heap / stack overhead.

## Verification & Test Scenarios

The test suite in `types.kt` validates:
* Standard operational paths against expected outcomes.
* Extreme values and edge inputs to ensure robust failure handling.
* State stability across sequential and repeated operations.

```bash
# Execute local verification runner
kotlin types.kt
```

---

*Reference implementation verified by [@myonathanlinkedin](https://github.com/myonathanlinkedin)*
