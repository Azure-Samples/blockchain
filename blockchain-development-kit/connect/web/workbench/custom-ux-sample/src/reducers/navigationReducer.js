import * as types from '../actions/actionTypes';

const initialState = {};

export default function navigationReducer(state = initialState, action) {
  switch (action.type) {
    case types.SAVE_APPLICATION_ID:
      return {
        ...state,
        applicationId: action.payload,
      };

    case types.SAVE_WORKFLOW_ID:
      return {
        ...state,
        workflowId: action.payload,
      };

    case types.SAVE_WORKFLOWINSTANCE_ID:
      return {
        ...state,
        workflowInstanceId: action.payload,
      };

    default:
      return state;
  }
}
