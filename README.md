Customer App
-------
Customer Android Application is an online/offline capable customer tracking application. The Application provides functionality to insert customers into the local database of the device if there is no internet connection at that moment and parses the data from the database and sends them to a pre-defined server(e.g. http://demo.micropowermanager.com/api/) when internet connection is established. Location services and GPS is necessary to get the exact coordinates of a customer while insertion.

Download
--------
To configure the app, download the latest version from master branch and open with Android Studio.

Configuration
-------------
- To specificy the server url, DEFAULT_BASE_URL in SharedPreferencesWrapper.kt should be configured. Server url can can also be specified by tapping on Settings menu on main page and restarting the application.
- The App can either be used in Debug or in Release version. 

Project Info
------------
- minSdkVersion: 21
- compileSdkVersion: 29
- Programming Language: Kotlin
- Permissions: ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION, ACCESS_NETWORK_STATE, INTERNET, READ_PHONE_STATE, GET_ACCOUNTS, READ_PROFILE, READ_CONTACTS
