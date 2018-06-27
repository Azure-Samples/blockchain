<#
.SYNOPSIS

Collects logs and metrics from an Azure Blockchain Workbench instance for
troubleshooting

.DESCRIPTION

Collects logs and metrics from an Azure Blockchain Workbench instance for
troubleshooting. This script will prompt for Azure authentication if you
are not already logged in.

NOTE: If you don't have the latest Azure Powershell installed on your machine,
we recommend that you use the MSI installer to get the latest at
https://github.com/Azure/azure-powershell/releases. Or, run this script using
Azure Cloud shell at https://shell.azure.com.

.PARAMETER SubscriptionID
SubscriptionID to create or locate all resources.

.PARAMETER ResourceGroupName
Name of the Azure Resource Group where Blockchain Workbench has been deployed to.

.PARAMETER OutputDirectory
Path to create the output .ZIP file. If not specified, defaults to the current
directory

.PARAMETER LookbackHours
Number of hours to use when pulling telemetry. Default to 24 hours. Maximum
value is 90 hours.

.PARAMETER OmsSubscriptionId
The subscription id where OMS is deployed. Only pass this parameter if the OMS
for the blockchain network is deployed outside of Blockchain Workbench's resource
group.

.PARAMETER OmsResourceGroup
The resource group where OMS is deployed. Only pass this parameter if the OMS
for the blockchain network is deployed outside of Blockchain Workbench's resource
group.

.PARAMETER OmsWorkspaceName
The OMS workspace name. Only pass this parameter if the OMS for the blockchain
network is deployed outside of Blockchain Workbench's resource group.

.INPUTS

None. You cannot pipe objects to this script.

.OUTPUTS

None. This script does not generate any output. It creates a .ZIP file which 
contains a summary file with a "top errors" report, last timestamp 
information for each Workbench microservice, and recommended actions.
Following that, it creates two subfolders, "details" and "metrics" for futher
troubleshooting.

.EXAMPLE

C:\tmp> .\collectBlockchainWorkbenchTroubleshooting.ps1 -SubscriptionID "<subscription_id>" -ResourceGroupName "<workbench-resource-group-name>"

#>


param(
    [Parameter(Mandatory=$true)][string]$SubscriptionID,
    [Parameter(Mandatory=$true)][string]$ResourceGroupName,
    [Parameter(Mandatory=$false)][string]$OutputDirectory,
    [Parameter(Mandatory=$false)][int]$LookbackHours = 24,
    [Parameter(Mandatory=$false)][string]$OmsSubscriptionId,
    [Parameter(Mandatory=$false)][string]$OmsResourceGroup,
    [Parameter(Mandatory=$false)][string]$OmsWorkspaceName
)

##############################################
## AppInsights query - Helper Functions
##############################################
function Get-AzureRmCachedAccessToken()
{
  $ErrorActionPreference = 'Stop'
  
  if(-not (Get-Module AzureRm.Profile)) {
    Import-Module AzureRm.Profile
  }
  $azureRmProfileModuleVersion = (Get-Module AzureRm.Profile).Version
  # refactoring performed in AzureRm.Profile v3.0 or later
  if($azureRmProfileModuleVersion.Major -ge 3) {
    $azureRmProfile = [Microsoft.Azure.Commands.Common.Authentication.Abstractions.AzureRmProfileProvider]::Instance.Profile
    if(-not $azureRmProfile.Accounts.Count) {
      Write-Error "Ensure you have logged in before calling this function."    
    }
  } else {
    # AzureRm.Profile < v3.0
    $azureRmProfile = [Microsoft.WindowsAzure.Commands.Common.AzureRmProfileProvider]::Instance.Profile
    if(-not $azureRmProfile.Context.Account.Count) {
      Write-Error "Ensure you have logged in before calling this function."    
    }
  }
  
  $currentAzureContext = Get-AzureRmContext
  $profileClient = New-Object Microsoft.Azure.Commands.ResourceManager.Common.RMProfileClient($azureRmProfile)
  Write-Debug ("Getting access token for tenant" + $currentAzureContext.Subscription.TenantId)
  $token = $profileClient.AcquireAccessToken($currentAzureContext.Subscription.TenantId)
  $token.AccessToken
}

function Execute-InsightsQuery([string]$subscription, [string]$resourceGroup, [string]$aiName, [string]$query)
{
    $queryUrlEscaped = [uri]::EscapeDataString($query)
    $requestUrl = "https://management.azure.com/subscriptions/{0}/resourceGroups/{1}/providers/microsoft.insights/components/{2}/api/query?query={3}&api-version=2015-05-01" -f $subscription, $resourceGroup, $aiName, $queryUrlEscaped
    $bearer = Get-AzureRmCachedAccessToken
    Invoke-RestMethod -Uri $requestUrl -Headers @{'Authorization' = 'Bearer ' + $bearer }
}

function RestQueryResultsToObjectView($restResponse){
    $table = $restResponse.Tables[0]

    $i = 0;
    $objectView = New-Object object[] $table.Rows.Count
    foreach ($row in $table.Rows) 
    {
        $properties = @{}
        for ($columnNum=0; $columnNum -lt $table.Columns.Count; $columnNum++) {
            $properties[$table.Columns[$columnNum].ColumnName] = $row[$columnNum]
        }
        $objectView[$i] = (New-Object PSObject -Property $properties)
        $null = $i++
    }
    $objectView
}

function ZipFiles( $zipfilename, $sourcedir )
{
   Add-Type -Assembly System.IO.Compression.FileSystem
   $compressionLevel = [System.IO.Compression.CompressionLevel]::Optimal
   [System.IO.Compression.ZipFile]::CreateFromDirectory($sourcedir,
        $zipfilename, $compressionLevel, $false)
}

##############################################
## Log anaylitcs query - Helper Functions
##############################################
$apiVersion = "2017-01-01-preview"

<#
    .DESCRIPTION
        Invokes a query against the Log Analtyics Query API.

    .EXAMPLE
        Invoke-LogAnaltyicsQuery -WorkspaceName my-workspace -SubscriptionId 0f991b9d-ab0e-4827-9cc7-984d7319017d -ResourceGroup my-resourcegroup
            -Query "union * | limit 1" -CreateObjectView

    .PARAMETER WorkspaceName
        The name of the Workspace to query against.

    .PARAMETER SubscriptionId
        The ID of the Subscription this Workspace belongs to.

    .PARAMETER ResourceGroup
        The name of the Resource Group this Workspace belongs to.

    .PARAMETER Query
        The query to execute.
    
    .PARAMETER Timespan
        The timespan to execute the query against. This should be an ISO 8601 timespan.

    .PARAMETER IncludeTabularView
        If specified, the raw tabular view from the API will be included in the response.

    .PARAMETER IncludeStatistics
        If specified, query statistics will be included in the response.

    .PARAMETER IncludeRender
        If specified, rendering statistics will be included (useful when querying metrics).

    .PARAMETER ServerTimeout
        Specifies the amount of time (in seconds) for the server to wait while executing the query.

    .PARAMETER Environment
        Internal use only.
#>
function Invoke-LogAnalyticsQuery {
param(
    [string]
    [Parameter(Mandatory=$true)]
    $WorkspaceName,

    [guid]
    [Parameter(Mandatory=$true)]
    $SubscriptionId,

    [string]
    [Parameter(Mandatory=$true)]
    $ResourceGroup,

    [string]
    [Parameter(Mandatory=$true)]
    $Query,

    [string]
    $Timespan,

    [switch]
    $IncludeTabularView,

    [switch]
    $IncludeStatistics,

    [switch]
    $IncludeRender,

    [int]
    $ServerTimeout,

    [string]
    [ValidateSet("", "int", "aimon")]
    $Environment = ""
    )

    $ErrorActionPreference = "Stop"

    $accessToken = GetAccessToken

    $armhost = GetArmHost $environment

    $queryParams = @("api-version=$apiVersion")

    $queryParamString = [string]::Join("&", $queryParams)

    $uri = BuildUri $armHost $subscriptionId $resourceGroup $workspaceName $queryParamString

    $body = @{
        "query" = $query;
        "timespan" = $Timespan
    } | ConvertTo-Json

    $headers = GetHeaders $accessToken -IncludeStatistics:$IncludeStatistics -IncludeRender:$IncludeRender -ServerTimeout $ServerTimeout

    $response = Invoke-WebRequest -UseBasicParsing -Uri $uri -Body $body -ContentType "application/json" -Headers $headers -Method Post

    if ($response.StatusCode -ne 200 -and $response.StatusCode -ne 204) {
        $statusCode = $response.StatusCode
        $reasonPhrase = $response.StatusDescription
        $message = $response.Content
        throw "Failed to execute query.`nStatus Code: $statusCode`nReason: $reasonPhrase`nMessage: $message"
    }

    $data = $response.Content | ConvertFrom-Json

    $result = New-Object PSObject
    $result | Add-Member -MemberType NoteProperty -Name Response -Value $response

    # In this case, we only need the response member set and we can bail out
    if ($response.StatusCode -eq 204) {
        $result
        return
    }

    $objectView = CreateObjectView $data

    $result | Add-Member -MemberType NoteProperty -Name Results -Value $objectView

    if ($IncludeTabularView) {
        $result | Add-Member -MemberType NoteProperty -Name Tables -Value $data.tables
    }

    if ($IncludeStatistics) {
        $result | Add-Member -MemberType NoteProperty -Name Statistics -Value $data.statistics
    }

    if ($IncludeRender) {
        $result | Add-Member -MemberType NoteProperty -Name Render -Value $data.render
    }

    $result
}

function GetAccessToken {
    $azureCmdlet = get-command -Name Get-AzureRMContext -ErrorAction SilentlyContinue
    if ($azureCmdlet -eq $null)
    {
        $null = Import-Module AzureRM -ErrorAction Stop;
    }
    $AzureContext = & "Get-AzureRmContext" -ErrorAction Stop;
    $authenticationFactory = New-Object -TypeName Microsoft.Azure.Commands.Common.Authentication.Factories.AuthenticationFactory
    if ((Get-Variable -Name PSEdition -ErrorAction Ignore) -and ('Core' -eq $PSEdition)) {
        [Action[string]]$stringAction = {param($s)}
        $serviceCredentials = $authenticationFactory.GetServiceClientCredentials($AzureContext, $stringAction)
    } else {
        $serviceCredentials = $authenticationFactory.GetServiceClientCredentials($AzureContext)
    }

    # We can't get a token directly from the service credentials. Instead, we need to make a dummy message which we will ask
    # the serviceCredentials to add an auth token to, then we can take the token from this message.
    $message = New-Object System.Net.Http.HttpRequestMessage -ArgumentList @([System.Net.Http.HttpMethod]::Get, "http://foobar/")
    $cancellationToken = New-Object System.Threading.CancellationToken
    $null = $serviceCredentials.ProcessHttpRequestAsync($message, $cancellationToken).GetAwaiter().GetResult()
    $accessToken = $message.Headers.GetValues("Authorization").Split(" ")[1] # This comes out in the form "Bearer <token>"

    $accessToken
}

function GetArmHost {
param(
    [string]
    $environment
    )

    switch ($environment) {
        "" {
            $armHost = "management.azure.com"
        }
        "aimon" {
            $armHost = "management.azure.com"
        }
        "int" {
            $armHost = "api-dogfood.resources.windows-int.net"
        }
    }

    $armHost
}

function BuildUri {
param(
    [string]
    $armHost,
    
    [string]
    $subscriptionId,

    [string]
    $resourceGroup,

    [string]
    $workspaceName,

    [string]
    $queryParams
    )

    "https://$armHost/subscriptions/$subscriptionId/resourceGroups/$resourceGroup/providers/" + `
        "microsoft.operationalinsights/workspaces/$workspaceName/api/query?$queryParamString"
}

function GetHeaders {
param(
    [string]
    $AccessToken,

    [switch]
    $IncludeStatistics,

    [switch]
    $IncludeRender,

    [int]
    $ServerTimeout
    )

    $preferString = "response-v1=true"

    if ($IncludeStatistics) {
        $preferString += ",include-statistics=true"
    }

    if ($IncludeRender) {
        $preferString += ",include-render=true"
    }

    if ($ServerTimeout -ne $null) {
        $preferString += ",wait=$ServerTimeout"
    }

    $headers = @{
        "Authorization" = "Bearer $accessToken";
        "prefer" = $preferString;
        "x-ms-app" = "LogAnalyticsQuery.psm1";
        "x-ms-client-request-id" = [Guid]::NewGuid().ToString();
    }

    $headers
}

function CreateObjectView {
param(
    $data
    )

    # Find the number of entries we'll need in this array
    $count = 0
    foreach ($table in $data.Tables) {
        $count += $table.Rows.Count
    }

    $objectView = New-Object object[] $count
    $i = 0;
    foreach ($table in $data.Tables) {
        foreach ($row in $table.Rows) {
            # Create a dictionary of properties
            $properties = @{}
            for ($columnNum=0; $columnNum -lt $table.Columns.Count; $columnNum++) {
                $properties[$table.Columns[$columnNum].name] = $row[$columnNum]
            }
            # Then create a PSObject from it. This seems to be *much* faster than using Add-Member
            $objectView[$i] = (New-Object PSObject -Property $properties)
            $null = $i++
        }
    }

    $objectView
}

$logId = 0

#############################################
#  Script Initialization
#############################################

Write-Progress -Id $logId -Activity "Login & Setup" -Status "Login to Azure" -PercentComplete 0

if (($LookbackHours -lt 1) -or ($LookbackHours -gt 90))
{
    throw "Please limit the number of hours of telemetry to pull to a value between 1 and 90."
}
$LookbackHoursStr = "$LookbackHours" + "h"

if ((Get-Command "Login-AzureRmAccount" -errorAction SilentlyContinue) -eq $null)
{
    throw "Azure Powershell cmdlets were not detected. We recommend that you follow the instructions on
    https://www.powershellgallery.com/packages/AzureRM/6.0.1 to obtain the latest version. Or, you can run 
    this script using Azure Cloud shell at https://shell.azure.com/powershell"
}

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


$rg = Get-AzureRmResourceGroup -Name $ResourceGroupName
if ($rg -eq $null)
{
    throw "We couldn't locate the resource group $ResourceGroupName. Please check the name and try again"
}

# Locate Resources in Blockchain Workbench deployment

$appInsightsResource = Get-AzureRmApplicationInsights -ResourceGroupName $ResourceGroupName -ErrorAction SilentlyContinue
if ($appInsightsResource -eq $null)
{
    throw "Could not locate App Insights within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}
$appInsightsResource = $appInsightsResource[0] # Select the App Insights

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

$serviceBus = Get-AzureRmServiceBusNamespace -ResourceGroupName $ResourceGroupName -ErrorAction SilentlyContinue
if ($serviceBus -eq $null)
{
    throw "Could not locate Service Bus within the resource group $ResourceGroupName. Is this a Blockchain Workbench deployment?"
}
$serviceBus = $serviceBus[0]

$logAnalytics = Get-AzureRmResource -ResourceGroupName $ResourceGroupName -ResourceType "Microsoft.OperationalInsights/workspaces" -ErrorAction SilentlyContinue
if ($logAnalytics -ne $null)
{
    $logAnalytics = $logAnalytics[0]
    $OmsSubscriptionId = $SubscriptionID
    $OmsResourceGroup = $ResourceGroupName
    $OmsWorkspaceName = $logAnalytics.Name
}
else
{
    # Fall back to manually specified OMS if it's not in the same resource group
    if ($OmsSubscriptionId -ne $null -and $OmsResourceGroup -ne $null -and $OmsWorkspaceName -ne $null)
    {
        Set-Azurermcontext -SubscriptionId $OmsSubscriptionId
        $logAnalytics = Get-AzureRmResource -ResourceGroupName $OmsResourceGroup -ResourceName $OmsWorkspaceName -ResourceType "Microsoft.OperationalInsights/workspaces" -ErrorAction SilentlyContinue
        if ($logAnalytics -ne $null)
        {
            $logAnalytics = $logAnalytics[0]
        }
        Set-AzureRmContext -SubscriptionId $SubscriptionID
    }
}

Write-Progress -Id $logId -Activity "Login & Setup" -Status "Login to Azure" -PercentComplete 100


#############################################
#  Create Temporary Directory
#############################################
$logId++

Write-Progress -Id $logId -Activity "Create Temporary Directory" -Status "Creating Temporary Directories" -PercentComplete 0

$tempPath = [System.IO.Path]::GetTempPath()
$guidDir = [System.Guid]::NewGuid()
$outputPath = Join-Path $tempPath $guidDir
New-Item -ItemType Directory -Path $outputPath | Out-Null
New-Item -ItemType Directory -Path $outputPath\details\blockchain | Out-Null
New-Item -ItemType Directory -Path $outputPath\details\workbench | Out-Null
New-Item -ItemType Directory -Path $outputPath\metrics\workbench | Out-Null
New-Item -ItemType Directory -Path $outputPath\metrics\blockchain | Out-Null

if ([string]::IsNullOrEmpty($OutputDirectory))
{
    $OutputDirectory = (Get-Item -Path ".\" -Verbose).FullName
}

Write-Progress -Id $logId -Activity "Create Temporary Directory" -Status "Created Temporary Directories" -PercentComplete 100

#############################################
#  Create Summary File
#############################################
$logId++
Write-Progress -Id $logId -Activity "Summary Report" -Status "Generating summary report" -PercentComplete 0

$summaryFile = Join-Path $outputPath "summary.txt"

$lastEvent = Execute-InsightsQuery -subscription $SubscriptionID -resourceGroup $ResourceGroupName -aiName $appInsightsResource.Name -query "traces
| where timestamp > ago($LookbackHoursStr)
| evaluate bag_unpack(customDimensions)
| summarize LastLog = max(timestamp) by ServiceName"

$topErrors = Execute-InsightsQuery -subscription $SubscriptionID -resourceGroup $ResourceGroupName -aiName $appInsightsResource.Name -query "exceptions
| where timestamp > ago($LookbackHoursStr)
| reduce by outerMessage"

Add-Content $summaryFile "Summary Report Output" 
Add-Content $summaryFile "====================="
Add-Content $summaryFile ""

$runningServices = RestQueryResultsToObjectView($lastEvent) | select ServiceName | foreach { $_.ServiceName }
$services = @("sql-consumer", "dlt-consumer", "appbuilder.api", "dlt-native-api", "key-service", "dlt-api", "dlt-watcher", "telemetry-collector" )

Add-Content $summaryFile "[1] Recommended Actions"
if($services.Count -ne $runningServices.Count)
{
    Add-Content $summaryFile (" * Did not detect heartbeat from the following services: " + ((Compare-Object $runningServices $services) | select -ExpandProperty InputObject) -join ",")
}
Add-Content $summaryFile ""

Add-Content $summaryFile "[2] Last Telemetry Timestamp, Per Component"
RestQueryResultsToObjectView($lastEvent) | format-table | out-file $summaryFile -Append utf8
Add-Content $summaryFile ""
Add-Content $summaryFile ""

Add-Content $summaryFile "[3] Top Errors, Last 18 hours"
RestQueryResultsToObjectView($topErrors) | format-table -AutoSize | out-file $summaryFile -Append utf8
Add-Content $summaryFile ""
Add-Content $summaryFile ""

Write-Progress -Id $logId -Activity "Summary reporting" -Status "Generated summary report" -PercentComplete 100

#############################################
#  Collect Exceptions
#############################################
$logId++
Write-Progress -Id $logId -Activity "Workbench Log Collection" -Status "Collecting Exceptions" -PercentComplete 0
$exceptionsPath = Join-Path $outputPath "\details\workbench\exceptions.csv"

$lastExceptions = Execute-InsightsQuery -subscription $SubscriptionID -resourceGroup $ResourceGroupName -aiName $appInsightsResource.Name -query "exceptions
| order by timestamp desc
| take 500"

RestQueryResultsToObjectView($lastExceptions) | ConvertTo-Csv | out-file $exceptionsPath -Append utf8

#############################################
#  Collect API Metrics
#############################################
Write-Progress -Id $logId -Activity "Workbench Log Collection" -Status "Collecting API Metrics" -PercentComplete 25
$apiMetrics = Join-Path $outputPath "\metrics\workbench\apiMetrics.txt"

$http5xx = Get-AzureRmMetric -ResourceId $apiWebsite.Id -MetricName Http5xx -StartTime (get-date).AddHours(0 - $LookbackHours) -TimeGrain 01:00:00 -WarningAction Ignore
$http4xx = Get-AzureRmMetric -ResourceId $apiWebsite.Id -MetricName Http4xx -StartTime (get-date).AddHours(0 - $LookbackHours) -TimeGrain 01:00:00 -WarningAction Ignore
$http3xx = Get-AzureRmMetric -ResourceId $apiWebsite.Id -MetricName Http3xx -StartTime (get-date).AddHours(0 - $LookbackHours) -TimeGrain 01:00:00 -WarningAction Ignore
$http2xx = Get-AzureRmMetric -ResourceId $apiWebsite.Id -MetricName Http2xx -StartTime (get-date).AddHours(0 - $LookbackHours) -TimeGrain 01:00:00 -WarningAction Ignore
$requests = Get-AzureRmMetric -ResourceId $apiWebsite.Id -MetricName Requests -StartTime (get-date).AddHours(0 - $LookbackHours) -TimeGrain 01:00:00  -WarningAction Ignore
$avgRespTime = Get-AzureRmMetric -ResourceId $apiWebsite.Id -MetricName AverageResponseTime -StartTime (get-date).AddHours(0 - $LookbackHours) -TimeGrain 00:01:00 -WarningAction Ignore

Add-Content $apiMetrics "Web API Metrics" 
Add-Content $apiMetrics "====================="
Add-Content $apiMetrics ""
Add-Content $apiMetrics "[1] Requests 5xx (total/hour)"
$http5xx.Data | format-table | out-file $apiMetrics -Append utf8
Add-Content $apiMetrics ""

Add-Content $apiMetrics "[2] Requests 4xx (total/hour)"
$http4xx.Data | format-table | out-file $apiMetrics -Append utf8
Add-Content $apiMetrics ""

Add-Content $apiMetrics "[3] Requests 3xx (total/hour)"
$http3xx.Data | format-table | out-file $apiMetrics -Append utf8
Add-Content $apiMetrics ""

Add-Content $apiMetrics "[4] Requests 2xx (total/hour)"
$http2xx.Data | format-table | out-file $apiMetrics -Append utf8
Add-Content $apiMetrics ""

Add-Content $apiMetrics "[5] Requests - Total (total/hour)"
$requests.Data | format-table | out-file $apiMetrics -Append utf8
Add-Content $apiMetrics ""

Add-Content $apiMetrics "[6] Average Response Time (avg/min)"
$avgRespTime.Data | format-table | out-file $apiMetrics -Append utf8
Add-Content $apiMetrics ""

#############################################
#  Collect Service Bus Metrics
#############################################
Write-Progress -Id $logId -Activity "Workbench Log Collection" -Status "Collecting Service Bus Metrics" -PercentComplete 45
$sbMetrics = Join-Path $outputPath "\metrics\workbench\sbMetrics.txt"

$incomingMessages = Get-AzureRmMetric -ResourceId $serviceBus.Id -MetricName IncomingMessages -StartTime (get-date).AddHours(0 - $LookbackHours) -TimeGrain 01:00:00 -WarningAction Ignore
$outgoingMeesages = Get-AzureRmMetric -ResourceId $serviceBus.Id  -MetricName OutgoingMessages -StartTime (get-date).AddHours(0 - $LookbackHours) -TimeGrain 01:00:00 -WarningAction Ignore
$serverErrors = Get-AzureRmMetric -ResourceId $serviceBus.Id  -MetricName ServerErrors -StartTime (get-date).AddHours(0 - $LookbackHours) -TimeGrain 01:00:00 -WarningAction Ignore
$requests = Get-AzureRmMetric -ResourceId $serviceBus.Id  -MetricName IncomingRequests -StartTime (get-date).AddHours(0 - $LookbackHours) -TimeGrain 01:00:00  -WarningAction Ignore

Add-Content $sbMetrics "Service Bus Metrics" 
Add-Content $sbMetrics "====================="
Add-Content $sbMetrics ""
Add-Content $sbMetrics "[1] Incoming Messages"
$incomingMessages.Data | format-table | out-file $sbMetrics -Append utf8

Add-Content $sbMetrics "[2] Outgoing Messages"
$outgoingMeesages.Data | format-table | out-file $sbMetrics -Append utf8

Add-Content $sbMetrics "[3] Server Errors"
$serverErrors.Data | format-table | out-file $sbMetrics -Append utf8

Add-Content $sbMetrics "[4] Incoming Requests"
$requests.Data | format-table | out-file $sbMetrics -Append utf8

#############################################
#  Collect Availability Results
#############################################
Write-Progress -Id $logId -Activity "Workbench Log Collection" -Status "Collecting Availability Metrics" -PercentComplete 55
$availabilityTest = Join-Path $outputPath "\details\workbench\availabilityTest.csv"

$availability = Execute-InsightsQuery -subscription $SubscriptionID -resourceGroup $ResourceGroupName -aiName $appInsightsResource.Name -query "availabilityResults
| where timestamp > ago($LookbackHoursStr)"

RestQueryResultsToObjectView($availability) | ConvertTo-Csv | out-file $availabilityTest -Append utf8

#############################################
#  Collect microservices logs
#############################################

Write-Progress -Id $logId -Activity "Workbench Log Collection" -Status "Collecting Services Logs" -PercentComplete 75

foreach ($service in $services)
{
    $microserviceFile = Join-Path $outputPath "\details\workbench\servicelogs_$service.csv"

    $logs = Execute-InsightsQuery -subscription $SubscriptionID -resourceGroup $ResourceGroupName -aiName $appInsightsResource.Name -query "traces
    | where timestamp > ago($LookbackHoursStr)
    | where customDimensions contains '$service'
    | take 1500"

    RestQueryResultsToObjectView($logs) | ConvertTo-Csv | out-file $microserviceFile -Append utf8
}

Write-Progress -Id $logId -Activity "Workbench Log Collection" -Status "Collected Workbench logs" -PercentComplete 100

if ($logAnalytics -ne $null)
{
    $logId++
    Write-Progress -Id $logId -Activity "Blockchain Network Log Collection" -Status "Collecting Blockchain Network Logs" -PercentComplete 0

    #############################################
    #  Collect Blockchain network - Mined Transactions
    #############################################
    $networkMinedTxns = Join-Path $outputPath "\details\blockchain\network_mined_transaction.csv"

    $networkMinedTxnsResult =  Invoke-LogAnalyticsQuery -SubscriptionId $OmsSubscriptionId -ResourceGroup $logAnalytics.ResourceGroupName -WorkspaceName $logAnalytics.Name -Query "MinedTransaction_CL
    | take 500"

    $networkMinedTxnsResult.Results | ConvertTo-Csv | out-file $networkMinedTxns -Append utf8

    Write-Progress -Id $logId -Activity "Blockchain Network Log Collection" -Status "Collecting Blockchain Network Logs" -PercentComplete 25

    #############################################
    #  Collect Blockchain network - Pending Transactions
    #############################################
    $networkPendingTxns = Join-Path $outputPath "\details\blockchain\blockchainnetwork_pending_transaction.csv"

    $networkpendingTxnsResult = Invoke-LogAnalyticsQuery -SubscriptionId $OmsSubscriptionId -ResourceGroup $logAnalytics.ResourceGroupName -WorkspaceName $logAnalytics.Name -Query "PendingTransaction_CL
    | take 500"

    $networkpendingTxnsResult.Results | ConvertTo-Csv | out-file $networkPendingTxns -Append utf8

    Write-Progress -Id $logId -Activity "Blockchain Network Log Collection" -Status "Collecting Blockchain Network Logs" -PercentComplete 50

    #############################################
    #  Collect Blockchain network - Mined Blocks
    #############################################
    $networkMinedBlocks = Join-Path $outputPath "\details\blockchain\blockchainnetwork_mined_blocks.csv"

    $networkMinedblocksResult =  Invoke-LogAnalyticsQuery -SubscriptionId $OmsSubscriptionId -ResourceGroup $logAnalytics.ResourceGroupName -WorkspaceName $logAnalytics.Name -Query "MinedBlock_CL
    | take 500"

    $networkMinedblocksResult.Results | ConvertTo-Csv | out-file $networkMinedBlocks -Append utf8

    Write-Progress -Id $logId -Activity "Blockchain Network Log Collection" -Status "Collecting Blockchain Network Logs" -PercentComplete 75

    #############################################
    #  Collect Blockchain network - Perf Counters
    #############################################
    $networkPerf = Join-Path $outputPath "\metrics\blockchain\network_perf_counters.csv"

    $networkPerfResult =  Invoke-LogAnalyticsQuery -SubscriptionId $OmsSubscriptionId -ResourceGroup $logAnalytics.ResourceGroupName -WorkspaceName $logAnalytics.Name -Query "Perf
    | where TimeGenerated  > ago($LookbackHoursStr)
    | summarize min(CounterValue), max(CounterValue), avg(CounterValue), percentiles_array(CounterValue, 50, 90, 95), stdev(CounterValue)  by Computer, CounterPath, bin(TimeGenerated, 5m)
    | order by Computer, CounterPath, TimeGenerated"

    Write-Progress -Id $logId -Activity "Blockchain Network Log Collection" -Status "Collecting Blockchain Network Logs" -PercentComplete 100
}

#############################################
#  Create final zip
#############################################
$timestamp = Get-Date -Format o | foreach {$_ -replace ":", "."}
$finalPath = Join-Path $OutputDirectory "\workbench-logs-$timestamp.zip"
ZipFiles $finalPath $outputPath

Write-Output "Troubleshooting logs succesfully written to: $finalPath"