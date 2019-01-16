var qs = require('qs');
const axios = require('axios');


const AUTHORITY = 'https://login.microsoftonline.com/<tenant_name>';
const WORKBENCH_API_URL = "<Workbench API URL>";
const RESOURCE = "<Workbench AppId>";
const CLIENT_APP_Id = "<service principal AppId>";
const CLIENT_SECRET = "<service principal secret>";


// Getting token from AAD
const acquireTokenWithClientCredentials = async (resource, clientId, clientSecret, authority) => {
  const requestBody = {
    resource: resource,
    client_id: clientId,
    client_secret: clientSecret,
    grant_type: 'client_credentials'
  };

  const response = await axios({
    method: 'POST',
    url: `${authority}/oauth2/token`,
    data: qs.stringify(requestBody),
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
  });

  return response.data;
}


main = async () => {
  try {
    const token = await acquireTokenWithClientCredentials(RESOURCE, CLIENT_APP_Id, CLIENT_SECRET, AUTHORITY);

    // Calling workbench API
    const response = await axios({
      method: 'GET',
      url: `${WORKBENCH_API_URL}/api/v1/users`,
      headers: {'Authorization': `Bearer ${token.access_token}`},
    });

    console.log(response.data);
  }
  catch (err) {
    console.error(err);
  }
}

main();
