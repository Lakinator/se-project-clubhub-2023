## ClubHub

### [Application properties](src/main/resources/application.properties)

* Datasource anpassen (url, user, pw)
* Standardport: 8081

### Datenbank

Die benötigten Skripte sind unter [resources/ddl](src/main/resources/ddl) zu finden

### Ordnerstruktur

Standardstruktur mit Club Entity

<pre>
src
|-- main
|   |-- java
|   |   `-- de
|   |       `-- oth
|   |           `-- seproject
|   |               `-- clubhub
|   |                   |-- config
|   |                   |   `-- SecurityConfig.java
|   |                   |-- persistence
|   |                   |   |-- model
|   |                   |   |   `-- Club.java
|   |                   |   `-- repository
|   |                   |       `-- ClubRepository.java
|   |                   |-- rest
|   |                   |   `-- v1
|   |                   |       |-- advice
|   |                   |       |   `-- RestExceptionHandler.java
|   |                   |       `-- api
|   |                   |           |-- controller
|   |                   |           |   |-- ClubRestController.java
|   |                   |           |   `-- ClubRestControllerImpl.java
|   |                   |           |-- dto
|   |                   |           |   `-- ClubDTO.java
|   |                   |           `-- service
|   |                   |               |-- ClubRestService.java
|   |                   |               `-- ClubRestServiceImpl.java
|   |                   |-- web
|   |                   |   `-- HomeController.java
|   |                   `-- ClubHubApplication.java
|   `-- resources
|       |-- ddl
|       |   `-- 01_create_club.sql
|       |-- static
|       |   `-- index.html
|       |-- templates
|       |   |-- fragments
|       |   |   `-- header.html
|       |   `-- home.html
|       `-- application.properties
`-- test
    `-- java
        `-- de
            `-- oth
                `-- seproject
                    `-- ClubHubApplicationTests.java
</pre>

### REST API

Beispiel unter [http://localhost:8081/api/v1/clubs](http://localhost:8081/api/v1/clubs)

```json
[
   {
      "timeStamp":"2022-11-26T15:29:34.8602973",
      "name":"SC Lorenzen"
   },
   {
      "timeStamp":"2022-11-26T15:29:34.8602973",
      "name":"FC Bayern"
   }
]
```