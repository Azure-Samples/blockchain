import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { getWorkflowContractsConstructor } from '../../actions/contractActions';
import { saveWorkflowId, saveApplicationId } from '../../actions/navigationActions';
import Header from '../header/header';
import TileContainer from '../tileContainer/tileContainer';
import './contracts.css';

class Contracts extends Component {
  componentDidMount() {
    const { applicationId, workflowId } = this.props.match.params;
    this.props.actions.saveApplicationId(applicationId);
    this.props.actions.saveWorkflowId(workflowId);
    this.props.actions.getWorkflowContractsConstructor(applicationId, workflowId);
  }

  handleClick = (id) => {
    const { applicationId, workflowId } = this.props.match.params;
    this.props.history.push(`/applications/${applicationId}/workflows/${workflowId}/workflowInstance/${id}`);
  }

  render() {
    if (!this.props.contracts) {
      return <div>Loading...</div>;
    }

    return (
      <div className="contractsPage">
        <Header headerText="Contracts" />
        <TileContainer
          data={this.props.contracts}
          handleClick={this.handleClick}
        />
      </div>
    );
  }
}

Contracts.propTypes = {
  contracts: PropTypes.arrayOf(PropTypes.object).isRequired,
  match: PropTypes.shape({
    params: PropTypes.shape({
      applicationId: PropTypes.node,
      workflowId: PropTypes.node,
    }).isRequired,
  }).isRequired,
  history: PropTypes.shape({
    push: PropTypes.func.isRequired,
  }).isRequired,
  actions: PropTypes.shape({
    getWorkflowContractsConstructor: PropTypes.func.isRequired,
    saveWorkflowId: PropTypes.func.isRequired,
    saveApplicationId: PropTypes.func.isRequired,
  }).isRequired,
};

const mapStateToProps = state => ({
  contracts: [...state.contracts.contracts],
});

const mapDispatchToProps = dispatch => ({
  actions: bindActionCreators({
    getWorkflowContractsConstructor,
    saveWorkflowId,
    saveApplicationId,
  }, dispatch),
});

export default connect(mapStateToProps, mapDispatchToProps)(Contracts);
