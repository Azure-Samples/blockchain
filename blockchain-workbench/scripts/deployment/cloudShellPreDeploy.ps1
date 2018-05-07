<#
.SYNOPSIS

This script automates some of the manual deployment steps of Azure Blockchain Workbench.

It has been tested and is intended to run only in Azure Cloud Shell.
.DESCRIPTION

The script performs the followng tasks - 

- Creates an Azure Active Directory Application using the domain you specify as a parameter

- Defines an application role and assigns it to the application

- Defines an admin user for workbench and adds it to the domain

- Creates a Key

- Creats a User Principal



.INPUTS

None. You cannot pipe objects to this script.

.OUTPUTS

This script will write key information to the screen needed to complete the installation of Azure Blockchain Workbench

.EXAMPLE

CloudShellPreDeploy.ps1 -AzureADDomain "blockchaindemos.onmicrosoft.com"

#>






#$AADDomain = "blockchaindemos.onmicrosoft.com" #$AzureADDomain # Example is blockchaindemos.onmicrosoft.com
#$PasswordForKey = "password"
$debug = $false

$tenantDetail = Get-AzureADTenantDetail
$version = "1.0"
Write-Host "----------------------------------------------------------------------------------------------------------------"
Write-Host ""
Write-Host "                          Azure Blockchain Workbench Pre-Deployment Script " 
Write-Host "                                                Version " $version
Write-Host ""
Write-Host "----------------------------------------------------------------------------------------------------------------"
Write-Host ""
Write-Host "This script is run prior to deploying Azure Blockchain Workbench.  It will automate a number of steps in the written"
Write-Host "documentation, specifically those steps used to configure Azure Active Directory. "
Write-Host ""
Write-Host "At the end of the script, you will be provided the domain name of the Azure AD tenant, as well as the"
Write-Host "Application ID and Application Key for the Azure AD Client that will be used with applications that "
Write-Host "Azure Blockchain Workbench generates."
Write-Host ""
Write-Host "In addition, an Adminsitrator for the application will be created for use with Azure Blockchain Workbench"
Write-Host ""
Write-Host "Note - As stated in the documentation, Cloud Shell and this script must be executed while the target Azure Active Directory is active."
Write-Host ""
Write-Host ""
Write-Host "                                       Press Any Key To Begin"
$key = $Host.UI.RawUI.ReadKey()
Write-Host ""
Write-Host ""
Write-Host "Please enter the domain used for the Azure Active Directory that you intend to use for Workbench, e.g. blockchaindemos.onomicrosoft.com"
$AADDomain = Read-Host -Prompt "Azure Active Directory Domain To Use" 
Write-Host ""
Write-Host ""
Write-Host "Please enter a password to use to generate the key used with the application (this can be any text string)"
#$PasswordForKey = Read-Host -Prompt "Password to Use for Key" 
Write-Host ""
Write-Host ""
Write-Host "You provded a domain of " $AADDomain 
# " and a password of " $PasswordForKey
Write-Host 
Write-Host "Do you want to continue to run the script with this?"
$valuesCorrect = Read-Host -Prompt "Please Enter Yes or No" 

if ($valuesCorrect -eq "Y"  -Or $valuesCorrect -eq "y" -or $valuesCorrect -eq "YES" -Or $valuesCorrect -eq "yes" -or $valuesCorrect -eq "Yes")
{
}
else
{

    if ($valuesCorrect -eq "debug" -Or $valuesCorrect -eq "DEBUG" -or $valuesCorrect -eq "Debug")
    {
        Write-Host "Entering Debug Mode"
        $debug = $true

    }
    else
    {

        Write-Host "You did not enter Yes, so the script will now end."    
        Write-Host "You can re-run this script at any time."
    Return
    }

}#################################################################################
#
#                        Define Admin App Role
#################################################################################

Write-Host 'Defining the Admin Role... ' -foregroundcolor white -backgroundcolor blue 
$appRoleGuid = New-Guid

$appRole = New-Object -TypeName "Microsoft.Open.AzureAD.Model.AppRole"
$appRole.DisplayName = "Administrator"
$appRole.Description = "This role administers Azure Blockchain Workbench"
$appRole.IsEnabled = $True
$appRole.Id = $appRoleGuid.ToString()
$appRole.Value = "Administrator"
$appRole.AllowedMemberTypes = "User"
$appRoles = New-Object -TypeName "System.Collections.Generic.List[Microsoft.Open.AzureAD.Model.AppRole]"
$appRoles.Add($appRole)
#################################################################################
#
#                        Register AAD Application for  Web Client
#################################################################################
Write-Host 'Defining the Azure AD Application... ' -foregroundcolor white -backgroundcolor blue
$existingApp = $false 

    $AADApplicationDisplayName = "Azure Blockchain Workbench"    #AAD Application Display Name Base  
    $AADApplicationHomepage = "http://www.microsoft.com"  #Homepage for Application
   # $AADIdentifierURIs = "http://" + $ADApplicationDisplayName   #Base URI for AAD applications
   

    $displayName = $AADApplicationDisplayName + " Web Client"
    $appGuid = New-Guid

    $identifiers = "http://" + $AADDomain + "/AzureBlockchainWorkbench/" + $appGuid.ToString() + "/WebClient"
    #$identifiers = "http://" + $AADDomain + "/AzureBlockchainWorkbench/WebClient"
    $homepage = "http://www.microsoft.com"

    $application = New-Object -TypeName Microsoft.Open.AzureAD.Model.Application

    try{



            Write-Host 'Creating the Azure AD Application  ' -foregroundcolor white -backgroundcolor blue 
            
            if ($debug)
            {
                Write-Host "Values sent to New-AzureADApplication"
                Write-Host 'displayname = ' $displayName
                write-Host 'homepage = ' $homepage
                Write-Host 'identifies = ' $identifiers
                Write-Host 'appRole = ' $appRole
            }

            $application = New-AzureADApplication -DisplayName $displayName -HomePage $homepage -IdentifierUris $identifiers -AppRoles $appRole 
         
    }catch{

         try {

                    Write-Host "About to Get-AzureADApplication"
                    if ($debug) {
                    Write-Host 'identifiers = ' $identifiers
                    Write-Host 'displayname = ' $displayName
                    Write-Host 'filter = DisplayName eq ' $displayName 
                    }
                    
                    $application = Get-AzureADApplication -Filter "DisplayName eq '$displayName'" 
                    
                    $existingApp = $true

                    Write-Host 'Azure AD Application application already exists, skipping... ' -foregroundcolor yellow -backgroundcolor red 
                }
         
        catch {
                    Write-Host 'Error occured creating Azure AD Application application... ' -foregroundcolor yellow -backgroundcolor red 

                    $ErrorMessage = $_.Exception.Message
                    $FailedItem = $_.Exception.ItemName
                    
                    Write-Host "Error Details"
                    Write-Host "----------------------------------------------------"
                    Write-Host $ErrorMessage
                    Write-Host $FailedItem
                    Return
         }
         
    }


   if ($debug)
    {
           Write-Host $identifiers
           Write-Host $application
           Write-Host $application.AppRoles
    }

#################################################################################
#
#                        Create Key
#################################################################################

Write-Host 'Creating the Azure AD Application Password Credential ... ' -foregroundcolor white -backgroundcolor blue 
$password = ""
if ($debug)
{
    Write-Host 'Application:' $application
        
}
try {

     
        $password = New-AzureADApplicationPasswordCredential -ObjectId $application.ObjectId -CustomKeyIdentifier "client secret" -StartDate ([DateTime]::Now.AddMinutes(-5)) -EndDate ([DateTime]::Now.AddYears(20)) -Value $PasswordForKey
    }
catch{

                    Write-Host 'Error occured creatring Azure AD Application Password Credential ... ' -foregroundcolor yellow -backgroundcolor red 

                    $ErrorMessage = $_.Exception.Message
                    $FailedItem = $_.Exception.ItemName
                    
                    Write-Host "Error Details"
                    Write-Host "----------------------------------------------------"
                    Write-Host $ErrorMessage
                    Write-Host $FailedItem
                    Return

    }

if ($debug)
{
    Write-Host $password
}
#################################################################################
#
#                        Create Admin
#################################################################################
# Admin Role Details
#################################################################################
$adminEmail = "BlockchainWorkbenchAdmin@"+$AADDomain  #Email address for the admin
$adminFirstName = "Blockchain Workbench" #Admin first name
$adminLastName = "Admin" #Admin last name
$adminMailNickname = "WorkbenchAdmin" #Admin mail nickname
$adminDisplayName = $adminFirstName + " " + $adminLastName # Admin display name
$adminPassword = "!Password.1" #Password to use for the admin
#################################################################################



$adminPasswordProfile = New-Object -TypeName Microsoft.Open.AzureAD.Model.PasswordProfile
$adminPasswordProfile.Password = $adminPassword


$adminUserObject = New-Object -TypeName Microsoft.Open.AzureAD.Model.User
$userId = ''
$existingAdmin = $false
try{
    Write-Host 'Creating the Azure Blockchain Workbench Administrator User ... ' -foregroundcolor white -backgroundcolor blue 

    
    $adminUserObject = New-AzureADUser -AccountEnabled $True -GivenName $adminFirstName -Surname $adminLastName -DisplayName $adminDisplayName -PasswordProfile $adminPasswordProfile -MailNickName $adminMailNickname -UserPrincipalName $adminEmail 
    $userId = $adminUserObject.ObjectId


    #Write-Host "Account creation successful."
    #Write-Host ""
    #Write-Host "An Admin user named " $adminDisplayName "with email address of " $adminemail
    #Write-Host "Your password is !Password.1 and you will be required to reset it on first log in."
    #Write-Host ""

}catch{
            try {
    
                    $adminUserObject = Get-AzureADUser -SearchString $adminDisplayName
                    $userId = $adminUserObject.ObjectId
                    $existingAdmin = $true
                    Write-Host 'Admin user already exists... skipping' -foregroundcolor yellow -backgroundcolor red
                    
            }
           catch {
                    Write-Host 'Error occured creatring Azure AD Application Password Credential ... ' -foregroundcolor yellow -backgroundcolor red 
                    
                    $ErrorMessage = $_.Exception.Message
                    $FailedItem = $_.Exception.ItemName
                    
                    Write-Host "Error Details"
                    Write-Host "----------------------------------------------------"
                    Write-Host $ErrorMessage
                    Write-Host $FailedItem
                    Return
           
           }
}




#################################################################################
#
#                        Create Service Principal
#################################################################################
$sp =""
try {
        Write-Host 'Creating the Service Princiapl for Azure Blockchain Workbench ' -foregroundcolor white -backgroundcolor blue 

        $sp = New-AzureADServicePrincipal -AppId $application.AppId
    }
catch {

        $sp = Get-AzureADServicePrincipal -SearchString  $application.AppId
    }

#################################################################################
#
#                        Create Role Assignment
#################################################################################

$existingRoleAssignment = $false
$ra =""
try {
        Write-Host 'Creating the Admin Role Assignment ' -foregroundcolor white -backgroundcolor blue 

        $ra = New-AzureADUserAppRoleAssignment -Id $appRoleGuid -ObjectId $userId -PrincipalId $userId -ResourceId $sp.ObjectId

    }
catch {

        Write-Host 'Role assignment already exists, skipping... ' -foregroundcolor white -backgroundcolor blue 

        $existingRoleAssignment = $true
        #$ra = Get-AzureADUserAppRoleAssignment -ObjectId $application.AppId
    }



#################################################################################
#
#                        Report Out to The User
#################################################################################

    Write-Host 
    Write-Host 
    if ($existingApp)
{
    Write-Host "You had an existing application named " $AADApplicationDisplayName " with an identifier of " $identifiers.
    Write-Host "A new appplciation was not created"

} else {
    Write-Host "A new application with a display name of " $AADApplicationDisplayName " with an identifier of " $identifiers " was created."
}
 
 
    Write-Host "The following are parameters that you will enter into your marketplace deployment of Azure Blockchain Workbench"
 
   
    Write-Host 
    Write-Host 
    Write-Host "Azure AD Tenant"
    Write-Host "----------------"
    Write-Host "Domain Name - " $AADDomain
    Write-Host 
    Write-Host "Azure AD Client"
    Write-Host "----------------"
    Write-Host "Application ID - " $application.AppId
    Write-Host "Application Key - " $password.Value    
    Write-Host 
    Write-Host 

if ($existingAdmin)
{ 
        
        Write-Host "An Administrator already exists in this domain with a name of " $adminDisplayname" and with with an email address of " $adminemail
}
else
{
        Write-Host "An Administrator user named " $adminDisplayName "with email address of " $adminemail  " was created in your Azure Active Directory."
        Write-Host "Your password is !Password.1 and you will be required to reset it on first log in."

}



if ($existingRoleAssignment) 
{
      Write-Host 
      Write-Host 
      Write-Host "That user was already assigned to the appropriate role. No additional action was required."
}      
else
{
      
      Write-Host 
      Write-Host 
      Write-Host "An Admin user named " $adminDisplayName "with email address of " $adminemail  " was created in your Azure Active Directory."
}



