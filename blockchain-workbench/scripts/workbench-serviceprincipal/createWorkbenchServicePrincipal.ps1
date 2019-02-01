<#
.SYNOPSIS
This script creates a service principal that can be used to access Workbench's API programmatically
.DESCRIPTION
The script performs the following tasks:
- Creates a Service Principal and assigns it to Workbench
- Can assign the service principal to be an Admin on Workbench
.INPUTS
None. You cannot pipe objects to this script.
.OUTPUTS
The status of the update
.EXAMPLE
createServicePrincipal.ps1 -TenantName [blockchaindemos.onmicrosoft.com] -WorkbenchAppId [My Workbench AppId]
#>

param(
    [Parameter(Mandatory=$true)][string]$TenantName,
    [Parameter(Mandatory=$true)][string]$WorkbenchAppId,
    [Parameter(Mandatory=$false)][string]$SpName="Service Principal for Workbench API",
    [Parameter(Mandatory=$false)][switch]$MakeAdmin
)

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
Log-Debug "AppId: $WorkbenchAppId"

try {
    Log-Debug "Trying to login to $TenantName"
    Log-Info "Logging in to AAD"
    $currentUser = Connect-AzureAD -TenantId $TenantName -ErrorAction SilentlyContinue
    Log-Debug $currentUser
} catch {
    Log-Error "There was a problem with login. Please try again." -Exception $_ -Exit
}

#################################################################################
#            Retrieving Workbench Application and Service Principal
#################################################################################

try {
    $workbenchApplication = Get-AzureADApplication -Filter "AppId eq '$WorkbenchAppId'"
    Log-Debug $workbenchApplication
} catch {
    Log-Error "Could not find an application with Id $WorkbenchAppId in directory $TenantName" -Exception $_ -Exit
}

try {
    $workbenchSP = Get-AzureADServicePrincipal -Filter "AppId eq '$WorkbenchAppId'"
    Log-Debug $workbenchSP
} catch {
    Log-Error "Could not find a service principal with Id $WorkbenchAppId in directory $TenantName" -Exception $_ -Exit
}

#################################################################################
#            Creating the App Registration
#################################################################################

try {
    Log-Info 'Creating the App registration'
    $application = New-AzureADApplication `
        -DisplayName $SpName `
        -IdentifierUris "https://$TenantName/$([System.GUID]::NewGuid().ToString())" `
        -PasswordCredentials $appSecret

    $password = New-AzureADApplicationPasswordCredential `
        -ObjectId $application.ObjectId `
        -CustomKeyIdentifier "Key1" `
        -StartDate $(Get-Date).AddMinutes(-5) `
        -EndDate $(Get-Date).AddYears(2)
} catch {
    # The user cannot create apps in their directory
    if ($_.Exception.Message -like "*Authorization_RequestDenied*") {
        Log-Error "You do not have sufficient privileges to create an application in this tenant. Please chose another tenant or have the tenant admin run this script. https://aka.ms/workbenchFAQ " -Exception $_ -Exit
    } else {
        Log-Error "Creating application failed. Please try again. https://aka.ms/workbenchFAQ" -Exception $_ -Exit
    }
}

#################################################################################
#                        Assigning the User Application (Optional)
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
}

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
#                  Assign Service Principal to the admin role
#################################################################################
if ($MakeAdmin) {

    $adminAppRole = $null
    # Looking for the App role with the Value of "Administrator"
    ForEach ($appRole in $workbenchApplication.AppRoles) {
        if ($appRole.Value -eq "Administrator") {
            $adminAppRole= $appRole
            break;
        }
    }

    if (-Not $adminAppRole) {
        Log-Error "Could not find the Admin app role for Application with AppId $WorkbenchAppId" -Exit
    }

    Log-Debug $adminAppRole

    try {
        $roleAssignment = New-AzureADServiceAppRoleAssignment `
            -Id $adminAppRole.Id `
            -ObjectId $sp.ObjectId `
            -PrincipalId $sp.ObjectId `
            -ResourceId $workbenchSP.ObjectId

        Log-Info "Successfully created role assignment"
        Log-Debug $roleAssignment
    } catch {
        Log-Error "Could not add service principal as an admin." -Exception $_ -Exit
    }
}


Log-Debug $application

Write-Host
Write-Host

Log-Success "Successfully created Service Principal"
Log-Info "AppId: $($application.AppId)"
Log-Info "AppKey: $($password.Value)"
Write-Host "=============================================================================================="
