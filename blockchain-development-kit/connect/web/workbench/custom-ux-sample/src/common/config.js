const isProduction = process.env.NODE_ENV === 'production';

const config = {};

if (isProduction) {
  config.baseUrl = '###{APP_BUILDER_API_URL}###';
  config.buildVersion = '###{DISPLAY_RELEASE_VERSION}###';
  config.aad = {
    tenant: '###{AAD_TENANT_DOMAIN_NAME}###',
    appId: '###{AAD_APP_ID}###'
  };
  config.bingmaps = '###{REACT_APP_BINGMAPS_KEY}###';
  config.weatherApi = '###{REACT_APP_WEATHER_API_URL}###';
} else {
  config.baseUrl = process.env.REACT_APP_APP_BUILDER_API_URL;
  config.buildVersion = 'dev';
  config.aad = {
    tenant: process.env.REACT_APP_AAD_TENANT_DOMAIN_NAME,
    appId: process.env.REACT_APP_AAD_APP_ID
  };
  config.bingmaps = process.env.REACT_APP_BINGMAPS_KEY;
  config.weatherApi = process.env.REACT_APP_WEATHER_API_URL;
}

// Removing trailing slash
config.baseUrl = config.baseUrl.replace(/\/$/, '');
config.redirectUri = window.location.origin;
config.apiVersion = 'v1';

export default config;
