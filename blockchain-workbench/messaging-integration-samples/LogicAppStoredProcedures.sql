/****** Object:  StoredProcedure [dbo].[LogicAppGetUserChainIdentifierFromEmailAddress]    Script Date: 5/9/2018 10:19:15 PM ******/
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
@WorkflowName NVARCHAR(50),
@EmailAddress NVARCHAR(255)
)
AS
BEGIN


Select Top 1 vw.WorkflowName,cc.ArtifactBlobStorageURL as ContractCodeArtifactBlobStorageURL,cc.LedgerId as ChainId, c.Id as ConnectionId,   (select top 1 ChainIdentifier
  from UserChainMapping ucm inner join
  [User] u on u.Id=ucm.UserID where u.EmailAddress = @EmailAddress) as ChainIdentifier
From 
vwWorkflow vw 
inner join ContractCode cc on vw.ApplicationId = cc.ApplicationId
inner join Connection c on c.LedgerId = cc.LedgerId
where vw.ApplicationName = @ApplicationName and vw.WorkflowName= @WorkflowName
Order By vw.ApplicationUploadedDtTm
END