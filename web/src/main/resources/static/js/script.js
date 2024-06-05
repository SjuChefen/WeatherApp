let stompClient = null;
let city = 'Odense';

document.addEventListener("DOMContentLoaded", function() {
    const urlParams = new URLSearchParams(window.location.search);
    city = urlParams.get('city') || city;
    document.getElementById("cityInput").value = city;
    connect();
    fetchWeatherData(city).then(data => {
        if (data) {
            renderChart(data);
        }
    });
});

function connect() {
    if (stompClient && stompClient.connected) {
        return;
    }
    const socket = new SockJS('/weather-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/weather/' + city, function(weatherUpdate) {
            const data = JSON.parse(weatherUpdate.body);
            updateChart(data);
        });
    }, function(error) {
        console.error('Connection error: ', error);
        setTimeout(connect, 5000); // Retry connection after 5 seconds
    });
}

let updateCityTimeout;
function updateCity() {
    clearTimeout(updateCityTimeout);
    updateCityTimeout = setTimeout(() => {
        const newCity = document.getElementById('cityInput').value;
        if (newCity && newCity !== city) {
            city = newCity;
            if (stompClient) {
                stompClient.disconnect(() => {
                    connect();
                    fetchWeatherData(city).then(data => {
                        if (data) {
                            renderChart(data);
                            document.getElementById('errorMessage').style.display = 'none';
                        }
                    }).catch(error => {
                        document.getElementById('errorMessage').textContent = error.message;
                        document.getElementById('errorMessage').style.display = 'block';
                    });
                });
            }
            const newUrl = `${window.location.origin}${window.location.pathname}?city=${city}`;
            window.history.pushState({ path: newUrl }, '', newUrl);
        }
    }, 500);
}

async function fetchWeatherData(city) {
    try {
        const response = await fetch('/api/weather?city=' + city);
        if (!response.ok) {
            throw new Error(await response.text());
        }
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error fetching weather data:', error);
        throw error;
    }
}

let weatherChart;
function renderChart(data) {
    const ctx = document.getElementById('weatherChart').getContext('2d');
    const times = data.map(d => new Date(d.time * 1000).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false }));
    const temperatures = data.map(d => d.temperature);

    if (weatherChart) {
        weatherChart.destroy();
    }

    weatherChart = new Chart(ctx, {
        type: 'line',
        data: {
            labels: times,
            datasets: [{
                label: 'Temperature (°C)',
                data: temperatures,
                borderColor: 'rgba(0, 123, 255, 1)',
                backgroundColor: 'rgba(0, 123, 255, 0.2)',
                borderWidth: 2
            }]
        },
        options: {
            plugins: {
                legend: {
                    labels: {
                        color: '#333333',
                        font: {
                            size: 14
                        }
                    }
                }
            },
            scales: {
                x: {
                    type: 'category',
                    title: {
                        display: true,
                        text: 'Time',
                        color: '#333333',
                        font: {
                            size: 14
                        }
                    },
                    ticks: {
                        color: '#333333',
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.1)'
                    }
                },
                y: {
                    beginAtZero: false,
                    title: {
                        display: true,
                        text: 'Temperature (°C)',
                        color: '#333333',
                        font: {
                            size: 14
                        }
                    },
                    ticks: {
                        color: '#333333',
                        font: {
                            size: 12
                        }
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.1)'
                    }
                }
            }
        }
    });

    updateWeatherDetails(data);
}

function updateChart(data) {
    if (!weatherChart) return;
    const times = data.map(d => new Date(d.time * 1000).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', hour12: false }));
    const temperatures = data.map(d => d.temperature);

    weatherChart.data.labels = times;
    weatherChart.data.datasets[0].data = temperatures;
    weatherChart.update();

    updateWeatherDetails(data);
}

function updateWeatherDetails(data) {
    if (data.length > 0) {
        const latestData = data[data.length - 1];
        document.getElementById('humidity').textContent = latestData.humidity;
        document.getElementById('windSpeed').textContent = latestData.windSpeed;
        document.getElementById('windDirection').textContent = getWindDirection(latestData.windDirection);
        document.getElementById('weatherIcon').src = `http://openweathermap.org/img/w/${latestData.icon}.png`;
    }
}

function getWindDirection(degree) {
    const directions = ['N', 'NE', 'E', 'SE', 'S', 'SW', 'W', 'NW'];
    const index = Math.round((degree % 360) / 45) % 8;
    return directions[index];
}
