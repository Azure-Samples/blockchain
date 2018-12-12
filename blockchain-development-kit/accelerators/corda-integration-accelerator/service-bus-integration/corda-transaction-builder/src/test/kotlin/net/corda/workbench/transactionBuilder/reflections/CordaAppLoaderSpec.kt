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
import java.io.File
import java.net.URL
import java.net.URLClassLoader


@RunWith(JUnitPlatform::class)
object CordaAppLoaderSpec : Spek({

    describe("It should dynamically scan and load Corda Apps") {

        val jarFile = File("../../cordapps/chat/lib/chat.jar")
        val cordaURL = URL("file://" + jarFile.absolutePath)
        val classLoader = URLClassLoader(arrayOf(cordaURL))

        it("should scan loaded cordapp jars for configs") {
            val loader = CordaAppLoader()

            loader.scan(classLoader)
            assert.that(loader.allApps().size, equalTo(1))
        }

        it("should find config by slug or id") {
            val loader = CordaAppLoader().scan(classLoader)
//19D3B4FA-FBB1-4FB3-9435-A9B32D9C4486
            assert.that(loader.findApp("312651FE-90EE-4F10-B6BF-CD9FF62AE5C8")!!.name, equalTo("Chat App"))
            assert.that(loader.findApp("chat")!!.name, equalTo("Chat App"))
            assert.that(loader.findApp("unknown"), equalTo(null as CordaAppConfig?))
        }

    }
})