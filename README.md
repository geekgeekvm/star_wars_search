### To run Angular App

```
cd/angular
docker build -t starwars_angular .
docker run -p 3000:3000 starwars_angular
```

### To run Spring Boot App

```
cd spring-boot
docker build -t starwars_spring_boot .
docker run -p 8080:8080 starwars_spring_boot
```


### Introduction to the Project

Star Wars Searcher provides a lightwieght UI to query a the Star Wars API using a Java/ Springboot wrapper to perform data interpretation of the contents of SWAPI.

Design and Implmentation approach:

For the UI i used Angular, which provides the user with two inputs, the Type and the Name, to query objects from SWAPI with the provided "Search" button. 
If the user provides correct inputs, the response is displayed on the screen with two fields - Count and Films.
In case of any errors, a Toast is displayed with a meaningful error message, including no data found messages.
There is also an offline toggle mode provided for the user in case the user does not want to fetch the data directly from the SWAPI.

I followed the design approach that does not add much business logic to the UI, the actual logical implementation of data manipulation is delegated to a Java service running in the backend. 
A SpringBoot framework was used to develop the service and a REST API was exposed for the UI to query. The application was structured with distinct Controller, Service and Model layers.
The API was designed to provide the response in exactly the format the user looks for, i.e,
where the SWAPI returns APIs for films, the service immediately queries SWAPI for details of the films and populates the response accordingly.
The standard Rest template is used for the B2B calls to SWAPI. 
To support downtime in the Third Party API, I have scraped all data from SWAPI and loaded it into a JSON file packaged along with the service. 
In case the application finds the API offline, it will query from the JSON.

Finally, the application has been dockerized and communication between the UI and Backend was established.

Design Patterns used in this approach were:
1. Singleton Pattern : Services created in Angular app such as Toast Service, Search Service
2. Inversion of Control Pattern: Dependency injection in Angular App such as using HttpClient
3. Observer Pattern:  EventEmitter used in Angular app to toggle offline mode and listened to be the Search Component
4. Factory Method Pattern : Object creation in Java layer for all SWAPI objects like people, planet etc extending from entity
