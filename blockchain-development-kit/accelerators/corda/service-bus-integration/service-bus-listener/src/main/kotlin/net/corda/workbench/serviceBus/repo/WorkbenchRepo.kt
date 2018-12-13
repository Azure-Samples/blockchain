package net.corda.workbench.serviceBus.repo

interface WorkbenchRepo {
    /**
     * Returns the contract id associated with the 'contractLedgerIdentifier'
     * (the unique identifier related to running contract, typically
     * the linearId)
     *
     */
    fun contractId(contractLedgerIdentifier: String): Int
}

class InMemoryWorkbenchRepo : WorkbenchRepo {
    val contractLedgerIdentifiers = ArrayList<String>()

    override fun contractId(contractLedgerIdentifier: String): Int {

        for (i in 0 until contractLedgerIdentifiers.size) {
            if (contractLedgerIdentifiers[i] == contractLedgerIdentifier) {
                return i + 1;
            }
        }
        synchronized(this) {
            contractLedgerIdentifiers.add(contractLedgerIdentifier)
            return contractLedgerIdentifiers.size

        }
    }
}