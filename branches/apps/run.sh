#!/bin/sh

cd "$(dirname $0)"

export ANT_HOME="$(pwd)/ant"

"${ANT_HOME}/bin/ant" $*
