The FileRegistry.sol file contains two contracts - one is a registry, the other is an item that represents a file.  The two contracts are connected, with the registry holding references for unique identifiers and addresses for each of the file contracts.

Also included is a configuration file which allows this to be loaded into Azure Blockchain Workbench as an application.

These contracts are used in conjunction with one or more of the logic apps that are contained in the root folder for the accelerator.  These logic apps are designed to listen for new files, hash them and place them in this registry.
