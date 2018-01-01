package com.sirnommington.squid.services.squid;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sirnommington.squid.activity.common.AsyncResponse;
import com.sirnommington.squid.services.common.HttpResponse;
import com.sirnommington.squid.services.google.GoogleSignIn;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collection;

/**
 * Squid web service adapter.
 */
public class SquidService {

    private static final String TAG = SquidService.class.getSimpleName();
    private static final int TIMEOUT_MILLIS = 30000;

    private final String endpoint;
    private final GoogleSignIn googleSignIn;

    /**
     * @param endpoint The endpoint. e.g. https://www.host.com
     * @param googleSignIn Google sign-in used to obtain ID tokens for silent auth.
     */
    public SquidService(String endpoint, GoogleSignIn googleSignIn) {
        this.endpoint = endpoint;
        this.googleSignIn = googleSignIn;
    }

    /**
     * Adds a new device.
     * @param name The device name.
     * @param gcmToken The device GCM token.
     * @return Result indicating added, already existed, etc.
     */
    public AsyncResponse<AddDeviceResult> addDevice(String name, String gcmToken) {
        final JSONObject body = new JSONObject();
        try {
            body.put("name", name);
            body.put("gcmToken", gcmToken);
        } catch(JSONException e) {
            Log.e(TAG, "addDevice(): error creating JSON body: " + e);
            return AsyncResponse.createError(e);
        }

        final HttpResponse<DeviceModel> response = this.sendRequest("POST", "/api/devices", body, new JsonParser() {
            @Override
            public Object parse(String jsonString) throws JSONException {
                return DeviceModel.from(jsonString);
            }
        });

        if(response.isSuccess()) {
            return AsyncResponse.create(new AddDeviceResult(response.body, response.statusCode == 200));
        } else {
            return AsyncResponse.createError(response.body);
        }
    }

    /**
     * Removes a device.
     * @param deviceId The
     * @return True if the device was successfully removed.
     */
    public boolean removeDevice(String deviceId) {
        final HttpResponse response = this.sendRequest("DELETE", "/api/devices/" + deviceId, null, null);
        return response.statusCode == 200;
    }

    /**
     * Gets the user's devices.
     * TODO What does this return if the user doesn't exist or had no devices?
     * @return The devices, or an error.
     */
    public AsyncResponse<Collection<DeviceModel>> getDevices() {
        final HttpResponse<Collection<DeviceModel>> response = this.sendRequest("GET", "/api/devices", null,
            new JsonParser() {
                @Override
                public Object parse(String jsonString) throws JSONException{
                    Gson gson = new Gson();
                    Type type = new TypeToken<Collection<DeviceModel>>() {}.getType();
                    return gson.fromJson(jsonString, type);
                }
            });

        if(response.statusCode == 200) {
            return AsyncResponse.create(response.body);
        }

        return AsyncResponse.createError(response.statusCode);
    }

    /**
     * Sends the URL to a device.
     * @param deviceId The recipient device ID.
     * @param url The URL to send.
     * @return True iff sending the URL succeeded.
     */
    public boolean sendUrl(String deviceId, String url) {
        JSONObject body;
        try {
            body = new JSONObject();
            body.put("url", url);
        } catch(JSONException e) {
            Log.e(TAG, "sendUrl(): error creating JSON body: " + e);
            return false;
        }

        final HttpResponse response = this.sendRequest("POST", "/api/devices/" + deviceId + "/commands", body, null);
        return response.isSuccess();
    }

    /**
     * Helper method for sending HTTP requests.
     * @param requestMethod GET, PUT, etc.
     * @param relativePath The relative path. Must be preceded with a forward slash.
     * @param requestBody The JSON request body, if any.
     * @return The HTTP response.
     */
    private HttpResponse sendRequest(
            final String requestMethod,
            final String relativePath,
            final JSONObject requestBody,
            final JsonParser responseParser) {
        // Bomb out early if we can't get an ID token for authentication
        final String idToken = this.googleSignIn.silentSignIn();
        if(idToken == null) {
            final String errorMessage = "Client side error. Unable to obtain Google ID token. User is not signed in.";
            Log.e(TAG, errorMessage);
            return new HttpResponse(401, errorMessage);
        }

        // Create the URL
        final String urlString = this.endpoint + relativePath;
        URL url;
        try {
            url = new URL(urlString);
        } catch(MalformedURLException e) {
            Log.e(TAG, "Invalid URL=" + urlString + ", e=" + e);
            return new HttpResponse(500, null);
        }

        try {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setRequestMethod(requestMethod);
            } catch (ProtocolException e) {
                Log.e(TAG, "Invalid HTTP method=" + requestMethod + ", e=" + e);
                return new HttpResponse(500, null);
            }
            conn.setConnectTimeout(TIMEOUT_MILLIS);

            // Header prefixes are one of the following:
            // 'Bearer Google OAuth Access Token='
            // 'Bearer Google OAuth ID Token='
            // See http://stackoverflow.com/questions/8311836/how-to-identify-a-google-oauth2-user/13016081#13016081 for
            // details on Google access vs. ID tokens
            conn.setRequestProperty("Authorization", "Bearer Google OAuth ID Token=" + idToken);

            if (requestBody != null) {
                conn.setRequestProperty("Content-Type", "application/json");
            }

            conn.connect();

            if (requestBody != null) {
                DataOutputStream output = new DataOutputStream(conn.getOutputStream());
                output.writeBytes(requestBody.toString());
                output.flush();
                output.close();
            }

            final int responseCode = conn.getResponseCode();
            Object parsedBody = null;
            if (responseParser != null && (responseCode == 200 || responseCode == 302)) {
                final String bodyString = readAll(conn.getInputStream());
                parsedBody = responseParser.parse(bodyString);
            }
            return new HttpResponse<>(responseCode, parsedBody);
        } catch(IOException e) {
            return new HttpResponse(500, null);
        } catch(JSONException e) {
            return new HttpResponse(500, null);
        }
    }

    /**
     * Reads everything from the stream into a string. Returns null if stream is null.
     */
    private static String readAll(InputStream stream) throws IOException {
        if(stream == null) return null;

        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuffer stringBuffer = new StringBuffer();

        String line;
        while((line = reader.readLine()) != null) {
            stringBuffer.append(line);
        }

        return stringBuffer.toString();
    }

    /**
     * Interface for parsing a JSON string
     * @param <T> The type that will be parsed.
     */
    private interface JsonParser<T> {
        T parse(String jsonString) throws JSONException;
    }
}
