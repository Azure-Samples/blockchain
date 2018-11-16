import * as types from '../actions/actionTypes';

const initialState = {
  top: types.CONTRACTS_DEFAULT_TOP,
  skip: types.CONTRACTS_DEFAULT_SKIP,
  contracts: {},
};

export default function (state = initialState, action) {
  switch (action.type) {
    case types.SET_WORKFLOW_CONTRACTS:
      return {
        ...state,
        contracts: action.payload,
      };

    default:
      return state;
  }
}
