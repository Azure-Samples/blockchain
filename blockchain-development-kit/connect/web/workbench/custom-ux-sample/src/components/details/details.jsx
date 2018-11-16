import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import { Icon } from 'office-ui-fabric-react/lib/Icon';
import * as utils from '../../common/utils';
import Card from '../card/card';
import { primitiveValues } from '../../common/constants';
import './details.css';

const Details = ({ ...props }) => {
  const getData = utils
    .formatData(
      Array(props.workflowInstance),
      utils.getColumnHeaders(props.columnsProps),
      props.contractStates,
    )
    .pop();

  const detailsCard = utils.formatDetailsDashboardData(getData);

  const mapItems = content =>
    content.map((item) => {
      let formattedValue = item.value.value;
      let formattedName = item.name;
      const { type } = item.value;

      if (!primitiveValues.includes(item.value.type)) {
        if (item.value.value !== '-') {
          const mappedUser = props.users.reduce((userResult, userInfo) => {
            // eslint-disable-next-line no-return-assign
            userInfo.userChainMappings.map(userChainMappingObj =>
              userChainMappingObj.chainIdentifier ===
                  // eslint-disable-next-line no-param-reassign
                  item.value.value && (userResult = { ...userInfo }));
            return userResult;
          }, {});

          if (Object.keys(mappedUser).length) {
            formattedValue = `${mappedUser.firstName} ${mappedUser.lastName}`;
          } else {
            formattedValue = 'Contract';
          }
        }
      }

      // if (item.name === 'contractAddress') {
      //   formattedName = 'Contract Address';
      //   formattedValue = item.value;
      // }

      if (item.name === 'id') {
        formattedValue = item.value;
        formattedName = 'Contract Id';
      }

      if (item.name === 'deployedByUserId') {
        formattedName = 'Created By';
        const mappedUser = utils.convertIdToUser(item.value, props.users);
        formattedValue = `${mappedUser.firstName} ${mappedUser.lastName}`;
      }

      if (item.name === 'timestamp') {
        formattedValue = moment(`${item.value}Z`).format('MM/DD/YY');
        formattedName = 'Created Date';
      }
      return {
        formattedName,
        formattedValue,
        type,
      };
    });

  const renderCards = () => {
    const slicedData = detailsCard.slice(0, 16);
    const matrix = utils.listToMatrix(
      mapItems(slicedData),
      slicedData.length / 4,
    );
    return matrix.map((item, index) => (
      <Card key={`Card ${index + 1}`} items={item} />
    ));
  };

  const getLastTemp = () => {
    if (props.hudmityTempData.length > 0) {
      const lastTemp = props.hudmityTempData.slice(-1).pop();
      return lastTemp.parameters
        .filter(param => param.name === 'Temperature')
        .pop().value;
    }
    return 'N/A';
  };

  const getLastHumidity = () => {
    if (props.hudmityTempData.length > 0) {
      const lastHumidity = props.hudmityTempData.slice(-1).pop();
      return lastHumidity.parameters
        .filter(param => param.name === 'Humidity')
        .pop().value;
    }
    return 'N/A';
  };

  const getTemperatureClassName = () => {
    if (
      getLastTemp() > Number(getData['Min Temperature'].value) &&
      getLastTemp() < Number(getData['Max Temperature'].value)
    ) {
      return 'details-content-right-Success';
    }
    return 'details-content-right-fail';
  };

  const getHumidityClassName = () => {
    if (
      getLastHumidity() > Number(getData['Min Humidity'].value) &&
      getLastHumidity() < Number(getData['Max Humidity'].value)
    ) {
      return 'details-content-right-Success';
    }
    return 'details-content-right-fail';
  };

  return (
    <div className="detailsContainer">
      <div className="detailsContainer-top">
        <div className="detailsContainer-top-content">
          <div className="details-content-center">
            STATUS:{' '}
            <span
              className={
                getData.State.style === 'Success'
                  ? 'Contract-Success'
                  : 'Contract-Fail'
              }
            >
              &nbsp;{getData.State.value.toUpperCase()}
            </span>
          </div>
        </div>
        <div className="detailsContainer-top-content">
          <div className="details-content-left">
            <Icon iconName="Frigid" className="myIcon" />
            <p className="details-content-paragraph-font">
              <span className="bigNumber">
                {getData['Min Temperature'].value}&deg;
              </span>{' '}
              MIN{' '}
              <span className="bigNumber">
                / {getData['Max Temperature'].value}&deg;
              </span>{' '}
              MAX
            </p>
          </div>
          <div className="details-divider" />
          <div className="details-content-right">
            <p className="details-content-paragraph-font">
              <span
                className={`${getTemperatureClassName()} bigNumber`}
              >
                {`${getLastTemp()}`}&deg;
              </span>{' '}
              &nbsp; CURRENT
            </p>
          </div>
        </div>
        <div className="detailsContainer-top-content">
          <div className="details-content-left">
            <Icon iconName="Drop" className="myIcon" />
            <p className="details-content-paragraph-font">
              <span className="bigNumber">
                {getData['Min Humidity'].value}%
              </span>{' '}
              MIN{' '}
              <span className="bigNumber">
                / {getData['Max Humidity'].value}%
              </span>{' '}
              MAX
            </p>
          </div>
          <div className="details-divider" />
          <div className="details-content-right">
            <p className="details-content-paragraph-font">
              <span
                className={`${getHumidityClassName()} bigNumber`}
              >
                {getLastHumidity()}%
              </span>{' '}
              &nbsp; CURRENT
            </p>
          </div>
        </div>
        <div className="detailsContainer-top-content">
          <div className="details-content-center">
            <Icon iconName="MapPin" className="myMapPinIcon" />
            <p>
              LAT: {getData.LastRecordedLatitude.value}, LONG:{' '}
              {getData.LastRecoredLongitude.value}
            </p>
          </div>
        </div>
      </div>
      <div className="detailsContainer-bottom">{renderCards()}</div>
    </div>
  );
};

Details.propTypes = {
  users: PropTypes.arrayOf(PropTypes.object).isRequired,
  workflowInstance: PropTypes.shape({
    contractActions: PropTypes.arrayOf(PropTypes.object).isRequired,
  }).isRequired,
  columnsProps: PropTypes.arrayOf(PropTypes.object).isRequired,
  contractStates: PropTypes.arrayOf(PropTypes.object).isRequired,
  hudmityTempData: PropTypes.arrayOf(PropTypes.object).isRequired,
  currentState: PropTypes.string.isRequired,
};

export default Details;
