# Supply Chain React App

A web application to illustrate alternate ways of visualizing Workbeanch applications and contracts. The web application was built using Create React App and makes API calls to a deployed Workbench instance.

It makes use of properties and actions as defined in the _Coffee_ smart contract and configuration files, available in the root of this source directory.

## Setup

From a command prompt, install NPM modules from the directory that contains the package.json file:

```
$ npm install --no-optional
```

This same directory also contains a .env.template file:

```
REACT_APP_AAD_TENANT_DOMAIN_NAME=
REACT_APP_AAD_APP_ID=
REACT_APP_APP_BUILDER_API_URL=<backendurl>
REACT_APP_BINGMAPS_KEY=<bingmaps_api_key>
REACT_APP_WEATHER_API_URL=<weather_api_url>
```

The first three are standard Workbench instance configuration variables.

| Entry                     | Description                                                                   |
| ------------------------- | :---------------------------------------------------------------------------- |
| REACT_APP_BINGMAPS_KEY    | _Required_: Used when rendering a Bing map on the Contract Detail screen.     |
| REACT_APP_WEATHER_API_URL | _Optional_: Used to lookup historical weather data for each map pin location. |

Once the Bing map API key is configured, a local instance of the web app can be run:

```
$ npm start
```

## Weather API

If no weather API URL is configured (via `REACT_APP_WEATHER_API_URL`) then a default JSON payload will be used. Hence, use of a weather API is optional for demomnstration purposes.

There are three options for how to configure a weather API.

1.  Leave the configuration entry empty and use the default JSON payload. Obviously, the weather reported at each map pin will always be the same regardless of time or place.
1.  Set the `REACT_APP_WEATHER_API_URL` entry to the DarkSky API endpoint URL directly. This will require a workaround for most browser's CORS security restrictions.
1.  Use the provided DarkSky proxy (an ASP.NET Core Web API). This will hide the DarkSky API key and supports CORS.

DarkSky's weather API provides a free tier, but intentionally does not support CORS as each user's API key is part of the API URL. Therefore, it is recommended to use an intermediate proxy.

Some browsers support extensions that can disable the CORS security restrictions and allow the non-CORS DarkSky API to be called directly from a browser. This may be fine for demomnstration purposes.

## Where To Find API Keys

- DarkSky Weather API: https://darksky.net/dev
- Bing Maps API: https://www.bingmapsportal.com/
