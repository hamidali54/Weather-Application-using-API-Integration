import com.google.gson.Gson;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class WeatherApp {

    // --- AAPKA ORIGINAL DATA STRUCTURE ---
    public static class WeatherAppResponse {
        public String name;
        public MainData main;
        public List<WeatherData> weather;

        public WeatherAppResponse() {
            main = new MainData();
            weather = new ArrayList<>();
            weather.add(new WeatherData());
        }
    }

    public static class MainData {
        public double temp; // Double kyunke aapne GUI mein Math.round() lagaya tha
        public int humidity;
    }

    public static class WeatherData {
        public String description;
    }


    public static WeatherAppResponse getWeatherData(String city) throws Exception {
     
        String apiKey ="USE YOUR API KEY HERE";
        // Check karein ke city ke baad .replace laga hua hai ya nahi
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city.replace(" ", "%20") + "&appid=" + apiKey + "&units=metric";

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            Gson gson = new Gson();
            return gson.fromJson(response.body(), WeatherAppResponse.class);
        } else {
            // Agar city na mile ya error ho toh exception throw karein
            throw new Exception("City not found. HTTP Status: " + response.statusCode());
        }
    }
}

