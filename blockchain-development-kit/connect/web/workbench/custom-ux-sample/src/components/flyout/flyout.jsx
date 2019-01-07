import React from 'react';
import PropTypes from 'prop-types';
import { Panel, PanelType } from 'office-ui-fabric-react/lib/Panel';
import PanelContent from '../panelContent/panelContent';
import * as utils from '../../common/utils';
import './flyout.css';

const Flyout = ({ ...props }) => {
  const formattedData = utils.formatData(
    Array(props.workflowInstance),
    utils.getColumnHeaders(props.columnsProps),
    props.contractStates,
  ).pop();

  return (
    <Panel
      isOpen={props.showPanel}
      isLightDismiss
      type={PanelType.medium}
      onDismiss={props.handleClick}
      closeButtonAriaLabel="Close"
      className="traceability-panel"
    >
      <PanelContent
        lastKnownParty={props.lastKnownParty}
        currentWeather={props.currentWeather}
        nearPoint={props.nearPoint}
        details={formattedData}
        clickedNumber={props.clickedNumber}
      />
    </Panel>
  );
};

Flyout.propTypes = {
  showPanel: PropTypes.bool,
  handleClick: PropTypes.func.isRequired,
  nearPoint: PropTypes.shape({
    id: PropTypes.number,
    parameters: PropTypes.arrayOf(PropTypes.object),
    provisioningStatus: PropTypes.number,
    timestamp: PropTypes.string,
    transactionId: PropTypes.number,
    userId: PropTypes.number,
    workflowFunctionId: PropTypes.number,
    workflowStateId: PropTypes.number,
  }),
  currentWeather: PropTypes.shape({
    currently: PropTypes.shape({
      summary: PropTypes.string,
    }),
    latitude: PropTypes.number,
    longitude: PropTypes.number,
  }),
  lastKnownParty: PropTypes.shape({
    displayName: PropTypes.string,
    value: PropTypes.string,
  }),
  clickedNumber: PropTypes.number,
  // eslint-disable-next-line react/forbid-prop-types
  workflowInstance: PropTypes.object,
  columnsProps: PropTypes.arrayOf(PropTypes.object).isRequired,
  contractStates: PropTypes.arrayOf(PropTypes.object).isRequired,
};

Flyout.defaultProps = {
  showPanel: false,
  nearPoint: null,
  currentWeather: null,
  lastKnownParty: null,
  workflowInstance: null,
  clickedNumber: null,
};

export default Flyout;
