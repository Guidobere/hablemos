package app.hablemos.weather;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {


    private static final Double HEAT_LIMIT_TEMPERATURE = 25.0D;

    private static final String OPEN_WEATHER_MAP_API =
            "http://api.openweathermap.org/data/2.5/weather?units=metric";

    private static String getLat() {
        //TODO implement method
        return "-34D";
    }

    private static String getLon() {
       //TODO implement method
       return "-58D";
    }

    private static JSONObject getWeather(String lat, String lon) {
        try {
            URL url = new URL(OPEN_WEATHER_MAP_API + "&lat=" + lat + "&lon=" + lon);
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

    public static Boolean isItHotToday() throws Exception {
        JSONObject weather = getWeather(getLat(), getLon());

        Double temp = null;
        try {
            temp = weather.getJSONObject("main").getDouble("temp");
        } catch (JSONException e) {
            throw new Exception();
        }

        if (temp >= HEAT_LIMIT_TEMPERATURE) return true;

        return false;
    }

    public static Boolean isSuitableForOutsideActivity() throws Exception {
        JSONObject weather = getWeather(getLat(), getLon());

        try {
            Double temp = weather.getJSONObject("main").getDouble("temp");
        } catch (JSONException e) {
            throw new Exception();
        }

        //TODO add conditions based on temperature, humidity, etc
        return true;
    }

}
