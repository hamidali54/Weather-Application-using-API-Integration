import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest;
import com.google.gson.Gson;
import java.net.URI;
import java.util.*;

public class WeatherApp {

    static class WeatherAppResponse {
        String name;
        MainData main;
        List<WeatherCondition> weather;
    }

    static class MainData {
        double temp;
        int humidity;
    }

    static class WeatherCondition {
        String description;
    }

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.print("enter city name : ");

        String city = scanner.nextLine();
        scanner.close();
        String apiKey = "7b2666f115e43f8e3fcfe13383273227";
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey + "&units=metric";

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                Gson gson = new Gson();
                WeatherAppResponse weatherData = gson.fromJson(response.body(), WeatherAppResponse.class);

                String desc = weatherData.weather.get(0).description;
                String formattedDesc = desc.substring(0, 1).toUpperCase() + desc.substring(1);

                System.out.println("=============================");
                System.out.println("   WEATHER REPORT: " + weatherData.name);
                System.out.println("=============================");
                System.out.println("Temperature : " + weatherData.main.temp + "°C");
                System.out.println("Humidity    : " + weatherData.main.humidity + "%");
                System.out.println("Conditions  : " + formattedDesc);
                System.out.println("=============================");

            } else {
                System.out.println("Failed to fetch data. HTTP Status Code: " + response.statusCode());
            }

        } catch (Exception e) {
            System.out.println("Failed to fetch data : " + e.getMessage());
        }
    }
}