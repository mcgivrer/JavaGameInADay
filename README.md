# README

[![Gitea action build](http://nextserver01:4000/frederic/JavaGameOnAWeek/actions/workflows/build.yml/badge.svg?branch=develop)](http://nextserver01:4000/frederic/JavaGameOnAWeek/actions?workflow=build.yml&actor=0&status=0 "build on /develop")

## Context

This is the readme file for "Java Game In A Week" tutorial projects (1.0.4)
where building a simple 2D game framework step by step by adding features, with a moderate ramp-up on complexity.

Hope this demo project will help you to on-board into the game development adventure with a common and accessible
language.

## Documentation

For more detailed information about implementation and design, look at the `src/docs` file path, two documents moving
step by
step on the design will drive you to explore each concept.

```plaintext
JavaGameOnADay
|_src
  |_ docs
     |_ 01-design       => Step by step driving you through the concetps
     |_ 02-user-manual  => Help you execute and configure the resulting demo
```

> NOTE : to generate AsciiDoctor documentation, please use the asciidoctor docker container. see all details at https://github.com/asciidoctor/docker-asciidoctor/blob/main/README.adoc.


## Build

To build the project, execute the following command line :

```bash
build.sh a
```

## Run

To execute the build project, run it with :

```bash
build.sh r
```

Or you can execute the command line :

```bash
java -jar target/JavaGameInAWeek-1.0.4.jar
```

Or lastly for the Linux machine owner or in a Windows Git Bash terminal :

```bash
target/build/JavaGameInAWeek-1.0.4.run
```

Enjoy !

Frédéric Delorme.
