#!/bin/bash
# Executing example code from the src/test path
# Copyright 2024 - Frederic Delorme<frederic.delorme@gmail.com>
export SRC=src/test
export classpath=target/test-classes

echo START Execute "$1" code example.

javac -d $classpath \
$(find $SRC -name "*.java") && \
find $SRC -name "*.properties" -exec cp --parents {} $classpath \; && \
java -cp $classpath "$1"

echo END Ending execution of "$1".
