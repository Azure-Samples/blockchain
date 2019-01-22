package net.corda.workbench.transactionBuilder

import com.natpryce.hamkrest.equalTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith
import com.natpryce.hamkrest.assertion.*
import com.natpryce.hamkrest.throws

@RunWith(JUnitPlatform::class)
object CordaClassLoaderSpec : Spek({


    describe("Finding corda classes") {


        it("should load a class") {

            val loader = CordaClassLoader()
            val clazz = loader.lookupClass<Any>("AccountState")

            assert.that(clazz.name, equalTo("net.corda.workbench.transactionBuilder.AccountState"))

        }

        it("should not throw exception for a missing class") {

            val loader = CordaClassLoader()
            assert.that({ loader.lookupClass<Any>("MissingClass") }, throws<RuntimeException>())
        }

    }
})