import * as types from '../actions/actionTypes';

const initialState = {
  canUploadApplication: false,
  canUploadContractCode: false,
  canModifyRoleAssignments: false,
  canProvisionUser: false,
  canCreateWorkflowInstance: false,
  isLoadingMeCapabilities: true,
};

export default function capabilitiesReducer(state = initialState, action) {
  switch (action.type) {
    case types.GET_CAPABILITIES:
      return {
        ...state,
        ...action,
      };

    case types.SET_LOADING_ME_CAPABILITIES: {
      return {
        ...state,
        isLoadingMeCapabilities: action.payload,
      };
    }

    default:
      return state;
  }
}
