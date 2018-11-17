import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import { Icon } from 'office-ui-fabric-react/lib/Icon';
import './panelContent.css';

const PanelContent = ({
  nearPoint,
  currentWeather,
  lastKnownParty,
  details,
  clickedNumber,
}) => {
  const getHumidity = () => {
    const humidity = nearPoint.parameters
      .filter(param => param.name === 'Humidity')
      .pop();
    if (humidity) {
      return humidity.value;
    }
    return 'N/A';
  };

  const getPassedTemperature = () => {
    const temp = nearPoint.parameters
      .filter(param => param.name === 'Temperature')
      .pop();
    if (temp) {
      return Number(temp.value);
    }
    return 'N/A';
  };

  if (!currentWeather) {
    return 'Loading...';
  }

  const getTempClassName = () => {
    const passedTemp = getPassedTemperature();
    if (
      passedTemp > Number(details['Min Temperature'].value) &&
      passedTemp < Number(details['Max Temperature'].value)
    ) {
      return 'panelRow-Title-Success';
    }
    return 'panelRow-Title-Fail';
  };

  const getHumidityClassName = () => {
    const passedHumidity = getHumidity();
    if (
      passedHumidity > Number(details['Min Humidity'].value) &&
      passedHumidity < Number(details['Max Humidity'].value)
    ) {
      return 'panelRow-Title-Success';
    }
    return 'panelRow-Title-Fail';
  };

  return (
    <div className="panelContent">
      <div className="panelRow-Header">
        <div className="circle">
          <h1 className="panelPlotNumber">{clickedNumber}</h1>
        </div>
        <h1 className="panelRow-Header-Title">{lastKnownParty.value}</h1>
      </div>
      <div className="panelRow">
        <h3 className="panelRow-Title">
          TIME:{' '}
          <span className="normal">
            {moment.unix(currentWeather.currently.time).format('LT')}
          </span>
        </h3>
        <h3 className="panelRow-Title">
          DATE:{' '}
          <span className="normal">
            {moment.unix(currentWeather.currently.time).format('MM/DD/YY')}
          </span>
        </h3>
      </div>
      <div className="panelRow">
        <h3 className="panelRow-Title">
          {currentWeather.currently.summary.toUpperCase()}
        </h3>
        <h3 className="panelRow-Title-Success">
          {currentWeather.currently.temperature}&deg;{' '}
          <span className="small">CURRENT</span>
        </h3>
      </div>
      <div className="panelRow-Solid">
        <div className="panelRow-Solid-Left">
          <Icon iconName="Frigid" className="myIcon-Panel" />
          <h3 className={getTempClassName()}>{getPassedTemperature()}&deg; </h3>
          &nbsp;
          <span className="small">SUBMITTED</span>
        </div>
        <div className="divider" />
        <div className="panelRow-Solid-Right">
          <h3 className="panelRow-Title bigNumber">
            {details['Min Temperature'].value}&deg;{' '}
            <span className="small">MIN</span>
          </h3>
          <h3 className="panelRow-Slash"> / </h3>
          <h3 className="panelRow-Title bigNumber">
            {details['Max Temperature'].value}&deg;{' '}
            <span className="small">MAX</span>
          </h3>
        </div>
      </div>
      <div className="panelRow-Solid">
        <div className="panelRow-Solid-Left">
          <Icon iconName="Drop" className="myIcon-Panel" />
          <h3 className={getHumidityClassName()}>{getHumidity()}%</h3>
          &nbsp;
          <span className="small">SUBMITTED</span>
        </div>
        <div className="divider" />
        <div className="panelRow-Solid-Right">
          <h3 className="panelRow-Title bigNumber">
            {details['Min Humidity'].value}% <span className="small">MIN</span>
          </h3>
          <h3 className="panelRow-Slash"> / </h3>
          <h3 className="panelRow-Title bigNumber">
            {details['Max Humidity'].value}% <span className="small">MAX</span>
          </h3>
        </div>
      </div>
      <div className="panelRow-Solid-Center">
        <Icon iconName="MapPin" className="myIcon-Panel" />
        <h3 className="panelRow-Title">
          LAT: {currentWeather.latitude}, LONG: {currentWeather.longitude}
        </h3>
      </div>
    </div>
  );
};

PanelContent.propTypes = {
  currentWeather: PropTypes.shape({
    currently: PropTypes.shape({
      summary: PropTypes.string,
    }),
    latitude: PropTypes.number,
    longitude: PropTypes.number,
  }),
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
  lastKnownParty: PropTypes.shape({
    displayName: PropTypes.string,
    value: PropTypes.string,
  }),
  // eslint-disable-next-line react/forbid-prop-types
  details: PropTypes.object,
  clickedNumber: PropTypes.number,
};

PanelContent.defaultProps = {
  currentWeather: null,
  nearPoint: null,
  lastKnownParty: null,
  details: null,
  clickedNumber: null,
};

export default PanelContent;
