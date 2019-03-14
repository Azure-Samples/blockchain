import React from 'react';
import PropTypes from 'prop-types';
import './header.css';
import Logo from '../../assets/images/logo.jpg';

const Header = ({ hasImage, headerText }) => (
  <div className="header">
    {hasImage &&
      <img
        src={Logo}
        alt="Logo"
        className="defaultImage"
      />
    }
    <h1 className="headerText">{headerText}</h1>
  </div>
);

Header.propTypes = {
  headerText: PropTypes.string.isRequired,
  hasImage: PropTypes.bool,
};

Header.defaultProps = {
  hasImage: false,
};

export default Header;
