Overview
==============
One of the key benefits of working with a large number of customers is that application patterns emerge.  

In our discussions, workshops, and projects with hundreds of customers over the last several years, we’ve identified a number of business scenarios which can be addressed with a common set of blockchain-powered technical implementations. 
Today, we are making the first set of these available as solution accelerators.  Each of these is delivered as a set of documentation and assets that will enable you to have solutions for these scenarios up and running for your consortium quickly.


[Corda Integration Accelerator](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators/corda-integration-accelerator)
===========================================
Lots of customers love the Corda blockchain and would like to be able to easily connect to all of the items released today as part of the Azure Blockchain Development Kit.  Working closely with Corda, we're happy to announce a new accelerator that enables Corda to listen to and send messages that map to the same format used by Workbench.  This initially ships with a Corda-specific version of our refrigerated transportation contract and can demonstrate integration with IoT, SMS, and Office.

[Attestable Documents and Media Accelerator](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators/attestable-documents-and-media)
===========================================
There are multiple scenarios where having attestable documents and media is valuable. 
Media doesn’t belong on the blockchain, but hashes of the media plus the media + metadata should be.  
This accelerator use logic app connectors to identify when media has been added to Azure Storage, One Drive, One Drive For Business, GDrive, Sharepoint, FTP, etc.   It will then hash the file, hash the metadata and the hash of the file and deliver it to a set of smart contracts in a smart contract hosted registry.

[Blockchain Based Registry Accelerator](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators/registry-generator)
=====================================
Registries are used in every industry and in multiple scenarios, and smart contracts can enable blockchain-based registries that are shared, immutable and cryptographically secure. Examples of registries include land, vehicle, equity, bond, music/audio, video/film, diploma, documents, luxury goods, lab certifications, licenses, entitlements, in game purchase ownership, etc.

This accelerator asks for several pieces of information about your desired registry and generates all the smart contracts required for it. Specifically, it creates a registry smart contract and an item smart contract specific to your scenario, wires them up together and generates a configuration file so you can deploy it directly to workbench. It asks questions about what the characteristics of the item, e.g. what attributes does it have, can it have an owner, does it support media, etc. and generates the contracts in seconds.


