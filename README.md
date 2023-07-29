# STRategy - an extensible software platform for managing and analyzing sequence-based short tandem repeats

Deployed Link: http://cucpbioinfo.me/

There are default usernames and passwords for accessing the system.

| Username   | Password | Role                   |
|------------|----------|------------------------|
| soncomqiq2 | 12345678 | **ADMIN**              |
| lab_user   | 12345678 | **LAB_USER**           |


## Requirement

- Docker
- Docker-Compose

## How to install, run, and, shutdown (Background)

###### Install and Run STRategy as background process.

* Run command below.

```
docker-compose up --build -d
```

###### Shutdown the STRategy

* Go to the root folder.
* Run command below.

```
docker-compose down
```

## How to install, run, and, shutdown (Foreground)

###### Install and Run STRategy as foreground process.

* Run command below.

```
docker-compose up --build
```

###### Shutdown the STRategy

* Go to the terminal window.
* Press `CONTROL + C`

###### Note

* During the first run, the process may take anywhere from 30 minutes to 1 hour, depending on your computer's performance. This duration is due to the system initializing a significant amount of mock data in the database.

* If you run the process in the foreground `(docker-compose up --build)`, you may encounter backend errors until the database initialization is fully completed. However, once the database initialization is successful, you should receive a confirmation message, such as

```
db                   | 2023-07-29T16:54:07.836863Z 0 [System] [MY-011323] [Server] X Plugin ready for connections. Bind-address: '::' port: 33060, socket: /var/run/mysqld/mysqlx.sock
db                   | 2023-07-29T16:54:07.836987Z 0 [System] [MY-010931] [Server] /usr/sbin/mysqld: ready for connections. Version: '8.0.34'  socket: '/var/run/mysqld/mysqld.sock'  port: 3306  MySQL Community Server - GPL.
```

and the backend will be displayed as

```
strategy-backend-1   | 2023-07-29 16:54:10.936  INFO 1 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8080 (http) with context path ''
strategy-backend-1   | 2023-07-29 16:54:10.938  INFO 1 --- [           main] DeferredRepositoryInitializationListener : Triggering deferred initialization of Spring Data repositories…
strategy-backend-1   | 2023-07-29 16:54:11.465  INFO 1 --- [           main] DeferredRepositoryInitializationListener : Spring Data repositories initialized!
strategy-backend-1   | 2023-07-29 16:54:11.477  INFO 1 --- [           main] th.ac.chula.fims.STRategyApplication     : Started STRategyApplication in 28.497 seconds (JVM running for 28.833)
```

* If you run the process in the background `(docker-compose up -d --build)` you can use `docker logs` to see these message.

* After the first-time setup, running the system again will be significantly faster because Docker won't need to rebuild it. However, please be aware that if you delete the db (database) container, the process will take a long time again since it will need to generate data from scratch.
