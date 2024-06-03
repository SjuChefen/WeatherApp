# City Weather Dashboard

## Overview

The City Weather Dashboard is a web application that provides real-time weather updates for cities. It utilizes WebSockets for real-time communication and updates, fetching weather data from an external API, and displaying it on a responsive user interface.

## Features

- Real-time weather updates using WebSockets
- Fetch weather data from an external API
- Display temperature, humidity, wind speed, wind direction, and weather icon
- Handle incorrect or misspelled city names with error messages
- Interactive chart displaying temperature over time

## Technologies Used

- **Backend**: Spring Boot, Spring Web, Spring Data JPA, REST API, WebSockets
- **Frontend**: HTML, CSS, JavaScript, Chart.js, SockJS, STOMP.js
- **Database**: PostgreSQL
- **Others**: Lombok, Maven, SLF4J

## Getting Started

1. Configure API keys and URLs:

   Create a `application.properties` file in `src/main/resources` and add your API key and URL:

    ```properties
    weather.api.key=your_openweather_api_key
    weather.api.url=http://api.openweathermap.org/data/2.5/weather?q=%s&appid=%s
    weather.update.rate=60000
    ```

2. Build and run the application:

    ```bash
    mvn clean install
    mvn spring-boot:run
    ```

3. Open your browser and navigate to:

    ```
    http://localhost:8080/weather?city=Odense
    ```

## Usage

1. **City Input**: Enter the name of a city in the input box and press "Update Weather".
2. **Real-time Updates**: The dashboard will automatically update with the latest weather data.
3. **Error Handling**: If an incorrect or misspelled city name is entered, an error message will be displayed.

## Acknowledgements


- [OpenWeather](https://openweathermap.org/) for providing the weather data API.
- [Chart.js](https://www.chartjs.org/) for the charting library.
- [SockJS](https://github.com/sockjs/sockjs-client) and [STOMP.js](https://stomp-js.github.io/stomp-websocket/doc/) for WebSocket communication.

