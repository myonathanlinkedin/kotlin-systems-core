package hash

import kotlin.math.max

private const val EMPTY: Byte = 0
private const val OCCUPIED: Byte = 1
private const val DELETED: Byte = -1

/**
 * A production‑grade open addressing hash map using Robin Hood hashing.
 * Keys and values must be non‑null.
 */
class RobinHoodHashMap<K : Any, V : Any>(initialCapacity: Int = 16) {
    private var keys: Array<Any?> = arrayOfNulls(initialCapacity)
    private var values: Array<Any?> = arrayOfNulls(initialCapacity)
    private var control: ByteArray = ByteArray(initialCapacity) { EMPTY }
    private var mask: Int = initialCapacity - 1
    private var _size: Int = 0

    fun size(): Int = _size

    private fun hash(key: K): Int = key.hashCode()

    private fun idealIndex(hash: Int): Int = hash and mask

    private fun probeDistance(current: Int, ideal: Int): Int =
        (current - ideal + keys.size) and mask

    private fun resize(newCap: Int) {
        val oldKeys = keys
        val oldValues = values
        val oldControl = control
        keys = arrayOfNulls(newCap)
        values = arrayOfNulls(newCap)
        control = ByteArray(newCap) { EMPTY }
        mask = newCap - 1
        _size = 0
        for (i in oldKeys.indices) {
            if (oldControl[i] == OCCUPIED) {
                @Suppress("UNCHECKED_CAST")
                put(oldKeys[i] as K, oldValues[i] as V)
            }
        }
    }

    fun put(key: K, value: V): V? {
        if (_size * 2 >= keys.size) resize(keys.size * 2)
        var curKey: K = key
        var curVal: V = value
        var curHash = hash(curKey)
        var index = idealIndex(curHash)
        var dist = 0
        while (true) {
            val ctrl = control[index]
            if (ctrl == EMPTY) {
                keys[index] = curKey
                values[index] = curVal
                control[index] = OCCUPIED
                _size++
                return null
            }
            if (ctrl == OCCUPIED) {
                @Suppress("UNCHECKED_CAST")
                val existingKey = keys[index] as K
                if (existingKey == curKey) {
                    @Suppress("UNCHECKED_CAST")
                    val old = values[index] as V
                    values[index] = curVal
                    return old
                }
                val existingHash = hash(existingKey)
                val existingDist = probeDistance(index, idealIndex(existingHash))
                if (dist > existingDist) {
                    // Robin Hood swap
                    val tmpKey = keys[index] as K
                    val tmpVal = values[index] as V
                    keys[index] = curKey
                    values[index] = curVal
                    curKey = tmpKey
                    curVal = tmpVal
                    curHash = existingHash
                    dist = existingDist
                }
            }
            index = (index + 1) and mask
            dist++
        }
    }

    fun get(key: K): V? {
        var hash = hash(key)
        var index = idealIndex(hash)
        var dist = 0
        while (true) {
            val ctrl = control[index]
            if (ctrl == EMPTY) return null
            if (ctrl == OCCUPIED) {
                @Suppress("UNCHECKED_CAST")
                val existingKey = keys[index] as K
                if (existingKey == key) {
                    @Suppress("UNCHECKED_CAST")
                    return values[index] as V
                }
                val existingHash = hash(existingKey)
                val existingDist = probeDistance(index, idealIndex(existingHash))
                if (dist > existingDist) return null
            }
            index = (index + 1) and mask
            dist++
        }
    }

    fun remove(key: K): V? {
        var hash = hash(key)
        var index = idealIndex(hash)
        var dist = 0
        while (true) {
            val ctrl = control[index]
            if (ctrl == EMPTY) return null
            if (ctrl == OCCUPIED) {
                @Suppress("UNCHECKED_CAST")
                val existingKey = keys[index] as K
                if (existingKey == key) {
                    @Suppress("UNCHECKED_CAST")
                    val old = values[index] as V
                    keys[index] = null
                    values[index] = null
                    control[index] = DELETED
                    _size--
                    // Optional: backward shift deletion could be added for better clustering
                    return old
                }
                val existingHash = hash(existingKey)
                val existingDist = probeDistance(index, idealIndex(existingHash))
                if (dist > existingDist) return null
            }
            index = (index + 1) and mask
            dist++
        }
    }

    fun containsKey(key: K): Boolean = get(key) != null

    fun clear() {
        keys = arrayOfNulls(keys.size)
        values = arrayOfNulls(values.size)
        control = ByteArray(control.size) { EMPTY }
        _size = 0
    }

    fun keys(): List<K> {
        val list = ArrayList<K>(_size)
        for (i in keys.indices) {
            if (control[i] == OCCUPIED) {
                @Suppress("UNCHECKED_CAST")
                list.add(keys[i] as K)
            }
        }
        return list
    }

    fun values(): List<V> {
        val list = ArrayList<V>(_size)
        for (i in values.indices) {
            if (control[i] == OCCUPIED) {
                @Suppress("UNCHECKED_CAST")
                list.add(values[i] as V)
            }
        }
        return list
    }
}
