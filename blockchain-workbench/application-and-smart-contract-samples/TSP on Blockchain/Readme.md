Team Process Management on Blockchain
====================================================

Overview 
---------

“Working remotely” and “Work from home” are some of the buzzwords in the present day. 
These might have made working easier but at the same time made the task of team management
much difficult. Managing diverse teams and monitoring the progress of each team member has 
presented various new challenges. Hence, we’ve come up with a solution for the same.


![ISD](https://user-images.githubusercontent.com/22838732/65394126-42b5d300-dda7-11e9-8b09-b5dba26ab286.jpeg)

Application Roles 
------------------

| Name       | Description                                                                                         |
|------------|-----------------------------------------------------------------------------------------------------|
| Assigner   |  The person who creates a task and assign it to someone.                                            |
| Reviewer   | The one who reviews whether a task completed by an assignee is up to the mark.                      |         
| Assignee   | The person who is assigned a task by an assigner.                                                   |


States 
-------

| Name                 | Description                                                                                        |
|----------------------|----------------------------------------------------------------------------------------------------|
| Active               |  A task is in active state if it is still being worked upon by an assignee.                        |  | Under Review         | A task is under review when the assignee has completed it and it is being reviewed by a reviewer.  | | Completed            | A task is marked complete if the reviewer verifies and accept it as valid.                         | | Expired              | An expired task is one whose allowed time has finished and it is still incomplete.                 |
                                 
                 

Workflow Details
----------------
Our task manager is a smart-contract for managing team-related tasks. This contract manages a task lifecycle by assigning different states like “Active”, “Under-Review”, “Completed”, or “Expired”. 
Each task is initialized with some reward points and a deadline. 
The deadline, if not followed may incur some penalty points to the assignee. 
A task once completed, will go through a review process by the reviewer specified while creating the task. 
If the task is approved by the reviewer, the assignee gets the reward points, otherwise, his work gets rejected and depending on the deadline, the task either expires or remains active.
In this way, we offer a hassle-free experience for managing team-related tasks in a decentralized and secure way.

