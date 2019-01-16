from flask import Flask
from adal import AuthenticationContext

# Create an instance of the Flask class that is the WSGI application.
# The first argument is the name of the application module or package,
# typically __name__ when using a single module.
app = Flask(__name__)
auth_context = AuthenticationContext("https://login.microsoftonline.com/{Tenant-ID}")

# Flask route decorators map / and /hello to the hello function.
# To add other resources, create functions that generate the page contents
# and add decorators to define the appropriate resource locators for them.

@app.route('/')
@app.route('/hello')
def hello():
    # Render the page
    # Use this token to make API calls to workbench
	token = auth_context.acquire_token_with_client_credentials("{Workbench-API-Application-ID}", "{Client-Application-ID}", "{Client-Secret}")
	return "Hello World!"

if __name__ == '__main__':
    # Run the app server on localhost:4449
    app.run('localhost', 4449)
