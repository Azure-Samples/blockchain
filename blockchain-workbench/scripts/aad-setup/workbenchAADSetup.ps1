<#
.SYNOPSIS
This script automates the creation of the Azure AD application registration for Workbench deployments
.DESCRIPTION
The script performs the following tasks - https://aka.ms/workbenchAADSteps
- Creates an Azure Active Directory Application using the domain you specify as a parameter
- Defines an application role and assigns it to the application
- Defines the set of resources needed by this application (e.g. Graph API)
- Defines an admin user for Workbench and adds it to the domain
- Creates a User Principal
- Assigns the user to be Admin of Workbench
- Updates the Workbench deployment to use the new AD application
- Sets the reply URL
.INPUTS
None. You cannot pipe objects to this script.
.OUTPUTS
This script will write key information to the screen needed to complete the installation of Azure Blockchain Workbench
.EXAMPLE
workbenchAADSetup.ps1 -TenantName [blockchaindemos.onmicrosoft.com] -AADAppName [My Workbench Deployment] -SubscriptionId [subscriptionId] -ResourceGroupName [resourceGroupName]
#>

param(
    [Parameter(Mandatory=$false)][string]$TenantName,
    [Parameter(Mandatory=$false)][string]$AADAppName="Azure Blockchain Workbench",
    [Parameter(Mandatory=$false)][string]$SubscriptionId,
    [Parameter(Mandatory=$false)][string]$ResourceGroupName,
    [Parameter(Mandatory=$false)][string]$DeploymentId=[System.GUID]::NewGuid().ToString()
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

    # Issue: https://github.com/Azure-Samples/blockchain/issues/222
    # Azure cloud shell deliberately hided Connect-AzureAD cmdlet in a 2020 Feb update and used a wrapper with identical
    # name Connect-AzureAD. This change pollutes cmdlet names in this script. The following line fixes the issue:

    # Create an alias for Connect-AzureAD cmdlet depending on the version of AAD module
    # so that the cmdlet could be used easier later
    Set-Alias -Name Connect-AzureADAlias -Value "AzureAD.Standard.Preview\Connect-AzureAD"

# For Windows PowerShell
} elseif (Get-Module -ListAvailable -Name "AzureADPreview")  {
    Log-Debug "Importing module AzureADPreview"
    Import-Module "AzureADPreview"
    Set-Alias -Name Connect-AzureADAlias -Value "AzureADPreview\Connect-AzureAD"

} elseif (Get-Module -ListAvailable -Name "AzureAD")  {
    Log-Debug "Importing module AzureAD"
    Import-Module  "AzureAD"
    Set-Alias -Name Connect-AzureADAlias -Value "AzureAD\Connect-AzureAD"

} else {
    Log-Error "This script is not compatible with your computer, Please use Azure CloudShell https://shell.azure.com/powershell" -Exit
}

Log-Debug "Running script with params:"
Log-Debug "TenantName: $TenantName"
Log-Debug "AADAppName: $AADAppName"
Log-Debug "SubscriptionId: $SubscriptionId"
Log-Debug "ResourceGroupName: $ResourceGroupName"
Log-Debug "DeploymentId: $DeploymentId"

#################################################################################
#                        Define Variables
#################################################################################
$application = $null
$currentUser = $null
Log-Debug "Creating Admin Role object"
$adminRole = New-Object Microsoft.Open.AzureAD.Model.AppRole( `
    @("Application", "User"), `
    "This role administers Azure Blockchain Workbench", `
    "Administrator", `
    [System.GUID]::NewGuid().ToString(), `
    $true, `
    "Administrator")

Log-Debug "Admin Role object created"
Log-Debug $adminRole

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
#            Authenticate to Azure AD for the desired subscription
#################################################################################
Do  {
    Log-Debug "Starting do loop"
    Log-Debug "TenantName: $TenantName"
    Log-Debug "Application: $application"

    if (-Not $TenantName) {
        Log-Debug "Asking for TenantName"
        $TenantName = Read-Host -Prompt "Please enter the Azure Active Directory tenant you would like to use (Go to https://aka.ms/workbenchFAQ for more info)"
        Log-Debug "User chose tenant $TenantName"
    }

    # If tenant Id is provided then login to that tenant, if not ask the user again
    try {
        if ($TenantName) {
            Log-Debug "Trying to login to $TenantName"
            $currentUser = Connect-AzureADAlias -TenantId $TenantName -ErrorAction SilentlyContinue
        } else {
            continue;
        }
    } catch {
        Log-Error "There was a problem with login. Please try again." -Exception $_
        Log-Debug "Resetting the tenantName and continuing"
        $tenantName = $null
        continue;
    }

    Log-Debug $currentUser
    $TenantName = $currentUser.TenantDomain
    Log-Debug "TenantName was set to: $TenantName"

#################################################################################
#            Creating the AzureAD Application Registration
#################################################################################
    try {
        Log-Info 'Creating the AAD application'
        $application = New-AzureADApplication `
            -DisplayName $AADAppName `
            -AppRoles $adminRole `
            -IdentifierUris "https://$TenantName/$([System.GUID]::NewGuid().ToString())" `
            -Oauth2AllowImplicitFlow $True `
            -RequiredResourceAccess $requiredResourceAccessList
    } catch {
        # The user cannot create apps in their directory
        if ($_.Exception.Message -like "*Authorization_RequestDenied*") {
            Log-Error "You do not have sufficient privileges to create an application in this tenant. Please chose another tenant or have the tenant admin run this script. https://aka.ms/workbenchFAQ " -Exception $_
        } else {
            Log-Error "Creating application failed. Please try again. https://aka.ms/workbenchFAQ" -Exception $_
        }

        Log-Debug "Resetting the tenantName and continuing"
        $tenantName = $null
        $currentUser = $null
        continue
    }

    Log-Debug $application
    Log-Info "Successfully created AAD application with appId: $($application.AppId)"

} while (-Not $application)


#################################################################################
#                        Create Service Principal
#################################################################################
Log-Info 'Creating the Service Principal for Azure Blockchain Workbench '
try {
    $sp = New-AzureADServicePrincipal -AppId $application.AppId
    Log-Debug "Successfully created Service Principal"
    Log-Debug $sp
} catch {
    Log-Error 'Failed to create Service Principal. Please try again.' -Exception $_ -Exit
}


#################################################################################
#                        Create Role Assignment
#################################################################################
Log-Info 'Adding current service principal as an admin'
try {
    $roleAssignment = New-AzureADServiceAppRoleAssignment `
        -Id $adminRole.Id `
        -ObjectId $sp.ObjectId `
        -PrincipalId $sp.ObjectId `
        -ResourceId $sp.ObjectId

    Log-Info "Successfully created role assignment"
    Log-Debug $roleAssignment
} catch {
    Log-Warning "Could not add service principal as an admin. This step is optional" -Exception $_
}

#################################################################################
#                        Assigning User to Admin Role and Application
#################################################################################

Log-Info "Looking for your user '$($currentUser.Account.Id)' in '$TenantName' tenant"
try {
    $matchedUsers = Get-AzureADUser -Filter "mail eq '$($currentUser.Account.Id)' or userPrincipalName eq '$($currentUser.Account.Id)' or OtherMails eq '$($currentUser.Account.Id)'"
    Log-Debug $matchedUsers
} catch {
    Log-Warning "Failed to retrieve your user information. Please refer to the docs to manually add yourself as an admin. https://aka.ms/workbenchAADSteps" -Exception $_
}


if ($matchedUsers -And $matchedUsers.length -gt 0) {
    Log-Info "$($matchedUsers.length) user(s) were found with email '$($currentUser.Account.Id)'"
    $user = $matchedUsers[0]
    Log-Debug $user

    try {
        Log-Info "Assign the current logged in user to be the owner of the Application."
        $ownerAssignment = Add-AzureADApplicationOwner `
            -ObjectId $application.ObjectId `
            -RefObjectId $user.ObjectId
        Log-Debug "Successfully assigned the user as the application owner"
        Log-Debug $ownerAssignment
    } catch {
        Log-Debug "Failed assigned the user as the application owner. The user is probably already an owner"
        Log-Debug $_
    }

    # Assign the current logged in user to be in the admin role
    try {
        Log-Debug "Adding $user to the admin role"
        $adminAssignment = New-AzureADUserAppRoleAssignment `
            -Id $adminRole.Id `
            -ObjectId $user.ObjectId `
            -PrincipalId $user.ObjectId `
            -ResourceId $sp.ObjectId
        Log-Info "Added '$($currentUser.Account.Id)' as an admin on the application"
        Log-Debug $adminAssignment
    } catch {
        Log-Warning 'Failed to add you to the Workbench Admin role. Please refer to the docs to do this manually. https://aka.ms/workbenchAADSteps ' -Exception $_
    }

} else {
    Log-Warning "No user was found with email '$($currentUser.Account.Id)' in '$TenantName' tenant.  Please refer to the docs to manually add yourself as an admin. https://aka.ms/workbenchAADSteps "
}

#################################################################################
# Update the Azure Web Apps if SubscriptionId and $ResourceGroupName are provided
#################################################################################
if ($SubscriptionId -XOR $ResourceGroupName) {
    Log-Warning "You need to provide both 'SubscriptionID' and 'ResourceGroupName' in order to configure your instance. Only one value provided."
}

if ($SubscriptionId -And $ResourceGroupName) {
    Log-Info "Updating your Workbench Instance with the Active Directory Application Info. This may take some time..."

    # For Cloud Shell
    if (Get-Module -ListAvailable -Name Az.Accounts) {
        Log-Debug "Importing module Az"
        Import-Module Az
    } else {
        Log-Error "This script is not compatible with your computer. Please use Azure CloudShell https://shell.azure.com/powershell" -Exit
    }

    Log-Debug "Getting the Azure Context"
    $context = Get-AzContext
    Log-Debug $context

    if (-Not $context)
    {
        Log-Debug "The user is not logged in"
        Log-Info "Logging in to Azure"
        try {
            $account = Connect-AzAccount -SubscriptionId $SubscriptionId -ErrorAction SilentlyContinue
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
        $context = Set-AzContext -SubscriptionId $SubscriptionId -ErrorAction SilentlyContinue
        Log-Debug $context
        if (-Not $context) {
            throw "Context is null"
        }
    } catch {
        Log-Error "Couldn't switch to subscription $SubscriptionId. Please double check the subscriptionId and try again." -Exception $_ -Exit
    }

    try {
        Log-Debug "Looking for resource group $ResourceGroupName"
        $rg = Get-AzResourceGroup -Name $ResourceGroupName -ErrorAction SilentlyContinue
        Log-Debug $rg
        if (-Not $rg) {
            throw "Resource group is null"
        }
    } catch {
        Log-Error "We couldn't locate the resource group $ResourceGroupName. Please check the name and try again" -Exception $_ -Exit
    }

    try {
        Log-Debug "Looking for Web apps within $ResourceGroupName"
        $websites = Get-AzWebApp -ResourceGroupName $ResourceGroupName
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

    $replyUrl = $null
    ForEach($site in $websites) {
        if ($site.DefaultHostName -notlike "*-api*") {
            Log-Debug "Found UI Website URL $($site.DefaultHostName)"
            $replyUrl = $site.DefaultHostName
        }
    }

    if (-Not $replyUrl) {
        Log-Error "Could not get the Workbench URL. Please check your SubscriptionId and ResourceGroupName" -Exit
    }

    $AADAppName = "$AADAppName $($replyUrl.split('.')[0])"

    try {
        Log-Info "Attempting to update the reply URl to https://$replyUrl"
        Set-AzureADApplication `
            -DisplayName $AADAppName `
            -ObjectId $application.ObjectId `
            -Homepage "https://$replyUrl" `
            -LogoutUrl "https://$replyUrl" `
            -ReplyUrls "https://$replyUrl"

        Log-Debug "Successfully updated the reply url."
    } catch {
        Log-Error "Could not set the reply url on the Azure AD application. Please try again."  -Exception $_ -Exit
    }

    Log-Debug "Updating the AAD tenants and App ID on App Service"
    Foreach ($site in $websites) {
        Log-Debug "Fetching App Service $($site.Name)"
        try {
            $fullWebsiteObject = Get-AzWebApp -ResourceGroupName $ResourceGroupName -Name $site.Name
            Log-Debug $fullWebsiteObject
            if (-Not $fullWebsiteObject) {
                throw "Azure App Service is null"
            }
        } catch {
            Log-Error "Could not fetch the App Service. Please try again." -Exception $_ -Exit
        }

        Log-Debug "Setting the env variables on the App Service $($site.Name)"

        $websiteConfig = @{}

        # Copy the original values
        ForEach($setting in $fullWebsiteObject.SiteConfig.AppSettings) {
            $websiteConfig[$setting.Name] = $setting.Value
        }

        Log-Debug "Setting the AAD_APP_ID to $($application.AppId)"
        $websiteConfig["AAD_APP_ID"] = $application.AppId

        Log-Debug "Setting the AAD_TENANT_DOMAIN_NAME to $TenantName"
        $websiteConfig["AAD_TENANT_DOMAIN_NAME"] = $TenantName

        try {
            Log-Debug "Saving the new values to App Service"
            $fullWebsiteObject  = Set-AzWebApp `
                -ResourceGroupName $ResourceGroupName `
                -Name $site.Name `
                -AppSettings $websiteConfig

            if (-Not $fullWebsiteObject) {
                throw "Azure App Service is null"
            }

            Log-Debug "Successfully set the new values"
        } catch {
            Log-Error "Could not get the Workbench URL. Please check your SubscriptionId and ResourceGroupName" -Exception $_ -Exit
        }
    }
}

Log-Info "Waiting for changes to propagate..."
sleep 20

Write-Host
Log-Info "Azure Active Directory Domain Name: $tenantName"
Log-Info "Application Name: $AADAppName"
Log-Info "Application Client Id: $($application.AppId)"
Write-Host
Write-Host
Write-Host

if ($SubscriptionId -And $ResourceGroupName) {
    Log-Success "Your Workbench instance was successfully provisioned. Navigate to https://$replyUrl to use your instance."
} else {
    Log-Success "Successfully created your AAD application registration. Make sure to manually set the Reply Url after deployment."
}

Log-Info "Please refer to https://aka.ms/workbenchFAQ to read more about user management in Workbench."
Write-Host "============================================================================================================================"
