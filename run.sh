#!/bin/sh
if [ -n "$JAVA_HOME" ] ; then
	export PATH="${JAVA_HOME}/bin:${PATH}"
fi
java -cp build/classes/java/main org.example.Main <stdin.txt
