import React from 'react';
import { ReactBingmaps } from 'react-bingmaps';
import rocketIcon from '../../assets/icons/rocket.svg';
import './map.css';

const BingMap = ({ ...props }) => {
  return (<ReactBingmaps
    bingmapKey="AiH74JJiZAJpbgQ6VETgoFpLqIjoqGF6_QvJE1OEdS3aOtZUCKgksDiSXx87Djrv"
    className="map"
    id="myMap"
    center={[47.6062, -122.335167]}
    zoom={13}
      // infoboxesWithPushPins={[
      //   {
      //     location: [47.6062, -122.335167],
      //     addHandler: 'mouseover', // on mouseover the pushpin, infobox shown
      //     infoboxOption: { title: 'Infobox Title', description: 'Infobox' },
      //     pushPinOption: {
      //       title: 'Pushpin Title',
      //       description: 'Pushpin',
      //       color: 'blue',
      //       text: '1',
      //       className: 'custom-push-pin',
      //     },
      //     infoboxAddHandler: { type: 'click', callback: this.clickedPin },
      //     pushPinAddHandler: { type: 'click', callback: this.clickedPin },
      //   },
      //   {
      //     location: [47.6101, -122.2015],
      //     addHandler: 'mouseover', // on mouseover the pushpin, infobox shown
      //     infoboxOption: { title: 'Infobox Title', description: 'Infobox' },
      //     pushPinOption: {
      //       title: 'Pushpin Title',
      //       description: 'Pushpin',
      //       color: 'red',
      //       text: '2',
      //     },
      //     infoboxAddHandler: { type: 'click', callback: this.clickedPin },
      //     pushPinAddHandler: { type: 'click', callback: this.clickedPin },
      //   },
      // ]
      // }
    pushPins={
        [
          {
            location: [47.6062, -122.345267],
            option: {
              icon: rocketIcon,
              anchor: (0, 0),
            },
          },
        ]
      }
      // polyline={
      //   {
      //     location: [[47.6062, -122.335167], [47.6101, -122.2015]],
      //     option: {
      //       strokeColor: 'blue',
      //       strokeThickness: 10,
      //       strokeDashArray: [1, 2, 5, 10],
      //     },
      //   }
      // }
    directions={
        {
          requestOptions: {
            routeMode: 'driving',
            maxRoutes: 1,
          },
          wayPoints: [
            {
              address: '47.6062, -122.335167',
            },
            {
              address: 'Bellevue, WA',
            },
          ],
        }
      }
  />);
};

export default BingMap;
