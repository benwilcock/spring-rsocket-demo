# RSocket To Spring Boot

In this tutorial we will use the RSocket-CLI from before, and use it to communicate with a Spring Boot service which uses the RSocket protocol. 

## Pre-requisites

#### The RSocket CLI

## Step 2: Start The RSocket Server (Spring Boot)

There is very little of note in the Spring Boot RSocket server, but there are some things that help us bootstrap and get under way.

#### The Project File.

`pom.xml`

I used the [Spring initializr (start.spring.io)][initializr] to build the project. Things of note here are that I'm using Spring Boot SNAPSHOT 2.2.2 because (at the time of writing) this has the best RSocket feature set. I'm selecting the dependencies `Lombok` and `RSocket` only. I haven't had to include any of the traditional "Web" components as they're not required.

#### The Application Properties.

`application.properties`

```java
spring.main.lazy-initialization=true
spring.rsocket.server.port=7000
```

#### The Code.

Of note in the code is the `CommandRSocketController`. This class is decorated as a Spring Boot `@Controller`. It contains a single method called `runCommand()` which is decorated with the `@MessageMapping("command")` annotation. This annotation declares that messages containing the **route** 'command' should routed to this method.

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

> You don't need to use Lombok if you don't want to, regular Java 'beans' are just as good.

#### Let's Start The Server.

```bash
cd rsocket-server
./mvnw package spring-boot:run &
cd ..
```

The server is now running on a background process.

## Step 3: Send A Command With RSocket CLI

Now we can send a command message to the RSocket server. The command message needs a route, which we can add using the ready-encoded metadata file `command-metatdata`.

```bash
source send-rsocket-command.sh
```

This script sends a command called `DoSomething` to the RSocket server listening on port 7000 using the TCP protocol. There is some metadata sent before the command (taken from a file called `command-metatdata`) which tells the server where the clients requests should be routed. Spring uses this information to select the controller and the method to call. The payload of the message is in JSON format, which Spring Boot converts to a Java object using Jackson.

You should get debug information in the terminal explaining what happened during the request-response interaction between the client and the server.

```bash
16:08:01.558	sending ->
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
|00000000| 7b 22 63 6f 6d 6d 61 6e 64 22 3a 22 44 6f 53 6f |{"command":"DoSo|
|00000010| 6d 65 74 68 69 6e 67 22 7d                      |mething"}       |
+--------+-------------------------------------------------+----------------+
16:08:01.627	receiving ->
Frame => Stream ID: 1 Type: NEXT_COMPLETE Flags: 0b1100000 Length: 78
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 63 6f 6d 6d 61 6e 64 22 3a 22 43 6f 6d 6d |{"command":"Comm|
|00000010| 61 6e 64 20 44 6f 53 6f 6d 65 74 68 69 6e 67 20 |and DoSomething |
|00000020| 72 65 63 65 69 76 65 64 20 40 20 32 30 31 39 2d |received @ 2019-|
|00000030| 31 32 2d 30 33 54 31 36 3a 30 38 3a 30 31 2e 36 |12-03T16:08:01.6|
|00000040| 31 36 32 39 36 5a 22 7d                         |16296Z"}        |
+--------+-------------------------------------------------+----------------+
16:08:01.641	sending ->
Frame => Stream ID: 1 Type: CANCEL Flags: 0b0 Length: 6
Data:

{"command":"Command DoSomething received @ 2019-12-03T16:08:01.616296Z"}
```

> If you're curious, open the script to see the `rsocket-cli` command being used. If you have `bat` installed, you can also examine the `command-metatdata` file.


[initializr]: https://start.spring.io
[initializr-link]: https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.2.1.RELEASE&packaging=jar&jvmVersion=1.8&groupId=io.pivotal&artifactId=rsocket-server&name=rsocket-server&description=Demo%20project%20for%20Spring%20Boot&packageName=io.pivotal.rsocket-server&dependencies=lombok,rsocket

