# Spring Boot RSocket Cookbook

A series of short, bite sized exercises that get you started with [RSocket][rsocket] on [Spring Boot][boot].

All the code, including [this website][website] and documentation can be found [in the accompanying code repository][repo].

## RSocket Recipes

### [Introduction: Why RSocket][intro] - 5 Mins

Just a few words on why learning about RSocket is worth your time.

### [Before You Start With RSocket][pre] - 10 mins

Things you'll need to do before you get started on the other recipes.

### [No Code RSocket Quickstart With RSocket CLI][one] - 15 mins

Getting started with RSocket couldn't be simpler. In this recipe, you'll try out RSocket communication between a client and a server using no code, just the RSocket CLI tool.

### [Request Response With RSocket CLI And Spring Boot Server][two] - 15 mins

In this recipe, you'll begin creating a Spring Boot RSocket server and communicate with it using the RSocket CLI.

### [Streaming Data With RSocket CLI And Spring Boot Server][three] - 15 mins

Here, you'll learn how to add streaming to your Spring Boot RSocket server, and stream some data using the RSocket CLI.

### [Resilient Streaming With RSocket CLI And Spring Boot Server][four] - 15 mins

In this recipe, you'll discover how to cope with spotty networks by adding a 'pause and resume' feature to your Spring Boot RSocket server.

### [Request Response With A Spring Boot Client And Server][five] - 20 mins

Moving on from the server-side, you'll now create your own shell terminal client that will exchange request-response messages with your Spring Boot RSocket server.

### Fire And Forget With A Spring Boot Client And Server

TODO. Sometimes, you don't need an answer. In this recipe you'll try out one-way 'fire-and-forget' messaging with Sppring Boot RSocket. 

### Channels (Two Way Streaming) With A Spring Boot Client And Server

TODO. You should NEVER cross the streams, but sometimes you do want to 'channel' them. Here you'll learn how to create bi-directional streams for exchanging data between components.

### Streaming Back-pressure With A Spring Boot Client And Server

TODO. You've seen server streaming already, but when writing clients, it's helpful if you can control the flow of the stream. In this recipe, you'll learn how that's done in Spring Boot RSocket clients.

### Load Balancing With A Spring Boot Client And Multiple Servers

TODO. RSocket comes with built in client side Load Balancing. In this recipe you'll get to grips with how to use it.

### Retry With A Spring Boot Client And Server

TODO. More on coping with communicatiion issues.

### RSocket With Spring Cloud Gateway

TODO. RSocket everywhere. Using RSocket in the enterprise to improve routing and cope with other common microservice architecture issues.

## Great RSocket Blogs, Documentation & Code Samples

* [Rafal Kowalski's RSocket Blog Series](https://grapeup.com/blog/read/reactive-service-to-service-communication-with-rsocket-introduction-63)
* [Spring Framework - RSocket Documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket).
* [Brian Clozel's 'Spring Flights' RSocket Sample](https://github.com/bclozel/spring-flights)
* [Hantsy Bai's RSocket Sample](https://github.com/hantsy/rsocket-sample)

## About The Author

Ben Wilcock works in Spring Marketing at Pivotal. [Follow Ben on twitter][twitter].

[rsocket]: https://rsocket.io
[boot]: https://spring.io/projects/spring-boot
[repo]: https://github.com/benwilcock/spring-rsocket-demo
[website]: https://benwilcock.github.io/spring-rsocket-demo/rsocket-shell-client.html
[pre]: ./prerequisites.md
[one]: ./first-try-rsocket.md
[two]: ./request-response.md
[three]: ./request-stream.md
[four]: ./stream-resumption.md
[five]: ./rsocket-shell-client.md
[intro]: ./why-rsocket.md

[factory]: https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/rsocket/server/ServerRSocketFactoryProcessor.java

[twitter]: https://twitter.com/benbravo73
