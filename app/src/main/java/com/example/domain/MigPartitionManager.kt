package com.example.domain

data class MigInstance(
    val gpuId: Int,
    val instanceId: String,
    val profile: String,       // e.g. "3g.40gb", "1g.10gb"
    val computeSlices: Int,
    val memorySlices: Int,
    val vramGb: Int,
    val utilizationPercent: Float
)

class MigPartitionManager {
    private val instances = mutableListOf<MigInstance>()

    fun add(instance: MigInstance) = instances.add(instance)
    fun remove(instanceId: String) = instances.removeAll { it.instanceId == instanceId }

    fun getForGpu(gpuId: Int): List<MigInstance> = instances.filter { it.gpuId == gpuId }

    fun getTotalVramAllocatedGb(gpuId: Int): Int = getForGpu(gpuId).sumOf { it.vramGb }

    fun validateProfile(profile: String): Boolean =
        profile in setOf("7g.80gb", "4g.40gb", "3g.40gb", "2g.20gb", "1g.10gb", "1g.5gb")
}
