import { apiService } from '../services';
import * as types from './actionTypes';

const setApplicationsData = applicationsData => ({
  type: types.SET_APPLICATIONS_DATA,
  payload: applicationsData,
});

const getAllApplications = (showEnabledApplications, top = null, skip = null) =>
  (dispatch, getState) => {
    const currentApplications = getState().applications;
    let applicationsTop = top;
    let applicationsSkip = skip;
    if (!applicationsTop && !applicationsSkip) {
      applicationsTop = currentApplications.top;
      applicationsSkip = currentApplications.skip;
    }
    apiService.getApplications('DisplayName', showEnabledApplications, applicationsTop, applicationsSkip)
      .then(({ applications }) => {
        if (!applications) {
          return;
        }
        dispatch(setApplicationsData(applications));
      });
  };

export {
  getAllApplications,
  setApplicationsData,
};
