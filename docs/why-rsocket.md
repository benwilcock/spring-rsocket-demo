# Why RSocket

**Time: Approx. 5 mins.** 

In the diverse world of communications, HTTP can lead to a few challenges. Network connections can drop, messages can flood in, the load might need to be balanced, messages may need to be re-routed, and one-way communication or streaming may be required. The HTTP protocol isn't really designed for many of these use-cases. You *can* overcome these challenges with HTTP, but this usually means adding additional code and infrastructure, such as circuit breakers, retry mechanisms, load balancers, etc.

So what do you do if you don't want to use HTTP? Switch to [RSocket][rsocket]! 

RSocket is a protocol that's purpose-built to solve these communication challenges. 

With RSocket you get flexible transport of messages over TCP or WebSockets, back-pressure, resumption, routing, and flow control, plus multiple message modes including fire-and-forget, request-response, and streaming. RSocket is also fully reactive, so it's designed from the start for next-gen Java applications.

In this blog series, you'll learn how to get started with RSocket so that you can get more familiar with how it works, and experience its power for yourself.

[rsocket]: https://rsocket.io