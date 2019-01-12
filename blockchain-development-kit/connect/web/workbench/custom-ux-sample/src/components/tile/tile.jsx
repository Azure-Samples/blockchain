import React from 'react';
import PropTypes from 'prop-types';
import { TooltipHost, TooltipOverflowMode } from 'office-ui-fabric-react/lib/Tooltip';
import './tile.css';
import Coffee from '../../assets/images/Coffee_2.svg';
import Cow from '../../assets/images/Cow_1.svg';
import Fish from '../../assets/images/Fish_2.svg';
import Box from '../../assets/images/Box_3.svg';

const Tile = ({ ...props }) => {
  let image;
  switch (props.name) {
    case 'Coffee':
      image = Coffee;
      break;
    case 'Beef':
      image = Cow;
      break;
    case 'Tuna':
      image = Fish;
      break;
    default:
      image = Box;
  }

  const makeTitle = () => (
    <TooltipHost
      content={props.name}
      overflowMode={TooltipOverflowMode.Parent}
    >
      <p className="tileContent-name">{props.name}</p>
    </TooltipHost>
  );

  return (
    <div className="Tile" onClick={props.handleClick} role="button" tabIndex="0" onKeyPress={props.handleClick}>
      <div className="custom-tile">
        <img
          src={image}
          alt="lorem ipsum"
          className="tileImage"
        />
      </div>
      <div className="tileContent-container">
        <div className="tileContent">{makeTitle()}</div>
        <div className="tileContent">ID {props.applicationId}</div>
      </div>
    </div>
  );
};

Tile.propTypes = {
  name: PropTypes.string.isRequired,
  handleClick: PropTypes.func.isRequired,
  applicationId: PropTypes.number.isRequired,
};

export default Tile;
