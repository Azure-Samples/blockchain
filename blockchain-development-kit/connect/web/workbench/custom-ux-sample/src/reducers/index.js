import { combineReducers } from 'redux';
import usersReducer from './usersReducer';
import capabilitiesReducer from './capabilitiesReducer';
import dashboardReducer from './dashboardReducer';
import applicationsReducer from './applicationsReducer';
import workflowReducer from './workflowsReducer';
import contractsReducer from './contractsReducer';
import navigationReducer from './navigationReducer';
import viewReducer from './viewReducer';

const rootReducer = combineReducers({
  users: usersReducer,
  capabilities: capabilitiesReducer,
  dashboard: dashboardReducer,
  applications: applicationsReducer,
  workflows: workflowReducer,
  contracts: contractsReducer,
  navigation: navigationReducer,
  view: viewReducer,
});

export default rootReducer;
