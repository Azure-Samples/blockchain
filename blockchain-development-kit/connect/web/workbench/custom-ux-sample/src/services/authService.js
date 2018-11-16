import AuthenticationContext from 'adal-angular/dist/adal.min';

export default class AuthService {
  constructor(tenant, clientId, redirectUri, expireOffsetSeconds = 300) {
    this.clientId = clientId;
    this.authContext = new AuthenticationContext({
      instance: 'https://login.microsoftonline.com/',
      tenant,
      clientId,
      redirectUri,
      expireOffsetSeconds,
      cacheLocation: 'localStorage', // Need this, otherwise Safari and Edge dont work
    });
  }

  login = () => {
    const error = this.authContext.getLoginError();
    if (error) {
      return;
    }

    if (this.authContext.isCallback(window.location.hash)) {
      this.authContext.handleWindowCallback();
    } else {
      this.authContext.login();
    }
  }

  logout = () => {
    this.authContext.logOut();
  }

  getAccessToken = callback =>
    new Promise((resolve, reject) => {
      this.authContext.getCachedUser(); // need this, otherwise token renewal wont work
      this.authContext.acquireToken(this.clientId, (error, accessToken) => {
        if (error || !accessToken) {
          this.authContext.login();
          return callback ? callback(error) : reject();
        }

        return callback ? callback(null, accessToken) : resolve(accessToken);
      });
    });

  isAuthenticated = () => this.authContext.getCachedToken(this.clientId) !== null
}
