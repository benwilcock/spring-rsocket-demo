#!/bin/bash

rsocket-cli --stream --debug --input="Ben" --metadataFormat="message/x.rsocket.routing.v0" --metadata=@events-metadata tcp://localhost:7000
rsocket-cli --stream --debug --input="{\"command\":\"subscribe\"}" --dataFormat="json" --metadataFormat="message/x.rsocket.routing.v0" --metadata=@events-metadata tcp://localhost:7000
