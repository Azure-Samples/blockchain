.
[Attestable Documents and Media Accelerator](https://github.com/Azure-Samples/blockchain/tree/master/blockchain-development-kit/accelerators/attestable-documents-and-media/blockchain-workbench)
===========================================
There are multiple scenarios where having attestable documents and media is valuable. 
Media doesn’t belong on the blockchain, but hashes of the media plus the media + metadata should be.  
This accelerator uses logic app connectors to identify when media has been added to Azure Storage, One Drive, One Drive For Business, GDrive, Sharepoint, FTP, etc.   It will then hash the file, hash the metadata and the hash of the file and deliver it to a set of smart contracts in a smart contract hosted registry.

This initial version processes a JSON file through a simple UI, with a wizard UI to fast follow.
