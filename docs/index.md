# RSocket Recipes

## Why RSocket

What do you do if you discover a communication use-case that isn't a great fit for HTTP based messaging? For example, on mobile devices, HTTP's connectionless nature can pose a few problems. Connections drop all the time, notifications need to be sent (preferably without polling), and messages can flood into the server or the device unexpectedly.

[RSocket][rsocket] is purpose built to help solve these problems. With RSocket you get flexible transport of messages over TCP or WebSockets, back-pressure, resumption, and routing for flow control, and multiple message modes including fire and forget, request response, and streaming. 

In these recipes I'll show you how to get started with RSocket so that you can try it out for yourself.

## RSocket Recipe List

### [Prerequisites][pre]

Things you'll need before you get started.

### [No Code Quickstart][first]

RSocket communication from a client to a server using just the RSocket CLI (no code).

### [Request Response With Spring Boot RSocket Server][second]

Creating a Spring Boot RSocket server and communicating with it using the RSocket CLI.

[pre]: ./prerequisites.md
[first]: ./first-try-rsocket.md
[second]: ./rsocket-to-spring.md
[rsocket]: https://rsocket.io