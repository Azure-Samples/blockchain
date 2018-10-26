<#
.SYNOPSIS
This script automates some of the manual deployment steps of Azure Blockchain Workbench.
.DESCRIPTION
The script performs the following tasks -
- Creates an Azure Active Directory Application using the domain you specify as a parameter
- Defines an application role and assigns it to the application
- Defines the set of resources needed by this application (i.e Graph api)
- Defines an admin user for workbench and adds it to the domain
- Creates a Key
- Creates a User Principal
- Assigns the user to be Admin of Workbench
.INPUTS
None. You cannot pipe objects to this script.
.OUTPUTS
This script will write key information to the screen needed to complete the installation of Azure Blockchain Workbench
.EXAMPLE
cloudShellPreDeploy.ps1 -TenantName blockchaindemos.onmicrosoft.com -AADAppName "My Workbench Deployment"
#>

param(
    [Parameter(Mandatory=$true)][string]$TenantName,
    [Parameter(Mandatory=$false)][string]$AADAppName
)

if (-not $AADAppName) {
    $AADAppName = "Azure Blockchain Workbench"
}

$GRAPH_RESOURCE_ID = "00000003-0000-0000-c000-000000000000"
$GRAPH_READ_ALL_USERS_PERMISSION_ID = "df021288-bdef-4463-88db-98f22de89214"
$GRAPH_PERMISSION_TYPE = "Role"

function GenerateRandomPassword()
{
  $passwordArray = ([char[]]([char]65..[char]90) + [char[]]([char]97..[char]122) + 0..9 | Sort-Object {Get-Random})[0..45]
  $specialChar = Get-Random -InputObject @('*', '^', '+', '~', '!', '=')
  $randomIndex = Get-Random -Minimum 0 -Maximum ($passwordArray.Length - 1)
  $passwordArray[$randomIndex] = $specialChar
  $passwordArray -join ''
}

#################################################################################
#
#                        Install azure AD tools
#################################################################################
# For cloud shell
if (Get-Module -ListAvailable -Name "AzureAD.Standard.Preview") {
    Write-Host "AzureAD.Standard.Preview module already exists"
    Import-Module "AzureAD.Standard.Preview"
# For Windows PowerShell
} elseif (Get-Module -ListAvailable -Name "AzureADPreview")  {
    Write-Host "AzureADPreview module already exists."
    Import-Module  "AzureADPreview"
} elseif (Get-Module -ListAvailable -Name "AzureAD")  {
    Write-Host "AzureAD module already exists."
    Import-Module  "AzureAD"
} else {
    try {
        Write-Host "AzureAD module doesn't exist. Installing..."
        Install-Module -Name "AzureAD"
    } catch {
        Write-Host 'FAILURE: Could not install AzureAD module in your computer. Exiting... ' -foregroundcolor red
        exit 1
    }
}

#################################################################################
#
#            Authenticate to Azure AD for the desired subscription
#################################################################################
$currentUser = Connect-AzureAD -TenantId $TenantName

if (-Not $currentUser) {
    Write-Host 'There was a problem with login' -foregroundcolor red
    exit 1
}

#################################################################################
#
#                        Define Admin Role
#################################################################################
Write-Host 'Defining the Admin Role... '
$adminRole = New-Object Microsoft.Open.AzureAD.Model.AppRole("User", `
    "This role administers Azure Blockchain Workbench", `
    "Administrator", `
    [System.GUID]::NewGuid().ToString(), `
    $True, `
    "Administrator")

#################################################################################
#
#                        Define Resource Access
#################################################################################
$graphReadAllUsersAccess = New-Object Microsoft.Open.AzureAD.Model.ResourceAccess ($GRAPH_READ_ALL_USERS_PERMISSION_ID, $GRAPH_PERMISSION_TYPE)
$graphResourceAccess = New-Object Microsoft.Open.AzureAD.Model.RequiredResourceAccess($GRAPH_RESOURCE_ID, $graphReadAllUsersAccess)

#################################################################################
#
#                        Creating app secret
#################################################################################
Write-Host 'Creating the app secret'

$appSecret = New-Object Microsoft.Open.AzureAD.Model.PasswordCredential($null, `
    [DateTime]::Now.AddYears(2), `
    [System.GUID]::NewGuid().ToString(), `
    [DateTime]::Now, `
    (GenerateRandomPassword))

#################################################################################
#
#                        Register AAD Application for  Web Client
#################################################################################
Write-Host 'Creating the AAD application'
try {
    $application = New-AzureADApplication `
        -DisplayName $AADAppName `
        -AppRoles $adminRole `
        -RequiredResourceAccess $graphResourceAccess `
        -PasswordCredentials $appSecret
    Write-Host 'Successfully created AAD application with appId: ' $application.AppId
} catch {
    Write-Host 'FAILURE: Failed to create Azure AD application.' $_ -foregroundcolor red
    Write-Host 'Exiting...' -foregroundcolor red
    exit 1
}


#################################################################################
#
#                        Create Service Principal
#################################################################################
Write-Host 'Creating the Service Principal for Azure Blockchain Workbench '
try {
    $sp = New-AzureADServicePrincipal -AppId $application.AppId
} catch {
    Write-Host 'FAILURE: Failed to create service principal. Exiting...' -foregroundcolor red
    exit 1
}

#################################################################################
#
#                        Create Role Assignment
#################################################################################
Write-Host 'Adding current service principal as an admin'
try {
    $null = New-AzureADServiceAppRoleAssignment `
        -Id $adminRole.Id `
        -ObjectId $sp.ObjectId `
        -PrincipalId $sp.ObjectId `
        -ResourceId $sp.ObjectId
} catch {
    # This is nice to have, so just continue
}


Write-Host "Looking for your user '$($currentUser.Account.Id)' in '$TenantName' tenant"
try {
    $matchedUsers = Get-AzureADUser -Filter "mail eq '$($currentUser.Account.Id)' or userPrincipalName eq '$($currentUser.Account.Id)'"
} catch {
    Write-Host 'WARNING: Failed to retrieve your user information. Please refer to the docs to manually add yourself as an admin.' -foregroundcolor yellow
}


if ($matchedUsers -And $matchedUsers.length -gt 0) {
    Write-Host "$($matchedUsers.length) user(s) were found with email '$($currentUser.Account.Id)'"
    $user = $matchedUsers[0]
    # Assign the current logged in user to be the owner of the Application. (this is nice to have)
    try {
        # Note: There is a bug in method Add-AzureADApplicationOwner in AzureAD.Standard.Preview lib
        $null = Add-AzureADApplicationOwner `
            -ObjectId $application.ObjectId `
            -RefObjectId $user.ObjectId
    } catch {
        # Write-Host "'$($currentUser.Account.Id)' is already an owner of this application."
    }

    # Assign the current logged in user to be in the admin role
    try {
        $null = New-AzureADUserAppRoleAssignment `
            -Id $adminRole.Id `
            -ObjectId $user.ObjectId `
            -PrincipalId $user.ObjectId `
            -ResourceId $sp.ObjectId
    } catch {
        Write-Host 'WARNING: Failed to add you to the Workbench Admin role. Please refer to the docs to do this manually' -foregroundcolor yellow
    }


    Write-Host "Added '$($currentUser.Account.Id)' as an admin on the application"
} else {
    Write-Host "WARNING: No user was found with email '$($currentUser.Account.Id)' in '$TenantName' tenant. Please refer to the docs to do this manually" -foregroundcolor yellow
}

Write-Host "SUCCESS: Your AAD application was successfully provisioned" -foregroundcolor green


Write-Host
Write-Host
Write-Host
Write-Host
Write-Host "                  You can use the values bellow in your Azure Portal" -foregroundcolor green
Write-Host "============================================================================================================"
Write-Host
Write-Host "Domain Name: $tenantName" -foregroundcolor green
Write-Host "Application Name: $AADAppName" -foregroundcolor green
Write-Host "Application Client: $($application.AppId)" -foregroundcolor green
Write-Host "Application Key: $($appSecret.Value)" -foregroundcolor green
Write-Host "WARNING: Please navigate to Azure portal and press the 'Grant Permission' on the AAD App. Make sure to add the Reply Url after the deployment is done (refer to the docs http://aka.ms/workbenchAADPostDeployment for more information)" -foregroundcolor red
