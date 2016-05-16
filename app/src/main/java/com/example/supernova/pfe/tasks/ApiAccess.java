package com.example.supernova.pfe.tasks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiAccess extends AsyncTask<Void, Void, Response>{
    private final static String TAG = "TEST";

    private String http_method;
    private JsonElement body;
    private JSONObject body1;
    private ApiAccessWork caller;
    private Uri uri;

    @Override
    protected void onPostExecute(Response response) {
        if (caller != null) {
            caller.doStuffWithResult(response);
        }
    }

    @Override
    protected Response doInBackground(Void...params) {
        Response response = new Response();
        HttpURLConnection connection;
        BufferedReader reader = null;
        Boolean flag = this.http_method.equalsIgnoreCase("post");
        try {
            connection = (HttpURLConnection) new URL(this.uri.toString())
                    .openConnection();
            connection.setRequestMethod(this.http_method.toUpperCase());
            connection.setRequestProperty("Content-Type", "application/json");
            if (flag){
                connection.setDoOutput(true);
                connection.setDoInput(true);
                OutputStream oStream = connection.getOutputStream();
                if (this.body != null && !this.body.isJsonNull())
                    oStream.write(this.body.toString().getBytes());
                else oStream.write(this.body1.toString().getBytes());
                oStream.flush();
                oStream.close();
            }
            connection.connect();
            int code = connection.getResponseCode();
            response.setCode(code);
            if (Response.isSuccess(code)){
                StringBuilder buffer = new StringBuilder();
                if (connection.getInputStream() == null) return null;
                InputStream iStream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(iStream));
                String line;
                while ((line = reader.readLine()) != null) buffer.append(line).append("\n");
                if (buffer.length() == 0) return null;
                response.setBody(buffer.toString());
            }
        }catch(Exception e){
            e.printStackTrace();
            Log.v(TAG, " "+e.toString()+"\t\t++++++++++++++++++");
            try {
                if (reader != null) reader.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return response;
    }

    public ApiAccess setCaller(ApiAccessWork caller){
        this.caller = caller;
        return this;
    }

    public ApiAccess setMethod(String method){
        this.http_method = method;
        return this;
    }

    public ApiAccess setBody(JsonElement body){
        this.body = body;
        return this;
    }

    public ApiAccess setBody(JSONObject body){
        this.body1 = body;
        return this;
    }

    public ApiAccess setUri(Uri uri){
        this.uri = uri;
        return this;
    }

    /*
    * une interface pour les classes qui on besoin de récuperer le resultat du ApiAccess class
    */
    public interface ApiAccessWork {
        void doStuffWithResult(Response response);
    }
}
