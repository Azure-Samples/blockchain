// Get appId from the text box
function getAppId() {
  var appId = $.trim($('#appIdTextBox').val());
  if (!appId) {
    return;
  }

  return appId;
}

// Set app Id on the text box
function setAppId(appId) {
  $('#appIdTextBox').val(appId);
}

// Set user's name
function setName(name) {
  $('#userName').text('Logged in as ' + name);
}

// Set reply URL text box
function setReplyUrlAddress() {
  $('#replyUrl').text(window.location.origin);
}

// Set bearer token
function setBearerToken(token) {
  $('#bearerTokenTextArea').text(token);
}

// Get the tenant name from text box
function getTenantName() {
  var tenantName = $.trim($('#tenantNameTextBox').val());
  if (!tenantName) {
    return 'common';
  }

  if (tenantName.endsWith('.onmicrosoft.com')) {
    return tenantName;
  }

  return tenantName + '.onmicrosoft.com';
}

// Set tenant name on the text box
function setTenantName(tenantName) {
  if (!tenantName || tenantName === 'common') {
    tenantName = '';
  } else if (tenantName.endsWith('.onmicrosoft.com')) {
    tenantName = tenantName.split('.')[0]
  }

  $('#tenantNameTextBox').val(tenantName);
}

// Enable/disable buttons
function toggleButtons() {
  $('#loginBtn').attr('disabled', !getAppId());
  $('#resetForm').attr('disabled', !getAppId() && !$('#tenantNameTextBox').val());
}

// Initialize copy to clipboard
function setupClipBoard() {
  var clipboard = new ClipboardJS('#copyToClipboardBtn');
  clipboard.on('success', function (e) {
    e.clearSelection();
  });
}

$(function () {
  // Setup
  setupClipBoard();
  setReplyUrlAddress();

  // Get the values from local storage if present and setting them on the text boxes
  setAppId(localStorage.getItem('appId'))
  setTenantName(localStorage.getItem('tenantName'));

  // Even handlers
  toggleButtons();
  $('#tenantNameTextBox').on('input', toggleButtons);
  $('#appIdTextBox').on('input', toggleButtons);

  var authContext;

  // If app Id is set then create the authContext
  if (getAppId()) {
    authContext = new AuthenticationContext({
      tenant: getTenantName(),
      clientId: getAppId(),
      redirectUri: window.location.origin // Set the redirect url to the origin of this website
    });
  }

  // Handle AAD call back
  if (authContext && authContext.isCallback(window.location.hash)) {
    var err = authContext.getLoginError();
    if (err) {
      // If error, set the error in the box
      return setBearerToken(err);
    }

    authContext.handleWindowCallback();
  }

  // Make sure authContext is not null and user is logged in
  if (authContext && authContext.getCachedToken(getAppId())) {
    // Enable the logout button
    $('#logoutBtn').attr('disabled', false);

    // Getting the current user
    var currentUser = authContext.getCachedUser()
    setName(currentUser.profile.name)

    authContext.getCachedUser(); // Note: due to a bug in the ADAL library this function needs to be called before calling acquireToken
    authContext.acquireToken(getAppId(), function (err, accessToken) {
      // In case of error
      if (err || !accessToken) {
        return setBearerToken(err);
      }

      setBearerToken(accessToken);
    });
  }

  // Handle login logic
  $('#loginBtn').click(function (e) {
    e.preventDefault();
    // Getting the values form text boxes
    var appId = getAppId();
    var tenantName = getTenantName();

    // Set the values in local storage for them to be available after redirect
    localStorage.setItem('appId', appId);
    localStorage.setItem('tenantName', tenantName);


    console.log('Logging with to App Id ' + appId + 'on tenant ' + tenantName);
    // Creating the authContext object
    authContext = new AuthenticationContext({
      tenant: tenantName,
      clientId: appId,
      redirectUri: window.location.origin
    });

    authContext.login();
  });

  // Reset the form and clear the localstorage
  $('#resetForm').click(function (e) {
    e.preventDefault();
    setTenantName('');
    setAppId('');
    setBearerToken('');
    localStorage.clear();
    toggleButtons();
  });

  // Handle logout logic
  $('#logoutBtn').click(function (e) {
    e.preventDefault();
    authContext.logOut();
  });
});
