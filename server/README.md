## Server

**Requisite**

In the terminal

```

cd server 

``` 

launch the database

```
docker run --name rci-db -p 5432:5432 -e "POSTGRES_USER=rci" -e "POSTGRES_PASS=rci" -d kartoza/postgis:9.5-2.2 
```

**build**

```
mvn clean install
```

**Run**

```
mvn spring-boot:run
```

**Try**

Swagger API docs should be  available at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
