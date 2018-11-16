import React from 'react';
import PropTypes from 'prop-types';
import './row.css';

const Row = ({ ...props }) => {
  const getClassName = () => {
    if (props.activityFeedRow) {
      return 'noPlotNumber';
    }
    return 'row-left';
  };

  return (
    <div className="row-container">
      <div className={typeof props.leftSide === 'number' ? 'activityCircle' : 'no-circle'}>
        <p className={typeof props.leftSide === 'number' ? 'plotNumber' : getClassName()}>{props.leftSide}{props.divider} </p>
      </div>
      <p className="row-right">{props.rightSide}</p>
    </div>
  );
};

Row.propTypes = {
  leftSide: PropTypes.oneOfType([PropTypes.number, PropTypes.string]),
  rightSide: PropTypes.string,
  divider: PropTypes.string,
  activityFeedRow: PropTypes.bool,
};

Row.defaultProps = {
  rightSide: '',
  divider: '',
  leftSide: '',
  activityFeedRow: false,
};

export default Row;
