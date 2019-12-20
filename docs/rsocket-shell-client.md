# RSocket Shell Client

Spring Boot also brings simplicity to the task of writing RSocket clients. In this recipe, you'll learn how to write your own RSocket client using Spring Shell. You'll use this client to send commands to the RSocket server you built earlier.

**Time: Approx. 20 mins.**

Before you start, check you have all the [required software][pre] installed on your computer. You also need to be able to build and run the `rsocket-server` code. Follow the recipe: [Request Response With Spring Boot RSocket Server][request-response] if you need to get up to speed on how to do this.

## Problem

Your RSocket Spring Boot server from [earlier recipes][request-response] is ready to accept commands, but so far the only tool we have to send commands to the server is the `rsocket-cli` tool. This tool only accepts our commands via the `--input` option which is a bit inflexible.

## Solution

You can build and run your own RSocket client. If you build build it with Spring Shell, you'll be able to dispatch commands to the server from the terminal window using nothing more than plain text. 

## How To Do It

In the steps below you'll specify, download, and unpack a new RSocket client project using the [Spring Initializr (start.spring.io)][start]. You will then add Spring Shell support and code which allows you to send messages to your RSocket server. 

### Step 1: Create A New Spring Boot Project

Open you browser and navigate to [Spring Initializr (start.spring.io)][start]. Use the following settings to create your project.

* Project: Maven
* Language: Java
* Spring Boot: 2.2.2 (or latest stable)
* Group: io.pivotal
* Artifact: rsocket-client
* Dependencies: RSocket, Lombok

Click the green "Generate" button. Download the project and extract the archive. Open the extracted project in your IDE.

> The `rsocket-client` code has been provided in the [code repository][repo] you downloaded. Take a look in the `/rsocket-client` directory, if you don't want to start from scratch.

Next, we will customize the project further.

### Step 2: Add Spring Shell To The Project

At the time of writing, the Spring Initializr doesn't offer [Spring Shell][spring-shell-docs], but you can still add it manually. Open the Maven `pom.xml` file, and add the following dependency to the `<dependencies>` section:

```xml
<dependency>
	<groupId>org.springframework.shell</groupId>
	<artifactId>spring-shell-starter</artifactId>
	<version>2.0.0.RELEASE</version>
</dependency>
```

This will enable Spring Shell in our new RSocket client project.

### Step 3: Turn Off Web Application Support In Spring Boot

Because the project includes RSocket, you get embedded 'Netty', but I don't want Spring Boot to get confused and think this is a web application. In this case, You can switch off web support by setting the `web-application-type` property to `NONE` in the `application.properties` file. 

```java
spring.main.web-application-type=NONE
```

### Step 4: Write The Code

There are a few short coding tasks required in order to add the Spring Shell and RSocket support to your project.

#### 4.1 Disable The Tests

You can either delete, disable, or skip the tests that came along with your Spring Boot project. If you don't do this, odd things can happen when you build (as detailed in the [Spring Shell Docs][spring-shell-docs]).

To disable the tests, add the `@Disabled` annotation to the `RsocketclientApplicationTests` test class like this:

```java
@Disabled
@SpringBootTest
class RsocketclientApplicationTests {
    ...
}
```

> If you would like to simply skip the tests instead, add `-DskipTests=true` to your Maven command line whenever you run the client. For example: `./mvnw clean package spring-boot:run -DskipTests=true`

#### 4.2 Create An RSocket `@Service` Class

First, copy over the `CommandRequest.java` and `CommandResponse.java` data classes from the `rsocket-server` project. You will need these data types in order to exchange data with the server.

Next, create a new class called the `RSocketClient`. This class uses the `@Service` stereotype and must be constructed with an `RSocketRequester.Builder` which you use to create an `RSocketRequester`. 

> **Note: **
> When the code runs, Spring will construct this class for you thanks to the `@Autowired` annotation (which isn't strictly required in this case, but it can be declared for extra code clarity).

In the constructor, the `RSocketRequester` must be customized using the `connectTcp()` method, where you can specify your RSocket server's IP address and port. It should also be told to `block()` and await further interaction.

Lastly, add a method to your `RSocketClient` called `sendCommand()`. This method will expect a String parameter called `name`. The method will use the `RSocketRequester` from earlier to retrieve a `Mono` object containing a `CommandResponse`. In order to do that, it will first set the message *route* to "command" and build a new `CommandRequest` using the command name given. 

The code for the RSocketClient looks something like this:

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

#### 4.3 Create An `RSocketCommandSender` Class

With some additional code, you can use the `RSocketClient.java` class you created in the last step to send command messages to your RSocket server. You can use the Spring Shell to capture these command names from the user in the terminal window.

First, create a new class called the `RSocketCommandSender` in your project. Annotate this class with the `@ShellComponent` and `@Slf4j` annotations. This class needs to be constructed with your `RSocketClient` from earlier. 

> **Note: **
> When the code runs, Spring will construct this class for you thanks to the `@Autowired` annotation (which isn't strictly required in this case, but it can be declared for extra code clarity).

You must then add a `sendCommand()` method. This method is annotated with `@ShellMethod()` which will tell Spring to expose this method as `send-command` in the terminal. The method takes a single String parameter called `command`, and this parameter has been annotated with `@ShellOption` to declare that it is an option that can be used with `send-command`. In the method, you must call the RSocketClient's `sendCommand()` method, and `subscribe()` to the Mono which is returned.

The code for the RSocketCommandSender can be seen in the example below: 

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

### Build And Run The RSocket Server

To sart the RSocket server, move to it's directory and run the application. Once it's running, move the process into the background as follows:

```bash
cd rsocket-server
./mvnw clean package spring-boot:run

# Press CTRl-Z to suspend the process
bg # Run the process in the background
cd ..
```

The server will start up on localhost port 7000 by default.

### Build And Run The RSocket Client

To compile and run your RSocket client, move to it's directory and run the code as follows:

```bash
cd rsocket-client
./mvnw clean package spring-boot:run -DskipTests=true
shell:>
```

When the client runs, Spring Shell should present you with a new prompt: `shell:>`

You can use this prompt in a similar way to a regular terminal.

Now, you can send a command message to the RSocket server by typing `request-response do-something`. The server will then send a response, and this response will be printed out in the terminal.

```bash
shell:> request-response do-something
Event response is: EventResponse(event=Response for 'do-something', created=1576860076)
```

> **Note:** You can see the full list of supported commands by typing `help` at the `shell:>` prompt. 

## Tidy Up

1. To exit the RSocket Client type `exit`.
2. Get a list of the current background processes (jobs) with the command `jobs` (lists all jobs and their job numbers).
3. Bring a background process (job) to the foreground with the command `fg %n` (where `n` is the job number).
4. Stop the process now in in the foreground by pressing `Ctrl-C`.
5. Repeat until `jobs` command shows an empty list.

## How It Works

Spring Shell allows you to write terminal programs using Java and Spring Boot. When you type `send-command <your-command-name>` at the `shell:>` prompt, in the `RSocketCommandSender`, Spring Shell invokes the `sendCommand()` method passing the command name. This method then calls the `sendCommand` method in the `RSocketClient`. This method uses the RSocketRequester class which takes care of contacting the RSocket server to send the request data, and gathers the response data. The response data is printed on the screen in the terminal window.

## Final Thoughts

In this recipe you learned how to write a simple RSocket client, but also, how to write simple terminal applications using the Spring Shell library. That's two for the price of one. Buy one get one free!

[request-response]: ./request-response.md
[start]: https://start.spring.io
[spring-shell-docs]: https://docs.spring.io/spring-shell/docs/2.0.0.RELEASE/reference/htmlsingle/
[web-app-none]: https://docs.spring.io/spring-boot/docs/2.2.2.RELEASE/reference/html/appendix-application-properties.html#rsocket-properties
[repo]: https://github.com/benwilcock/spring-rsocket-demo