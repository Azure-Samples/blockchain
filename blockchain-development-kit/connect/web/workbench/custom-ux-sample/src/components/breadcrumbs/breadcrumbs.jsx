import React from 'react';
import PropTypes from 'prop-types';
import { connect } from 'react-redux';
import { withRouter } from 'react-router';
import { Breadcrumb } from 'office-ui-fabric-react';
import { NavLink } from 'react-router-dom';
import './breadcrumb.css';

const MyBreadcrumbs = ({ ...props }) => {
  const onRenderItem = (item, defaultRender) => {
    item.style = { // eslint-disable-line no-param-reassign
      fontWeight: item.isCurrentItem ? 'bold' : 'normal',
    };
    return (
      item.href !== undefined ?
        <NavLink
          to={item.href}
          key={item.text}
          className="app-breadcrumb"
          href="/applications"
        >
          {defaultRender({ ...item, href: undefined })}
        </NavLink>
        :
        defaultRender({ ...item, href: undefined })
    );
  };

  const { location } = props.history;
  const {
    applicationId, workflowId, workflowInstanceId, workflowInstance,
  } = props;
  const makeItems = () => {
    const items = [
      {
        text: 'Applications',
        href: '/applications',
      },
    ];
    const getContractDescription = () => {
      if (workflowInstance) {
        return workflowInstance.contractProperties[0].value;
      }
      return '';
    };

    const onContractsPage = location.pathname === `/applications/${applicationId}/workflows/${workflowId}`;
    const onDashboardPage = location.pathname === `/applications/${applicationId}/workflows/${workflowId}/workflowInstance/${workflowInstanceId}`;
    const contractBreadcrumb = {
      text: 'Contracts',
      href: `/applications/${applicationId}/workflows/${workflowId}`,
    };
    const dashboardBreadcrumb = {
      text: getContractDescription(),
    };

    if (onContractsPage) {
      items.push(contractBreadcrumb);
    }

    if (onDashboardPage) {
      items.push(contractBreadcrumb);
      items.push(dashboardBreadcrumb);
    }

    return items;
  };

  if (location.pathname === '/' || location.pathname === '/applications') {
    return <div className="emptyBreadcrumb" />;
  }

  if (!props.currentView) {
    return <div />;
  }

  return (
    <Breadcrumb
      onRenderItem={onRenderItem}
      items={makeItems()}
      className="myBreadcrumbs"
    />
  );
};

const mapStateToProps = state => ({
  applicationId: state.navigation.applicationId,
  workflowId: state.navigation.workflowId,
  workflowInstanceId: state.navigation.workflowInstanceId,
  currentView: state.view.showAdminView,
  workflowInstance: state.dashboard.workflowInstance,
});

MyBreadcrumbs.propTypes = {
  applicationId: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  workflowId: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  workflowInstanceId: PropTypes.oneOfType([
    PropTypes.string,
    PropTypes.number,
  ]),
  history: PropTypes.shape({
    location: PropTypes.shape({
      pathname: PropTypes.string,
    }),
  }).isRequired,
  currentView: PropTypes.bool.isRequired,
  // eslint-disable-next-line react/forbid-prop-types
  workflowInstance: PropTypes.object,
};

MyBreadcrumbs.defaultProps = {
  applicationId: '',
  workflowId: '',
  workflowInstanceId: '',
  workflowInstance: null,
};

export default withRouter(connect(mapStateToProps)(MyBreadcrumbs));
