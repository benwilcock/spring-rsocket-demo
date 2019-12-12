# RSocket Recipes

## Why RSocket

What do you do if you discover a communication use-case that isn't a great fit for HTTP based messaging? For example, on mobile devices, HTTP's connectionless nature can pose a few problems. Connections drop all the time, notifications need to be sent (preferably without polling), and messages can flood into the server or the device unexpectedly.

[RSocket][rsocket] is purpose built to help solve these problems. With RSocket you get flexible transport of messages over TCP or WebSockets, back-pressure, resumption, and routing for flow control, and multiple message modes including fire and forget, request response, and streaming. 

In these recipes I'll show you how to get started with RSocket so that you can try it out for yourself.

> Want to know more about RSocket? Read this excellent [blog post on RSocket][rafal1] by my good friend Rafal Kowalski of GrapeUp.

## RSocket Recipe List

### [Prerequisites][pre]

Things you'll need before you get started.

### [No Code Quickstart][first]

RSocket communication from a client to a server using just the RSocket CLI (no code).

### [Request Response With Spring Boot RSocket Server][second]

Creating a Spring Boot RSocket server and communicating with it using the RSocket CLI.

### [Streaming Data With Spring Boot RSocket Server][third]

Creating a Spring Boot RSocket server and communicating with it using the RSocket CLI.

### [Resiliant Streaming With RSocket][fourth]

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