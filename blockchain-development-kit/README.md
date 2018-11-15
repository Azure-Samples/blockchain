**Introducing the Azure Blockchain Development Kit**

“Developers! Developers! Developers!” That phrase is synonymous with Microsoft’s
history of democratizing complex technologies and empowering anyone with an idea
to build software.

Over four decades, we’ve lowered barriers to development with developer tooling,
enterprise integration, DevOps, PaaS, and SaaS. Today, serverless offerings from
Functions and Logic Apps to Azure DevOps and IoT Central remove friction for
development in the cloud.

This morning, we’re excited to announce the initial release of the [Azure
Blockchain Development Kit](http://aka.ms/bcdevkit) which is built on
Microsoft’s serverless technologies and seamlessly integrates blockchain with
the best of Microsoft and third-party SaaS.

This kit extends the capabilities of our blockchain developer templates and
[Azure Blockchain
Workbench](https://azure.microsoft.com/en-us/features/blockchain-workbench/),
which incorporates Azure services for key management, off-chain identity and
data, monitoring, and messaging APIs into a reference architecture that can be
used to rapidly build blockchain-based applications.

These tools have become the first step for many organizations on their journey
to re-invent the way they do business. Apps have been built for everything from
[democratizing supply chain financing in
Nigeria](https://customers.microsoft.com/en-us/story/interswitch-banking-capital-markets-azure-blockchain-workbench)
to [securing the food
supply](https://cloudblogs.microsoft.com/industry-blog/industry/manufacturing/buhler-will-track-crops-from-farm-to-fork-using-blockchain-technology/)
in the UK, but as patterns emerged across use cases, our teams identified new
ways for Microsoft to help developers go farther, faster.

This initial release prioritizes capabilities related to three key themes:
connecting interfaces, integrating data and systems and deploying blockchain
networks.

**Connect**

To deliver end to end blockchain solutions for consortiums, developers need to
enable organizations, people, and devices to connect to the blockchain and do it
from a heterogenous set of user interfaces.

Take for example an end to end supply chain for a commodity such as cocoa.

-   **SMS and voice interfaces** enable small hold farmers in Africa to transact
    and track their goods at the first mile of the supply chain.

-   **Internet of Things (IoT)** devices deliver sensor data to track the
    conditions of the goods at different points in their journey to market –
    tracking the humidity in the containers where the beans are held to the
    temperature of the end product of ice cream that it is incorporated into.

-   **Mobile clients** enable logistics providers to accept and transfer
    responsibility for products on their journey from manufacturer to retail
    using the compute power that already exists in the pockets of its employees.
    Mobile devices also have sensors such as GPS and cameras that can add
    complementary data that can help attest to the what, where, and when of
    deliveries.

-   **Backend Systems and Data** in the form of ERP systems such as Dynamics and
    SAP are used to manage core processes for different participants. These
    systems also become clients via extension and need to interact with smart
    contracts to provide and receive attestable data on behalf of an
    organization.

-   **Bots and assistants** enable manufacturers and retailers to interact with
    the supply chain. This includes interacting with smart contracts for orders
    and provenance using natural language and using attestable data from the
    blockchain to direct actions taken on behalf of a user.

-   **Web clients** enable end consumers to query the origin of the product
    purchased at retail, typically a mix of provenance and story of their
    journey of their product from “farm to fork”

The [Azure Blockchain Development Kit](http://aka.ms/bcdevkit) includes samples
for all of these scenarios, including [inbound](http://aka.ms/bcsmsin) and
[outbound](http://aka.ms/bcsmsout) SMS, [IVR](http://aka.ms/bcivr), [IoT
Hub](http://aka.ms/bciothub) and [IoT Central](http://aka.ms/bciotcentral),
[Xamarin mobile client](http://aka.ms/bcmobile) for iOS and Android, [Dynamics
integration via Common Data Service (CDS)](http://aka.ms/bccds), bots and
assistants ([Cortana](http://aka.ms/bccortana), [Alexa](http://aka.ms/bcalexa),
[Google Assistant](http://aka.ms/bcgoogle)) and [web UX](http://aka.ms/bcweb) .

**Integrate**

Businesses are using blockchain and smart contracts to facilitate multi-party
processes. Blockchain also delivers real-time transparency of the states and
events of those contracts to appropriate participants.

End to end blockchain solutions require integration with data, software, and
media that live “off chain”. External updates and events can trigger actions on
smart contracts. Smart contract events and state changes can then trigger
actions and data updates to “off chain” systems and data. These external systems
and AI will also need the ability to query attestable data from smart contracts
to inform action.

Specifically, there are two areas of integration where guidance is most needed:

**Documents and Media:** Documents and media do not belong on chain, but
business processes often involve images, videos, audio, Office documents, CAD
files for 3D printers or other file types.

The common pattern is to generate a unique hash of the media and the metadata
that describes it. Those hashes are then placed on a public or private chain. If
authenticity of a file is ever questioned, the “off chain” files can be
re-hashed at a later time and that hash is compared to the “on chain” hash
stored on the blockchain. If the hashes match, the document is authentic, but if
so much as a pixel of an image or letter in a document is changed, the hashes
will not match and this will make obvious that tampering has occurred.

Today we’re releasing a set of Logic Apps that enable the hashing of files and
file related metadata. Also included are smart contracts for files and a file
registry to store the hashes on chain.

Logic Apps have been created to deliver this functionality for files added to
the most popular sources for documents and media, including [Azure
Storage](http://aka.ms/bcstorage), [OneDrive](http://aka.ms/bconedrive), [One
Drive for Business](http://aka.ms/bconedrive4biz),
[SharePoint](http://aka.ms/bcsharepoint) , [Box](http://aka.ms/bcbox), [Adobe
Creative Cloud](http://aka.ms/bcacc), and [FTP](http://aka.ms/bcftp).

![](media/82ff6d52ffa0b6e4549008573fab1314.png)

**Smart Contract Interaction:** Getting blockchain off the whiteboard and into
production means dealing with the realities of how counterparties interact
today. That reality is that Enterprise integration is messy.

Microsoft brings our decades of experience in this area to blockchain. Our work
with integrating Enterprise systems began almost two decades ago with the
introduction of BizTalk server, and our focus on database integration traces
back to our co-development of Open Database Connectivity (ODBC) in the 1990s.
All of our experience has been captured and made available in Azure services.
This includes 200+ connectors available in Logic Apps and Flow, and the robust
capabilities in our data platform.

![](media/6d6828a959554cb7257eb18e13396c1a.png)

The [Blockchain Application Development Kit](http://aka.ms/bcdevkit) includes
Workbench integration samples in the following areas

-   **Legacy Applications and Protocols** – [Sending](http://aka.ms/bcftpsend)
    and [receiving](http://aka.ms/bcftpreceive) files via FTP, [processing comma
    separated files](http://aka.ms/bccsv), and [email delivery of
    data](http://aka.ms/bcemail)

-   **Data** – [SQL](http://aka.ms/bcsql), [Azure
    Search](http://aka.ms/bcazuresearch), [Excel](http://aka.ms/bcexel), and
    [PowerBI](http://aka.ms/bcpowerbi)

-   **SaaS** – [SharePoint](http://aka.ms/bcsharepoint),
    [Dynamics](http://aka.ms/bcdynamics), [Outlook](http://aka.ms/bcoutlook),
    and [Gmail](http://aka.ms/bcgmail)

-   **Registries** – An [accelerator](http://aka.ms/bcregistryaccelerator) that
    takes user input generates custom linked registry and registry item smart
    contracts to accommodate any scenario.

**Logic App Connectors for Blockchain**

Today, we are also announcing that we will release a set of Logic App and Flow
Connectors to extend these samples to ledgers like
[Ethereum](https://aka.ms/bcla4eth), [Corda](https://aka.ms/bccorda), Bitcoin
and others

*"At R3, we are committed to ensuring developers can deploy CorDapps quickly,
securely and easily. The Azure Blockchain Development Kit will give our
enterprise customers tools to integrate with the applications, software, and
devices that people use every day like Outlook, Alexa, SMS,  and web UX.
Blockchain is moving out of the labs and into everyday business applications.”*
** – Mike Ward, Head of Product Management, R3**

The Ethereum blockchain connector is available today and enables users to deploy
contracts, call contract actions, read contract state and trigger other Logic
Apps based on events from the ledger.

![](media/135905a23064740cb4a90480511e0cca.png)

**Deploy**

With the mainstreaming of blockchain technology in Enterprise software
development, organizations are asking for guidance on how to deliver DevOps for
smart contracts and blockchain projects.

Common questions include:

-   My business logic and data schema for that logic are reflected in smart
    contracts. Smart contracts are written in languages I’m less familiar with
    like
    [Solidity](https://marketplace.visualstudio.com/items?itemName=JuanBlanco.solidity)
    for Ethereum,
    [Kotlin](https://marketplace.visualstudio.com/search?term=Kotlin&target=VSCode&category=All%20categories&sortBy=Relevance)
    for Corda, or
    [Go](https://marketplace.visualstudio.com/items?itemName=ms-vscode.Go) for
    Hyperledger Fabric. What tools can I use to develop those in?

-   How do I do unit testing and debugging on smart contracts?

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
DevOps model.

![](media/3a3cb957a3837b963f35b032276a1a79.png)

Today, we’re announcing the release of the whitepaper, “[DevOps for Blockchain
Smart Contracts](http://aka.ms/bcdevopspaper).”

*We're excited to work with Microsoft to create the canonical DevOps experience
for blockchain engineers. Our paper, "DevOps for Blockchain Smart Contracts",
goes into rigorous detail and provides examples on how to develop blockchain
applications with an eye toward CI/CD in consortium environments.” - Tim
Coulter, Founder of Truffle*

Complementing the whitepaper is an [implementation
guide](http://aka.ms/bcdevops), available through the Azure Blockchain
Development Kit, that shows how to implement CI/CD for smart contracts and
infrastructure as code using Visual Studio Code, GitHub, Azure DevOps and OSS
from [Truffle](https://www.truffleframework.com/).

**The best platform for blockchain application development**

The [Azure Blockchain Development Kit](http://aka.ms/bcdevkit) is the next step
in our journey to make developing end to end blockchain applications accessible,
fast, and affordable to anyone with an idea. It is built atop our investments in
blockchain and connects to the compute, data, messaging, and integration
services available in both Azure and the broader Microsoft Cloud to provide a
robust palette for a developer to realize their vision.

Logic Apps and Flow deliver a graphical design environment with over 200
connectors dramatically simplifying the development of end to end blockchain
solutions, and Azure Functions enable the rapid integration of custom code.

A serverless approach also reduces costs and management overhead. With no VMs to
manage, built in scalability, and an approachable pricing model (currently Logic
App’s cost is \$0.000125 per standard connector action) the Azure Blockchain
Development Kit is within reach of every developer – from enthusiasts to ISVs to
Enterprises.

Solutions are written using online visual workflow designers and [Visual Studio
Code](https://code.visualstudio.com/Download), a free download that provides an
integrated development environment on Windows, Mac, and Linux.

The resulting applications will run atop a [network that has higher rated cloud
performance than other large scale
providers](https://mspoweruser.com/report-microsoft-azure-beats-google-cloud-and-amazon-aws-in-network-performance/),
and enable federating identities between participants using Azure Active
Directory. With Azure, those applications can be deployed to more regions than
any other cloud provider and benefit from more certifications.

We look forward to seeing what you’ll build, and we’ll continue to both listen
and look for ways to help as we build a decentralized future together.

To learn more about how to use these samples to build and extend blockchain
applications, you can find a host of videos on our [Channel 9 show Block
Talk](http://aka.ms/bcblocktalk).

You can also stay up to date with the latest updates from Azure Blockchain by
following us on Twitter [\@MSFTBlockchain](https://twitter.com/MSFTBlockchain)
