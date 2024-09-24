### To run Angular App

```
docker build -t starwars_angular .
docker run -p 3000:3000 starwars_angular
```

### To run Spring Boot App

```
docker build -t starwars_spring_boot .
docker run -p 8080:8080 starwars_spring_boot
```