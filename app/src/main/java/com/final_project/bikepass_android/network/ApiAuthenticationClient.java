package com.final_project.bikepass_android.network;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Berk on 09.02.2020.
 */
public class ApiAuthenticationClient {

    private String username;
    private String password;

    public ApiAuthenticationClient(String username, String password) {
        this.username = username;
        this.password = password;
        // This is important. The application may break without this line.
        System.setProperty("jsse.enableSNIExtension", "false");
    }

    public String executeForLogin() {
        try {
            URL url = new URL("");
            JSONObject postDataParams = new JSONObject();
            //add name pair values to the connection
            postDataParams.put("register", false);
            postDataParams.put("username", username);
            postDataParams.put("password", password);
            Log.e("params", postDataParams.toString());
            Log.e("URL", url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(postDataParams.toString());
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            Log.e("responseCode", "responseCode " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {//code 200 connection OK
                //this part is to capture the server response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                do {
                    sb.append(line);
                    Log.e("MSG sb", sb.toString());
                } while ((line = in.readLine()) != null);
                in.close();
                Log.e("response", conn.getInputStream().toString());
                Log.e("textmessage", sb.toString());
                return sb.toString();//server response message
            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            //error on connection
            return new String("Exception: " + e.getMessage());
        }
    }

    public String executeForRegister() {
        try {
            URL url = new URL("");
            JSONObject postDataParams = new JSONObject();
            //add name pair values to the connection
            postDataParams.put("register", true);
            postDataParams.put("username", username);
            postDataParams.put("password", password);
            Log.e("params", postDataParams.toString());
            Log.e("URL", url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(postDataParams.toString());
            writer.flush();
            writer.close();
            os.close();
            int responseCode = conn.getResponseCode();
            Log.e("responseCode", "responseCode " + responseCode);
            if (responseCode == HttpsURLConnection.HTTP_OK) {//code 200 connection OK
                //this part is to capture the server response
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";
                do {
                    sb.append(line);
                    Log.e("MSG sb", sb.toString());
                } while ((line = in.readLine()) != null);
                in.close();
                Log.e("response", conn.getInputStream().toString());
                Log.e("textmessage", sb.toString());
                return sb.toString();//server response message
            } else {
                return new String("false : " + responseCode);
            }
        } catch (Exception e) {
            //error on connection
            return new String("Exception: " + e.getMessage());
        }
    }
}