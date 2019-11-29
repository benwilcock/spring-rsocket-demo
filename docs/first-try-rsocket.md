# Getting Started with RSocket

Our journey into [RSocket][rsocket] can begin without needing to write a single line of code. With the `rsocket-cli` tool we can setup a simple server and attach a client to it before streaming some data between the them.

## What You'll Need

#### Java 8

To follow along with this tutorial you'll need Java 8 installed on your PC. You can find out if you have Java 8 by opening a command-line and typing `java -version`. If you don't have Java 8 already, you can get it from [AdoptOpenJDK][adopt-open-jdk]. If you're a developer, you might want to consider using [SDKMan][sdkman].

#### Git

You'll also need `git`, but there is a workaround if you don't have it. 

## Step 1: Build The RSocket CLI

Because the RSocket-CLI tool is for developers, it comes as source code rather than as a ready compiled executable program. This means you have to build the `rsocket-cli` tool on your computer before you can use it.

> If you have a Mac OS computer with Homebrew installed, you have the option of install the `rsocket-cli` without having to build it yourself. The instructions are on the [RSocket-CLI GitHub project page][rsocket-cli].

First, clone the RSocket CLI code into a folder on your computer.

```bash
git clone https://github.com/rsocket/rsocket-cli.git
```

> If you don't have `git`, you can visit the [rsocket-cli GitHub page][rsocket-cli] where you can choose to Download ZIP. Once you have the Zip archive, unpack it and continue to the next step.

Next, build the `rsocket-cli` tool with the following command.

```bash
cd rsocket-cli
./rsocket-cli --help
```

> This command might take some time as the build need to download the Gradle Wrapper and depends on a number of Java libraries. Access to the internet will be required.

Once the `rsocket-cli` is built, you are ready to try [RSocket][rsocket].

## Step 2: Start The RSocket Server

You can create a simple RSocket server using the `rsocket-cli` and Ubuntu's built in dictionary of words. This server will sit an wait for requests to come in, and them respond using the data in the dictionary.

```bash
./rsocket-cli -i=@/usr/share/dict/words --server --debug tcp://localhost:8765
```

> If you don't have Ubuntu, create a plain text file with one word per line and use this file as your input in the command (`rsocket-cli -i=@/filename.txt ...`).
> ```text
> A
> Aaron
> Abbot
> Abby
> etc.
> ```

## Step 3: Attach An RSocket Client

Let's create an RSocket client that will ask for a stream of words from the Word Server over RSocket.

```bash
./rsocket-cli --stream --setup=0 --input=0 --requestn=1 tcp://localhost:8765
```

The client will request a stream containing just a single word. You can see how this works if you examine the command: `--stream requestn=1`.

The server will respond with a single word - `A` - which the client will print on the screen.  The server will print out the debug of the data that it sent, which will look something like this:

```text
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 41                                              |A               |
+--------+-------------------------------------------------+----------------+
13:07:58.284	receiving ->
Frame => Stream ID: 1 Type: CANCEL Flags: 0b0 Length: 6
```

> There will be some additional output on the server. This is related to how the communication was setup.

## Wrapping Up

That's it, your first foray into the world of RSocket was super-simple.  You built the `rsocket-cli` tool, setup a server, attached a client, and had them both talk to each other. 

This proved that RSocket is working and now we're ready to delve deeper into RSocket development with Java and Spring.

[adopt-open-jdk]: https://adoptopenjdk.net/
[rsocket-cli]: https://github.com/rsocket/rsocket-cli
[rsocket]: http://rsocket.io/
[sdkman]: https://sdkman.io/
