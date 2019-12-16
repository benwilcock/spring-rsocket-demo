# RSocket Shell Client

Spring Boot also brings simplicity to the task of writing RSocket clients. In this recipe, you'll learn how to write your own RSocket command-line client.

**Time: Approx. 15 mins.**

Before you start, check you have all the [prerequisite software][pre] installed on your computer. You must able to build and run the `rsocket-server` code from the code repository. Follow the recipe: [Request Response With Spring Boot RSocket Server][request-response] if you need to get up to speed with how to do this.

## Problem

Your RSocket Spring Boot server is ready to process commands, but so far all we have to send commands to the server is the `rsocket-cli` tool. This tool only accepts our commands via the `--input` option which is a bit inflexible.

## Solution

You can build our own client. If you build build it with Spring Shell, you'll be able to despatch commands to the server from the terminal window using nothing more than plain english. 

## How To Do It

Specify, download, and unpack a new Spring Boot project using the [Spring Initializr (start.spring.io)][start]. Call the project the `rsocket-client`. When customising your project, specify a Maven as the build tool, the latests stable version of Spring Boot, and add `RSocket` and `Lombok` as dependencies.

Now we will customise the project further.

> The `rsocket-client` code has been included in the repository you downloaded. Take a look in the `/rsocket-client` directory, if you don't want to start from scratch.

### Step 1: Add Spring Shell To The POM

The Spring Initializr doesn't include [Spring Shell][spring-shell-docs], but you can still add it manually. Open up your project in your IDE. Open the Maven `pom.xml` file, and add the following dependency to the `<dependencies>` section:

```xml
		<dependency>
			<groupId>org.springframework.shell</groupId>
			<artifactId>spring-shell-starter</artifactId>
			<version>2.0.0.RELEASE</version>
		</dependency>
```

This will enable Spring Shell in our new RSocket Client Spring Boot application.

### Step 2: Turn Off Web Application Support In Spring Boot

Because the project includes RSocket, I get embedded 'Netty', but I don't want Spring Boot to think this is a web application. In these cases, I can explicitly declare my intent by setting the `web-application-type` property to `NONE` as follows. 

```java
spring.main.web-application-type=NONE
```

### Step 3: Write The Code

There are a few small tasks you must complete.

#### 3.1 Disable The Tests

You can either delete, disable, or skip the tests that came along with your Spring Boot project. If you don't do this, odd things can happen when you build (as detailed in the [Spring Shell Docs][spring-shell-docs]).

To disable the tests, add the `@Disabled` annotation to the test class' declaration like this:

```java
@Disabled
@SpringBootTest
class RsocketclientApplicationTests {
    ...
}
```

To skip the tests instead, add `-DskipTests=true` to your Maven command line whenever you run the client. For example:

```bash
./mvnw clean package spring-boot:run -DskipTests=true
```

#### 3.2 Create An RSocket Client Service

First, copy over the `CommandRequest.java` and `CommandResponse.java` data classes from the `rsocket-server` project. You will need these data types in order to exchange data with the server.

Next, create a new class called the `RSocketClient`. This class uses the `@Service` stereotype and must be constructed with an `RSocketRequester.Builder` which you use to create an `RSocketRequester`. The requester will be customized using the `connectTcp` method with your RSocket server's IP address and port. It will also be told to `block()` and await further interaction.

Once you have created the `RSocketClient` class, you must add a method to it called `sendCommand()`. This method expects a String parameter called `name`. The method will use the `RSocketRequester` to retrieve a `Mono` object containing a `CommandResponse`. In order to do that, it will first set the message *route* and build a new `CommandRequest` using the name given. The code looks something like this:

```java
@Service
public class RSocketClient {

    private final RSocketRequester rsocketRequester;

    public RSocketClient(RSocketRequester.Builder rsocketRequesterBuilder{
        this.rsocketRequester = rsocketRequesterBuilder
                .connectTcp("localhost", 7000).block();
    }

    public Mono<CommandResponse> sendCommand(String name) {
        return this.rsocketRequester
                   .route("command")
                   .data(new CommandRequest(name))
                   .retrieveMono(CommandResponse.class);
    }
}
```

#### 3.3 Create An RSocket Command Sender

Now the `RSocketClient.java` code is complete, you can use it in your code to send command messages to your RSocket server.

You will use Spring Shell to capture input from the terminal so that if a user types `send-command doSomething` - a command is sent to the server. When the server responds, you will print out the response in the terminal window.

Create a class called the `RSocketCommandSender` and annotate the class with the `@ShellComponent` and `@Slf4j` annotations. This class must be constructed with your `RSocketClient`. Spring will construct this for you thanks to the `@Autowired` annotation (which isn't strictly required in this case, but it can be declared for extra code clarity).

You must then add your `sendCommand()` method. This method is annotated with `@ShellMethod` which will tell Spring to expose this `send-command` method in the terminal. The method takes a single String parameter called `command`, and this parameter has been annotated with `@ShellOption` to declare that it is an option that can be used with `send-command`.

In the method, you must call the RSocketClient's `sendCommand()` method, and `subscribe()` to the Mono which is returned.

The code for the `RSocketCommandSender` can be seen in the example below: 

```java
@Slf4j
@ShellComponent
public class RSocketCommandSender {

    private final RSocketClient rSocketClient;

    @Autowired
    public RSocketCommandSender(RSocketClient rSocketClient) {
        this.rSocketClient = rSocketClient;
    }

    @ShellMethod("Send a command message to the RSocket server. Response will be printed.")
    public void sendCommand(@ShellOption(defaultValue = "doSomething") String command) {
        rSocketClient.sendCommand(command).subscribe(cr -> log.info("\nServer command response is: {}", cr));
        return;
    }
}
```

> In this case, the subscriber bound in the `subscribe()` method is a simple lambda function (`cr -> log.info("\nServer command response is: {}", cr)`) which takes the `CommandResponse` and logs it to the terminal with Slf4j. 

## How It Works

### Run The RSocket Server

```bash
cd rsocket-server
./mvnw clean package spring-boot:run

# Press CTRl-Z
bg
```

### Run The RSocket Client

```bash
cd rsocket-client
./mvnw clean package spring-boot:run -DskipTests=true
shell:>
```

When the client runs, it should present you with a new prompt `shell:>`.

You can now use this prompt in a similar way to a regular terminal.

You can send a command to the server as follows:

```bash
shell:> send-command doSomething
Server command response is: CommandResponse(command=doSomething, received=2019-12-16T16:01:17.285267Z)
```

> **Note:** You can see the full list of supported commands by typing `help` at the `shell:>` prompt. 

## Tidy Up

1. To exit the RSocket Client type `exit`.
2. Get a list of the current background processes (jobs) with the command `jobs` (lists all jobs and their job numbers).
3. Bring a background process (job) to the foreground with the command `fg %n` (where `n` is the job number).
4. Stop the process now in in the foreground by pressing `Ctrl-C`.
5. Repeat until `jobs` command shows an empty list.

## Final Thoughts

In this recipe you learned how to write a simple RSocket cleint, and also, how to write simple terminal applications using Spring Shell. That's two for the price of one. Buy one get one free. Cashback!

[request-response]: ./request-response.md
[start]: https://start.spring.io
[spring-shell-docs]: https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/
[web-app-none]: https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/html/appendix-application-properties.html#rsocket-properties