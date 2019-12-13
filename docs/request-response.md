# Request Response With Spring Boot RSocket Server

In this recipe you will learn how to use the RSocket-CLI to communicate with a Spring Boot service built using the [RSocket][rsocket] protocol. You will run a Spring Boot RSocket server locally and connect a client to it in order to exchange a test message. You will also learn about RSocket's 'routing' feature.

**Time: Approx. 15 mins.**

Before you start, check you have all the [prerequisite][pre] software installed and you have prepared your terminal to use the `rsocket-cli`.

## Problem

You would like to build your own RSocket server to handle traditional request-response style interactions.

## Solution

Spring Boot helps us build RSocket microservices services quckly. Spring Boot's RSocket 'starter' integrates RSocket with ease, reducing the coding effort.

## How To Do It

In this recipe you will see that the amount of code required to build an RSocket server with Spring Boot is very small. To keep things simple, the code has been provided for you but you should examine the code when directed so that you become familiar with it. You could even build the code yourself if you prefer.

Before you start, make sure you have all the [prerequisites][pre] installed. It's also helpful if you've followed the [first recipe][recipe], and in particular, that you have prepared the `rsocket-cli` for use in the terminal window that you want to use.

### Step 1: Examine the RSocket Server Code

There is very little code necessary to integrate RSocket with Spring Boot, but here are the highlights:

#### 1.1 The Project File

In the Maven `pom.xml` file you can see the `<dependencies>` required by the Spring Boot RSocket server code. Spring Boot v2.2.2 is used in this example because (at the time of writing) this version has the most up to date RSocket features. The project also uses the `lombok` and `spring-boot-starter-rsocket` libraries. The first brings in a number of useful coding utilities to help with data classes and logging. The second integrates RSocket with Spring Boot by automatically configuring some Spring beans and putting them into Spring's application context.

> **Note:**
> You can recreate the project using the [Spring initializr (start.spring.io)][initializr]. 'RSocket' and 'Lombok' were chosen as dependencies.
> There's no need to include any of the traditional Spring "Web" components as they're not required by this RSocket based server.
> The resulting executable JAR is pretty small - under 20Mb in size.

#### 1.2 The Application Properties

In the `application.properties` it's necessary to set the TCP PORT that the RSocket server will use to handle requests. In this case I set it to port `7000` as you can see below.

```java
spring.rsocket.server.port=7000
spring.main.lazy-initialization=true
```

I also turned on Spring Boot's [lazy initialisation][lazy] feature for a faster startup time.

> **Note:**
> Even on my ancient laptop (which features a built-in DVD drive and a working keyboard), the server starts up in just a few seconds which is pretty quick!

#### 1.3 The Java Code

Of particular note in the Java code is the `CommandRSocketController.java` class. Open it up and take a look. The class is decorated as a Spring Boot `@Controller` which essentially means that it declares service endpoints. The class contains a method called `runCommand()` which is decorated with the `@MessageMapping("command")` annotation. This annotation declares that messages containing the RSocket **route** `command` should be routed to this method. You will use this route as metadata when you later send a message from a client to this server.

```java
@Controller
public class CommandRSocketController {

    @MessageMapping("command")
    Mono<CommandResponse> runCommand(CommandRequest request) {
        return Mono.just(new CommandResponse(request.getCommand()));
    }
}
```

Within the code, a new single response message (called a `Mono`) is created. The Mono contains a new `CommandResponse` object. The `Mono` class comes from Spring Boot's *reactive* codebase ([Reactor][reactor]).

Elsewhere in the project source code, there is also a couple of [Lombok][lombok] `@Data` classes (`CommandRequest.java` and `CommandResponse.java`) which are used to model the server's request and response messages. They are fairly trivial in nature as you'll see if you examine their code.

> **Note:**
> You don't need to use Lombok if you don't want to, regular Java 'beans' will work just fine.

### Step 2: Start The Spring Boot RSocket Server

Let's start the Spring Boot RSocket server. In your terminal window, make `rsocket-server` your current directory then build and run the RSocket server using these commands:
 
```bash
cd rsocket-server
./mvnw clean package spring-boot:run -DskipTests=true
```

Once the server has started, send the server process to the background by pressing `Ctrl-Z` to suspend it and then type `bg` to allow it to continue in the background.

```bash
[1]  + 18976 suspended  ./mvnw clean package spring-boot:run -DskipTests=true
$> bg
[1]  + 22006 continued  ./mvnw clean package spring-boot:run -DskipTests=true
```

Now go back up to the project root before continuing to the next step.

```bash
cd ..
```

> **Note:**
> You can view all your background processes at any time by typing `jobs` at the prompt. You can bring any background process to the foreground using `fg %n` where `n` is the job number. If you don't want to process switch in the terminal, just use the "Run" command in your Java IDE.

### Step 3: Send A Command To The Server With The RSocket CLI

Next, use the `rsocket-cli` from the [previous recipe][recipe] to send a command message to the RSocket server. The command message needs to have the `command` route declared in its metadata, which you can do by adding the `--metadata` option and the ready-encoded metadata file `command-metatdata` like this:

```bash
rsocket-cli --request --debug --input="{\"command\":\"doSomething\"}" --dataFormat="json" --metadata=@command-metadata --metadataFormat="message/x.rsocket.routing.v0"  tcp://localhost:7000
```

> If you see an error message explaining that the `rsocket-cli` command cannot be found, correct this by running `source get-rsocket-cli.sh` before retrying.

When the command runs, you will see some debug information in the terminal window explaining what is happening during the request-response interaction between the client and the server.

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
```

The first block shows the command metadata being sent to the server. This is part of the process used to establish the RSocket communication channel. The second block shows the JSON message that you set as your `--input`. The third block shows the server's response message (also JSON). 

On the very last line, you will see the JSON formatted response from the server printed in isolation, confirming that our command message was successfully received and acknowledged by the server.

```json
{"command":"doSomething","received":"2019-12-06T15:57:53.523739Z"}
```

> **Note:**
> If you have `bat`, `nano`, `vim`, or another text editor installed, you can examine the contents of the `command-metatdata` file. This file contains HEX encoded information in the format `<data-length><data>` as decribed in the [routing metadata payload specification][metadata] for RSocket.

## Tidy Up

1. Get a list of the current background processes (jobs) with the command `jobs` (lists all jobs and their job numbers).
2. Bring a background process (job) to the foreground with the command `fg %n` (where `n` is the job number).
3. Stop the process now in in the foreground by pressing `Ctrl-C`.
4. Repeat until `jobs` command shows an empty list.


## How It Works

The `rsocket-cli` sends the JSON command message `{"command":"doSomething"}` to the RSocket server using the RSocket protocol. There is some metadata sent by the client before the message (taken from the file called `command-metatdata` chosen with the `--metadata` option). This metadata tells the server how to *route* the  message. Spring uses this metadata to select the correct `@MessageMapping` endpoint to call.

The payload of the message is in JSON format, which Spring Boot automatically converts to a Java object using Jackson.

## Final Thoughts

In this recipe you saw how easy it can be to create a simple RSocket server using Spring Boot. You examined the Java code and started the Spring Boot server locally before sending a message to the server over the RSocket protocol and observing the response. You also saw how it is possible to *route* your messages to specific endpoints using message metadata. Once more, you used the versatile `rsocket-cli` tool as your RSocket client.

[initializr]: https://start.spring.io
[initializr-link]: https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.2.1.RELEASE&packaging=jar&jvmVersion=1.8&groupId=io.pivotal&artifactId=rsocket-server&name=rsocket-server&description=Demo%20project%20for%20Spring%20Boot&packageName=io.pivotal.rsocket-server&dependencies=lombok,rsocket
[recipe]: ./first-try-rsocket.md
[lazy]: https://spring.io/blog/2019/03/14/lazy-initialization-in-spring-boot-2-2
[pre]: ./prerequisites.md
[rsocket]: https://rsocket.io
[metadata]: https://github.com/rsocket/rsocket/blob/master/Extensions/Routing.md
[lombok]: https://projectlombok.org/
[reactor]: https://projectreactor.io/
