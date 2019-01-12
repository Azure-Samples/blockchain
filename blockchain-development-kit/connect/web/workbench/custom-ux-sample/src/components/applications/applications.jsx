import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { getAllApplications } from '../../actions/applicationsActions';
import { getWorkflows } from '../../actions/workflowsActions';
import Header from './../header/header';
import TileContainer from './../tileContainer/tileContainer';
import './applications.css';

class Applications extends Component {
  componentDidMount() {
    const { showEnabledApplications } = this.props;
    this.props.actions.getAllApplications(showEnabledApplications);
  }

  handleClick = (id) => {
    this.props.actions.getWorkflows(id);
  }

  render() {
    return (
      <div className="applicationsPage">
        <Header headerText="Applications" />
        <TileContainer
          data={this.props.applications}
          handleClick={this.handleClick}
        />
      </div>
    );
  }
}

const mapStateToProps = state => ({
  applications: state.applications.applications,
  showEnabledApplications: state.applications.showEnabledApplications,
});

const mapDispatchToProps = dispatch => ({
  actions: bindActionCreators({
    getAllApplications,
    getWorkflows,
  }, dispatch),
});

Applications.propTypes = {
  applications: PropTypes.arrayOf(PropTypes.object).isRequired,
  showEnabledApplications: PropTypes.bool.isRequired,
  actions: PropTypes.shape({
    getAllApplications: PropTypes.func.isRequired,
    getWorkflows: PropTypes.func.isRequired,
  }).isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(Applications);
