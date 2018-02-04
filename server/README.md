## Server

**Requisite**

`docker run --name rci-db -p 5432:5432 -e "POSTGRES_USER=rci" -e "POSTGRES_PASS=rci" -d kartoza/postgis:9.5-2.2 `

**build**

`mvn clean install
`

**Run**

`mvn spring-boot:run
`

