package main

data class Buffer<T>(val data: MutableList<T>, var refCount: Int)