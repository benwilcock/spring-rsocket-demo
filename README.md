# Getting Started With RSocket On Spring Boot (Series)

In the diverse world of microservices, working exclusively with the HTTP protocol can have some challenges. RSocket is a new messaging protocol that’s designed to solve some of these challenges. RSocket is a modern, flexible protocol that can do bi-directional communication delivered via TCP or WebSockets. RSocket brings modern features like multiplexing, back-pressure, resumption, routing, and several distinct messaging ‘modes’ including fire-and-forget, request-response, streaming, and channels. 

But it doesn’t stop there. RSocket is also fully reactive, so it’s ideal for your high-throughput microservice applications. Early adopters include Netflix, Alibaba, and Facebook — all experts in delivering scalable Internet-based services.

In this series of exercises, you’ll learn how to get started with RSocket using Spring Boot. You’ll become familiar with how it works, and experience some of its power. By the end, you’ll have added RSocket to your skill set so that next time you’re figuring out your options, you’ll have an additional protocol to choose from. Each exercise takes around 20 minutes to complete, so you’ll be off to the races in no time.

Give RSocket a try, and I promise, you’ll be the envy of all your friends and co-workers!

## All The Articles

Each article is around 6-8 minutes reading time and between 10-20 minutes coding time.

### [Getting Started With RSocket: Spring Boot Server](https://spring.io/blog/2020/03/02/getting-started-with-rsocket-spring-boot-server)

You’ll begin by creating some server-side code for ‘request-response’ messaging using Spring Boot and RSocket, and then check your server works by using a generic command-line client.

### [Getting Started With RSocket: Spring Boot Client](https://spring.io/blog/2020/03/09/getting-started-with-rsocket-spring-boot-client)

Continuing your exploration of RSocket, you’ll create a command-line client of your own using Spring Boot and Spring Shell. You’ll test this client against the RSocket server you created in the previous exercise.

### [Getting Started With RSocket: Spring Boot Fire And Forget](https://spring.io/blog/2020/03/16/getting-started-with-rsocket-spring-boot-fire-and-forget)

In this exercise, you’ll upgrade your client and server by adding ‘fire-and-forget’ messaging to both and then testing the results.

### [Getting Started With RSocket: Spring Boot Request-Stream](https://spring.io/blog/2020/03/23/getting-started-with-rsocket-spring-boot-request-stream)

You’ll now add streaming to your client and server applications and observe the results for yourself by starting a stream.

### [Getting Started With RSocket: Spring Boot Channels](https://github.com/benwilcock/spring-rsocket-demo/blob/master)

Channels add bi-directional streams to your applications, so clients and servers can stay in constant touch. This tutorial adds channels to your code.

### [Getting Started With RSocket: Spring Boot Servers Calling Clients](https://spring.io/blog/2020/05/12/getting-started-with-rsocket-servers-calling-clients)

With RSocket, the convention of client and server can be relaxed. In this exercise, you’ll discover how clients and servers can become ‘requesters’ and ‘responders.’  You’ll add code that allows your server to send requests which your clients can respond to.

### [Getting Started With RSocket: Testing Spring Boot Responders](https://spring.io/blog/2020/05/25/getting-started-with-rsocket-testing-spring-boot-responders)

Running realistic tests on responders isn’t difficult with Spring Boot. In this exercise, you’ll add an integration test for your server-side code and configure Maven to run your integration tests in isolation.

### [Getting Started With RSocket: Spring Security](https://spring.io/blog/2020/06/17/getting-started-with-rsocket-spring-security)

Spring Security simplifies the process of securing your RSocket applications. In this exercise, you’ll add the required dependencies, configure server-side security, pass credentials, and add authentication and authorization features to your RSocket applications.

For latest updates, news, and interestings, follow me on [Twitter][twitter]

[twitter]: https://twitter.com/benbravo73


