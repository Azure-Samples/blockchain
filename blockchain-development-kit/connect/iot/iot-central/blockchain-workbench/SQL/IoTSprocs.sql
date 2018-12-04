

-- =============================================
-- Author:      Marc Mercuri
-- Create Date: May 4, 2018
-- Description: <Returns device-specific contract info>
-- =============================================
CREATE PROCEDURE [dbo].[GetContractInfoForDeviceId]
(
    @DeviceID NVARCHAR(255)
)
AS
BEGIN

    SET NOCOUNT ON

	DECLARE @ErrorMessage NVARCHAR(255)
	DECLARE @ContractId INT
	DECLARE @WorkflowFunctionId INT
	DECLARE @ConnectionId INT
	DECLARE @ContractCodeBlobStorageURL NVARCHAR(255)
	DECLARE @UserChainIdentifier NVARCHAR(255)
	DECLARE @ContractLedgerIdentifier NVARCHAR(255)
	DECLARE @WorkflowFunctionName NVARCHAR(50)
	DECLARE @WorkflowName NVARCHAR(50)
	DECLARE @WorkflowId INT
	DECLARE @IngestTelemetry_ContractStateActionId INT
	DECLARE @IngestTelemetry_WorkflowFunctionId INT
	DECLARE @IngestTelemetry_ApplicationRoleId INT
	DECLARE @IngestTelemetry_Humidity_WorkflowFunctionParameterId INT
	DECLARE @IngestTelemetry_Temperature_WorkflowFunctionParameterId INT
	DECLARE @IngestTelemetry_Timestamp_WorkflowFunctionParameterId INT

	IF @DeviceID IS NULL OR LEN(@DeviceID) = 0
		BEGIN
			SET @Errormessage = '@DeviceID cannot be null.'
			RAISERROR(@ErrorMessage, 11, 1)
		END
	
	IF OBJECT_ID('tempdb..#TEMP') IS NOT NULL DROP TABLE #TEMP

	SELECT DISTINCT
		c.Id as ContractId,
		c.ConnectionId as ConnectionId,
		c.LedgerIdentifier as ContractLedgerIdentifier,
		c.Timestamp as Timestamp,
		DeviceRole.ContractCodeBlobStorageUrl as ContractCodeBlobStorageUrl,
		DeviceRole.UserChainIdentifier as UserChainIdentifier,
		DeviceRole.WorkflowName as WorkflowName, 
		DeviceRole.WorkflowId as WorkflowId,
		DeviceRole.WorkflowFunctionName as WorkflowFunctionName,
		DeviceRole.WorkflowFunctionId as WorkflowFunctionId,
		DeviceRole.ApplicationRoleId as ApplicationRoleId,
		DeviceRole.ApplicationId as ApplicationId,
		DeviceRole.RoleAssignmentId as RoleAssignmentID

	INTO #TEMP
	From 
		[Contract] c inner join 
		(select u.Id as UserID, u.ExternalId as UserExternalId, ucm.ChainIdentifier as UserChainIdentifier, a.Id as ApplicationId, w.Id as WorkflowId, w.Name as WorkflowName, wf.Id as WorkflowFunctionId, wf.Name as WorkflowFunctionName, ar.Id as ApplicationRoleId, ar.Name as ApplicationRoleName, ra.Id as RoleAssignmentId, cc.ArtifactBlobStorageURL as ContractCodeBlobStorageUrl
		from [Application] a
			inner join workflow w on w.ApplicationId = a.Id
			inner join WorkflowFunction wf on w.Id=wf.WorkflowId and wf.Name = 'IngestTelemetry'
			inner join ApplicationRole ar on a.Id = ar.ApplicationId
			inner join RoleAssignment ra on ar.Id = ra.ApplicationRoleId
			Inner Join [User] u on ra.UserId = u.Id
			Inner Join UserChainMapping ucm on u.Id = ucm.UserID
			Inner Join ContractCode cc on cc.ApplicationId = ar.ApplicationId
			where UPPER(ar.Name) = 'DEVICE'and u.ExternalId =  @DeviceID
		)  DeviceRole on c.WorkflowId = DeviceRole.WorkflowId

		

	SET @ContractId = (SELECT TOP 1 ContractId FROM #TEMP Order By Timestamp DESC)
	SET @WorkflowFunctionId = (SELECT TOP 1 WorkflowFunctionId FROM #TEMP Order By Timestamp DESC)
	SET @ConnectionId = (SELECT TOP 1 ConnectionId FROM #TEMP Order By Timestamp DESC)
	SET @ContractCodeBlobStorageURL = (SELECT TOP 1 ContractCodeBlobStorageURL FROM #TEMP Order By Timestamp DESC)
	SET @UserChainIdentifier = (SELECT TOP 1 UserChainIdentifier FROM #TEMP Order By Timestamp DESC)
	SET @ContractLedgerIdentifier = (SELECT TOP 1 ContractLedgerIdentifier FROM #TEMP Order By Timestamp DESC)
	SET @WorkflowFunctionName = (SELECT TOP 1 WorkflowFunctionName FROM #TEMP Order By Timestamp DESC)
	SET @WorkflowName = (SELECT TOP 1 WorkflowName FROM #TEMP Order By Timestamp DESC)
	SET @IngestTelemetry_WorkflowFunctionId = (SELECT Top 1 WorkflowFunctionId from #TEMP Order by Timestamp DESC)
	SET @IngestTelemetry_Humidity_WorkflowFunctionParameterId = (SELECT TOP 1 wfp.[ID] FROM [dbo].[WorkflowFunctionParameter] wfp WHERE wfp.[WorkflowFunctionId] = @IngestTelemetry_WorkflowFunctionId AND wfp.[Name] = 'humidity')
	SET @IngestTelemetry_Temperature_WorkflowFunctionParameterId = (SELECT TOP 1 wfp.[ID] FROM [dbo].[WorkflowFunctionParameter] wfp WHERE wfp.[WorkflowFunctionId] = @IngestTelemetry_WorkflowFunctionId AND wfp.[Name] = 'temperature')
	SET @IngestTelemetry_Timestamp_WorkflowFunctionParameterId = (SELECT TOP 1 wfp.[ID] FROM [dbo].[WorkflowFunctionParameter] wfp WHERE wfp.[WorkflowFunctionId] = @IngestTelemetry_WorkflowFunctionId AND wfp.[Name] = 'timestamp')
    
	SELECT 
		"ContractId" = @ContractId, 
		"WorkflowFunctionId" = @WorkflowFunctionId, 
		"ConnectionId" = @ConnectionId, 
		"ContractLedgerIdentifier" = @ContractLedgerIdentifier, 
		"ContractCodeBlobStorageUrl" = @ContractCodeBlobStorageURL, 
		"UserChainIdentifier" = @UserChainIdentifier, 
		"WorkflowFunctionName" = @WorkflowFunctionName,
		"WorkflowName" = @WorkflowName,
		"IngestTelemetry_ContractWorkflowFunctionID" = @IngestTelemetry_WorkflowFunctionId, 
		"IngestTelemetry_ContractPersonaID" = @IngestTelemetry_ApplicationRoleId, 
		"IngestTelemetry_Humidity_WorkflowFunctionParameterID" = @IngestTelemetry_Humidity_WorkflowFunctionParameterId, 
		"IngestTelemetry_Temperature_WorkflowFunctionParameterID" = @IngestTelemetry_Temperature_WorkflowFunctionParameterId, 
		"IngestTelemetry_Timestamp_WorkflowFunctionParameterID" = @IngestTelemetry_Timestamp_WorkflowFunctionParameterId
		


END
GO


