DevOps for Blockchain Apps
==========================

Blockchain has emerged from the shadow of its cryptocurrency origins to be seen
as a transformative data technology that can power the next generation of
software for multi-party Enterprise and consumer scenarios. With the trust and
transparency that blockchain can deliver, this shared data technology is seen as
a disruptor that [can radically transform assumptions, costs, and approaches
about how business is
done](https://hbr.org/sponsored/2017/10/how-blockchain-will-accelerate-business-performance-and-power-the-smart-economy).

![](media/3f30193bb0a0bc1d5a27da8e17ea9cbc.png)

Microsoft Azure has had blockchain offerings since 2015 and the technology is
now moving from initial PoCs and pilots to mainstream production. Customers
around the world are building blockchain solutions on Azure that are innovating
the way they do business – from Interswitch’s work in payments in Africa, to
Maersk transforming maritime insurance, to Buhler using it to help ensure the
food supply chain is safe.

With the mainstreaming of blockchain technology in Enterprise software
development, organizations are asking for guidance on how to deliver DevOps for
smart contracts and blockchain projects.

Common questions include –

-   My business logic and data schema for that logic are reflected in smart
    contracts. Smart contracts are written in languages I’m less familiar with
    like Solidity for Ethereum, Kotlin for Corda, or Go for Hyperledger Fabric.
    What tools can I use to develop those in?

-   How do I do I do things like unit testing and debugging on smart contracts?

-   Many blockchain scenarios reflect multi-party transactions and business
    workflows. These workflows include signed transactions from multiple parties
    happening in specific sequences. How do I think about data for test
    environments in that context?

-   Smart contracts are deployed to the blockchain, which is immutable. How do I
    need to think about things such as infrastructure as code, local dev/test,
    upgrading contracts, etc.?

-   Blockchain is a data technology shared across multiple organizations in a
    consortium, what are the impacts on source code control, build and release
    pipelines in a global, multi-party environment?

While there are some nuances to the approach, the good news is that just like
other types of solution development, this model can readily be addressed in a
DevOps model. Today, we’re announcing the release of the whitepaper, “[DevOps
for Blockchain Smart Contracts](https://github.com/Azure-Samples/blockchain/blob/master/blockchain-development-kit/devops/DevOps%20for%20Blockchain%20Smart%20Contracts.pdf).”

![](media/3a3cb957a3837b963f35b032276a1a79.png)

Complementing the whitepaper is an [implementation
guide](https://github.com/Azure-Samples/blockchain/blob/master/blockchain-development-kit/devops/DevOps%20for%20Blockchain%20Smart%20Contracts%20-%20Implementation%20Guide.pdf), available through the Azure Blockchain
Development Kit, that shows how to implement CI/CD for smart contracts and
infrastructure as code using Visual Studio Code, GitHub, Azure DevOps and OSS
from [Truffle](https://www.truffleframework.com/).
