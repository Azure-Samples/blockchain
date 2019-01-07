import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { isEqual } from 'lodash';
import { ReactBingmaps } from 'react-bingmaps';
import * as config from '../../common/config';
import './map.css';

class BingMap extends Component {
  shouldComponentUpdate(nextProps) {
    if (!isEqual(this.props.data, nextProps.data)) {
      return true;
    }
    return false;
  }

  getMidPoint = () => {
    const copy = [...this.props.data];
    if (copy.length <= 1) {
      const endPoint = copy.pop();
      return [Number(endPoint.coordinates[0].value), Number(endPoint.coordinates[1].value)];
    }
    const midPoint = copy.slice(0, copy.length / 2).pop();
    return [Number(midPoint.coordinates[0].value), Number(midPoint.coordinates[1].value)];
  };

  renderPushPins = () => (
    this.props.data.map((item, index) => {
      const coords = item.coordinates.map(coord => coord.value.replace(/[^\d.-]/g, ''));

      return ({
        location: [Number(coords[0]), Number(coords[1])],
        option: {
          color: 'black',
          text: `${index + 1}`,
        },
        addHandler: {
          type: 'click',
          callback: (() => this.props.handleClick(item, index + 1)),
        },
      });
    })
  );

  render() {
    if (this.props.data.length > 0) {
      return (<ReactBingmaps
        bingmapKey={config.default.bingmaps}
        className="map"
        id="myMap"
        center={this.getMidPoint()}
        zoom={4}
        pushPins={
            this.renderPushPins()
          }
      />);
    }
    return (
      <ReactBingmaps
        bingmapKey={config.default.bingmaps}
        className="map"
        id="myMap"
        center={[47.608013, -122.335167]}
        zoom={4}
      />
    );
  }
}

BingMap.propTypes = {
  data: PropTypes.arrayOf(PropTypes.object).isRequired,
  handleClick: PropTypes.func.isRequired,
};

export default BingMap;
