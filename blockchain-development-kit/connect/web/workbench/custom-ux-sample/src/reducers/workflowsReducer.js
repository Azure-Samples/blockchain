import * as types from '../actions/actionTypes';

const initialState = {
  top: types.WORKFLOWS_DEFAULT_TOP,
  skip: types.WORKFLOWS_DEFAULT_SKIP,
};

export default function (state = initialState, action) {
  switch (action.type) {
    case types.SET_LOADING_WORKFLOWS:
      return {
        ...state,
        isLoadingWorkflows: action.payload,
      };

    case types.SET_ERROR_WORKFLOWS:
      return {
        ...state,
        hasErrored: action.payload,
      };

    case types.SET_WORKFLOWS:
      return {
        ...state,
        workflows: action.payload,
      };
    default:
      return state;
  }
}
