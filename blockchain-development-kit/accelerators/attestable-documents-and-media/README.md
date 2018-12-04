[Attestable Documents and Media Accelerator](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench)
===========================================
There are multiple scenarios where having attestable documents and media is valuable. 
Media doesn’t belong on the blockchain, but hashes of the media plus the media + metadata should be.  
This accelerator uses logic app connectors to identify when media has been added to Azure Storage, One Drive, One Drive For Business, GDrive, Sharepoint, FTP, etc.   It will then hash the file, hash the metadata and the hash of the file and deliver it to a set of smart contracts in a smart contract hosted registry.

The general flow of the samples is - 

- A file is delivered to the specified service
- A trigger is received by a Logic App that has metadata about the file
- Code retrieves the file contents and runs a SHA256 hash over it
- Code also hashes the meta-data about the file
- The Logic App sends the hashes to the constructor of a File smart contract which registers itself to a File Registry contract.

The smart contracts for the accelerator can be found [here](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench/smart-contracts)

Azure Blockchain Workbench Samples
----------------------------------
[Adobe Creative Cloud](https://github.com/Azure-Samples/blockchain/blob/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench/AdobeCreativeCloud/README.md)

[Azure Storage](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench/azure-blob-storage)

[Box](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench/box)

[FTP](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench/ftp)

[Google Drive](https://github.com/Azure-Samples/blockchain/blob/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench/google/README.md)

[OneDrive](https://github.com/Azure-Samples/blockchain/blob/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench/onedrive/README.md)

[One Drive for Business](https://github.com/Azure-Samples/blockchain/blob/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench/onedrive-for-business/README.md)

[SharePoint](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench/sharepoint)

Ethereum Blockchain Connector for Logic Apps Samples
-----------------------------------------------------
(Coming Soon)

Corda Ledger Connector for Logic Apps Sample (Coming Soon)
-----------------------------------------------------------
(Coming Soon)

