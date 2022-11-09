package com.ivanornes.memoryleaktest

import junit.framework.TestCase
import org.junit.Test
import java.lang.ref.WeakReference

class Person(var friend: Person? = null)

class MemoryLeakTrackingTest: MemoryLeakTrackingTestCase() {

    @Test
    fun testRetainCycles() {
        val a = Person()
        val b = Person()

        a.friend = b
        b.friend = a

        trackForMemoryLeaks(a)
        trackForMemoryLeaks(b)
    }
}

abstract class MemoryLeakTrackingTestCase: TestCase() {
    private var teardownBlocks: MutableList<()->Unit> = mutableListOf()

    override fun tearDown() {
        super.tearDown()
        System.gc()
        teardownBlocks.forEach { it.invoke() }
    }

    fun trackForMemoryLeaks(instance: Any) {
        val weakRef = WeakReference(instance)
        teardownBlocks.add {
            assertNull(weakRef.get())
        }
    }
}
