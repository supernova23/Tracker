package com.example.supernova.pfe.tasks;

import android.net.Uri;
import android.util.Log;

import com.example.supernova.pfe.data.LocationInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ExecutionException;

public class SendLocation {

    private String toSend;

    public SendLocation(LocationInfo location){
        this.toSend = buildJson(location);
    }

    public String startSending(){
        Log.v("SendLocation : ", " start sending *****************************");
        if(this.toSend != null && this.toSend.length() > 0){
            Uri uri = Uri.parse("http://10.0.3.2:3000").buildUpon()
                    .appendPath("api")
                    .appendPath("insertPosition").build();
            try {
                return new ApiAccess()
                        .setUri(uri).setMethod("POST")
                        .setBody(this.toSend).execute()
                        .get();
            } catch (Exception e) { e.printStackTrace(); }
        }
        return null;
    }

    public String buildJson(LocationInfo location){
        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        String toSend = null;
        try {
            json.put("imei", location.getUuid());
            json.put("time", location.getTime());
            JSONArray array = new JSONArray();
            array.put(0, location.getLt());
            array.put(1, location.getLg());
            json.put("position",array);
            jsonArray.put(json);
            toSend = jsonArray.toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return toSend;
    }
}