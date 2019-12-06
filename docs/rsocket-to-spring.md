# RSocket Spring Boot Server

In this recipe you will use the RSocket-CLI from before, but this time you'll use it to communicate with a Spring Boot service which uses the RSocket protocol. 

## Problem

You would like to build an RSocket server for the first time.

## Solution

Some code has been provided. In this recipe you will examine the code and run it. You will also connect a client to your server and exchange messages.

## How To Do It

Before you start, make sure you have all the [prerequisites][pre] installed. It's also helpful if you've followed the [first recipe][recipe], and in particular, that you have prepared the `rsocket-cli` for use in your terminal.

### Step 1: Examine the Spring Boot RSocket Server Code

There is very little code required to use RSocket with Spring Boot, but here are the highlights:

#### 1.1 The Project File.

In the `pom.xml` you can see the dependencies needed by the server code.

I used the [Spring initializr (start.spring.io)][initializr] to create the project. I'm using Spring Boot 2.2.2 because (at the time of writing) this has the most up to date RSocket features. 

In my project I'm using the dependencies `lombok` and `spring-boot-starter-rsocket`. Notice that I haven't had to include any of the traditional "Web" components as they're not required by this server.

> The resulting JAR is pretty small - under 20Mb in size.

#### 1.2 The Application Properties.

In the `application.properties` I needed to set the TCP PORT that the RSocket server will use to handle requests. In this case I set it to port `7000`. 

```java
spring.main.lazy-initialization=true
spring.rsocket.server.port=7000
```

I also turned on Spring Boot's [lazy initialisation][lazy] feature for a faster startup time. 

> Even on my ancient laptop (including a built-in DVD drive!), the server starts up in under 2 seconds which is pretty quick!

#### 1.3 The Java Code.

Of note in the Java code is the `CommandRSocketController`. This class is decorated as a Spring Boot `@Controller`. It contains a method called `runCommand()` which is decorated with the `@MessageMapping("command")` annotation. This annotation declares that messages containing the RSocket **route** `command` should routed to this method.

```java
@Controller
public class CommandRSocketController {

    @MessageMapping("command")
    CommandResponse runCommand(CommandRequest request) {
        log.info("Received Command: {} at {}", request.getCommand(), Instant.now());
        return new CommandResponse(request.getCommand());
    }
}
```

In the code, there is also a couple of [Lombok][lombok] `@Data` classes which are used to model the server request and response messages. They are fairly trivial in nature as you will see if you explore the code. 

> You don't need to use Lombok if you don't want to, regular Java 'beans' will work just fine.

### Step 2: Start The Spring Boot RSocket Server.

In your terminal window, make the rsocket-server directory your current directory and run the RSocket server using Maven's Spring Boot plugin like so:
 
```bash
cd rsocket-server
./mvnw package spring-boot:run &
cd ..
```

The server is now running as a background process.

### Step 3: Send A Command To The Server With The RSocket CLI

You can use the `rsocket-cli` from the [previous recipe][recipe] to send a command message to the RSocket server. The command message needs the `command` route to be declared, which you can do by adding the `--metadata` option and the ready-encoded metadata file `command-metatdata` like this:

```bash
rsocket-cli --request --input="{\"command\":\"doSomething\"}" --dataFormat="json" --requestn=1 --debug --metadataFormat="message/x.rsocket.routing.v0" --metadata=@command-metadata tcp://localhost:7000
```

You will see debug information in the terminal window explaining what happened during the request-response interaction between the client and the server.

```bash
15:57:53.421	sending ->
Frame => Stream ID: 1 Type: REQUEST_RESPONSE Flags: 0b100000000 Length: 42
Metadata:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 07 63 6f 6d 6d 61 6e 64                         |.command        |
+--------+-------------------------------------------------+----------------+
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 63 6f 6d 6d 61 6e 64 22 3a 22 64 6f 53 6f |{"command":"doSo|
|00000010| 6d 65 74 68 69 6e 67 22 7d                      |mething"}       |
+--------+-------------------------------------------------+----------------+
15:57:53.556	receiving ->
Frame => Stream ID: 1 Type: NEXT_COMPLETE Flags: 0b1100000 Length: 72
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 63 6f 6d 6d 61 6e 64 22 3a 22 64 6f 53 6f |{"command":"doSo|
|00000010| 6d 65 74 68 69 6e 67 22 2c 22 72 65 63 65 69 76 |mething","receiv|
|00000020| 65 64 22 3a 22 32 30 31 39 2d 31 32 2d 30 36 54 |ed":"2019-12-06T|
|00000030| 31 35 3a 35 37 3a 35 33 2e 35 32 33 37 33 39 5a |15:57:53.523739Z|
|00000040| 22 7d                                           |"}              |
+--------+-------------------------------------------------+----------------+
15:57:53.571	sending ->
Frame => Stream ID: 1 Type: CANCEL Flags: 0b0 Length: 6
Data:

{"command":"doSomething","received":"2019-12-06T15:57:53.523739Z"}
```

> If you have `bat` installed, you can examine the contents of the `command-metatdata` file. It contains HEX encoded information in the format <data-length><data>.

## How It Works

This `rsocket-cli` sends a command called `doSomething` to the RSocket server using the TCP protocol. 

There is some metadata sent before the command (taken from a file called `command-metatdata`). This metadata tells the server where the clients requests should be routed. Spring uses this metadata to select the correct `@MessageMapping` method to call. 

The payload of the command message is in JSON format, which Spring Boot converts to a Java object using Jackson.

## Final Thoughts








[initializr]: https://start.spring.io
[initializr-link]: https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.2.1.RELEASE&packaging=jar&jvmVersion=1.8&groupId=io.pivotal&artifactId=rsocket-server&name=rsocket-server&description=Demo%20project%20for%20Spring%20Boot&packageName=io.pivotal.rsocket-server&dependencies=lombok,rsocket
[recipe]: ./first-try-rsocket.md
[lazy]: https://spring.io/blog/2019/03/14/lazy-initialization-in-spring-boot-2-2

