#!/bin/bash
rm -f jsiv.jar
rm -rf jar/
javac -d jar/ -cp src/ src/*.java
jar -cfvm jsiv.jar manifest.txt -C jar .
