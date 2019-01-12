import * as types from './actionTypes';
import { apiService } from '../services';

const setWorkflowContracts = (data) => {
  const contracts = ((data || []));
  return {
    type: types.SET_WORKFLOW_CONTRACTS,
    payload: contracts,
  };
};

const getWorkflowContracts = (workflowId, Timestamp, top = null, skip = null) =>
  (dispatch, getState) => {
    const currentContracts = getState().contracts;
    let contractsTop = top;
    let contractsSkip = skip;
    if (!contractsTop && !contractsSkip) {
      contractsTop = currentContracts.top;
      contractsSkip = currentContracts.skip;
    }
    apiService.getWorkflowContracts(workflowId, Timestamp, contractsTop, contractsSkip)
      .then(({ contracts }) => {
        if (!contracts) {
          return;
        }
        dispatch(setWorkflowContracts(contracts));
      });
  };

const getWorkflowContractsConstructor = (applicationId, workflowId) => (dispatch) => {
  apiService.getWorkflowContractsConstructor(applicationId, workflowId)
    .then((constructor) => {
      dispatch(getWorkflowContracts(constructor.id, 'Timestamp'));
    });
};

export {
  getWorkflowContractsConstructor,
  getWorkflowContracts,
  setWorkflowContracts,
};
