import * as types from './actionTypes';
import { apiService } from '../services';
import { history } from '../store/configureStore';

const setLoading = bool => ({
  type: types.SET_LOADING_WORKFLOWS,
  payload: bool,
});

const setError = bool => ({
  type: types.SET_ERROR_WORKFLOWS,
  payload: bool,
});

const setWorkflows = data => ({
  type: types.SET_WORKFLOWS,
  payload: data,
});

const getWorkflows = (applicationId, top = null, skip = null) => (dispatch, getState) => {
  const currentWorkflows = getState().workflows;
  let workflowsTop = top;
  let workflowsSkip = skip;
  if (workflowsTop === null && workflowsSkip === null) {
    workflowsTop = currentWorkflows.top;
    workflowsSkip = currentWorkflows.skip;
  }

  apiService.getWorkflows(applicationId, workflowsTop, workflowsSkip)
    .then(({ workflows }) => {
      if (!workflows) {
        return;
      }
      if (workflows.length === 1) {
        dispatch(setWorkflows(workflows));
        history.push(`/applications/${applicationId}/workflows/${
          workflows[0].id
        }`);
        dispatch(setLoading(false));
      }
    })
    .catch(() => {
      dispatch(setError(true));
      dispatch(setLoading(false));
    });
};

export {
  getWorkflows,
  setWorkflows,
};
