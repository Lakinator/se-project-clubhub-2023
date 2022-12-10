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
|       |   |-- js
|       |       `-- main.js
|       |-- templates
|       |   |-- fragments
|       |   |   `-- layout.html
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
    "timeStamp": "2022-12-10T13:20:53.6280917",
    "name": "SC Lorenzen",
    "memberCount": 4
  },
  {
    "timeStamp": "2022-12-10T13:20:53.6481221",
    "name": "FC Bayern",
    "memberCount": 1
  }
]
```

Weiteres Beispiel unter [http://localhost:8081/api/v1/club/SC%20Lorenzen/group/1.%20Mannschaft/events?start=2022-12-10&end=2022-12-17](http://localhost:8081/api/v1/club/SC%20Lorenzen/group/1.%20Mannschaft/events?start=2022-12-10&end=2022-12-17)

```json
[
  {
    "eventDate": "2022-12-10",
    "eventStart": "16:54:00",
    "eventEnd": "18:54:00",
    "location": "Deutschland, Regensburg, 93055, Kirchfeldallee, 4, SV Burgweinting",
    "title": "fdgfdgf",
    "description": "aa",
    "eventType": "CHRISTMAS_PARTY"
  },
  {
    "eventDate": "2022-12-16",
    "eventStart": "15:58:00",
    "eventEnd": "18:58:00",
    "location": "Deutschland, Lappersdorf, 93138, Am Sportzentrum, 1, TSV Kareth Lappersdorf e.V.",
    "title": "dsfsc",
    "description": "sdfsdfdsfsdf",
    "eventType": "TRAINING"
  }
]
```