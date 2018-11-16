import Config from '../common/config';
import AuthService from './authService';
import ApiService from './apiService';
import GraphService from './graphService';
import WeatherService from './weatherService';

const authService = new AuthService(Config.aad.tenant, Config.aad.appId, Config.redirectUri);
const apiService = new ApiService(Config.baseUrl, Config.apiVersion, authService);
const graphService = new GraphService(`${Config.baseUrl}/api/${Config.apiVersion}/graphProxy`, authService);
const weatherService = new WeatherService();

export {
  authService,
  apiService,
  graphService,
  weatherService,
};
