<#
.SYNOPSIS

Upgrades Azure Blockchain Workbench to version 1.1.0.


.DESCRIPTION

Upgrades Azure Blockchain Workbench to version 1.1.0.

.PARAMETER SubscriptionID
SubscriptionID to create or locate all resources.

.PARAMETER ResourceGroupName
Name of the Azure Resource Group where Blockchain Workbench has been deployed to,
which will be upgraded.

.INPUTS

None. You cannot pipe objects to this script.

.OUTPUTS

None. This script does not generate any output. 
.EXAMPLE

C:\tmp> .\azureBlockchainWorkbenchUpgradeTov1_1_0.ps1 -SubscriptionID "<subscription_id>" -ResourceGroupName "<workbench-resource-group-name>"

#>


param(
    [Parameter(Mandatory=$true)][string]$SubscriptionID,
    [Parameter(Mandatory=$true)][string]$ResourceGroupName
)

$targetDockerTag = "1.1.0"
$artifactsRoot = "https://gallery.azure.com/artifact/20151001/microsoft-azure-blockchain.azure-blockchain-workbenchazure-blockchain-workbench.1.0.2/Artifacts"


#############################################
#  Script Initialization
#############################################

$logId = 0
Write-Progress -Id $logId -Activity "Login & Setup" -Status "Login to Azure" -PercentComplete 0

if ((Get-Command "Login-AzureRmAccount" -errorAction SilentlyContinue) -eq $null)
{
    throw "Azure Powershell cmdlets were not detected. We recommend that you follow the instructions on
    https://www.powershellgallery.com/packages/AzureRM/6.0.1 to obtain the latest version. Or, you can run 
    this script using Azure Cloud shell at https://shell.azure.com/powershell"
}

# AzureRM.Websites v5.0 or greater is required for support for upgrading Docker containers
if ((Get-Command "Get-AzureRmWebApp").Version.Major -lt 5)
{
    throw "The required version of the Azure Powershell cmdlets was not detected. We recommend that you follow the 
    instructions on https://www.powershellgallery.com/packages/AzureRM/6.0.1 to update to a compatible version. Or,
    you can run  this script using Azure Cloud shell at https://shell.azure.com/powershell"
}

$context = Get-AzureRmContext

if ($context -eq $null -or ($context.Name -eq "Default"))
{
    $account = Login-AzureRmAccount -SubscriptionId $SubscriptionID
    if ($account -eq $null)
    {
        throw "Failed to login to Azure. Please try to login again."
    }
}

Write-Progress -Id $logId -Activity "Login & Setup" -Status "Loading Azure Resources" -PercentComplete 35

Set-AzureRmContext -SubscriptionId $SubscriptionID -ErrorAction Stop

$rg = Get-AzureRmResourceGroup -Name $ResourceGroupName
if ($rg -eq $null)
{
    throw "We couldn't locate the resource group $ResourceGroupName. Please check the name and try again"
}

# Locate Resources in Blockchain Workbench deployment

$vmss = Get-AzureRmVmss -ResourceGroupName $ResourceGroupName -ErrorAction SilentlyContinue
if ($vmss -eq $null)
{
    throw "Could not locate VM Scale Set within the resource group $ResourceGroupName. Verify that Resource Group contains an Azure Blockchain Workbench deployment."
}

$workerVMSS = ($vmss | Where-Object { $_.Name -like "*-worker-*" })[0] # Select the Workbench Worker VMSS
if ($workerVMSS -eq $null)
{
    throw "Could not locate Azure Blockchain Workbench Worker VMSS in $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

$websites = Get-AzureRmWebApp -ResourceGroupName $ResourceGroupName -ErrorAction SilentlyContinue
if ($websites -eq $null)
{
    throw "Could not locate App Service within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

$apiWebsite = ($websites | Where-Object { $_.Name -like "*-api" })[0] # Select the Workbench API
if ($apiWebsite -eq $null)
{
    throw "Could not locate API App Service within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

# Perform explicit get to obtain all properties not returned in `list` operation
$apiWebsite = Get-AzureRmWebApp -ResourceGroupName $ResourceGroupName -Name $apiWebsite.Name 

$uiWebsite = ($websites | Where-Object { $_.Name -notlike "*-api" })[0] # Select the Workbench GUI
if ($uiWebsite -eq $null)
{
    throw "Could not locate Webapp App Service within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

# Perform explicit get to obtain all properties not returned in `list` operation
$uiWebsite = Get-AzureRmWebApp -ResourceGroupName $ResourceGroupName -Name $uiWebsite.Name 

Write-Progress -Id $logId -Activity "Login & Setup" -Status "Login to Azure" -PercentComplete 100

function ApplyVersionSpecificChanges1_0_1($json)
{
    ## v1.0.1 Deployment Changes
    ## =========================
    ## 1) New Environment variables added to Compose Worker task, for heartbeat functionality
    ## 2) Cron job added to monitor dlt-watcher

    $composeInvokeCommand = $json | Where-Object { $_.name -eq "Compose Worker" }
    if($composeInvokeCommand.environment.DLT_WATCHER_HEARTBEAT_DIR_PATH -eq $null)
    {
        $composeInvokeCommand.environment | Add-Member -NotePropertyName DLT_WATCHER_HEARTBEAT_DIR_PATH -NotePropertyValue "/dlt-watcher-heartbeat"
    }

    if($composeInvokeCommand.environment.DLT_WATCHER_HEARTBEAT_FILE -eq $null)
    {
        $composeInvokeCommand.environment | Add-Member -NotePropertyName DLT_WATCHER_HEARTBEAT_FILE -NotePropertyValue ".dlt-watcher-heartbeat"
    }   

    if ( $json[0].name.Contains("Setup cron job") -eq $false)
    {
        $cronEntry = @{
            name = "Setup cron job to restart dlt-watcher when no activity is detected";
            command = "echo '*/5 * * * * root if [  ``find ~/.dlt-watcher-heartbeat -mmin +5`` ]; then docker restart root_dlt-watcher_1; fi' > /etc/cron.d/workbench-dlt-watcher";
        }

        $json = ,$cronEntry + $json
    }

    return $json
}


function UpgradeInitBlob( $orig)
{
    $stringContents = [System.Text.Encoding]::ASCII.GetString([System.Convert]::FromBase64String($orig))

    $json = ConvertFrom-Json $stringContents
    
    foreach ($entry in $json)
    {
        if($entry.environment.DOCKER_TAG)
        {
            $entry.environment.DOCKER_TAG = $targetDockerTag
        }     
    }

    $sqlComposeDownloadCommand = $json | Where-Object { $_.name -eq "Download SQL Compose" }
    $sqlComposeDownloadCommand.command = "curl -f -S -s --connect-timeout 5 --retry 15 -o /root/docker-sql-compose.yaml `"$artifactsRoot/docker-compose.db.yaml`""

    $mainComposeDownloadCommand = $json | Where-Object { $_.name -eq "DownloadWorker" }
    $mainComposeDownloadCommand.command = "curl -f -S -s --connect-timeout 5 --retry 15 -o /root/docker-compose.yaml `"$artifactsRoot/docker-compose.prod.yaml`""
    
    $json = ApplyVersionSpecificChanges1_0_1($json)

    $jsonString = ConvertTo-Json $json -Compress

    # Powershell may wrap the commands JSON in a higher level structure. Keep only the array of commands, wrapped in `[]`
    $openingBracketPos = $jsonString.IndexOf("[")
    $closingBracketPos = $jsonString.LastIndexOf("]")
    $jsonString = $jsonString.Substring($openingBracketPos, $closingBracketPos - $openingBracketPos + 1)

    $bytes = [System.Text.Encoding]::ASCII.GetBytes($jsonString)
    $encodedText =[Convert]::ToBase64String($bytes)

    return $encodedText
}

#############################################
#  Upgrade the VMSS
#############################################
$logId++

Write-Progress -Id $logId -Activity "Upgrade Worker" -Status "Downloading VM Extension Configuration" -PercentComplete 0

$oldExtension = $workerVMSS.VirtualMachineProfile.ExtensionProfile.Extensions | Where-Object {$_.Name -eq "Initialize-Machine" }[0]

$oldExtensionConfig = $oldExtension.Settings.ToString()
$oldExtensionObject = ConvertFrom-Json $oldExtensionConfig

Write-Progress -Id $logId -Activity "Upgrade Worker" -Status "Updating VM Extension Configuration" -PercentComplete 20

$commandLineParts = $oldExtensionObject.commandToExecute -split '\s+'

if($commandLineParts.Length -lt 5)
{
    throw "VMSS command to execute was not in the expected format. Is this a valid Azure Blockchain Workbench deployment?"
}

$keyVaultUri = $commandLineParts[3]
$initBlob = UpgradeInitBlob($commandLineParts[4])

# Create new deployment configuration
$newExtensionConfig = @{
    fileUris = @("$artifactsRoot/scripts/runScripts.sh");
    commandToExecute = "sh runScripts.sh $artifactsRoot $keyVaultUri $initBlob"
}

Write-Progress -Id $logId -Activity "Upgrade Worker" -Status "Applying VM Extension Configuration" -PercentComplete 60

# Change the Initialize-Machine extension to match the latest version
$workerVMSS = Remove-AzureRmVmssExtension -VirtualMachineScaleSet $workerVMSS -Name Initialize-Machine
$workerVMSS = Add-AzureRmVmssExtension -VirtualMachineScaleSet $workerVMSS -Name Initialize-Machine -Type CustomScript -Publisher Microsoft.Azure.Extensions -TypeHandlerVersion 2.0 -AutoUpgradeMinorVersion $True -Setting $newExtensionConfig

# This cmdlet will perform an update on the VMSS if required, or just return if the goal configuration is the same as the current configuration
$workerVMSS = Update-AzureRmVmss -ResourceGroupName $ResourceGroupName -VMScaleSetName $workerVMSS.Name -VirtualMachineScaleSet $workerVMSS
if($workerVMSS -eq $null)
{
    throw "Unable to update the Workbench Worker VM. Please wait several minutes and try to run this script again. A description of the error can be found above."
}

Write-Progress -Id $logId -Activity "Upgrade Worker" -Status "Completed Upgrade of Worker" -PercentComplete 100

#############################################
#  Upgrade the Workbench API
#############################################
$logId++

Write-Progress -Id $logId -Activity "Upgrade Workbench API" -Status "Applying New Configuration" -PercentComplete 0

ForEach($setting in $apiWebsite.SiteConfig.AppSettings)
{
    if($setting.Name -eq 'DOCKER_CUSTOM_IMAGE_NAME')
    {
        $setting.Value = "blockchainworkbenchprod.azurecr.io/appbuilder.api:$targetDockerTag"
    }
}

$apiWebsite.SiteConfig.LinuxFxVersion = "DOCKER|blockchainworkbenchprod.azurecr.io/appbuilder.api:$targetDockerTag"

$apiWebsite = Set-AzureRmWebApp $apiWebsite -ErrorAction Stop
if($apiWebsite-eq $null)
{
    throw "Unable to update the Workbench API. More information is contained above. Please try again in a few minutes."
}

Write-Progress -Id $logId -Activity "Workbench API" -Status "Restarting Web Application" -PercentComplete 75

$apiWebsite = Restart-AzureRmWebApp -WebApp $apiWebsite -ErrorAction Stop
if($apiWebsite -eq $null)
{
    throw "Unable to restart the Workbench API. More information is contained above. Please try again in a few minutes."
}

Write-Progress -Id $logId -Activity "Workbench API" -Status "Completed Upgrade of Workbench API" -PercentComplete 100

#############################################
#  Upgrade the Website
#############################################
$logId++

Write-Progress -Id $logId -Activity "Upgrade Workbench Website" -Status "Applying New Configuration" -PercentComplete 0

ForEach($setting in $uiWebsite.SiteConfig.AppSettings)
{
    if($setting.Name -eq 'DISPLAY_RELEASE_VERSION')
    {
        $setting.Value = "$targetDockerTag"
    }

    if($setting.Name -eq 'DOCKER_CUSTOM_IMAGE_NAME')
    {
        $setting.Value = "blockchainworkbenchprod.azurecr.io/webapp:$targetDockerTag"
    }
}

$uiWebsite.SiteConfig.LinuxFxVersion = "DOCKER|blockchainworkbenchprod.azurecr.io/webapp:$targetDockerTag"

$uiWebsite = Set-AzureRmWebApp $uiWebsite -ErrorAction Stop
if($uiWebsite -eq $null)
{
    throw "Unable to update the Workbench Website. More information is contained above. Please try again in a few minutes."
}

Write-Progress -Id $logId -Activity "Upgrade Workbench Website" -Status "Restarting Web Application" -PercentComplete 75

$uiWebSite = Restart-AzureRmWebApp -WebApp $uiWebsite -ErrorAction Stop
if($uiWebsite -eq $null)
{
    throw "Unable to restart the Workbench Website. More information is contained above. Please try again in a few minutes."
}

Write-Progress -Id $logId -Activity "Upgrade Workbench Website" -Status "Completed Upgrade of Workbench Website" -PercentComplete 100

#############################################
#  Script exit
#############################################

Write-Output "Azure Blockchain Workbench in Resource Group $ResourceGroupName was succesfully updated to version 1.1.0."





