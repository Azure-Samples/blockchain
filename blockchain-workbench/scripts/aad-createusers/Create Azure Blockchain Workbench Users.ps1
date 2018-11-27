# This script allows you to create an Azuer Active Directory user in a named tenant.
# It also assumes the user of the script has the Global Administrative role to run this script.
# You must use an admin account created in your new Azure AD instance to run this script.

CLS
"***********************************************************************************"
" Welcome to the Azure Blockchain Workbench user provisioning tool.                 "
" When prompted for your MS Online credentials, this must be run in the context of  "
" your admin user in the new ABW AD instance                                        "
" e.g. myadmin@myabw.onmicrosoft.com                                                "
"                                                                                   "
" This tool is not intended for provisioning enterprise users and is meant only     "
" to create users for Azure Blockchain Workbench sample/test applications           "
"***********************************************************************************"
""

Write-Host 'Press ENTER to continue...' -ForegroundColor Yellow
Read-Host

# Set the PowerShell execution policy for the current process/session so the script
# is not blocked
set-ExecutionPolicy -Scope Process -ExecutionPolicy Unrestricted -Force

# Test if the MSOnline module is installed.  If not, install the module
if (-not(Get-Module -ListAvailable -Name MSOnline))
{
    Write-Host 'Installing MSOnline Module...' -ForegroundColor Black -BackgroundColor White
    Install-Module -Name MSOnline -Scope CurrentUser -Force
}
else
{
    # Check if MSOnline module is loaded in current session
    if (-not(Get-Module MSOnline))
    {
        Write-Host 'Importing MSOnline Module into current session...'  -ForegroundColor Black -BackgroundColor White
        Import-Module MSOnline 
    }
    Write-Host 'MSOnline module is loaded. Proceeding with script'  -ForegroundColor Black -BackgroundColor White
}

# Connect to MSOnline Service
Write-Host ''
Write-Host ''
Write-Host "When prompted for your MS Online credentials, this must be run in the context of  " -ForegroundColor Yellow
Write-Host "your admin user in the new ABW AD instance.   e.g. myadmin@myabw.onmicrosoft.com  " -ForegroundColor Yellow
Write-Host ''
Write-Host "Press ENTER to continue..." -ForegroundColor Yellow
Read-Host

Connect-MsolService

$createMore = $true

# Enter your new Azure AD domain  
Write-Host 'Enter your tenant name, e.g. myabw.  **DO NOT INCLUDE .onmicrosoft.com.**:  ' -ForegroundColor Yellow -NoNewline
$tenantName = read-host 
$upnSuffix = $tenantName + '.onmicrosoft.com'

while ($createMore)
{
    # Enter the user name
    Write-Host "Enter the name for the new user, e.g assetowner.  **DO NOT INCLUDE THE DOMAIN, i.e. @$tenantName.onmicrosoft.com.**:  " -ForegroundColor Yellow -NoNewline
    $userName = read-host 
    $userPrincipalName = "$userName@" + $upnSuffix
    Write-Host ' '

    # Enter the initial password for the users
    Write-Host "Enter the password for the user.:  " -ForegroundColor Yellow -NoNewline
    $initialPassword = read-host
    Write-Host ' '
    Write-Host 'The password ' $initialPassword ' is set to never expire and it does not require a new password at initial login' -ForegroundColor Black -BackgroundColor White
    Write-Host ' '

    New-MsolUser -UsageLocation "US" -DisplayName "$userName" -FirstName "$userName" -LastName "Blockchain" -UserPrincipalName $userPrincipalName -Password $initialPassword -ForceChangePassword $False -PasswordNeverExpires $true

    Write-Host '****************************************' -ForegroundColor Black -BackgroundColor White
    Write-Host '   PROVISIONING COMPLETED                  ' -ForegroundColor Black -BackgroundColor White
    Write-Host '   Added user ' $userPrincipalName ' to ' $upnSuffix -ForegroundColor Black -BackgroundColor White
    Write-Host '****************************************' -ForegroundColor Black -BackgroundColor White

    # Ask to create another user in the same tenant
    Write-Host "Do you want to create another user in @$tenantName.onmicrosoft.com?:  " -ForegroundColor Yellow -NoNewline
    $confirmation = read-host 
    if ($confirmation.ToUpper() -eq 'Y')
    {
        $createMore = $true
    }
    else
    {
        $createMore = $false
    }
}

