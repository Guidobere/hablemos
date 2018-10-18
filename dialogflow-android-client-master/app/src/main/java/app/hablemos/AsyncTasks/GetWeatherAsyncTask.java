package app.hablemos.AsyncTasks;

import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import app.hablemos.services.LocationParams;

public class GetWeatherAsyncTask extends AsyncTask<LocationParams, Void, JSONObject> {

    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?units=metric";

    private static final String OPEN_WEATHER_MAP_API_KEY = "9aca5d4292eccdfa7848453f7f7aabc2";

    @Override
    protected JSONObject doInBackground(LocationParams... params) {
        try {
            URL url = new URL(OPEN_WEATHER_MAP_API + "&lat=" + params[0].getLat() + "&lon=" + params[0].getLon());
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key",
                    "9aca5d4292eccdfa7848453f7f7aabc2");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            //If fails
            if(data.getInt("cod") != 200){
                return null;
            }

            return data;
        }catch(Exception e){
            return null;
        }
    }

}