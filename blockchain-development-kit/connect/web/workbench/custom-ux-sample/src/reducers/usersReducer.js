import * as types from '../actions/actionTypes';

const initialState = {};

export default function usersReducer(state = initialState, action) {
  switch (action.type) {
    case types.GET_ME:
      return {
        ...state,
        me: action.me,
      };

    case types.SET_USERS:
      return {
        ...state,
        users: action.payload,
      };

    default:
      return state;
  }
}
