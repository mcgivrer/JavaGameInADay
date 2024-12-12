#!/bin/bash
# Executing example code from the src/test path
# Copyright 2024 - Frederic Delorme<frederic.delorme@gmail.com>
export SRC=src/test
export classpath=target/test-classes
echo execute "$1" code example.
javac -d $classpath \
$(find $SRC -name "*.java") && \
find $SRC -name "*.properties" -exec cp --parents {} $classpath \; && \
java -cp $classpath "$1"
echo ending execution of "$1".
