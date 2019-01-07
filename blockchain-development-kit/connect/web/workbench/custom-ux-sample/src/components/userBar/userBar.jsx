import React from 'react';
import { authService } from '../../services';
import './userBar.css';

const UserBar = () => (
  <div className="userBar">
    <button
      onClick={authService.logout}
      className="signOutButton"
    >
    Sign Out
    </button>
  </div>
);

export default UserBar;
