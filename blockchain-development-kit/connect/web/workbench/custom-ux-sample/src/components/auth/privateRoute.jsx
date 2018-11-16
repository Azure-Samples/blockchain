import React from 'react';
import { Route } from 'react-router-dom';
import PropTypes from 'prop-types';
import { authService } from '../../services';

const PrivateRoute = ({ component: Component, ...rest }) => (
  <Route
    {...rest}
    render={(props) => {
      if (authService.isAuthenticated()) {
        return (<Component {...props} />);
      }

      authService.login();
      return (<p className="redirectText">Redirecting you to the login page...</p>);
  }}
  />
);

PrivateRoute.propTypes = {
  component: PropTypes.func.isRequired,
};

export default PrivateRoute;
