#!/bin/sh

## A simple unix launcher for the Wosaic project.  Simply call
## appropriate java command

## TODO: Perhaps check if we the java command first, and then launch

## TODO: Also, I'm not sure exactly how we put ourselves in the right
##       directory, I think there's probably an easy way...

java -jar `dirname $0`/bin/wosaic.jar

