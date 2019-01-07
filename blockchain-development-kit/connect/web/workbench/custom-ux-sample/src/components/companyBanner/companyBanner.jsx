import React from 'react';
import PropTypes from 'prop-types';
import logo from '../../assets/icons/logo.png';
import './companyBanner.css';

const CompanyBanner = ({ companyName }) => (
  <div className="companyBanner">
    <img src={logo} className="logo" alt="logo" />
    <h1 className="companyNameText">{companyName}</h1>
  </div>
);

CompanyBanner.propTypes = {
  companyName: PropTypes.string.isRequired,
};

export default CompanyBanner;

