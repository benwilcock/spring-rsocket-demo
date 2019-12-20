# Why RSocket

**Time: Approx. 5 mins.** 

What do you do if you discover a communication use-case that isn't a great fit for connectionless HTTP request-response messaging?

In the diverse world of communications, HTTP can lead to a few challenges. Network connections can drop, messages can flood in, load might need to be balanced, messages may need to be re-routed, and one-way communication or streaming may be required. 

The HTTP protocol isn't really designed for many of these use-cases. You *can* overcome these challenges with HTTP, but docing so usually means adding additional code and infrastructure, such as circuit breakers, retry mechanisms, load balancers, etc.

[RSocket][rsocket] is purpose built to solve some of these common communication challenges. 

With RSocket you get flexible transport of messages over TCP or WebSockets, back-pressure, resumption, routing, and flow control, plus multiple message modes including fire and forget, request response, and streaming. RSocket is also fully reactive, so it's designed from the start for next-gen Java applications.

In this series of recipes, you'll learn how to get started with RSocket so that you can get more familiar with how it works, and experience its power for yourself.