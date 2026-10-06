# OrigaMIG: MIG-Aware VM Placement with a Neighborhood-Restricted BILP and Live Migration (Kotlin)

> A clean, dependency-free **Kotlin** implementation of **OrigaMIG: MIG-Aware VM Placement with a Neighborhood-Restricted BILP and Live Migration**, focused on predictable latency, strict memory layout, and deterministic execution.

## Overview & Mechanics

The implementation focuses on the core mathematical properties of **OrigaMIG: MIG-Aware VM Placement with a Neighborhood-Restricted BILP and Live Migration**:
* **Data Organization**: Built upon `Standard Memory Primitives` to ensure predictable traversal and storage overhead.
* **Safety Invariants**: Contiguous memory layouts are favored over scattered heap allocations for optimal traversal speed.
* **Execution Guarantees**: State transitions adhere to strict ordering guarantees with explicit synchronization fences where necessary.

## Complexity Profile

* **Time Complexity**:
  * Fast Path (Best): `$O(1)$`
  * Generalized (Avg / Worst): `$O(N)$`
* **Space Footprint**: `$O(N)$` resident heap / stack overhead.

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

*Authored & verified by [@myonathanlinkedin](https://github.com/myonathanlinkedin) • Systems Engineering Portfolio*