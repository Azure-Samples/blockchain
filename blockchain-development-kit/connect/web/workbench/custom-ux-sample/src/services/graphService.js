import { Client } from '@microsoft/microsoft-graph-client';

export default class GraphService {
  constructor(baseUrl, authService, debugLogging = false) {
    this.client = Client.init({
      baseUrl,
      authProvider: authService.getAccessToken,
      debugLogging,
    });
  }

  searchForPeople = async (searchText, top = 20) => {
    if (!searchText) {
      return [];
    }

    const query = [
      `startswith(givenName, '${searchText}') or`,
      `startswith(surname, '${searchText}') or`,
      `startswith(mail, '${searchText}') or`,
      `startswith(userPrincipalName, '${searchText}') or`,
      `startswith(displayName, '${searchText}')`,
    ].join(' ');

    const result = await this.client.api('/users')
      .filter(query)
      .top(top)
      .get();

    return result ? result.value : [];
  }
}
