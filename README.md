# PAPER

This is real time messaging web-app created by using SSE(Server Sent Event) async protocol and Spring Reactive stack.
## Getting Started

To run this project you need Jdk 11, Gradle, MongoDB credential.

### Prerequisites

You should have intermediate knowledge about Spring boot and Reactive api (Project Reactor).

### Installing

1. Before doing anything you need to clone this repository from the GitHub


2. Go to your favourite directory and type following command.
```
https://github.com/tusharchaudhari30/chat-app.git

cd chat-app

```
3. Add executable configuration to Gradle and install dependencys.
```
chmod +x gradlew
./gradlew bootrun
```
4. You also need to configure MongoDB in application.property file.
```
spring.data.mongodb.host=mongoserver
spring.data.mongodb.port=27017
```

## Project Brief Information

This project is the demonstration of how to implement reactive dom which changes without touching the refresh.
The best feature of using this is you don't need to use Single page application such as React or Angular you can do it with just
small code of plain Javascript. This application is not Single page application which result in low dependency size and good performance. This project utilizes concept of Server Sent event with thymleaf support to reactive streams. I used various aspect where
you can implement is various highlight given bellow.

Highlights of this application are:
   * Use of Thymeleaf's integration module for Spring 5's WebFlux reactive web framework.
   * Use of Thymeleaf's data-driven support for rendering HTML in a reactive-friendly manner.
   * Use of Server-Sent Events (SSE) rendered in HTML by Thymeleaf from a reactive data stream.
   * Use of Server-Sent Events (SSE) rendered in JSON by Spring WebFlux from a reactive data stream.
   * Use of Spring Data MongoDB's reactive (Reactive Streams) driver support.
   * Use of Spring Data MongoDB's support for infinite reactive data streams based on MongoDB tailable cursors.
   * Use of Thymeleaf's fully-HTML5-compatible syntax
   * Use of many weird, randomly generated team and player names.
   
  
## Screenshot
![](https://github.com/tusharchaudhari30/chat-app/blob/master/doc/paperlogout.PNG | width=100)
![Image](https://github.com/tusharchaudhari30/chat-app/blob/master/doc/paperlogout.PNG =250*250)

![Image](https://github.com/tusharchaudhari30/chat-app/blob/master/doc/paperhome.PNG)
# About
----
For More information and help in Project Mail me chaudharitushar2077@gmail.com
  
  
