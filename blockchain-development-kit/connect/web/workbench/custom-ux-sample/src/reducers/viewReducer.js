import * as types from '../actions/actionTypes';

const initialState = {
  showAdminView: true,
};

export default function (state = initialState, action) {
  switch (action.type) {
    case types.SET_CURRENT_VIEW:
      return {
        ...state,
        showAdminView: action.payload,
      };

    default:
      return state;
  }
}
