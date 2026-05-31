#!/bin/bash
rm -f jsiv.jar
rm -rf jar/
javac -d jar/ -cp "src/:libs/*" src/jsiv/*.java src/usermanual/*.java
jar -cfvm jsiv.jar manifest.txt -C . *.MD *.txt *.sh src/ -C jar/ . -C . assets/ -C . libs/
