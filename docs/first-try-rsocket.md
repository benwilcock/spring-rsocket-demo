# Getting Started with RSocket

Our journey into [RSocket][rsocket] can begin without needing to write a single line of code. With the `rsocket-cli` tool we can setup a simple server and attach a client to it before streaming some data between the them.

## What You'll Need

#### Java 8

To follow along with this tutorial you'll need Java 8 installed on your PC. You can find out if you have Java 8 by opening a command-line and typing `java -version`. If you don't have Java 8 already, you can get it from [AdoptOpenJDK][adopt-open-jdk]. If you're a developer, you might want to consider using [SDKMan][sdkman].

#### Git

You'll also need `git`, but there is a workaround if you don't have it. 

#### Bash/ZSH

There are some scripts provided that require a Linux terminal to run. If you're on Windows, you can use [Git Bash][gitbash] or you can check out the scripts and apply some of your Windows Command Prompt expertise to get them running.

## Step 1: Checkout The Code And Build The RSocket CLI

Because the RSocket-CLI tool is for developers, it comes as source code rather than as a ready compiled executable program. This means you have to build the `rsocket-cli` tool on your computer before you can use it.

First, clone the code repository for this tutorial into a folder on your computer:

```bash
git clone https://github.com/benwilcock/spring-rsocket-demo.git
```

Next, build the `rsocket-cli` tool with the following command:

```bash
source get-rsocket-cli.sh
```

> This script might take some time. The Gradle Wrapper is required to build the `rsocket-cli` project, and the project itself depends on a number of other libraries. These downloads may take a while.

> The script creates a temporary alias for the `rsocket-cli`. 

Once the `rsocket-cli` is built, you are ready to try [RSocket][rsocket].

## Step 2: Start The RSocket-CLI Server

You can create a simple RSocket server using the `rsocket-cli` and Ubuntu's built in dictionary of words. This server will sit an wait for requests to come in, and them respond using the data in the dictionary. 

```bash
rsocket-cli -i=@/usr/share/dict/words --server --debug tcp://localhost:8765 &
```

> The ampersand ("&") is important, it means to 'run this process in the background'. Make a note of the process number given (e.g. [2]24398), you'll need it later to kill the process.

> If you don't have Ubuntu, create a plain text file with one word per line and use this file as your input in the command (`rsocket-cli -i=@/filename.txt ...`).
> ```text
> A
> Aaron
> Abbot
> Abby
> etc.
> ```

## Step 3: Attach The RSocket-CLI Client

Next, let's create an RSocket client that will ask for a stream of words from the Word Server over the RSocket protocol.

```bash
rsocket-cli --stream --setup=0 --input=0 --requestn=1 tcp://localhost:8765
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

If you'd like to see more than just the word 'A' in the client output, increase the `--requestn=1` setting to a higher number (such as 20) and run the command again. You should see a longer list of words on the client-side, and much more debug output on the server-side.

## Wrapping Up

Kill the RSocket server using the PID you were given. If you lost it, type 'ps' and look for the number of the process labelled `Java`.

```bash
kill -SIGTERM [pid]
```

That's it, your first foray into the world of RSocket was super-simple.  You built the `rsocket-cli` tool, setup a server, attached a client, and had them both talk to each other. 

This proved that RSocket is working correctly and now we're ready to delve deeper into the topic of using Spring with RSocket.

[adopt-open-jdk]: https://adoptopenjdk.net/
[rsocket-cli]: https://github.com/rsocket/rsocket-cli
[rsocket]: http://rsocket.io/
[sdkman]: https://sdkman.io/
[gitbash]: https://gitforwindows.org/
