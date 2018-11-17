import * as types from './actionTypes';
import { apiService, weatherService } from '../services';
import { staticIdColumn, staticModifiedColumns } from '../common/constants';

const setLoadingContractDashboard = bool => ({
  type: types.SET_LOADING_DASHBOARD,
  payload: bool,
});

const setContractDashboard = contractDashboardInfo => ({
  type: types.SET_DASHBOARD,
  payload: contractDashboardInfo,
});

const setColumnProps = (data) => {
  const newColumns = [...data.properties];
  newColumns.splice(0, 0, staticIdColumn);
  newColumns.splice(2, 0, ...staticModifiedColumns);
  return {
    type: types.SET_COLUMN_PROPS,
    payload: newColumns,
  };
};

const setError = bool => ({
  type: types.SET_ERROR_CONTRACT_DASHBOARD,
  payload: bool,
});

const setContractDashboardConstructor = constructor => ({
  type: types.SET_CONTRACT_DASHBOARD_CONSTRUCTOR,
  payload: constructor,
});

const setCurrentWeather = data => ({
  type: types.SET_CURRENT_WEATHER,
  payload: data,
});

const getContractDashboard = workflowInstanceId => (dispatch) => {
  dispatch(setLoadingContractDashboard(true));
  dispatch(setError(false));
  return apiService.getContractDashboard(workflowInstanceId)
    .then((data) => {
      dispatch(setLoadingContractDashboard(false));
      dispatch(setContractDashboard(data));
    })
    .catch(() => {
      dispatch(setLoadingContractDashboard(false));
      dispatch(setError(true));
    });
};

const getContractDashboardConstructor = (applicationId, workflowId, workflowInstanceId) =>
  dispatch => apiService.getWorkflowContractsConstructor(applicationId, workflowId)
    .then((data) => {
      dispatch(setContractDashboardConstructor(data));
      dispatch(getContractDashboard(workflowInstanceId));
      dispatch(setColumnProps(data));
    });

const getWeather = (latitude, longitude, time) =>
  dispatch => weatherService.getWeather(latitude, longitude, time).then((data) => {
    dispatch(setCurrentWeather(data));
  });

const clearContractDashboard = () => ({
  type: types.CLEAR_CONTRACT_DASHBOARD,
});

export {
  getContractDashboard,
  setLoadingContractDashboard,
  setError,
  getContractDashboardConstructor,
  getWeather,
  clearContractDashboard,
};
