import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { Route, Redirect, Switch } from 'react-router-dom';
import { bindActionCreators } from 'redux';
import { getMe } from './actions/usersActions';
import Applications from './components/applications/applications';
import UserBar from './components/userBar/userBar';
import Contracts from './components/contracts/contracts';
import ContractDashboard from './components/contractDashboard/contractDashboard';
import MyBreadcrumbs from './components/breadcrumbs/breadcrumbs';
import MyToggle from './components/toggle/toggle';
import Consumer from './components/consumer/consumer';
import CompanyBanner from './components/companyBanner/companyBanner';
import './App.css';

class App extends Component {
  componentDidMount() {
    this.props.actions.getMe();
  }

  render() {
    return (
      <div className="App">
        <CompanyBanner companyName="ABC Company" />
        <MyBreadcrumbs />
        <UserBar />
        <MyToggle />
        <Switch>
          <Redirect from="/null" to="/" />
          <Route path="/applications/:applicationId/workflows/:workflowId/workflowInstance/:workflowInstanceId" component={ContractDashboard} />
          <Route path="/applications/:applicationId/workflows/:workflowId" component={this.props.currentView ? Contracts : Consumer} />
          <Route exact path="/applications" component={this.props.currentView ? Applications : Consumer} />
          <Route exact path="/" component={this.props.currentView ? Applications : Consumer} />
        </Switch>
      </div>
    );
  }
}

const mapStateToProps = state => ({
  currentView: state.view.showAdminView,
});

const mapDispatchToProps = dispatch => ({
  actions: bindActionCreators({ getMe }, dispatch),
});

App.propTypes = {
  actions: PropTypes.shape({
    getMe: PropTypes.func.isRequired,
  }).isRequired,
  currentView: PropTypes.bool.isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(App);
