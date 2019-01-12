import axios from 'axios';
import * as config from '../common/config';
import defaultData from '../assets/data/weather-api.json';

export default class WeatherService {
  constructor() {
    this.baseUrl = config.default.weatherApi;
  }

  static get GET() {
    return 'GET';
  }

  async makeRequest(method, path) {
    try {
      const req = await axios({
        method,
        url: `${this.baseUrl}/${path}`
      });
      return req.data;
    } catch (err) {
      throw err;
    }
  }

  getWeather(latitude, longitude, time) {
    if (!this.baseUrl) {
      return Promise.resolve(defaultData);
    }

    const path = `${latitude},${longitude},${time}?exclude=minutely,hourly,daily,alerts,flags`;
    return this.makeRequest(WeatherService.GET, path);
  }
}
