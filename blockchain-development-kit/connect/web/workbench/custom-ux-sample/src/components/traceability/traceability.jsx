import React from 'react';
import PropTypes from 'prop-types';
import BingMap from '../map/bingmap';
import Card from '../card/card';
import './traceability.css';

const Traceability = ({
  ...props
}) => (
  <div className="traceabilityPage">
    <div className="activityFeed">
      <h2 className="activityFeedHeader">ACTIVITY FEED</h2>
      <Card
        items={props.activityData}
      />
    </div>
    <div className="bingMaps">
      <BingMap
        data={props.data}
        handleClick={props.handleClick}
      />
    </div>
  </div>
);

Traceability.propTypes = {
  data: PropTypes.arrayOf(PropTypes.object).isRequired,
  handleClick: PropTypes.func.isRequired,
  activityData: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.object), PropTypes.object]).isRequired,
};

export default Traceability;
