SET ANSI_NULLS ON
GO

SET QUOTED_IDENTIFIER ON
GO


CREATE PROCEDURE [dbo].[LogicAppGetUserChainIdentifierFromEmailAddress]
(
@UserEmailAddress NVARCHAR(255)
)
AS
BEGIN

Select ucm.ChainIdentifier from
[vwUser] u inner join UserChainMapping ucm on u.Id = ucm.ID	
Where u.EmailAddress = @UserEmailAddress

END

GO

CREATE PROCEDURE [dbo].[LogicAppGetUserChainIdentifierFromName]
(
@FirstName NVARCHAR(256),
@LastName NVARCHAR(64)
)
AS
BEGIN

Select ucm.ChainIdentifier from
[vwUser] u inner join UserChainMapping ucm on u.Id = ucm.ID	
Where u.FirstName = @FirstName and u.LastName = @LastName

END

Go

CREATE PROCEDURE [dbo].[LogicAppGetContractCreationDetails]
(
@ApplicationName NVARCHAR(50),
@WorkflowName NVARCHAR(50)
)
AS
BEGIN


Select Top 1 vw.WorkflowName,cc.ArtifactBlobStorageURL as ContractCodeArtifactBlobStorageURL,cc.LedgerId as ChainId, c.Id as ConnectionId
From 
vwWorkflow vw 
inner join ContractCode cc on vw.ApplicationId = cc.ApplicationId
inner join Connection c on c.LedgerId = cc.LedgerId
where vw.ApplicationName = @ApplicationName and vw.WorkflowName= @WorkflowName
Order By vw.ApplicationUploadedDtTm
