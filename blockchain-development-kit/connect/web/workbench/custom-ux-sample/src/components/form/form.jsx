import React, { Component } from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { bindActionCreators } from 'redux';
import { TextField } from 'office-ui-fabric-react/lib/TextField';
import { PrimaryButton } from 'office-ui-fabric-react/lib/Button';
import { redirectConsumer } from '../../actions/viewActions';
import './form.css';

class Form extends Component {
  constructor(props) {
    super(props);
    this.state = {
      value: 'Product SKU'
    };
  }

  handleClick = evt => {
    evt.preventDefault();
    this.props.actions.redirectConsumer();
  };

  render() {
    return (
      <form onSubmit={evt => this.handleClick(evt)} className="custom-form">
        <TextField className="textField" value={this.state.value} />
        <PrimaryButton type="submit">GO</PrimaryButton>
      </form>
    );
  }
}

const mapDispatchToProps = dispatch => ({
  actions: bindActionCreators(
    {
      redirectConsumer
    },
    dispatch
  )
});

Form.propTypes = {
  actions: PropTypes.shape({
    redirectConsumer: PropTypes.func.isRequired
  }).isRequired
};

export default connect(
  null,
  mapDispatchToProps
)(Form);
