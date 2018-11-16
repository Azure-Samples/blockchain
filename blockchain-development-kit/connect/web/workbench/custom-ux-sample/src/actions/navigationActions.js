import * as types from './actionTypes';

const saveApplicationId = applicationId => ({
  type: types.SAVE_APPLICATION_ID,
  payload: applicationId,
});

const saveWorkflowId = workflowId => ({
  type: types.SAVE_WORKFLOW_ID,
  payload: workflowId,
});

const saveWorkflowInstanceId = workflowInstanceId => ({
  type: types.SAVE_WORKFLOWINSTANCE_ID,
  payload: workflowInstanceId,
});

export {
  saveApplicationId,
  saveWorkflowId,
  saveWorkflowInstanceId,
};
