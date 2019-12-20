# Spring Boot RSocket Cookbook

A series of short, bite sized exercises that get you started with [RSocket][rsocket] on [Spring Boot][boot].

## Why RSocket

What do you do if you discover a communication use-case that isn't a great fit for connectionless HTTP request-response messaging?

In the diverse world of communications, HTTP can lead to a few challenges. Network connections can drop, messages can flood in, load might need to be balanced, messages may need to be re-routed, and one-way communication or streaming may be required. 

The HTTP protocol isn't really designed for many of these use-cases. You *can* overcome these challenges with HTTP, but docing so usually means adding additional code and infrastructure, such as circuit breakers, retry mechanisms, load balancers, etc.

[RSocket][rsocket] is purpose built to solve some of these common communication challenges. 

With RSocket you get flexible transport of messages over TCP or WebSockets, back-pressure, resumption, routing, and flow control, plus multiple message modes including fire and forget, request response, and streaming. RSocket is also fully reactive, so it's designed from the start for next-gen Java applications.

In this series of recipes, you'll learn how to get started with RSocket so that you can get more familiar with how it works, and experience its power for yourself.

## RSocket Recipes

### [Before You Start][pre] - 10 mins

Things you'll need to do before you get started on the other recipes.

### [No Code Quickstart][one] - 15 mins

In this recipe, you'll try out RSocket communication between a client and a server using just the RSocket CLI tool.

### [Request Response With Spring Boot][two] - 15 mins

In this recipe, you'll begin creating a Spring Boot RSocket server and communicate with it using the RSocket CLI.

### [Streaming Data With Spring Boot][three] - 15 mins

Here, you'll learn how to add streaming to your Spring Boot RSocket server, and stream some data using the RSocket CLI.

### [Resilient Streaming][four] - 15 mins

In this recipe, you'll discover how to cope with spotty networks by adding a 'pause and resume' feature to your Spring Boot RSocket server.

### [RSocket Clients With Spring Boot][five] - 20 mins

Moving on from the server-side, you'll now create your own shell terminal client that will exchange request-response messages with your Spring Boot RSocket server.

### Back-pressure & Flow Control (Throttling)

TODO.

### Load Balancing (Clients)

TODO.

### Retry

TODO.

### RSocket With Spring Cloud Gateway

TODO.

## RSocket Blogs, Documentation & Code Samples

* [Rafal Kowalski's RSocket Blog Series](https://grapeup.com/blog/read/reactive-service-to-service-communication-with-rsocket-introduction-63)
* [Spring Framework - RSocket Documentation](https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#rsocket).
* [Brian Clozel's 'Spring Flights' RSocket Sample](https://github.com/bclozel/spring-flights)
* [Hantsy Bai's RSocket Sample](https://github.com/hantsy/rsocket-sample)

## About The Author

Ben Wilcock works in Spring Marketing at Pivotal. [Follow Ben on twitter][twitter].

[rsocket]: https://rsocket.io
[boot]: https://spring.io/projects/spring-boot
[pre]: ./prerequisites.md
[one]: ./first-try-rsocket.md
[two]: ./request-response.md
[three]: ./request-stream.md
[four]: ./stream-resumption.md
[five]: ./rsocket-shell-client.md

[factory]: https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/rsocket/server/ServerRSocketFactoryProcessor.java

[twitter]: https://twitter.com/benbravo73
