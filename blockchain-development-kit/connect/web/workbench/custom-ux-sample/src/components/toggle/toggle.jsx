import React from 'react';
import PropTypes from 'prop-types';
import { Toggle } from 'office-ui-fabric-react/lib/Toggle';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { toggleView } from '../../actions/viewActions';
import './toggle.css';

const MyToggle = ({ ...props }) => (<Toggle
  defaultChecked={props.currentView}
  onText="Admin"
  offText="Consumer"
  onClick={() => props.actions.toggleView(!props.currentView)}
  className="toggle"
/>);

const mapStateToProps = state => ({
  currentView: state.view.showAdminView,
});

const mapDispatchToProps = dispatch => ({
  actions: bindActionCreators({
    toggleView,
  }, dispatch),
});

MyToggle.propTypes = {
  actions: PropTypes.shape({
    toggleView: PropTypes.func.isRequired,
  }).isRequired,
  currentView: PropTypes.bool.isRequired,
};

export default connect(mapStateToProps, mapDispatchToProps)(MyToggle);
