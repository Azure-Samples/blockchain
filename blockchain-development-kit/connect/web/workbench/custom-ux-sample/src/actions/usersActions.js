import * as types from './actionTypes';
import User from '../types/user';
import { apiService } from '../services';

const getMe = () => dispatch => apiService.getMe()
  .then(({ currentUser, capabilities }) => {
    const me = User.parseWBUser(currentUser);
    dispatch({ type: types.GET_ME, me });
    dispatch({ type: types.GET_CAPABILITIES, ...capabilities });
    dispatch({ type: types.SET_LOADING_ME_CAPABILITIES, payload: false });
  })
  .catch((error) => {
    throw error;
  });

const setUsers = users => ({
  type: types.SET_USERS,
  payload: users,
});

const getUsers = () => dispatch => apiService.getUsers('FirstName')
  .then(({ users }) => {
    dispatch(setUsers(users));
  });

export {
  getMe,
  getUsers,
};
