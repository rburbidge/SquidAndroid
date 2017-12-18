package com.sirnommington.squid.services.squid;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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
import java.net.URL;
import java.util.Collection;

/**
 * Squid web service adapter.
 */
public class SquidService {

    private static final String TAG = "SquidService";

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
     * @throws IOException If there is an issue sending the request.
     * @throws JSONException If there is an issue constructing the outgoing request.
     */
    public AddDeviceResult addDevice(String name, String gcmToken) throws IOException, JSONException {
        final JSONObject body = new JSONObject();
        body.put("name", name);
        body.put("gcmToken", gcmToken);

        final HttpResponse<DeviceModel> response = this.sendRequest("POST", "/api/devices", body, new JsonParser() {
            @Override
            public Object parse(String jsonString) throws JSONException {
                return DeviceModel.from(jsonString);
            }
        });
        if(response.statusCode != 200 && response.statusCode != 302) {
            throw new IOException("An error occurred while adding the device");
        }

        return new AddDeviceResult(response.body, response.statusCode == 200);
    }

    /**
     * Removes a device.
     * @param deviceId The
     * @return True if the device was successfully removed.
     * @throws IOException If there is an issue sending the request.
     * @throws JSONException If there is an issue parsing the response.
     */
    public boolean removeDevice(String deviceId) throws IOException, JSONException {
        final HttpResponse response = this.sendRequest("DELETE", "/api/devices/" + deviceId, null, null);
        return response.statusCode == 200;
    }

    /**
     * Gets the user's devices.
     *
     * TODO What does this return if the user doesn't exist or had no devices?
     * @return The devices, or null if an error occurred.
     * @throws IOException If there is an issue sending the request.
     * @throws JSONException If there is an issue parsing the response.
     */
    public Collection<DeviceModel> getDevices() throws IOException, JSONException {
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
            return response.body;
        }

        return null;
    }

    /**
     * Sends the URL to a device.
     * @param deviceId The recipient device ID.
     * @param url The URL to send.
     * @throws IOException If there is an issue sending the request.
     * @throws JSONException If there is an issue parsing the response.
     */
    public void sendUrl(String deviceId, String url) throws JSONException, IOException {
        final JSONObject body = new JSONObject();
        body.put("url", url);

        this.sendRequest("POST", "/api/devices/" + deviceId + "/commands", body, null);
    }

    /**
     * An HTTP Response.
     * @param <TBody> The body type.
     */
    private static class HttpResponse<TBody> {
        public final int statusCode;
        public final TBody body;

        public HttpResponse(int statusCode, TBody body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }

    /**
     * Helper method for sending HTTP requests.
     * @param requestMethod GET, PUT, etc.
     * @param relativePath The relative path. Must be preceded with a forward slash.
     * @param requestBody The JSON request body, if any.
     * @throws IOException If there is an issue sending the request.
     * @return The HTTP response.
     */
    private HttpResponse sendRequest(
            final String requestMethod,
            final String relativePath,
            final JSONObject requestBody,
            final JsonParser responseParser) throws IOException, JSONException {
        // Bomb out early if we can't get an ID token for authentication
        final String idToken = this.googleSignIn.silentSignIn();
        if(idToken == null) {
            final String errorMessage = "Client side error. Unable to obtain Google ID token. User is not signed in.";
            Log.e(TAG, errorMessage);
            return new HttpResponse(401, errorMessage);
        }

        URL url = new URL(this.endpoint + relativePath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(requestMethod);

        // Header prefixes are one of the following:
        // 'Bearer Google OAuth Access Token='
        // 'Bearer Google OAuth ID Token='
        // See http://stackoverflow.com/questions/8311836/how-to-identify-a-google-oauth2-user/13016081#13016081 for
        // details on Google access vs. ID tokens
        conn.setRequestProperty("Authorization", "Bearer Google OAuth ID Token=" + idToken);

        if(requestBody != null) {
            conn.setRequestProperty("Content-Type", "application/json");
        }

        conn.connect();

        if(requestBody != null) {
            DataOutputStream output = new DataOutputStream(conn.getOutputStream());
            output.writeBytes(requestBody.toString());
            output.flush ();
            output.close ();
        }

        final int responseCode = conn.getResponseCode();
        Object parsedBody = null;
        if(responseParser != null && (responseCode == 200 || responseCode == 302)) {
            final String bodyString = readAll(conn.getInputStream());
            parsedBody = responseParser.parse(bodyString);
        }
        return new HttpResponse<>(responseCode, parsedBody);
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
