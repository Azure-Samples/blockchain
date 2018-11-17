import * as types from '../actions/actionTypes';
import * as utils from '../common/utils';

const initialState = {
  contractStates: [],
};

export default function dashboardReducer(state = initialState, action) {
  switch (action.type) {
    case types.SET_DASHBOARD:
      return {
        ...state,
        workflowInstance: action.payload,
      };

    case types.SET_CONTRACT_DASHBOARD_CONSTRUCTOR:
      return {
        ...state,
        userFunctionsDictionary: utils.makeDictionary(action.payload.functions),
        contractStates: action.payload.states,
      };

    case types.SET_COLUMN_PROPS:
      return {
        ...state,
        columnsProps: action.payload,
      };

    case types.SET_CURRENT_WEATHER:
      return {
        ...state,
        currentWeather: action.payload,
      };

    case types.CLEAR_CONTRACT_DASHBOARD:
      return initialState;

    default:
      return state;
  }
}
