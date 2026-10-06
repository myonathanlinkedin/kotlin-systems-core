package main

import kotlin.math.max
import kotlin.math.min

/**
 * Represents a Virtual Machine (VM) with resource requirements and migration costs.
 */
data class VirtualMachine(
    val id: Int,
    val cpu: Double,
    val ram: Double,
    val disk: Double,
    val migrationCost: Double
) {
    companion object {
        fun create(id: Int, cpu: Double, ram: Double, disk: Double, migrationCost: Double): VirtualMachine {
            return VirtualMachine(id, cpu, ram, disk, migrationCost)
        }
    }
}

/**
 * Represents a Physical Host (Server) with capacity constraints.
 */
data class Host(
    val id: Int,
    val cpuCapacity: Double,
    val ramCapacity: Double,
    val diskCapacity: Double
) {
    companion object {
        fun create(id: Int, cpu: Double, ram: Double, disk: Double): Host {
            return Host(id, cpu, ram, disk)
        }
    }
}

/**
 * Represents the current placement state of a VM on a specific host.
 */
data class Placement(
    val vmId: Int,
    val hostId: Int
)

/**
 * Represents a migration action: moving a VM from one host to another.
 */
data class Migration(
    val vmId: Int,
    val fromHostId: Int,
    val toHostId: Int
)

/**
 * Represents the result of the optimization engine.
 */
data class OptimizationResult(
    val finalPlacements: List<Placement>,
    val migrations: List<Migration>,
    val totalMigrationCost: Double,
    val loadBalanceScore: Double,
    val isFeasible: Boolean
)

/**
 * Interface for the optimization engine.
 */
interface PlacementEngine {
    fun optimize(
        hosts: List<Host>,
        vms: List<VirtualMachine>,
        initialPlacements: List<Placement>
    ): OptimizationResult
}