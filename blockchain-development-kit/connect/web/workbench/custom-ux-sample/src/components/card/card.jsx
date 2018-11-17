import React from 'react';
import PropTypes from 'prop-types';
import moment from 'moment';
import Row from '../row/row';
import './card.css';

const Card = ({ items }) => {
  const getTime = (item) => {
    if (item.paramTimestamp) {
      return moment.unix(item.paramTimestamp.value).format('LT');
    }
    return moment(`${item.timestamp}Z`).format('LT');
  };

  const renderItems = () => {
    if (!Array.isArray(items)) {
      return Object.keys(items).map(currentItem => (
        <div key={currentItem}>
          <p className="timeline-date"><span>{currentItem}</span></p>
          {items[currentItem].map((item) => {
            const user = item.mappedUsers.pop();
            return (
              <Row
                key={item.id}
                leftSide={item.plotNumber}
                rightSide={`${user.value} recorded action ${item.functionName} at ${getTime(item)}`}
                activityFeedRow
              />
            );
          })}
        </div>
      ));
    }
    return items.map(item => (<Row
      key={item.formattedName}
      leftSide={item.formattedName}
      rightSide={item.formattedValue}
      divider=":"
    />));
  };

  return (
    <div className="card">
      {renderItems()}
    </div>
  );
};

Card.propTypes = {
  items: PropTypes.oneOfType([
    PropTypes.arrayOf(PropTypes.object), PropTypes.object]).isRequired,
};

export default Card;
