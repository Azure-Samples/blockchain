import React from 'react';
import PropTypes from 'prop-types';
import Tile from '../tile/tile';
import './tileContainer.css';

const TileContainer = ({ ...props }) => {
  const renderContainer = () =>
    props.data.map((item) => {
      let { displayName } = item;
      if (!item.displayName) {
        displayName = item.contractProperties[0].value;
      }
      return (<Tile
        key={item.id}
        name={displayName}
        handleClick={() => props.handleClick(item.id)}
        applicationId={item.id}
      />);
    });

  return (
    <div className="tileContainer">
      {renderContainer()}
    </div>
  );
};

TileContainer.propTypes = {
  data: PropTypes.arrayOf(PropTypes.object).isRequired,
};

export default TileContainer;
