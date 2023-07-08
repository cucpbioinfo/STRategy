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
