import React from 'react';
import ReactDOM from 'react-dom';
import { Router } from 'react-router-dom';
import { Provider } from 'react-redux';
import { initializeIcons } from 'office-ui-fabric-react/lib/Icons';
import PrivateRoute from './components/auth/privateRoute';
import store, { history } from './store/configureStore';
import './index.css';
import App from './App';
import registerServiceWorker from './registerServiceWorker';

initializeIcons();
ReactDOM.render(
  <Provider store={store}>
    <Router history={history}>
      <PrivateRoute component={App} />
    </Router>
  </Provider>,
  document.getElementById('root'),
);
registerServiceWorker();
