# What You'll Need

To practice these RSocket recipes, you'll need a few things.

## Java

To follow along with this tutorial you'll need Java installed on your PC. You can find out if you have Java by opening a command-line and typing `java -version`. If you don't have Java already, you can get it from [AdoptOpenJDK][adopt-open-jdk]. If you're a developer, you might want to consider using [SDKMan][sdkman] to handle the install for you. When installing, choose a recent LTS version such as Java 11 or Java 8.

## Git

You'll need to [download and install Git][git] so that you can clone the code repository. 

## Bash/GitBash/ZSH

There are some scripts provided in the source code repository that require a Linux terminal to run (such as those provided in Ubuntu or Mac OS). If you're on Windows, you can try [Git Bash][gitbash], or the [Windows Subsystem for Linux][wsl], or just check out the scripts and apply some of your Windows Command Prompt expertise to get them running manually.

## Helpful Commands

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