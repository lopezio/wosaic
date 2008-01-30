#!/bin/sh

## A simple unix launcher for the Wosaic uninstaller.  Simply call
## appropriate java command

## TODO: Perhaps check if we the java command first, and then launch

java -jar `dirname $0`/Uninstaller/uninstaller.jar

