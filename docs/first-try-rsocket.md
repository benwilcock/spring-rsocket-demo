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

Once the `rsocket-cli` is built, it should output the `--help` text to the terminal window. You are now ready to practice with [RSocket][rsocket].

> **Note:** 
> I'm using the `source` command. This is because the script creates a temporary alias called `rsocket-cli` which you will use in later steps and recipes.

#### Step 2: Start An RSocket Server

You can create a basic RSocket server using the `rsocket-cli` and a simple multiline text file (such as Ubuntu's built in dictionary of words in `/usr/share/dict/words`). This server will sit an wait for requests to come in, and then respond to them using the data from the dictionary. 

```bash
rsocket-cli -i=@words.txt --server tcp://localhost:8765
```

To send this server process to the background, press `Ctrl-Z` and then type `bg` as follows: 

```bash
[1]  + 22787 suspended  "${RSOCKET}"/build/install/rsocket-cli/bin/rsocket-cli "$@" -i=@words.txt
$> bg
[1]  + 22787 continued  "${RSOCKET}"/build/install/rsocket-cli/bin/rsocket-cli "$@" -i=@words.txt
```

> **Note:**
> Pressing `Ctrl-Z` suspends the currently running process in the terminal. Typing `bg` continues the suspended process in the background. Typing `jobs` shows you a list of background processes. More information on these commands can be found in the [prerequisites][pre].

#### Step 3: Attach An RSocket Client

Next, you should create an RSocket client and ask for a stream of words from the RSocket Server. These words will be streamed over TCP using the [RSocket][rsocket] protocol.

```bash
rsocket-cli --debug --stream -i="" --requestn=1 tcp://localhost:8765
```

The rsocket-cli will act as a client and request a stream containing just one word. The server will respond with a single word - `A` - which the client will print on the screen.

You can see how this works if you examine the `rsocket-cli` command we issued above. Streaming mode is set with `--stream` and the number of words to send in the stream is set with `--requestn=1`. 

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

When you're done experimenting, you can stop the RSocket server process as follows. 

1. Get a list of the current background processes (jobs) with the command `jobs` (lists all jobs and their job numbers).
2. Bring a background process (job) to the foreground with the command `fg %n` (where `n` is the job number).
3. Stop the process now in in the foreground by pressing `Ctrl-C`.
4. Repeat until `jobs` command shows an empty list.

## How it works

In this recipe, the `rsocket-cli` is started twice, in a different mode each time. The first time we start the tool it's started in `--server` mode. The second time we start the tool it's just acting as a regular client.

The client process talks to the server process using RSocket over TCP (on `localhost` port `8765`). It asks the server to send it one item (`--requestn=1`) using RSocket's stream (`--stream`) mode. The server process responds, sending one item back to the client. The clients interactions are logged to the terminal in detail because the debug option is set (`--debug`).

## Final Thoughts

That's it, your first foray into the world of RSocket was super-simple.  You built the `rsocket-cli` tool, setup a server, attached a client, and had them both talk to each other.

If you followed this recipe, you proved that RSocket is working correctly for you and now we're ready to delve deeper into the topic of using Spring with RSocket.

[rsocket-cli]: https://github.com/rsocket/rsocket-cli
[rsocket]: http://rsocket.io/
[pre]: ./prerequisites.md
