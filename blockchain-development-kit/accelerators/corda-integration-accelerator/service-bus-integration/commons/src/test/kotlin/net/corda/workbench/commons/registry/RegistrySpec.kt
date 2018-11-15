package net.corda.workbench.commons.registry

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.sameInstance
import com.natpryce.hamkrest.throws

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

@RunWith(JUnitPlatform::class)
object RegistrySpec : Spek({

    describe("A simple registry of objects") {

        it("should store and load a class") {
            val a = ClassA()
            val b = ClassB()
            val registry = Registry(a, b)

            assert.that(registry.retrieve(ClassA::class.java), sameInstance(a))
            assert.that(registry.retrieve(ClassB::class.java), sameInstance(b))
            assert.that({ registry.retrieve(ClassC::class.java) }, throws<RuntimeException>())
        }

        it("should store and load an interface") {
            val one = InterfaceAImplOne()
            val registry = Registry(one)

            assert.that(registry.retrieve(InterfaceAImplOne::class.java), sameInstance(one))
            assert.that(registry.retrieve(InterfaceA::class.java), sameInstance(one as InterfaceA))
        }

        it("should flush current store") {
            val registry = Registry.INSTANCE
            val a = ClassA()
            registry.store(a)

            assert.that(registry.retrieve(ClassA::class.java), sameInstance(a))
            registry.flush()
            assert.that({ registry.retrieve(ClassA::class.java) }, throws<RuntimeException>())
        }

        it("should override registry") {
            val registry1 = Registry()
            val one = DataClass("one")
            registry1.store(one)
            val two = DataClass("two")
            val registry2 = registry1.overide(two)


            assert.that(registry1.retrieve(DataClass::class.java), sameInstance(one))
            assert.that(registry2.retrieve(DataClass::class.java), sameInstance(two))
        }
    }
})

class ClassA

class ClassB

class ClassC

interface InterfaceA

class InterfaceAImplOne : InterfaceA

@Suppress("unused")
class InterfaceAImplTwo : InterfaceA

data class DataClass(val name: String)