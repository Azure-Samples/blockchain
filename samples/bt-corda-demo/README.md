# BlockTalk - Corda Cordapp Example
This session covered the most basic functionality provided by the Corda ledger technology.  The demonstration that was presented in the video can be run by following the steps below.

# Prerequisites
In order to run the demo, the following should be created/installed

* Create a [Windows based Virtual Machine](https://portal.azure.com/?pub_source=email&pub_status=success#create/Microsoft.WindowsServer2016Datacenter-ARM) in Azure

    ` NOTE: A DS2v2 is a good size for the demo.`

* Install JAVA 8 JVM
    - Visit http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
    - Scroll down to “Java SE Development Kit 8uXXX” (where “XXX” is the latest minor version number)
    - Toggle “Accept License Agreement”
    - Click the download link for jdk-8uXXX-windows-x64.exe (where “XXX” is the latest minor version number)
    - Download and run the executable to install Java (use the default settings)
    - Open a new command prompt and run java -version to test that Java is installed correctly

* Install Git
    - Visit https://git-scm.com/download/win
    - Click the “64-bit Git for Windows Setup” download link.
    - Download and run the executable to install Git (use the default settings)
    - Open a new command prompt and type git --version to test that git is installed correctly

* Install the Sample Project
    - Open a command prompt
    - Clone the CorDapp example repo by running `git clone https://github.com/corda/cordapp-example`
    - Move to the folder created `cd cordapp-example`

# Run the Application
- From the cordapp-example folder, deploy the nodes by running `gradlew deployNodes`
- Start the nodes by running `call kotlin-source/build/nodes/runnodes.bat`
- Wait until all the terminal windows display either “Webserver started up in XX.X sec” or “Node for “NodeC” started up and registered in XX.XX sec”
- Test the CorDapp is running correctly by visiting the front end at http://localhost:10007/web/example/