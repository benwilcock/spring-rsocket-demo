#!/bin/bash

echo "This may take a while. Go get a drink or something..."
git clone https://github.com/rsocket/rsocket-cli.git
cd rsocket-cli
sh rsocket-cli --help
export RSOCKET=$(pwd)
alias rsocket-cli='"${RSOCKET}"/build/install/rsocket-cli/bin/rsocket-cli "$@"'
cd ..
