#!/bin/bash
rm -f jsiv.jar
rm -rf jar/
javac -d jar/ -cp src/ src/jsiv/*.java src/usermanual/*.java
jar -cfvm jsiv.jar manifest.txt -C jar/ . -C . assets/
