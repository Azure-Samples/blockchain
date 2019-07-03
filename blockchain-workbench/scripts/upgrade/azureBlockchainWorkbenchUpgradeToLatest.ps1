<#
.SYNOPSIS

Upgrades Azure Blockchain Workbench to version 1.7.2.


.DESCRIPTION

Upgrades Azure Blockchain Workbench to version 1.7.2.

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

C:\tmp> .\azureBlockchainWorkbenchUpgradeTov1_7_1.ps1 -SubscriptionID "<subscription_id>" -ResourceGroupName "<workbench-resource-group-name>"

#>


param(
    [Parameter(Mandatory = $true)][string]$SubscriptionID,
    [Parameter(Mandatory = $true)][string]$ResourceGroupName,
    [Parameter(Mandatory = $false)][string]$TargetDockerTag = "1.7.2",
    [Parameter(Mandatory = $false)][string]$ArtifactsRoot = "https://catalogartifact.azureedge.net/publicartifacts/microsoft-azure-blockchain.azure-blockchain-workbench-c92dd56c-382d-4a3d-9d9b-0d74fd3fa2e4-azure-blockchain-workbench/Artifacts",
    [Parameter(Mandatory = $false)][string]$DockerRepository = "blockchainworkbenchprod.azurecr.io",
    [Parameter(Mandatory = $false)][string]$DockerLogin = $null,
    [Parameter(Mandatory = $false)][string]$DockerPw = $null,
    [Parameter(Mandatory = $false)][switch]$TestApi,
    [Parameter(Mandatory = $false)][switch]$TestEnv
)

# Comment the next line in before publishing
# Set-Item Env:\SuppressAzurePowerShellBreakingChangeWarnings "true"

#############################################
#  Constants
#############################################
$MIN_TLS_VERSION = 1.2

#############################################
#  Script Initialization
#############################################

$logId = 0

Write-Progress -Id $logId -Activity "Checking compatibility & Importing modules" -Status "Importing Az modules" -PercentComplete 0

# This script only works with the AZ module. AZ is a cross platform module available for all operating systems
if (Get-Module -ListAvailable -Name Az.Accounts) {
    Import-Module Az
}
# Remove this when Test Machines have AZ installed
elseif ($TestEnv -And (Get-Module -ListAvailable -Name AzureRM)) {
    Write-Host "Running in test env"
    Import-Module AzureRM
    ## Alias Az commands to AzureRm commands
    Set-Alias -Name Connect-AzAccount -Value Login-AzureRmAccount
    Set-Alias -Name Get-AzContext -Value Get-AzureRmContext
    Set-Alias -Name Set-AzContext -Value Set-AzureRmContext
    Set-Alias -Name Get-AzResourceGroup -Value Get-AzureRmResourceGroup
    Set-Alias -Name Get-AzVmss -Value Get-AzureRmVmss
    Set-Alias -Name Get-AzWebApp -Value Get-AzureRmWebApp
    Set-Alias -Name Get-AzResource -Value Get-AzureRMResource
    Set-Alias -Name Get-AzAppServicePlan -Value Get-AzureRmAppServicePlan
    Set-Alias -Name Get-AzServiceBusNamespace -Value Get-AzureRmServiceBusNamespace
    Set-Alias -Name Get-AzEventGridTopic -Value Get-AzureRmEventGridTopic
    Set-Alias -Name Get-AzEventGridTopicKey -Value Get-AzureRmEventGridTopicKey
    Set-Alias -Name Get-AzServiceBusQueue -Value Get-AzureRmServiceBusQueue
    Set-Alias -Name New-AzServiceBusQueue -Value New-AzureRmServiceBusQueue
    Set-Alias -Name Get-AzServiceBusTopic -Value Get-AzureRmServiceBusTopic
    Set-Alias -Name New-AzServiceBusTopic -Value New-AzureRmServiceBusTopic
    Set-Alias -Name Remove-AzVmssExtension -Value Remove-AzureRmVmssExtension
    Set-Alias -Name Add-AzVmssExtension -Value Add-AzureRmVmssExtension
    Set-Alias -Name Update-AzVmss -Value Update-AzureRmVmss
    Set-Alias -Name Set-AzAppServicePlan -Value Set-AzureRmAppServicePlan
    Set-Alias -Name Set-AzResource -Value Set-AzureRmResource
    Set-Alias -Name Set-AzWebApp -Value Set-AzureRmWebApp
    Set-Alias -Name Restart-AzWebApp -Value Restart-AzureRmWebApp
}
else {
    throw "Could not find AZ module. Please user Azure CloudShell or install the AZ module https://docs.microsoft.com/en-us/powershell/azure/install-az-ps"
}

Write-Progress -Id $logId -Activity "Checking compatibility & Importing modules" -Status "Importing Az modules" -PercentComplete 100

Write-Progress -Id $logId -Activity "Login & Setup" -Status "Login to Azure" -PercentComplete 0

$context = Get-AzContext

if (-Not $context -or ($context.Name -eq "Default")) {
    $account = Connect-AzAccount -SubscriptionId $SubscriptionID
    if (-Not $account) {
        throw "Failed to login to Azure. Please try to login again."
    }
}

Write-Progress -Id $logId -Activity "Login & Setup" -Status "Loading Azure Resources" -PercentComplete 35

$context = Set-AzContext -SubscriptionId $SubscriptionID -ErrorAction Stop

$rg = Get-AzResourceGroup -Name $ResourceGroupName
if (-Not $rg) {
    throw "We couldn't locate the resource group $ResourceGroupName. Please check the name and try again"
}

# Locate Resources in Blockchain Workbench deployment

$vmss = Get-AzVmss -ResourceGroupName $ResourceGroupName -ErrorAction SilentlyContinue
if (-Not $vmss) {
    throw "Could not locate VM Scale Set within the resource group $ResourceGroupName. Verify that Resource Group contains an Azure Blockchain Workbench deployment."
}

$workerVMSS = ($vmss | Where-Object { $_.Name -like "*-worker-*" })[0] # Select the Workbench Worker VMSS
if (-Not $workerVMSS) {
    throw "Could not locate Azure Blockchain Workbench Worker VMSS in $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

$websites = Get-AzWebApp -ResourceGroupName $ResourceGroupName -ErrorAction SilentlyContinue
if (-Not $websites) {
    throw "Could not locate App Service within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

$apiWebsite = ($websites | Where-Object { $_.Name -like "*-api" })[0] # Select the Workbench API
if (-Not $apiWebsite) {
    throw "Could not locate API App Service within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

# Perform explicit get to obtain all properties not returned in `list` operation
$apiWebsite = Get-AzWebApp -ResourceGroupName $ResourceGroupName -Name $apiWebsite.Name

# Locate the underlying app service plan
$spResource = Get-AzResource -ResourceId $apiWebsite.ServerFarmId
$appServicePlan = Get-AzAppServicePlan -ResourceGroupName $spResource.ResourceGroupName -Name $spResource.Name

# Select the Workbench GUI
$uiWebsite = ($websites | Where-Object { $_.Name -notlike "*-api" })[0] # Select the Workbench GUI
if (-Not $uiWebsite) {
    throw "Could not locate Webapp App Service within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

# Perform explicit get to obtain all properties not returned in `list` operation
$uiWebsite = Get-AzWebApp -ResourceGroupName $ResourceGroupName -Name $uiWebsite.Name

# Locate the service bus
$serviceBusNs = Get-AzServiceBusNamespace -ResourceGroupName $ResourceGroupName -ErrorAction SilentlyContinue
if (-Not $serviceBusNs) {
    throw "Could not locate Service Bus within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

# Discrepancy between Az and AzureRM
if ($TestEnv) {
    $eventGrid = (Get-AzEventGridTopic -ResourceGroupName $ResourceGroupName)[0]
}
else {
    $eventGrid = (Get-AzEventGridTopic -ResourceGroupName $ResourceGroupName).PsTopicsList
}

if (-Not $eventGrid) {
    throw "Could not locate EventGrid within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

$eventGridTopicEndpoint = $eventGrid.Endpoint

$eventGridKey = (Get-AzEventGridTopicKey -ResourceGroupName $ResourceGroupName -Name $eventGrid.TopicName).Key1

if (-Not $eventGridKey) {
    throw "Could not get the EventGrid key within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}

Write-Progress -Id $logId -Activity "Login & Setup" -Status "Resources loaded" -PercentComplete 100
function Coalesce($a, $b) { if ($a) { $a } else { $b } }

function GetEnvironmentSetting($json, $settingName) {
    # Locate the setting from another task
    foreach ($entry in $json) {
        if ($entry.environment.$settingName) {
            return $entry.environment.$settingName
        }
    }
    throw "Couldn't set environment variable correctly, please try again later."
}

function ApplyVersionSpecificChanges1_4_0($json) {
    ## v1.4.0 Deployment Changes
    ## =========================
    ## 1) Configuration manager is added to the system
    ## 2) Cron job for dlt-watcher monitoring is removed, as dlt-watcher is replaced by eth-watcher

    # Removes the cron job for dlt-watcher monitoring if found
    if ($json[0].name.Contains("Setup cron job")) {
        $json = $json | Select-Object -Skip 1
    }

    $dlConfigManagerCommand = $json | Where-Object { $_.name -eq "Download Config Manager Compose" }
    if (-Not $dlConfigManagerCommand) {
        $dlConfigManager = @{
            name    = "Download Config Manager Compose";
            command = "curl -f -S -s --connect-timeout 5 --retry 15 -o /root/docker-config-manager-compose.yaml `"$ArtifactsRoot/docker-compose.config-manager.yaml`"";
        }

        $env = @{ }
        $env.APPLICATION_INSIGHTS_KEY = GetEnvironmentSetting $json  "APPLICATION_INSIGHTS_KEY"
        $env.DOCKER_REPOSITORY = GetEnvironmentSetting $json  "DOCKER_REPOSITORY"
        $env.DOCKER_TAG = GetEnvironmentSetting $json  "DOCKER_TAG"
        $env.KEY_VAULT_URI = GetEnvironmentSetting $json  "KEY_VAULT_URI"
        $env.GETH_RPC_ENDPOINT = GetEnvironmentSetting $json  "GETH_RPC_ENDPOINT"
        $env.EVENT_GRID_TOPIC_ENDPOINT = GetEnvironmentSetting $json  "EVENT_GRID_TOPIC_ENDPOINT"
        $envObject = New-Object –TypeName PSObject –Prop $env

        if (-Not $envObject) {
            throw "Couldn't set environment correctly, please try again later."
        }

        $createConfigManager = @{
            name        = "Create Config Manager";
            command     = "docker-compose -f /root/docker-config-manager-compose.yaml up --force-recreate";
            environment = $envObject;
        }

        $firstEntry = $json[0]
        $rest = $json | Select-Object -Skip 1

        $json = , $firstEntry + $dlConfigManager + $createConfigManager + $rest
    }

    return $json
}
function LocateGethEndpoint($blob) {
    $stringContents = [System.Text.Encoding]::ASCII.GetString([System.Convert]::FromBase64String($blob))

    $json = ConvertFrom-Json $stringContents

    foreach ($entry in $json) {
        if ($entry.environment.GETH_RPC_ENDPOINT) {
            return $entry.environment.GETH_RPC_ENDPOINT
        }
    }

    return ""
}

function UpgradeInitBlob( $orig) {
    $stringContents = [System.Text.Encoding]::ASCII.GetString([System.Convert]::FromBase64String($orig))

    $json = ConvertFrom-Json $stringContents

    foreach ($entry in $json) {
        if ($entry.environment.DOCKER_TAG) {
            $entry.environment.DOCKER_TAG = $TargetDockerTag
        }

        if ($entry.environment.DOCKER_REPOSITORY -and $DockerRepository) {
            $entry.environment.DOCKER_REPOSITORY = $DockerRepository
        }

        if ($entry.environment.DOCKER_PASSWORD -and $DockerPw) {
            $entry.environment.DOCKER_PASSWORD = $DockerPw
        }

        if ($entry.environment.DOCKER_LOGIN -and $DockerLogin) {
            $entry.environment.DOCKER_LOGIN = $DockerLogin
        }
    }

    $sqlComposeDownloadCommand = $json | Where-Object { $_.name -eq "Download SQL Compose" }
    $sqlComposeDownloadCommand.command = "curl -f -S -s --connect-timeout 5 --retry 15 -o /root/docker-sql-compose.yaml `"$ArtifactsRoot/docker-compose.db.yaml`""

    $mainComposeDownloadCommand = $json | Where-Object { $_.name -eq "DownloadWorker" }
    $mainComposeDownloadCommand.command = "curl -f -S -s --connect-timeout 5 --retry 15 -o /root/docker-compose.yaml `"$ArtifactsRoot/docker-compose.prod.yaml`""

    # Stop and remove docker containers that are no longer a part of this version via the "--remove-orphans" flag
    $composeWorkerCommand = $json | Where-Object { $_.name -eq "Compose Worker" }
    $composeWorkerCommand.command = "docker-compose -f /root/docker-compose.yaml up -d --force-recreate --remove-orphans"

    $json = ApplyVersionSpecificChanges1_4_0($json)

    $configManagerDownloadCommand = $json | Where-Object { $_.name -eq "Download Config Manager Compose" }
    $configManagerDownloadCommand.command = "curl -f -S -s --connect-timeout 5 --retry 15 -o /root/docker-config-manager-compose.yaml `"$ArtifactsRoot/docker-compose.config-manager.yaml`""

    $jsonString = ConvertTo-Json $json -Compress

    # Powershell may wrap the commands JSON in a higher level structure. Keep only the array of commands, wrapped in `[]`
    $openingBracketPos = $jsonString.IndexOf("[")
    $closingBracketPos = $jsonString.LastIndexOf("]")
    $jsonString = $jsonString.Substring($openingBracketPos, $closingBracketPos - $openingBracketPos + 1)

    $bytes = [System.Text.Encoding]::ASCII.GetBytes($jsonString)
    $encodedText = [Convert]::ToBase64String($bytes)

    return $encodedText
}

#############################################
# Pre-requisite Upgrades
#############################################

$logId++

Write-Progress -Id $logID -Activity "Pre-Requisites" -Status "Checking for service bus queue..." -PercentComplete 0

# Create the Service Bus queue "ingressQueue", "internalQueue" and topic "egressTopic" if they don't exist
$queueName = "internalQueue"
$queue = Get-AzServiceBusQueue -ResourceGroupName $ResourceGroupName -Namespace $serviceBusNs -Name $queueName -ErrorAction SilentlyContinue
if (-Not $queue) {
    Write-Progress -Id $logID -Activity "Pre-Requisites" -Status "Checking for service bus queue..." -PercentComplete 35

    $queue = New-AzServiceBusQueue -ResourceGroupName $ResourceGroupName `
        -NamespaceName $serviceBusNs.Name `
        -Name $queueName `
        -EnablePartitioning $true `
        -DefaultMessageTimeToLive "14.00:00:00" `
        -LockDuration "00:00:30" `
        -DuplicateDetectionHistoryTimeWindow "00:10:00" `
        -ErrorAction Stop

    if (-Not $queue) {
        throw "Unable to create Service Bus queue! Last Error: $($Error[0])"
    }

    Write-Progress -Id $logID -Activity "Pre-Requisites" -Status "Created service bus queue $queueName!" -PercentComplete 75
}

$queueName = "ingressQueue"
$queue = Get-AzServiceBusQueue -ResourceGroupName $ResourceGroupName -Namespace $serviceBusNs -Name $queueName -ErrorAction SilentlyContinue
if (-Not $queue) {
    Write-Progress -Id $logID -Activity "Pre-Requisites" -Status "Checking for service bus queue..." -PercentComplete 35

    $queue = New-AzServiceBusQueue -ResourceGroupName $ResourceGroupName `
        -NamespaceName $serviceBusNs.Name `
        -Name $queueName `
        -EnablePartitioning $true `
        -DefaultMessageTimeToLive "14.00:00:00" `
        -LockDuration "00:00:30" `
        -DuplicateDetectionHistoryTimeWindow "00:10:00" `
        -ErrorAction Stop

    if (-Not $queue) {
        throw "Unable to create Service Bus queue! Last Error: $($Error[0])"
    }

    Write-Progress -Id $logID -Activity "Pre-Requisites" -Status "Created service bus queue $queueName!" -PercentComplete 75
}

$topicName = "egressTopic"
$topic = Get-AzServiceBusTopic -ResourceGroupName $ResourceGroupName -Namespace $serviceBusNs -Name $topicName -ErrorAction SilentlyContinue
if (-Not $topic) {
    Write-Progress -Id $logID -Activity "Pre-Requisites" -Status "Checking for service bus topic..." -PercentComplete 35

    $topic = New-AzServiceBusTopic `
        -ResourceGroupName $ResourceGroupName `
        -NamespaceName $serviceBusNs.Name `
        -Name $topicName `
        -EnablePartitioning $true `
        -DefaultMessageTimeToLive "14.00:00:00" `
        -DuplicateDetectionHistoryTimeWindow "00:10:00" `
        -ErrorAction Stop

    if (-Not $topic) {
        throw "Unable to create Service Bus topic! Last Error: $($Error[0])"
    }

    Write-Progress -Id $logID -Activity "Pre-Requisites" -Status "Created service bus topic $topicName!" -PercentComplete 75
}

Write-Progress -Id $logID -Activity "Pre-Requisites" -Status "Pre-requisite resources are created!" -PercentComplete 100

#############################################
#  Upgrade the VMSS
#############################################
$logId++

Write-Progress -Id $logId -Activity "Upgrade Worker" -Status "Downloading VM Extension Configuration" -PercentComplete 0

$oldExtension = $workerVMSS.VirtualMachineProfile.ExtensionProfile.Extensions | Where-Object { $_.Name -eq "Initialize-Machine" }[0]

$oldExtensionConfig = $oldExtension.Settings.ToString()
$oldExtensionObject = ConvertFrom-Json $oldExtensionConfig

Write-Progress -Id $logId -Activity "Upgrade Worker" -Status "Updating VM Extension Configuration" -PercentComplete 20

$commandLineParts = $oldExtensionObject.commandToExecute -split '\s+'

if ($commandLineParts.Length -lt 5) {
    throw "VMSS command to execute was not in the expected format. Is this a valid Azure Blockchain Workbench deployment?"
}

$initBlobOld = $commandLineParts[$commandLineParts.Length - 1]
$keyVaultUri = $commandLineParts[$commandLineParts.Length - 2]
$initBlob = UpgradeInitBlob($initBlobOld)
$gethEndpoint = LocateGethEndpoint($initBlobOld)

# Create new deployment configuration
$newExtensionConfig = @{
    fileUris         = @("$ArtifactsRoot/scripts/runScripts.sh");
    commandToExecute = "/bin/bash runScripts.sh $gethEndpoint $ArtifactsRoot $keyVaultUri $initBlob"
}

Write-Progress -Id $logId -Activity "Upgrade Worker" -Status "Applying VM Extension Configuration" -PercentComplete 60

# Change the Initialize-Machine extension to match the latest version (these two cmdlets only update the local $workerVMSS variable)
$workerVMSS = Remove-AzVmssExtension -VirtualMachineScaleSet $workerVMSS -Name Initialize-Machine
$workerVMSS = Add-AzVmssExtension -VirtualMachineScaleSet $workerVMSS -Name Initialize-Machine -Type CustomScript -Publisher Microsoft.Azure.Extensions -TypeHandlerVersion 2.0 -AutoUpgradeMinorVersion $True -Setting $newExtensionConfig

# This cmdlet will perform an update on the VMSS if required, or just return if the goal configuration is the same as the current configuration
$workerVMSS = Update-AzVmss -ResourceGroupName $ResourceGroupName -VMScaleSetName $workerVMSS.Name -VirtualMachineScaleSet $workerVMSS
if (-Not $workerVMSS) {
    throw "Unable to update the Workbench Worker VM. Please wait several minutes and try to run this script again. A description of the error can be found above."
}

Write-Progress -Id $logId -Activity "Upgrade Worker" -Status "Completed Upgrade of Worker" -PercentComplete 100

#############################################
#  Upgrade the App Service Plan
#############################################
$logId++

Write-Progress -Id $logId -Activity "Upgrade App Service Plan" -Status "Updating App Service plan's scale up pricing tier" -PercentComplete 0

$upgradedPlan = Set-AzAppServicePlan  -ResourceGroupName $appServicePlan.ResourceGroup -Name $appServicePlan.Name -Tier PremiumV2 -WorkerSize Small

Write-Progress -Id $logId -Activity "Upgrade App Service Plan" -Status "Updated App Service plan's scale up pricing tier" -PercentComplete 100

#############################################
#  Upgrade the Workbench API
#############################################
$logId++

Write-Progress -Id $logId -Activity "Upgrade Workbench API" -Status "Applying New Configuration" -PercentComplete 0

$apiConfig = @{ }

ForEach ($setting in $apiWebsite.SiteConfig.AppSettings) {
    $apiConfig[$setting.Name] = $setting.Value
}

# Set these values
$apiConfig["DOCKER_CUSTOM_IMAGE_NAME"] = "$DockerRepository/appbuilder.api:$TargetDockerTag"
$apiConfig["ServiceBus_InternalQueueName"] = "internalQueue"
$apiConfig["ServiceBus_EgressTopicName"] = "egressTopic"
$apiConfig["EVENT_GRID_TOPIC_ENDPOINT"] = $eventGridTopicEndpoint
$apiConfig["EventGrid_EventGrid"] = $eventGridKey

# Override these values if needed
$apiConfig["DOCKER_REGISTRY_SERVER_URL"] = Coalesce $DockerRepository $apiConfig["DOCKER_REGISTRY_SERVER_URL"]
$apiConfig["DOCKER_REGISTRY_SERVER_USERNAME"] = Coalesce $DockerLogin $apiConfig["DOCKER_REGISTRY_SERVER_USERNAME"]
$apiConfig["DOCKER_REGISTRY_SERVER_PASSWORD"] = Coalesce $DockerPw $apiConfig["DOCKER_REGISTRY_SERVER_PASSWORD"]

$apiWebsite = Set-AzWebApp `
    -ResourceGroupName $apiWebsite.ResourceGroup `
    -Name $apiWebsite.Name `
    -AppSettings $apiConfig `
    -HttpsOnly $true `
    -ErrorAction Stop

# Set min TLS version
$apiWebsiteConfig = Set-AzResource -ApiVersion '2018-02-01' `
    -ResourceName $('{0}/web' -f $apiWebsite.Name) `
    -ResourceGroupName $apiWebsite.ResourceGroup `
    -ResourceType 'Microsoft.Web/sites/config' `
    -Force `
    -PropertyObject @{
    minTlsVersion  = $MIN_TLS_VERSION
    linuxFxVersion = "DOCKER|$DockerRepository/appbuilder.api:$TargetDockerTag"
} `
    -ErrorAction Stop

if (-Not $apiWebsite -or -Not $apiWebsiteConfig) {
    throw "Unable to update the Workbench API. More information is contained above. Please try again in a few minutes."
}

Write-Progress -Id $logId -Activity "Workbench API" -Status "Restarting Web Application" -PercentComplete 75

$apiWebsite = Restart-AzWebApp -WebApp $apiWebsite -ErrorAction Stop
if (-Not $apiWebsite) {
    throw "Unable to restart the Workbench API. More information is contained above. Please try again in a few minutes."
}

Write-Progress -Id $logId -Activity "Workbench API" -Status "Completed Upgrade of Workbench API" -PercentComplete 100

#############################################
#  Upgrade the Website
#############################################
$logId++

Write-Progress -Id $logId -Activity "Upgrade Workbench Website" -Status "Applying New Configuration" -PercentComplete 0

$uiConfig = @{ }

# Copy the original values
ForEach ($setting in $uiWebsite.SiteConfig.AppSettings) {
    $uiConfig[$setting.Name] = $setting.Value
}

# Set these values
$uiConfig["DOCKER_CUSTOM_IMAGE_NAME"] = "$DockerRepository/webapp:$TargetDockerTag"
$uiConfig["DISPLAY_RELEASE_VERSION"] = $TargetDockerTag
$uiConfig["LAST_UPDATED"] = $(Get-Date).ToString()
$uiConfig["UPDATE_IN_PROGRESS"] = 'false'


# Override these values if needed
$uiConfig["DOCKER_REGISTRY_SERVER_URL"] = Coalesce $DockerRepository $uiConfig["DOCKER_REGISTRY_SERVER_URL"]
$uiConfig["DOCKER_REGISTRY_SERVER_USERNAME"] = Coalesce $DockerLogin $uiConfig["DOCKER_REGISTRY_SERVER_USERNAME"]
$uiConfig["DOCKER_REGISTRY_SERVER_PASSWORD"] = Coalesce $DockerPw $uiConfig["DOCKER_REGISTRY_SERVER_PASSWORD"]

# Set these values if they dont exist
$uiConfig["CUSTOMER_SUBSCRIPTIONID"] = Coalesce $uiConfig["CUSTOMER_SUBSCRIPTIONID"] $SubscriptionID
$uiConfig["RESOURCE_GROUP_NAME"] = Coalesce $uiConfig["RESOURCE_GROUP_NAME"] $ResourceGroupName

# Save the changes
$uiWebsite = Set-AzWebApp `
    -ResourceGroupName $uiWebsite.ResourceGroup `
    -Name $uiWebsite.Name `
    -AppSettings $uiConfig `
    -HttpsOnly $true `
    -ErrorAction Stop


# Set min TLS version
$uiWebsiteConfig = Set-AzResource -ApiVersion '2018-02-01' `
    -ResourceName $('{0}/web' -f $uiWebsite.Name) `
    -ResourceGroupName $uiWebsite.ResourceGroup `
    -ResourceType 'Microsoft.Web/sites/config' `
    -Force `
    -PropertyObject @{
    minTlsVersion  = $MIN_TLS_VERSION
    linuxFxVersion = "DOCKER|$DockerRepository/webapp:$TargetDockerTag"
} `
    -ErrorAction Stop

if (-Not $uiWebsite -or -Not $uiWebsiteConfig) {
    throw "Unable to update the Workbench Website. More information is contained above. Please try again in a few minutes."
}

Write-Progress -Id $logId -Activity "Upgrade Workbench Website" -Status "Restarting Web Application" -PercentComplete 75

$uiWebSite = Restart-AzWebApp -WebApp $uiWebsite -ErrorAction Stop
if (-Not $uiWebsite) {
    throw "Unable to restart the Workbench Website. More information is contained above. Please try again in a few minutes."
}

Write-Progress -Id $logId -Activity "Upgrade Workbench Website" -Status "Completed Upgrade of Workbench Website" -PercentComplete 100

#############################################
#  Wait for and test for api success
#############################################

if ($TestApi) {
    Write-Progress -Id $logId -Activity "Testing upgrade complete" -Status "Testing Upgrade of Workbench API" -PercentComplete 0

    $stop = $false
    $retryCount = 0
    $numberOfRetries = 10
    $sleepTime = 30

    While ($stop -eq $false) {
        $endPoint = $apiWebsite.EnabledHostNames[0] + "/api/health"
        $response = Invoke-WebRequest $endPoint
        if ($response.StatusCode -eq 200) {
            Write-Progress -Id $logId -Activity "Testing upgrade complete" -Status "Completed Testing of Upgrade of Workbench API" -PercentComplete 100
            $stop = $true
        }
        if ($retryCount -gt $numberOfRetries) {
            throw "Workbench API not up after $numberOfRetries retries. Upgrade failed. Waited for $($sleepTime * $numOfRetries) seconds"
        }
        else {
            Write-Host "Request to Workbench API returned $($response.StatusCode), retrying in $sleepTime seconds..."
            Start-Sleep -Seconds $sleepTime
            $retryCount = $retryCount + 1
        }
    }
}

#############################################
#  Script exit
#############################################

Write-Output "Azure Blockchain Workbench in Resource Group $ResourceGroupName was successfully updated to version 1.7.2."
