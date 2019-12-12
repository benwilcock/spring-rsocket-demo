# Streaming Data With Spring Boot RSocket Server

In this recipe you will learn how to start a very simple data stream using Spring Boot, the RSocket protocol, and the command line.

## Problem

You have a long running stream of data which you want to send to a client. You want the client to initiate the streaming, and the server to respond with the stream.

## Solution

RSocket supports data streaming.

## How To Do It

It's possible to use Spring Boot's RSocket server integration to create a simple streaming server that will send a never ending stream of data to a client. You can then connect a client to this server and subscribe to this stream of events. To keep things simple, some code has been provided. You should examine the code when prompted in the recipe so that you become familiar with it.

Before you start, make sure you have all the [prerequisites][pre] installed. It's also helpful if you've followed the [first recipe][recipe], and in particular, that you have prepared the `rsocket-cli` for use in the terminal window that you want to use.

### Step 1: Examine the RSocket Server Code

There is very little code necessary to create an RSocket streaming server with Spring Boot. Here are the things you should take a look at:

#### 1.1 The Project File & Application Properties

The project's `pom.xml` and `application.properties` files are unchanged from the [last recipe (request-response)][recipe]. In the Maven `pom.xml` file you can see the `<dependencies>` required by the Spring Boot RSocket server. In the `application.properties` it's necessary to set the TCP PORT that the RSocket server will use to handle requests. In this case it is set it to TCP port `7000` as follows:

```java
spring.rsocket.server.port=7000
```

#### 1.2 The Java Code

Take a look at the `CommandRSocketController.java` class in the source code of the `rsocket-server` you'll notice that there is a method called `streamEvents()`. This method is decorated with the `@MessageMapping("events")` annotation. This annotation specifies that messages containing the `events` *route* should be routed to this method. You will use this route as metadata when you later send a message from the client to to begin message streaming. The Java code looks something like this:

```java
@Controller
public class CommandRSocketController {

    @MessageMapping("events")
    Flux<EventResponse> streamEvents(CommandRequest request) {
        return Flux
                .fromStream(Stream.generate(() -> new EventResponse("subscription")))
                .delayElements(Duration.ofSeconds(1));
    }
}
```

Within the code, a new and endless stream of messages (called a `Flux`) is created. The Flux has a new `EventResponse` object added to it every second. The `Flux` object comes from Spring Boot's *reactive* codebase ([Reactor][reactor]).

As before, there are also a couple of [Lombok][lombok] `@Data` classes (`CommandRequest.java` and `EventResponse.java`) which are used to model the server's command request and event response messages. These classes are fairly basic as you'll see if you examine their code.

### Step 2: Start The Streaming Server

Let's start the Spring Boot RSocket server. In your terminal window, make `rsocket-server` your current directory then build and run the RSocket server using these commands:
 
```bash
cd rsocket-server
./mvnw clean package spring-boot:run -DskipTests=true
```

Once the server ha started, send the server process to the background by pressing `Ctrl-Z` to suspend it and then type `bg` to allow it to continue in the background.

```bash
[1]  + 18976 suspended  ./mvnw clean package spring-boot:run -DskipTests=true
$ bg
[1]  + 22006 continued  ./mvnw clean package spring-boot:run -DskipTests=true
```

Now go back up to the project root before continuing to the next step.

```bash
cd ..
```

> **Note:**
> You can view all your background processes at any time by typing `jobs` at the prompt. You can bring any background process to the foreground using `fg %n` where `n` is the job number.

### Step 3: Join A Stream With The RSocket CLI

Now, use the `rsocket-cli` from the [previous recipe][recipe] to send a "subscribe" command to the Spring Boot RSocket server. The command message needs to have the `event` route declared in its metadata, which you can do by adding the `--metadata` option and specifying the ready-encoded metadata file `events-metatdata` as your metadata like this:

```bash
rsocket-cli --stream --debug --input="{\"command\":\"subscribe\"}" --dataFormat="json" --metadata=@events-metadata --metadataFormat="message/x.rsocket.routing.v0" tcp://localhost:7000
```

> If you see an error message explaining that the `rsocket-cli` command cannot be found, correct this by running `source get-rsocket-cli.sh` before retrying.

When the `rsocket-cli` command runs, you will see some debug information in the terminal window. The first block of debug is telling us that the message *route* `events` should be used for this client-server interaction. The second block shows the "subscribe" message (in JSON format) that you defined as your `--input` being sent.

```bash
14:48:02.629	sending ->
Frame => Stream ID: 1 Type: REQUEST_STREAM Flags: 0b100000000 Length: 43
Metadata:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 06 65 76 65 6e 74 73                            |.events         |
+--------+-------------------------------------------------+----------------+
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 63 6f 6d 6d 61 6e 64 22 3a 22 73 75 62 73 |{"command":"subs|
|00000010| 63 72 69 62 65 22 7d                            |cribe"}         |
+--------+-------------------------------------------------+----------------+
```

After these blocks, you will see an endless stream of event messages. A new message will arrive every 1 second. Each message looks something like this:

```bash
15:02:09.492	receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 67
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 65 76 65 6e 74 22 3a 22 73 75 62 73 63 72 |{"event":"subscr|
|00000010| 69 70 74 69 6f 6e 22 2c 22 73 65 6e 74 22 3a 22 |iption","sent":"|
|00000020| 32 30 31 39 2d 31 32 2d 31 30 54 31 35 3a 30 32 |2019-12-10T15:02|
|00000030| 3a 30 38 2e 34 37 30 39 39 32 5a 22 7d          |:08.470992Z"}   |
+--------+-------------------------------------------------+----------------+
{"event":"subscription","sent":"2019-12-10T15:02:08.470992Z"}
```

Each block shows a new subscription message being sent from the server and received by the `rsocket-cli` client. Each of these subscription messages is in JSON format and each contains a timestamp of when it was sent by the server. These messages will continue to be received every second until you cancel the `rsocket-cli` process by pressing `Ctrl-C` in your terminal window.

> **Notes:**
> If you have `bat`, `nano`, `vim`, or any decent text editor installed, you can examine the contents of the `event-metatdata` file. This file contains HEX encoded information in the format `<data-length><data>` as decribed in the [routing metadata payload specification][metadata] for RSocket.

## Tidy Up

1. Get a list of the current background processes (jobs) with the command `jobs` (lists all jobs and their job numbers).
2. Bring a background process (job) to the foreground with the command `fg %n` (where `n` is the job number).
3. Stop the process now in in the foreground by pressing `Ctrl-C`.
4. Repeat until `jobs` command shows an empty list.

## How It Works

The `rsocket-cli` sends the JSON command message `{"command":"subscribe"}` to the RSocket server using the RSocket protocol. There is some metadata sent by the client before the message (taken from the file called `event-metatdata` specified by the `--metadata` option). This metadata tells the server how to *route* the  message. Spring uses this metadata to select the correct `@MessageMapping` endpoint to call, in this case, the `streamEvents()` method. The payload of the message is in JSON format, which Spring Boot automatically converts to a Java object using Jackson.

The server then responds by creating an endless stream of messages (called a `Flux`) which it then begins to return to the client. One new subscription message is created every second. The subscription messages are also JSON encoded and look something like this: `{"event":"subscription","sent":"2019-12-10T15:02:08.470992Z"}`. Each subscription message contains a different 'sent' time so that you can see they're different.

## Final Thoughts

In this recipe you saw how easy it can be to create a simple streaming data server with Spring Boot and RSocket. You examined the Java code and started the streaming server locally before sending a `subscribe` message to the server over the RSocket protocol and observing the response. You also saw how it is possible to *route* your messages to specific endpoints using message metadata. Once more, you used the versatile `rsocket-cli` tool as your RSocket client.

[initializr]: https://start.spring.io
[initializr-link]: https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.2.1.RELEASE&packaging=jar&jvmVersion=1.8&groupId=io.pivotal&artifactId=rsocket-server&name=rsocket-server&description=Demo%20project%20for%20Spring%20Boot&packageName=io.pivotal.rsocket-server&dependencies=lombok,rsocket
[recipe]: ./first-try-rsocket.md
[lazy]: https://spring.io/blog/2019/03/14/lazy-initialization-in-spring-boot-2-2
[pre]: ./prerequisites.md
[rsocket]: https://rsocket.io
[metadata]: https://github.com/rsocket/rsocket/blob/master/Extensions/Routing.md
[lombok]: https://projectlombok.org/
[recipe]: ./request-response.md
[reactor]: https://projectreactor.io/
