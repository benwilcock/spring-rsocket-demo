# RSocket No Code Quickstart

## Problem

Getting to know new tech can take a while. Sometimes you have to start new IDE projects and write new code before you see something that actually works. 

## Solution

The solution is to use code that's already written! With pre-prepared code you can see something working quickly without having to invest tons of time and effort. Nice.

## How To Do It

Your journey into [RSocket][rsocket] can begin without you writing a single line of code. With the `rsocket-cli` tool we can setup a simple server, attach a client to it, and send some data between the them in a few minutes.

> Before you start, check you have all the [prerequisites][pre] installed.

#### Step 1: Checkout The Code And Build The RSocket CLI

Because the RSocket-CLI tool is for developers, it comes as source code rather than as a ready compiled executable program. This means you have to build the `rsocket-cli` tool on your computer before you can use it. Fortunately that's quick and relatively simple process.

First, using [Git][pre], clone the code repository for this tutorial into a folder on your computer:

```bash
git clone https://github.com/benwilcock/spring-rsocket-demo.git
```

Next, build the `rsocket-cli` tool with the following command:

```bash
source get-rsocket-cli.sh
```

> This script might take some time. The Gradle Wrapper is required to build the `rsocket-cli` project, and the project itself depends on a number of other libraries. These downloads may take a while. It depends.

> Notice I'm using the `source` command. This is because the script creates a temporary alias called `rsocket-cli` which you will use in later steps.

Once the `rsocket-cli` is built, it should output the `--help` text to the terminal window. You are now ready to practice with [RSocket][rsocket].

#### Step 2: Start An RSocket Server

You can create a basic RSocket server using the `rsocket-cli` and a simple multiline text file (such as Ubuntu's built in dictionary of words in `/usr/share/dict/words`). This server will sit an wait for requests to come in, and then respond to them using the data from the dictionary. 

```bash
rsocket-cli -i=@/usr/share/dict/words --server tcp://localhost:8765 &
```

> The ampersand ("&") is important, it means to 'run this process in the background'. Make a note of the process number given (e.g. [2]24398), you'll need it when we tidy up later.

> If you don't have Ubuntu, a dummy plain text file called `words.txt` has been provided in the correct format. Use this file as your input in the command above (`rsocket-cli -i=@words.txt ...`).

#### Step 3: Attach An RSocket Client

Next, you should create an RSocket client and ask for a stream of words from the RSocket Server. These words will be streamed over TCP using the [RSocket][rsocket] protocol.

```bash
rsocket-cli --debug --stream --i="" --requestn=1 tcp://localhost:8765
```

The rsocket-cli will act as a client and request a stream containing just one word. The server will respond with a single word - `A` - which the client will print on the screen.

You can see how this works if you examine the command. Streaming mode is set with `--stream` and the number of words to send in the stream is set with `--requestn=1`. 

The client will print out the debug information of the data that it has received, which will look something like this:

```text
         +-------------------------------------------------+
         |  0  1  2  3  4  5  6  7  8  9  a  b  c  d  e  f |
+--------+-------------------------------------------------+----------------+
|00000000| 41                                              |A               |
+--------+-------------------------------------------------+----------------+
13:07:58.284	receiving ->
Frame => Stream ID: 1 Type: CANCEL Flags: 0b0 Length: 6
```

If you'd like to see more output, increase the `--requestn=1` setting to a higher number (such as 20) and run the command again. You should see a longer list of words on the client-side. If you omit `--requestn=1` you will get all the data available.

#### Tidy Up

When you're done experimenting, kill the RSocket server process using the PID you were given. 

```bash
kill -SIGTERM [pid]
```

If you lost the PID, type 'ps' and look for the number (PID) of a process labelled `Java`.

## How it works

In this recipe, the `rsocket-cli` is started twice, in a different mode each time. The first time we start the tool it's started in server mode. The second time we start the tool it's acting as a regular client.

The client talks to the server using RSocket over TCP. It asks the server to send it one item using RSocket's 'stream' mode. The server responds, sending one item back to the client. The details are logged to the terminal because the `--debug` option is ON in the client.

## Final Thoughts

That's it, your first foray into the world of RSocket was super-simple.  You built the `rsocket-cli` tool, setup a server, attached a client, and had them both talk to each other. 

If you followed this recipe, you proved that RSocket is working correctly for you and now we're ready to delve deeper into the topic of using Spring with RSocket.

[rsocket-cli]: https://github.com/rsocket/rsocket-cli
[rsocket]: http://rsocket.io/
[pre]: ./prerequisites.md
