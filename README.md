## Run Akka HTTP server

Start the server with command `sbt run`. It is listening for connections at `http://localhost:8080`.

## Interacting with the sample

#### Create a calendar:

    curl -H "Content-type: application/json" -X POST -d "{\"events\":[{\"id\": 1, \"start\": 60, \"end\": 120}, {\"id\": 2, \"start\": 100, \"end\": 240},{\"id\": 3, \"start\": 700, \"end\": 720}]}" http://localhost:8080/daily-calendars

#### Get the details of the calendar:
- Use id of the calendar from previous response.


    curl -X GET http://localhost:8080/daily-calendars/53d20c6c-2a08-4594-a67d-1240b3cbbbbd