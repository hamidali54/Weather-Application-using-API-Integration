# 🌤️ Java Weather App

A simple and efficient command-line weather application built with Java. This app fetches real-time weather data for any city using the OpenWeatherMap API and displays it in a clean, readable format directly in the terminal.

## 🚀 Features
* **Real-time Data:** Fetches live weather conditions, temperature, and humidity.
* **Modern HTTP Client:** Uses Java 11's native `java.net.http.HttpClient` for making fast API requests.
* **Smart JSON Parsing:** Utilizes Google's **Gson** library to cleanly deserialize raw JSON responses into Java Objects (POJOs).
* **Interactive:** Uses `Scanner` to take dynamic city name inputs from the user.
* **Error Handling:** Gracefully handles invalid city names and HTTP errors

## 🛠️ Technologies Used
* Java
* OpenWeatherMap API
* Gson (Google JSON Library)

## ⚙️ How to Run Locally

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/ShazilAhmedShaikh/Weather-Application-using-API-Integration.git](https://github.com/ShazilAhmedShaikh/Weather-Application-using-API-Integration.git)

2. Get your API Key:

    Sign up at OpenWeatherMap and generate a free API key.

3. Configure the API Key:

   Open WeatherApp.java.

   Replace the apiKey variable string with your actual API key.

4. Add Gson Dependency:

   Ensure the gson library (e.g., gson-2.10.1.jar) is added to your project's classpath.

Compile and Run!
