import React from 'react';
import './imageContainer.css';
import Field from '../../assets/images/field1.jpg';

const backgroundStyle = {
  backgroundImage: `url(${Field})`
};

const ImageContainer = () => (
  <div className="imageContainer" style={backgroundStyle} />
);

export default ImageContainer;
