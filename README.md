# Squid Android App
The Squid Android app is built using native Android Java.

## Setup
1. Clone the repo.
2. Copy data.xml to ```SquidAndroid\Squid\app\src\main\res\values```
3. Copy google-services.json to ```SquidAndroid\Squid```
4. Open in Android Studio, sync gradle, and build the app.

### Missing files
data.xml and google-services.json were intentionally omitted from the repo because they contain secret and/or configuration specific details.

If you are crafting your own data.xml, it should be fairly straightforward to determine what fields are missing.