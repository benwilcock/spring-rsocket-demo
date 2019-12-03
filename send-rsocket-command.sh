#!/bin/bash

# Metadata format is Binary, with the length encoded first.
# See: https://github.com/rsocket/rsocket/blob/master/Extensions/Routing.md
# For example, in a hex editor the route 'command' would be...
# 07 63 6F 6D 6D 61 6E 64
# The '07' at the front describes the length of the word ('command' = 7) in hex (07).
# The file 'meta' can be used as the RSocket route as a shortcut.
# If you change the route, change the file with a Hex editor.

rsocket-cli --request --input="{\"command\":\"DoSomething\"}" --dataFormat="json" --requestn=1 --debug --metadataFormat="message/x.rsocket.routing.v0" --metadata=@command-metadata tcp://localhost:7000
