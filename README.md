# STRategy - an extensible software platform for managing and analyzing sequence-based short tandem repeats

Deployed Link: http://cucpbioinfo.me/

## Requirement

- Docker
- Docker-Compose

## How to install, run, and, shutdown (Background)

###### Install and Run FIMS as background process.

* Run command below.

```
docker-compose up --build -d
```

###### Shutdown the FIMS

* Go to the root folder.
* Run command below.

```
docker-compose down
```

## How to install, run, and, shutdown (Foreground)

###### Install and Run FIMS as foreground process.

* Run command below.

```
docker-compose up --build
```

###### Shutdown the FIMS

* Go to the terminal window.
* Press `CONTROL + C`
