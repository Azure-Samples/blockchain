<#
.SYNOPSIS
This script will upgrade the AAD App Registration for Workbench 1.5
.DESCRIPTION
The script performs the following tasks - https://aka.ms/workbenchAADUpgrade
- Updates the app registration with new Required resource access
- Sets Oauth2AllowImplicitFlow to true
.INPUTS
None. You cannot pipe objects to this script.
.OUTPUTS
The status of the update
.EXAMPLE
# To standalone alone App Registration
workbenchAADUpgrade.ps1 -TenantName [blockchaindemos.onmicrosoft.com] -AppId [My Workbench Deployment]

# To update an App Registration on a deployed Workbench instance
workbenchAADUpgrade.ps1 -SubscriptionId [subscriptionId] -ResourceGroupName [resourceGroupName]
#>

param(
    [Parameter(Mandatory=$false)][string]$TenantName,
    [Parameter(Mandatory=$false)][string]$AppId,
    [Parameter(Mandatory=$false)][string]$SubscriptionId,
    [Parameter(Mandatory=$false)][string]$ResourceGroupName
)

#################################################################################
#                        Constants
#################################################################################
$WINDOWS_AZURE_ACTIVE_DIRECTORY_RESOURCE_ID = "00000002-0000-0000-c000-000000000000"
$SIGN_IN_AND_READ_USER_PROFILE_PERMISSION_ID = "311a71cc-e848-46a1-bdf8-97ff7156d8e6"
$AAD_PERMISSION_TYPE = "Scope"
$GRAPH_RESOURCE_ID = "00000003-0000-0000-c000-000000000000"
$GRAPH_READ_ALL_USERS_PERMISSION_ID = "b340eb25-3456-403f-be2f-af7a0d370277"
$GRAPH_PERMISSION_TYPE = "Scope"

function Log-Debug {
    param(
        [Parameter(Mandatory=$false)][string]$Message
    )

    if ($Message) {
        Write-Verbose $Message
    }
}

function Log-Info {
    param(
        [Parameter(Mandatory=$false)][string]$Message
    )

    if ($Message) {
        Write-Host "INFO:", $Message
    }
}

function Log-Success {
    param(
        [Parameter(Mandatory=$false)][string]$Message
    )

    if ($Message) {
        Write-Host "SUCCESS:", $Message -foregroundcolor green
    }
}

function Log-Warning {
    param(
        [Parameter(Mandatory=$false)][string]$Message,
        [Parameter(Mandatory=$false)][string]$Exception
    )

    if ($Message) {
        Write-Warning $Message
    }

    if ($Exception) {
        Write-Host "ERROR MESSAGE:" $Exception -foregroundcolor yellow
    }
}

function Log-Error {
    param(
        [Parameter(Mandatory=$false)][string]$Message,
        [Parameter(Mandatory=$false)][string]$Exception,
        [Parameter(Mandatory=$false)][switch]$Exit
    )

    if ($Message) {
        Write-Host "ERROR:" $Message -foregroundcolor red
    }

    if ($Exception) {
        Write-Host "ERROR MESSAGE:" $Exception -foregroundcolor red
    }

    if ($Exit) {
        exit 1
    }
}

#################################################################################
#                        Import Azure tools
#################################################################################
# For CloudShell
if (Get-Module -ListAvailable -Name "AzureAD.Standard.Preview") {
    Log-Debug "Importing module AzureAD.Standard.Preview"
    Import-Module "AzureAD.Standard.Preview"
# For Windows PowerShell
} elseif (Get-Module -ListAvailable -Name "AzureADPreview")  {
    Log-Debug "Importing module AzureADPreview"
    Import-Module "AzureADPreview"
} elseif (Get-Module -ListAvailable -Name "AzureAD")  {
    Log-Debug "Importing module AzureAD"
    Import-Module  "AzureAD"
} else {
    Log-Error "This script is not compatible with your computer, Please use Azure CloudShell https://shell.azure.com/powershell" -Exit
}

Log-Debug "Running script with params:"
Log-Debug "TenantName: $TenantName"
Log-Debug "AppId: $AppId"
Log-Debug "SubscriptionId: $SubscriptionId"
Log-Debug "ResourceGroupName: $ResourceGroupName"
Log-Debug "DeploymentId: $DeploymentId"

#################################################################################
#                        Define Resource Access
#################################################################################
$requiredResourceAccessList = [System.Collections.ArrayList]@()

Log-Debug "Creating Active Directory Required Resource Access Object"
$activeDirectorySignInAndReadUserProfile = New-Object Microsoft.Open.AzureAD.Model.ResourceAccess ($SIGN_IN_AND_READ_USER_PROFILE_PERMISSION_ID, $AAD_PERMISSION_TYPE)
$activeDirectoryResourceAccess = New-Object Microsoft.Open.AzureAD.Model.RequiredResourceAccess($WINDOWS_AZURE_ACTIVE_DIRECTORY_RESOURCE_ID, $activeDirectorySignInAndReadUserProfile)
Log-Debug "Active Directory Required Resource Access Object created"
Log-Debug $activeDirectoryResourceAccess
$null = $requiredResourceAccessList.Add($activeDirectoryResourceAccess)

Log-Debug "Creating Graph API Required Resource Access Object"
$graphReadAllUsersAccess = New-Object Microsoft.Open.AzureAD.Model.ResourceAccess ($GRAPH_READ_ALL_USERS_PERMISSION_ID, $GRAPH_PERMISSION_TYPE)
$graphResourceAccess = New-Object Microsoft.Open.AzureAD.Model.RequiredResourceAccess($GRAPH_RESOURCE_ID, $graphReadAllUsersAccess)
Log-Debug "Graph API Required Resource Access Object Created"
Log-Debug $graphResourceAccess
$null = $requiredResourceAccessList.Add($graphResourceAccess)


#################################################################################
# Get $TenantName and $AppId from a deployment if they are not provided
#################################################################################

if (-Not ($TenantName -Or $AppId)) {
    if (-Not ($SubscriptionId -And $ResourceGroupName)) {
        Log-Error "You need to provide both 'SubscriptionID' and 'ResourceGroupName'." -Exit
    }

    Log-Info "Getting your AppId and Tenant name from your Workbench Instance."

    # For Cloud Shell
    if (Get-Module -ListAvailable -Name "Az.Websites") {
        Log-Debug "Importing module Az.Websites"
        Import-Module "Az.Profile"
        Import-Module "Az.Websites"
    # For Windows PowerShell
    } elseif (Get-Module -ListAvailable -Name "AzureRM.Websites") {
        Log-Debug "Importing module AzureRM.Websites"
        Import-Module "AzureRM.Websites"
        Import-Module "AzureRM.Profile"
    } else {
        Log-Error "This script is not compatible with your computer. Please use Azure CloudShell https://shell.azure.com/powershell" -Exit
    }

    Log-Debug "Getting the Azure Context"
    $context = Get-AzureRmContext
    Log-Debug $context

    if (-Not $context)
    {
        Log-Debug "The user is not logged in"
        Log-Info "Logging in to Azure"
        try {
            $account = Connect-AzureRmAccount -SubscriptionId $SubscriptionId -ErrorAction SilentlyContinue
            Log-Debug $account
            if (-Not $account)
            {
                throw "Azure account is null"
            }
        } catch {
            Log-Error "Failed to login to Azure. Please try to login again." -Exception $_ -Exit
        }
    }

    try {
        Log-Debug "Changing the Subscription to $SubscriptionId"
        $context = Set-AzureRmContext -SubscriptionId $SubscriptionId -ErrorAction SilentlyContinue
        Log-Debug $context
        if (-Not $context) {
            throw "Context is null"
        }
    } catch {
        Log-Error "Couldn't switch to subscription $SubscriptionId. Please double check the subscriptionId and try again." -Exception $_ -Exit
    }

    try {
        Log-Debug "Looking for resource group $ResourceGroupName"
        $rg = Get-AzureRmResourceGroup -Name $ResourceGroupName -ErrorAction SilentlyContinue
        Log-Debug $rg
        if (-Not $rg) {
            throw "Resource group is null"
        }
    } catch {
        Log-Error "We couldn't locate the resource group $ResourceGroupName. Please check the name and try again" -Exception $_ -Exit
    }

    try {
        Log-Debug "Looking for Web apps within $ResourceGroupName"
        $websites = Get-AzureRmWebApp -ResourceGroupName $ResourceGroupName
        Log-Debug "Found $($websites.length) App Service(s)"
        if (-Not $websites -Or $websites.length -eq 0) {
            throw "Websites is null"
        }

        if ($websites.length -ne 2) {
            throw "Expected to find 2 App Services in resource group, but found $($websites.length)."
        }
    } catch {
        Log-Error "Could not locate App Service within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?" -Exception $_ -Exit
    }

    $site = $websites[0]
    Log-Debug "Fetching App Service $($site.Name)"
    try {
        $fullWebsiteObject = Get-AzureRmWebApp -ResourceGroupName $ResourceGroupName -Name $site.Name
        Log-Debug $fullWebsiteObject
        if (-Not $fullWebsiteObject) {
            throw "Azure App Service is null"
        }
    } catch {
        Log-Error "Could not fetch the App Service. Please try again." -Exception $_ -Exit
    }

    Log-Debug "Getting the env variables on the App Service $($site.Name)"
    ForEach($setting in $fullWebsiteObject.SiteConfig.AppSettings)
    {
        if($setting.Name -eq "AAD_APP_ID")
        {
            Log-Debug "Found the AAD_APP_ID to be $($setting.Value)"
            $AppId = $setting.Value
        }

        if($setting.Name -eq "AAD_TENANT_DOMAIN_NAME")
        {
            Log-Debug "Found the AAD_TENANT_DOMAIN_NAME to be $($setting.Value)"
            $TenantName = $setting.Value
        }
    }

    if (-Not ($AppId -Or $TenantName)) {
        Log-Error "Could not get the AppId and TenantName from $ResourceGroupName in subscription $subscriptionId" -Exit
    }

    Log-Info "Found AppId: $AppId"
    Log-Info "Found TenantName: $TenantName"
}

if (-Not ($AppId -And $TenantName)) {
    Log-Error "No AppId and/or TenantName was passed." -Exit
}

try {
    Log-Debug "Trying to login to $TenantName"
    Log-Info "Logging in to AAD"
    $currentUser = Connect-AzureAD -TenantId $TenantName -ErrorAction SilentlyContinue
} catch {
    Log-Error "There was a problem with login. Please try again." -Exception $_ $Exit
}

try {
    $application = Get-AzureADApplication -Filter "AppId eq '$AppId'"
    Log-Debug $application
} catch {
    Log-Error "Could not find an application with Id $AppId in directory $TenantName" -Exception $_ $Exit
}

try {
    Set-AzureADApplication `
        -ObjectId $application.ObjectId `
        -Oauth2AllowImplicitFlow $true `
        -RequiredResourceAccess $requiredResourceAccessList

    Log-Info "Updated the AAD application"
} catch {
    Log-Error "Could not update the AAD application. Please try again."  -Exception $_ -Exit
}


Log-Info "Waiting for changes to propagate..."
sleep 20
Write-Host
Write-Host
Write-Host

Log-Success "Your AAD Application $AppId was successfully upgraded."
Write-Host "=============================================================================================="
