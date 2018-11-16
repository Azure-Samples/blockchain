import axios from 'axios';

export default class ApiService {
  constructor(baseUrl, apiVersion, authService) {
    this.baseUrl = baseUrl;
    this.apiVersion = apiVersion;
    this.authService = authService;
  }

  static get GET() {
    return 'GET';
  }

  static get POST() {
    return 'POST';
  }

  static get PUT() {
    return 'PUT';
  }

  static get PATCH() {
    return 'PATCH';
  }

  static get DELETE() {
    return 'DELETE';
  }

  static get APPLICATIONS_ROUTE() {
    return 'applications';
  }

  static get ROLE_ASSIGNMENTS_ROUTE() {
    return 'roleAssignments';
  }

  static get WORKFLOWS_ROUTE() {
    return 'workflows';
  }

  static get CONTRACTS_ROUTE() {
    return 'contracts';
  }

  static get CONTRACT_CODE_ROUTE() {
    return 'contractCode';
  }

  static get CAPABILITIES_ROUTE() {
    return 'capabilities';
  }

  static get CHECKERS_ROUTE() {
    return 'checkers';
  }

  static get USERS_ROUTE() {
    return 'users';
  }

  async makeRequest(method, path, params = {}, data = {}) {
    try {
      const token = await this.authService.getAccessToken();
      const req = await axios({
        method,
        url: `${this.baseUrl}/api/${this.apiVersion}/${path}`,
        headers: { Authorization: `Bearer ${token}` },
        params,
        data,
      });
      return req.data;
    } catch (err) {
      throw err;
    }
  }

  checkConfigFile(appFile) {
    if (!appFile) {
      throw new Error('expected valid appFile');
    }

    const path = `${ApiService.CHECKERS_ROUTE}/checkApplication`;
    const params = null;
    const data = new FormData();
    data.append('appFile', appFile);

    return this.makeRequest(ApiService.POST, path, params, data);
  }

  checkLogicFile(appFile, contractFile, ledgerId = 1) {
    if (!appFile || !contractFile) {
      throw new Error('expected valid file');
    }

    const path = `${ApiService.CHECKERS_ROUTE}/checkContractCode`;
    const params = { ledgerId };
    const data = new FormData();
    data.append('appFile', appFile);
    data.append('contractFile', contractFile);

    return this.makeRequest(ApiService.POST, path, params, data);
  }

  uploadConfigFile(appFile) {
    if (!appFile) {
      throw new Error('expected valid file');
    }

    const path = ApiService.APPLICATIONS_ROUTE;
    const params = null;
    const data = new FormData();
    data.append('appFile', appFile);

    return this.makeRequest(ApiService.POST, path, params, data);
  }

  uploadLogicFile(contractFile, applicationId, ledgerId = 1) {
    if (!contractFile) {
      throw new Error('expected valid file');
    }

    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}/${ApiService.CONTRACT_CODE_ROUTE}`;
    const params = { ledgerId };
    const data = new FormData();
    data.append('contractFile', contractFile);

    return this.makeRequest(ApiService.POST, path, params, data);
  }

  getApplications(sortBy, enabled, top, skip) {
    const path = ApiService.APPLICATIONS_ROUTE;
    const params = {
      sortBy, enabled, top, skip,
    };
    return this.makeRequest(ApiService.GET, path, params);
  }

  getWorkflows(applicationId, top, skip) {
    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}/${ApiService.WORKFLOWS_ROUTE}`;
    return this.makeRequest(ApiService.GET, path, { top, skip });
  }

  // TODO: pass top for pagination
  async getWorkflowContracts(workflowId, sortBy, top = 100, skip) {
    const path = ApiService.CONTRACTS_ROUTE;
    const params = {
      workflowId, sortBy, top, skip,
    };

    return this.makeRequest(ApiService.GET, path, params);
  }

  getWorkflowContractsConstructor(applicationId, workflowId) {
    // TODO: applicationId is not used
    const path = `${ApiService.APPLICATIONS_ROUTE}/${ApiService.WORKFLOWS_ROUTE}/${workflowId}`;
    return this.makeRequest(ApiService.GET, path);
  }

  postNewContract(postData, workflowId, contractCodeId, connectionId = 1) {
    const path = ApiService.CONTRACTS_ROUTE;
    const params = { workflowId, contractCodeId, connectionId };
    return this.makeRequest(ApiService.POST, path, params, postData);
  }

  // TODO: pass top for pagination
  getUsers(sortBy, top = 100, skip) {
    const path = ApiService.USERS_ROUTE;
    const params = { sortBy, top, skip };

    return this.makeRequest(ApiService.GET, path, params);
  }

  getMe() {
    const path = `${ApiService.USERS_ROUTE}/me`;
    return this.makeRequest(ApiService.GET, path);
  }

  createUser(data) {
    const path = ApiService.USERS_ROUTE;
    const params = null;
    return this.makeRequest(ApiService.POST, path, params, data);
  }

  getUsersByExternalId(externalId) {
    const path = ApiService.USERS_ROUTE;
    const params = { externalId };

    return this.makeRequest(ApiService.GET, path, params);
  }

  // TODO: pass top for pagination
  getUsersByAppId(applicationId, sortBy, top = 100, skip) {
    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}/${ApiService.ROLE_ASSIGNMENTS_ROUTE}`;
    const params = { sortBy, top, skip };
    return this.makeRequest(ApiService.GET, path, params);
  }

  getContractDashboard(contractId) {
    const path = `${ApiService.CONTRACTS_ROUTE}/${contractId}`;
    return this.makeRequest(ApiService.GET, path);
  }

  // TODO: pass top for pagination
  async getContractDashboardActions(contractId, top = 100, skip) {
    const path = `${ApiService.CONTRACTS_ROUTE}/${contractId}/actions`;
    const params = { top, skip };

    return this.makeRequest(ApiService.GET, path, params);
  }

  postContractDashboardAction(data, contractId, actionId) {
    const path = `${ApiService.CONTRACTS_ROUTE}/${contractId}/actions`;
    const params = null;

    return this.makeRequest(ApiService.POST, path, params, data);
  }

  createUserRoleMappingByAppId(applicationId, data) {
    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}/${ApiService.ROLE_ASSIGNMENTS_ROUTE}`;
    const params = null;

    return this.makeRequest(ApiService.POST, path, params, data);
  }

  deleteUserRoleMappingById(applicationId, userAssignmentId) {
    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}/${ApiService.ROLE_ASSIGNMENTS_ROUTE}/${userAssignmentId}`;
    return this.makeRequest(ApiService.DELETE, path);
  }

  updateUserRoleMappingById(applicationId, userAssignmentId, data) {
    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}/${ApiService.ROLE_ASSIGNMENTS_ROUTE}/${userAssignmentId}`;
    const params = null;

    return this.makeRequest(ApiService.PUT, path, params, data);
  }

  getApplicationById(applicationId) {
    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}`;
    return this.makeRequest(ApiService.GET, path);
  }

  // TODO: pass top for pagination
  getLedgerImplementationId(applicationId, top = 100, skip) {
    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}/${ApiService.CONTRACT_CODE_ROUTE}`;
    const params = { top, skip };

    return this.makeRequest(ApiService.GET, path, params);
  }

  // TODO: pass top for pagination
  getApplicationRoles(applicationId, applicationRoleId, top = 100, skip) {
    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}/${ApiService.ROLE_ASSIGNMENTS_ROUTE}`;
    const params = { applicationRoleId, top, skip };

    return this.makeRequest(ApiService.GET, path, params);
  }

  canCreateWorkflowInstance(workflowId) {
    const path = `${ApiService.CAPABILITIES_ROUTE}/canCreateContract/${workflowId}`;
    return this.makeRequest(ApiService.GET, path);
  }

  enableApplication(applicationId) {
    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}/enable`;
    return this.makeRequest(ApiService.PATCH, path);
  }

  disableApplication(applicationId) {
    const path = `${ApiService.APPLICATIONS_ROUTE}/${applicationId}/disable`;
    return this.makeRequest(ApiService.PATCH, path);
  }
}
