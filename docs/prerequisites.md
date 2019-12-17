# What You'll Need

To practice these RSocket recipes, you'll need a few things.

**Time: Approx. 10 mins.** 

## Java

To follow along with this tutorial *you'll need to know a little Java*, and have Java installed on your PC. You can find out which version of Java you have Java by opening a command-line and typing `java -version`. If you don't have Java already, you can get it from [AdoptOpenJDK][adopt-open-jdk]. If you're a developer, you might want to consider using [SDKMan][sdkman] to handle the install for you. When installing, choose a recent LTS version such as Java 11 or Java 8.

## Bash/GitBash/ZSH Terminal

These recipes use the Linux terminal a lot. It helps if you have some basic experience with the Linux terminal, but full instructions will be given. Some ready-made scripts have been provided for certain tasks. These scripts require a Linux terminal to run. You can get the Linux terminal almost anywhere, including on Windows. The scripts in the code repository have been built and tested using ZSH on Ubuntu. 

Depending which version of Mac OS you have, you may have either BASH or ZSH installed by default.

If you're on Windows and don't yet have a Linux terminal, you can install [Git Bash][gitbash], or the [Windows Subsystem for Linux][wsl].

## Git

You'll need to [download and install Git][git] so that you can clone the code repository. 

## Maven

You don't need to install [Maven][maven] (the portable Maven Wrapper has been used in the code), but you should have some familiarity with Maven's commands and Maven's default project structure and conventions.

## IDE

Working with code is much easier if you have a good IDE on your side. There are several great IDE's for Java, many of which are free to use. Examples include [IntelliJ IDEA][intellij], [Spring Tools][spring-tools], and [Visual Studio Code][vs-code].

## Browser & Internet

You'll need an up to date browser. You'll also need access to the Internet, and knowledge of how to connect and disconnect your device from it.

## Download The Cookbook Code

Using [Git][git], clone the code repository for this cookbook into a folder on your computer:

```bash
git clone https://github.com/benwilcock/spring-rsocket-demo.git
```

## Preparing The RSocket-CLI And Your Terminal

Because the RSocket-CLI tool is for developers, it comes as source code rather than as a ready compiled executable program. This means you have to build the `rsocket-cli` tool on your computer before you can use it. Fortunately that's quick and relatively simple process.

First, build the `rsocket-cli` tool with the following command:

```bash
source get-rsocket-cli.sh
```

> This script might take some time. The Gradle Wrapper is required to build the `rsocket-cli` project, and the project itself depends on a number of other libraries. These downloads may take a while. It depends. 

Once the `rsocket-cli` is built, it should output the `--help` text to the terminal window. You are now ready to practice with [RSocket][rsocket].

## Helpful Terminal Commands

* `Ctrl-Z` - Suspend a running process.
* `bg` - Run a suspended process (job) in the background.
* `jobs` - List current terminal processes (shows job numbers).
* `fg` - Bring a background process (job) to the foreground (use `fg %1` to bring job number 1 to the foreground)
* `source get-rsocket-cli.sh` - Prepare the current terminal to run the `rsocket-cli`
* `java -jar <path>` - Run a Java JAR file as an executable program (requires Java)
* `./mvnw` or `sh mvnw` - Run the Maven Wrapper to work with projects (doesn't require Maven to be installed)

[adopt-open-jdk]: https://adoptopenjdk.net/
[rsocket-cli]: https://github.com/rsocket/rsocket-cli
[rsocket]: http://rsocket.io/
[sdkman]: https://sdkman.io/
[gitbash]: https://gitforwindows.org/
[git]: https://git-scm.com/downloads
[wsl]: https://docs.microsoft.com/en-us/windows/wsl/install-win10
[intellij]: https://www.jetbrains.com/idea/download/#section=linux
[spring-tools]: https://spring.io/tools
[vs-code]: https://code.visualstudio.com/
[maven]: https://maven.apache.org/