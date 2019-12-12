# Resiliant Streaming With RSocket

There are tons of scenarios where you may need to communicate over spotty network connections. Mobile devices that are on the move, IoT devices affected by atmosheric conditions, or networks being interrupted in the cloud are all good examples. In this recipe you fill see how RSocket could help you work around these issues.

## Problem

You have data that you need to stream to a client. You start the stream successfully, but then the network goes down...

## Solution

With RSocket you can simply 'resume' the stream when the network recovers.

## How To Do It

Resumption is not active by default. To add resumption to your Spring Boot RSocket server you must add some configuration code. You can then test resumption is working by starting the server, attaching the client, and then interering with the network connection.

### Step 1: The Java Code

For Spring Boot RSocket servers, we need to configure the RSocket server to start with *resume mode* on.

To do this we add a Spring `@Component` to our project called `RSocketServerSetup` which must implement the `[ServerRSocketFactoryProcessor][processor]` interface. This interface contains a single `process()` method. At runtime, Spring will call this method passing a `[RSocketFactory.ServerRSocketFactory][factory]` which you can use to customise the RSocket server. In this case, you will call the `resume()` method on the factory to activate the resumption feature using some predetermined defaults. The code is as follows.

```java
@Profile("resumption")
@Component
public class RSocketServerSetup implements ServerRSocketFactoryProcessor {

    @Override
    public RSocketFactory.ServerRSocketFactory process(RSocketFactory.ServerRSocketFactory factory) {
        return factory.resume(); // By default duration=120s and store=InMemory and timeout=10s
    }
}
```

You will notice in the code above, that you can use a Spring `@Profile` called `resumption`. This will allow you to activate and decativate the feature using an environment variable. By default, factories which have `resume()` turned on will use a session duration of 120 seconds, and an '[InMemoryResumableFramesStore][irfs]' to store the frames when the connection is broken. You can change these defaults easily by calling methods on the `ServerRSocketFactory` factory.

### Step 2: Testing Resumption Works

To test if resumption is working you need to start the server, connect the client, and then break (and fix) the network connection.

#### Step 2.1 Start The Server

You can start the server and send it onto a background thread in the same way as in earlier recipes, but first you need to activate the `resumption` profile by setting the `SPRING_PROFILES_ACTIVE` environment variable to `resumption`.

```bash
$ cd rsocket-server
$ export SPRING_PROFILES_ACTIVE=resumption
$ ./mvnw clean package spring-boot:run -DskipTests=true
```

Once the server has started, press `Ctrl-Z` to suspend the process in the terminal.

```bash
[1]  + 18976 suspended  ./mvnw clean package spring-boot:run -DskipTests=true
$ bg
[1]  + 22006 continued  ./mvnw clean package spring-boot:run -DskipTests=true
$ cd ..
```

Typing `bg` will resume the suspended process in the background, leaving the terminal free to start the client.

#### Step 2.2 Start The Resumable Client

Before you start the client, you need to know the IP address assigned to your machine by the router.

```bash
$ ifconfig
```

In my case, I know my router is assigning IP's in the 192.168 range, so the `192.168.1.115` entry in the `ifconfig` output is my machine's current IP address.

```bash
enp0s25: flags=4163<UP,BROADCAST,RUNNING,MULTICAST>  mtu 1500
        inet 192.168.1.115 #...omitted the rest
```

You must use your current IP adress (not `localhost` or the loopback adapter `127.0.0.1`) when connecting the `rsocket-cli` client to the server as follows:

```bash
$ source get-rsocket-cli.sh
$ rsocket-cli --stream --resume --debug --input="{\"command\":\"subscribe\"}" --dataFormat="json" --metadata=@events-metadata --metadataFormat="message/x.rsocket.routing.v0" tcp://<your IP address here>:7000
```

The RSocket connection will begin steaming. Every second a new subscription event will arrive at the client and it's debug information will be printed.


#### Step 2.3 Break The Network

Unplug your network cable, or deactivate your wifi, or switch off your router.

> **Note:**
> If you don't like this method, you could try a port forward technique with a tool like `socat`. Or maybe you know a better way, something that anyone can replicate on any Operating System? Let me know!

After a few seconds, the debug output *should* stop. After a few seconds more, you should see the RSocket client trying to send 'KEEP_ALIVE' messages.

In the test, pay attention to the time that messages are received, and the "sent" time in the subscription message (epoch seconds).

```bash
13:43:16.989	receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 48
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 65 76 65 6e 74 22 3a 22 73 75 62 73 63 72 |{"event":"subscr|
|00000010| 69 70 74 69 6f 6e 22 2c 22 73 65 6e 74 22 3a 31 |iption","sent":1|
|00000020| 35 37 36 31 35 38 31 39 35 7d                   |576158195}      |
+--------+-------------------------------------------------+----------------+
{"event":"subscription","sent":1576158195}
```

Above you can see a subscription message which arrived at `13:43:16.989` and which contained a sent value of `1576158195`. In this test, this was the last message received before the network was disconnected. RSocket will try to keep the connection alive.

```bash
13:43:27.911	sending ->
Frame => Stream ID: 0 Type: KEEPALIVE Flags: 0b10000000 Length: 14
Data:
```

Above, you can see that the `rsocket-cli` is trying to `KEEPALIVE` the connection. No data was sent. This happens every 20 seconds by default.

Now, restore your network connection and move onto the next step.

#### Step 2.4 Fix The Network

When you restore the network, after a short wait, the message steam should resume where it left off:

```bash
13:43:54.293	receiving ->
Frame => Stream ID: 1 Type: NEXT Flags: 0b100000 Length: 48
Data:
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 7b 22 65 76 65 6e 74 22 3a 22 73 75 62 73 63 72 |{"event":"subscr|
|00000010| 69 70 74 69 6f 6e 22 2c 22 73 65 6e 74 22 3a 31 |iption","sent":1|
|00000020| 35 37 36 31 35 38 31 39 36 7d                   |576158196}      |
+--------+-------------------------------------------------+----------------+
{"event":"subscription","sent":1576158196}
```

Above we have the first subscription message to arrive after the connection was restored. The message arrived at `13:43:54.293`, a good 40 seconds since the network was first interrupted. The message containins a sent value of `1576158196`, which is the next message in the sequence. We can see therefore that the stream of messages has resumed successfully and that no messages were lost during the network failure.

Finally, press `Ctrl-C` to stop the RSocket client process and end the stream.

## Tidy Up

1. Get a list of the current background processes (jobs) with the command `jobs` (lists all jobs and their job numbers).
2. Bring a background process (job) to the foreground with the command `fg %n` (where `n` is the job number).
3. Stop the process now in in the foreground by pressing `Ctrl-C`.
4. Repeat until `jobs` command shows an empty list.
5. Unset the SPRING_PROFILES_ACTIVE with `unset SPRING_PROFILES_ACTIVE`.

## How it Works

RSocket's 'resumption' feature allows clients and servers to work around unreliable network connections. When the `resume()` is called on the `ServerRSocketFactory`, the the RSocket server uses an `InMemoryResumableFramesStore` to store messages when connections are lost. When connections are restored, the message steam continues where it  left off. No messages are lost.

## Final Thoughts

Because RSocket has no concept of `client-server` either clients or servers can act as the source of a steam. This means it's possible for a server to 'ask' for a stream from a client. RSocket is a very flexible protocol.

If you'd like to read more about RSocket resumption, check out this [excellent][rafal] blog by Rafal Kowalski


[rafal]: https://grapeup.com/blog/read/reactive-service-to-service-communication-with-rsocket-load-balancing--resumability-65
[factory]: https://github.com/rsocket/rsocket-java/blob/develop/rsocket-core/src/main/java/io/rsocket/RSocketFactory.java
[processor]: https://github.com/spring-projects/spring-boot/blob/master/spring-boot-project/spring-boot/src/main/java/org/springframework/boot/rsocket/server/ServerRSocketFactoryProcessor.java
[irfs]: https://github.com/rsocket/rsocket-java/blob/develop/rsocket-core/src/main/java/io/rsocket/resume/InMemoryResumableFramesStore.java