import * as types from '../actions/actionTypes';

const initialState = {
  applications: [],
  top: types.APPLICATIONS_DEFAULT_TOP,
  skip: types.APPLICATIONS_DEFAULT_SKIP,
  showEnabledApplications: true,
};

export default function applicationsReducer(state = initialState, action) {
  switch (action.type) {
    case types.SET_APPLICATIONS_DATA:
      return {
        ...state,
        applications: action.payload,
      };

    default:
      return state;
  }
}
