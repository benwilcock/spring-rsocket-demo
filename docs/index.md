# RSocket Recipes

A series of short, bite sized exercises that you can try to get started with [RSocket][rsocket] on Spring Boot.

## Why RSocket

What do you do if you discover a communication use-case that isn't a great fit for connectionless HTTP request-response messaging?


In the diverse world of communications, HTTP can lead to a few challenges. Network connections can drop, messages can flood in, load might need to be balanced, messages may need to be re-routed, and one-way communication or streaming may be required. 

The HTTP protocol isn't really designed for many of these use-cases. You can overcome these challenges with HTTP, but this means adding additional code and infrastructure such as circuit breakers, retry mechanisms, load balancers, etc.

[RSocket][rsocket] is purpose built to help solve some of these communication challenges. With RSocket you get flexible transport of messages over TCP or WebSockets, back-pressure, resumption, routing, and flow control, plus multiple message modes including fire and forget, request response, and streaming. RSocket is also fully reactive, so it's designed from the start for next-gen Java applications.

In this series of recipes, you'll learn how to get started with RSocket so that you can get more familiar with how it works, and experience its power for yourself.

## RSocket Recipe List

### [Prerequisites][pre] - 10 mins

Things you'll need to do before you get started on the other recipes.

### [No Code Quickstart][first] - 15 mins

In this no-code quickstart, you'll try out RSocket communication between a client and a server using just the RSocket CLI tool.

### [Request Response With Spring Boot RSocket Server][second] - 15 mins

In this recipe, you'll begin creating a Spring Boot RSocket server and communicate with it using the RSocket CLI.

### [Streaming Data With Spring Boot RSocket Server][third] - 15 mins

Here, you'll learn how to add streaming to your Spring Boot RSocket server, and stream some data using the RSocket CLI.

### [Resiliant Streaming With RSocket][fourth]- 15 mins

In this recipe, you'll discover how to cope with spotty networks by adding a 'pause and resume' feature to your Spring Boot RSocket server.

### RSocket Clients With Spring Boot

Some stuff will go here.

### Backpressure & Flow Control (Throttling)

Some stuff will also go here.

### Load Balancing (Clients)

And here.

## RSocket Blogs

Check out this excellent [blog post series on RSocket][rafal1] by Rafal Kowalski.

## About The Author

Ben Wilcock works in Spring Marketing at Pivotal. [Follow Ben on twitter][twitter].

[pre]: ./prerequisites.md
[first]: ./first-try-rsocket.md
[second]: ./request-response.md
[third]: ./request-stream.md
[fourth]: ./stream-resumption.md
[rsocket]: https://rsocket.io
[factory]: https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/rsocket/server/ServerRSocketFactoryProcessor.java
[rafal1]: https://grapeup.com/blog/read/reactive-service-to-service-communication-with-rsocket-introduction-63
[twitter]: https://twitter.com/benbravo73