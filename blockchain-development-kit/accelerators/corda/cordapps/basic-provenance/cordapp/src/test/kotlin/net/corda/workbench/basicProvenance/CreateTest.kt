/*
package com.example.contract

import com.example.state.ItemState
import net.corda.core.identity.CordaX500Name
import net.corda.testing.core.TestIdentity
import net.corda.testing.node.MockServices
import net.corda.testing.node.ledger
import org.junit.Test

class CreateTest {
    private val ledgerServices = MockServices()
    private val issuer = TestIdentity(CordaX500Name("Issuer", "", "GB"))
    private val A = TestIdentity(CordaX500Name("Alice", "", "GB"))
    private val B = TestIdentity(CordaX500Name("Bob", "", "GB"))
    private val C = TestIdentity(CordaX500Name("Carl", "", "GB"))
    private val D = TestIdentity(CordaX500Name("Demi", "", "GB"))

    val defaultIssuer = issuer.ref(Byte.MIN_VALUE)
    private fun partyKeys(vararg identities: TestIdentity) = identities.map { it.party.owningKey }


    private val newValidItem = ItemState(
            value = "banane",
            creator = A.party

    )

    @Test
    fun `Create a new Item Test`() {
        ledgerServices.ledger {
            // Valid start new campaign transaction.
            transaction {
                output(ItemContract.ITEM_ID, newValidItem)
                command(partyKeys(A), ItemContract.Create())
                this.verifies()
            }


        }
    }
}*/
