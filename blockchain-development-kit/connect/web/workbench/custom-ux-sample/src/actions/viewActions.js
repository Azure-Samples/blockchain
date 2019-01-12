import * as types from './actionTypes';
import { history } from '../store/configureStore';

const toggleView = bool => ({
  type: types.SET_CURRENT_VIEW,
  payload: bool,
});

const redirectConsumer = () => (dispatch) => {
  history.push('/applications/5/workflows/5/workflowInstance/16');
};

export { toggleView, redirectConsumer };
