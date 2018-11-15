package net.corda.workbench.transactionBuilder.reflections

import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import net.corda.workbench.transactionBuilder.CordaAppConfig
import net.corda.workbench.transactionBuilder.CordaAppLoader


import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.xit
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith


@RunWith(JUnitPlatform::class)
object CordaAppLoaderSpec : Spek({

    describe("It should dynamically scan and load Corda Apps") {

        it("should scan loaded cordapp jars for configs") {
            val loader = CordaAppLoader()

            loader.scan()
            assert.that(loader.allApps().size, equalTo(1))
        }

        xit("should find config by slug or id") {
            val loader = CordaAppLoader().scan()

            assert.that(loader.findApp("19D3B4FA-FBB1-4FB3-9435-A9B32D9C4486")!!.name, equalTo("Refrigerated Transportation"))
            assert.that(loader.findApp("refrigeration")!!.name, equalTo("Refrigerated Transportation"))
            assert.that(loader.findApp("unknown"), equalTo(null as CordaAppConfig?))
        }

    }
})