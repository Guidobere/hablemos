package app.hablemos.services;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.hablemos.AsyncTasks.GetWeatherAsyncTask;

public class WeatherService {

    private static final Double HEAT_LIMIT_TEMPERATURE = 25.0D;
    private static final Double HEAT_LOWER_LIMIT_OUTDOOR_ACTIVITY = 10.0D;
    private static final Double HEAT_UPPER_LIMIT_OUTDOOR_ACTIVITY = 25.0D;



    private String getLat() {
        //TODO implement method
        return "-34";
    }

    private String getLon() {
       //TODO implement method
       return "-58";
    }

    public Boolean isHotToday() throws Exception {

        try {
            LocationParams location = new LocationParams(this.getLat(), this.getLon());
            JSONObject weather = new GetWeatherAsyncTask().execute(location).get();
            Double temp = null;
            temp = weather.getJSONObject("main").getDouble("temp");
            if (temp >= HEAT_LIMIT_TEMPERATURE) return true;
        } catch (JSONException e) {
            throw new Exception();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public Boolean isSuitableForOutsideActivity() throws Exception {
        try {
            LocationParams location = new LocationParams(this.getLat(), this.getLon());
            JSONObject weather = new GetWeatherAsyncTask().execute(location).get();

            JSONArray weatherArray = weather.getJSONArray("weather");
            if (weatherArray!=null && weatherArray.length()>0) {
                for (int i = 0; i < weatherArray.length(); i++) {
                    JSONObject objects = weatherArray.getJSONObject(i);
                    int id = objects.getInt("id");

                    if (id < 800 || id >= 900) {
                        return false;
                    }
                }

                Double temp = weather.getJSONObject("main").getDouble("temp");

                if (temp >= HEAT_LOWER_LIMIT_OUTDOOR_ACTIVITY && temp <= HEAT_UPPER_LIMIT_OUTDOOR_ACTIVITY) {
                    return true;
                }

                return false;
            }

            return false;
        } catch (JSONException e) {
            throw new Exception();
        }
    }

}
