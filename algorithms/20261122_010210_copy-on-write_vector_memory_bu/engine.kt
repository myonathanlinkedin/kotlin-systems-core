package main

class CowVector<T> private constructor(val buffer: Buffer<T>) {

    companion object {
        fun <T> empty(): CowVector<T> {
            return CowVector(Buffer(mutableListOf<T>(), 1))
        }
    }

    fun size(): Int = buffer.data.size

    operator fun get(index: Int): T {
        if (index < 0 || index >= size()) throw IndexOutOfBoundsException()
        return buffer.data[index]
    }

    fun copy(): CowVector<T> {
        buffer.refCount++
        return CowVector(buffer)
    }

    operator fun set(index: Int, value: T): CowVector<T> {
        if (index < 0 || index >= size()) throw IndexOutOfBoundsException()
        val newBuffer = ensureUnique()
        newBuffer.data[index] = value
        return CowVector(newBuffer)
    }

    fun push(value: T): CowVector<T> {
        val newBuffer = ensureUnique()
        newBuffer.data.add(value)
        return CowVector(newBuffer)
    }

    fun pop(): Pair<T, CowVector<T>> {
        if (size() == 0) throw NoSuchElementException()
        val newBuffer = ensureUnique()
        val removed = newBuffer.data.removeAt(newBuffer.data.lastIndex)
        return Pair(removed, CowVector(newBuffer))
    }

    private fun ensureUnique(): Buffer<T> {
        if (buffer.refCount == 1) {
            return buffer
        }
        // Detach: decrement old refCount and create a fresh buffer
        buffer.refCount--
        return Buffer(buffer.data.toMutableList(), 1)
    }

    override fun toString(): String = "CowVector(${buffer.data})"
}