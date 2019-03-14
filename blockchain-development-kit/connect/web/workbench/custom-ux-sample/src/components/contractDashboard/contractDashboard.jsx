import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import moment from 'moment';
import Header from '../header/header';
import { getContractDashboardConstructor, getWeather, clearContractDashboard } from '../../actions/dashboardActions';
import { getUsers } from '../../actions/usersActions';
import Traceability from './../traceability/traceability';
import Flyout from '../flyout/flyout';
import * as utils from '../../common/utils';
import { primitiveValues } from '../../common/constants';
import { saveApplicationId, saveWorkflowId, saveWorkflowInstanceId } from '../../actions/navigationActions';
import Details from '../details/details';
import ImageContainer from '../imageContainer/imageContainer';
import './contractDashboard.css';

class ContractDashboard extends Component {
  constructor(props) {
    super(props);
    this.state = {
      showPanel: false,
      lastKnownParty: null,
    };
  }

  componentDidMount() {
    const { workflowInstanceId, applicationId, workflowId } = this.props.match.params;
    this.props.actions
      .getContractDashboardConstructor(applicationId, workflowId, workflowInstanceId);
    this.props.actions.getUsers();
    this.props.actions.saveWorkflowId(workflowId);
    this.props.actions.saveApplicationId(applicationId);
    this.props.actions.saveWorkflowInstanceId(workflowInstanceId);
  }

  componentWillUnmount() {
    this.props.actions.clearContractDashboard();
  }

  getNearPointInformation = (selectedLocation) => {
    const hasHumidity = this.props.workflowInstance.contractActions.filter(action => action.parameters.some(param => param.name === 'Humidity'));
    const nearPoint = hasHumidity.reduce((result, action) => {
      const target = action.parameters.filter(param => param.name === 'Timestamp').pop();
      const actionTime = Math.abs(target.value - selectedLocation.parameters[2].value);
      const resultTime =
        Math.abs(result.parameters[2].value - selectedLocation.parameters[2].value);
      if (action.id !== selectedLocation.id && resultTime > actionTime) {
        return {
          ...action,
        };
      }
      return result;
    }, { ...hasHumidity.shift() });
    return nearPoint;
  }

  getSelectedLocation = item => this.props.workflowInstance.contractActions
    .filter(action => action.id === item.actionId).pop()

  getLastKnownParty = (selectedLocation) => {
    const formattedData = this.plotPoints();
    const lastKnownUserAction = formattedData.reduce((result, action) => {
      if (action.mappedUsers.length > 0 && action.id < selectedLocation.actionId) {
        return {
          ...action,
        };
      }
      return result;
    }, {});
    this.setState({
      lastKnownParty: lastKnownUserAction.mappedUsers.pop(),
    });
  }

  flyoutVisibility = (item, number) => {
    if (!item) {
      this.setState({
        showPanel: !this.state.showPanel,
        nearPoint: null,
        clickedNumber: null,
      });
    } else {
      this.getLastKnownParty(item);
      const selectedLocation = this.getSelectedLocation(item);
      const nearPoint = this.getNearPointInformation(selectedLocation);
      this.props.actions
        .getWeather(
          item.coordinates[0].value,
          item.coordinates[1].value,
          item.paramTimestamp.value,
        );
      this.setState({
        showPanel: !this.state.showPanel,
        nearPoint,
        clickedNumber: number,
      });
    }
  }

  plotPoints = () => {
    const { workflowInstance, userFunctionsDictionary, users } = this.props;
    return workflowInstance.contractActions
      .filter(action =>
        Object.prototype.hasOwnProperty.call(userFunctionsDictionary, action.workflowFunctionId))
      .map((action) => {
        let coordinates = [];
        let mappedUsers = [];
        let paramTimestamp;
        const currentActionFunctionName = utils.buildFunctionName(action, userFunctionsDictionary);
        const functionsParams = utils
          .getFunctionsParameters(action.workflowFunctionId, userFunctionsDictionary);
        action.parameters.forEach((parameter) => {
          const hasParameterName =
          Object.prototype.hasOwnProperty.call(functionsParams, parameter.name);
          if (hasParameterName) {
            if ((functionsParams[parameter.name].name === 'latitude') || (functionsParams[parameter.name].name === 'longitude')) {
              coordinates = [
                ...coordinates,
                {
                  displayName: parameter.name,
                  value: parameter.value,
                },
              ];
            }
            if (functionsParams[parameter.name].name === 'timestamp') {
              paramTimestamp = {
                displayName: parameter.name,
                value: parameter.value,
              };
            }
            if (!primitiveValues.includes(functionsParams[parameter.name].type.name)) {
              const mappedUser = utils.convertHexCodeToUser(parameter.value, users);
              mappedUsers = [
                ...mappedUsers,
                {
                  displayName: parameter.name,
                  value: `${mappedUser.firstName} ${mappedUser.lastName}`,
                },
              ];
            }
          }
        });
        return {
          ...action,
          coordinates,
          paramTimestamp,
          mappedUsers,
          functionName: currentActionFunctionName,
        };
      });
  }

  mapPoints = () => {
    const formattedData = this.plotPoints();
    return formattedData.filter(action => action.coordinates.length > 1).map((action) => {
      const coordinates = [];
      action.coordinates.forEach((parameter) => {
        coordinates.push({
          displayName: parameter.displayName,
          value: parameter.value,
        });
      });
      return {
        ...action,
        coordinates,
        actionId: action.id,
        paramTimestamp: action.paramTimestamp,
      };
    });
  }

  hudmityTempData = () => {
    const formattedData = this.plotPoints();
    return formattedData.filter(action => (action.coordinates.length === 0 && action.functionName !== 'Transfer Responsibility' && action.functionName !== 'Create' && action.functionName !== 'Complete'));
  }

  activityData = () => {
    const formattedData = this.plotPoints();
    let counter = 0;
    return formattedData.filter(action => (action.coordinates.length > 1 || action.functionName === 'Transfer Responsibility')).map((action) => {
      const mappedUser = utils.convertIdToUser(action.userId, this.props.users);
      if (action.coordinates.length > 1) {
        counter += 1;
        const coordinates = [];
        action.coordinates.forEach((parameter) => {
          coordinates.push({
            displayName: parameter.displayName,
            value: parameter.value,
          });
        });
        return {
          ...action,
          coordinates,
          actionId: action.id,
          paramTimestamp: action.paramTimestamp,
          plotNumber: counter,
          mappedUsers: [{
            value: `${mappedUser.firstName} ${mappedUser.lastName}`,
          }],
        };
      }
      return {
        ...action,
        mappedUsers: [{
          value: `${mappedUser.firstName} ${mappedUser.lastName}`,
        }],
      };
    });
  }

  renderTimeline = () => {
    const activityData = this.activityData();
    return activityData.reduce((result, currentActivityItem) => {
      const today = moment(Date.now()).format('MM/DD/YY');
      const currentActivityDate = moment(`${currentActivityItem.timestamp}Z`).format('MM/DD/YY');
      if (today === currentActivityDate) {
        if (!result.Today) {
          // eslint-disable-next-line no-param-reassign
          result = {
            ...result,
            Today: [],
          };
        }
        // eslint-disable-next-line no-param-reassign
        result = {
          Today: [...result.Today, currentActivityItem],
        };
      } else {
        if (!result[currentActivityDate]) {
          // eslint-disable-next-line no-param-reassign
          result = {
            ...result,
            [currentActivityDate]: [],
          };
        }
        // eslint-disable-next-line no-param-reassign
        result = {
          ...result,
          [currentActivityDate]: [
            ...result[currentActivityDate],
            currentActivityItem,
          ],
        };
      }
      return result;
    }, {});
  }

  render() {
    const {
      workflowInstance,
      users,
      columnsProps,
      contractStates,
      currentView,
    } = this.props;
    if (!workflowInstance || !users) {
      return <div>Loading...</div>;
    }

    const getData = utils.formatData(
      Array(workflowInstance),
      utils.getColumnHeaders(columnsProps),
      contractStates,
    ).pop();

    return (
      <div className="contractDashboardPage">
        <Header
          headerText={`Provenance - ${workflowInstance.contractProperties[0].value}`}
        />
        {this.props.currentView ? (
          <Details
            workflowInstance={workflowInstance}
            columnsProps={columnsProps}
            contractStates={contractStates}
            users={users}
            currentView={currentView}
            hudmityTempData={this.hudmityTempData()}
            currentState={getData.State.style}
          />
        ) : (
          <ImageContainer />
        )}

        <Traceability
          data={this.mapPoints()}
          activityData={this.renderTimeline()}
          handleClick={this.flyoutVisibility}
        />
        {this.state.showPanel &&
          <Flyout
            lastKnownParty={this.state.lastKnownParty}
            showPanel={this.state.showPanel}
            handleClick={this.flyoutVisibility}
            nearPoint={this.state.nearPoint}
            currentWeather={this.props.currentWeather}
            workflowInstance={workflowInstance}
            columnsProps={columnsProps}
            contractStates={contractStates}
            users={users}
            clickedNumber={this.state.clickedNumber}
          />
        }
      </div>
    );
  }
}

const mapStateToProps = state => ({
  workflowInstance: state.dashboard.workflowInstance,
  columnsProps: state.dashboard.columnsProps,
  contractStates: state.dashboard.contractStates,
  userFunctionsDictionary: state.dashboard.userFunctionsDictionary,
  currentWeather: state.dashboard.currentWeather,
  users: state.users.users,
  currentView: state.view.showAdminView,
});

const mapDispatchToProps = dispatch => ({
  actions: bindActionCreators({
    getContractDashboardConstructor,
    getWeather,
    getUsers,
    saveApplicationId,
    saveWorkflowId,
    saveWorkflowInstanceId,
    clearContractDashboard,
  }, dispatch),
});

ContractDashboard.propTypes = {
  // eslint-disable-next-line react/forbid-prop-types
  workflowInstance: PropTypes.object,
  // eslint-disable-next-line react/forbid-prop-types
  userFunctionsDictionary: PropTypes.object,
  match: PropTypes.shape({
    params: PropTypes.shape({
      applicationId: PropTypes.node,
      workflowId: PropTypes.node,
      workflowInstanceId: PropTypes.node,
    }).isRequired,
  }).isRequired,
  actions: PropTypes.shape({
    getContractDashboardConstructor: PropTypes.func.isRequired,
    getWeather: PropTypes.func.isRequired,
    getUsers: PropTypes.func.isRequired,
    saveApplicationId: PropTypes.func.isRequired,
    saveWorkflowId: PropTypes.func.isRequired,
    saveWorkflowInstanceId: PropTypes.func.isRequired,
    clearContractDashboard: PropTypes.func.isRequired,
  }).isRequired,
  currentWeather: PropTypes.shape({
    currently: PropTypes.shape({
      summary: PropTypes.string,
    }),
    latitude: PropTypes.number,
    longitude: PropTypes.number,
  }),
  users: PropTypes.arrayOf(PropTypes.object),
  columnsProps: PropTypes.arrayOf(PropTypes.object),
  contractStates: PropTypes.arrayOf(PropTypes.object).isRequired,
  currentView: PropTypes.bool.isRequired,
};

ContractDashboard.defaultProps = {
  workflowInstance: null,
  userFunctionsDictionary: {},
  currentWeather: null,
  users: null,
  columnsProps: null,
};

export default connect(mapStateToProps, mapDispatchToProps)(ContractDashboard);
