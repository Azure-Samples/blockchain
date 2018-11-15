package net.corda.reflections.reflections



import com.natpryce.hamkrest.assertion.assert
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import net.corda.core.contracts.UniqueIdentifier
import net.corda.core.identity.CordaX500Name
import net.corda.core.identity.Party
import net.corda.reflections.keyPair
import net.corda.reflections.resolvers.InMemoryPartyResolver
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith


@RunWith(JUnitPlatform::class)
object ResolverSpec : Spek({

    describe("It should resolve Party objects via reflections") {

        context("Party resolver") {

            val alice = Party(CordaX500Name(organisation = "Alice", locality = "TestLand", country = "US"), keyPair().public)
            val bob = Party(CordaX500Name(organisation = "Bob", locality = "TestLand", country = "GB"), keyPair().public)

            val reflectionsKt = ReflectionsKt(InMemoryPartyResolver(listOf(alice, bob)))

            it("should resolve Alice to matching party ") {
                val params = mapOf("party" to "Alice")
                val result = reflectionsKt.invokeConstructor(PartyParam::class,params)

                assert.that(result, equalTo(PartyParam(alice)))
            }

            it("should not resolve Charlie") {
                val params = mapOf("party" to "Charlie")
                assert.that({reflectionsKt.invokeConstructor(PartyParam::class,params)}, throws<RuntimeException>())
            }
        }

        context ("UniqueIdentifier") {
            it("should resolve 'UniqueIdentifier' class from String") {
                val reflectionsKt = ReflectionsKt()
                fun doit(id: String) = reflectionsKt.invokeConstructor(UniqueIdentifierParam::class, mapOf("id" to id))

                val id1 = UniqueIdentifier()
                val id2 = UniqueIdentifier("123_XYZ")
                assert.that(doit(id1.toString()), equalTo(UniqueIdentifierParam(id1)))
                assert.that(doit(id2.toString()), equalTo(UniqueIdentifierParam(id2)))
            }

            it("should not 'UniqueIdentifier' if bad String") {
                val reflectionsKt = ReflectionsKt()
                fun doit(id: String) = reflectionsKt.invokeConstructor(UniqueIdentifierParam::class, mapOf("id" to id))

                assert.that({doit("not an id" )}, throws<RuntimeException>())
            }
        }
    }
})