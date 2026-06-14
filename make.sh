#!/bin/bash

printf "Cleaning...\n"
printf "    old jsiv.jar\n"
rm -f jsiv.jar
printf "    jar staging directory\n"
rm -rf jar/
printf "    jsiv application directory\n"
rm -rf jsiv/

printf "Compiling...\n"
javac -d jar/ -cp "src/:libs/*" src/jsiv/*.java src/usermanual/*.java
if [[ $? -ne 0 ]]; then
    printf "Compilation failed!\n"
    exit 1
fi

printf "Making executable JAR...\n"
jar -cfm jsiv.jar manifest.txt -C . *.MD *.txt *.sh src/ -C jar/ . -C . assets/ -C . libs/ -C . docs/
if [[ $? != 0 ]]; then
    printf "Could not create JAR!\n"
    exit 1
fi

printf "Packaging...\n"
mkdir jsiv_app
cp jsiv.jar jsiv_app/
jpackage.exe --type app-image --name jsiv --input jsiv_app --main-jar jsiv.jar --add-modules java.desktop,java.base
if [[ $? != 0 ]]; then
    printf "Could not package executable!\n"
    exit 1
fi

printf "Cleaning up staging files...\n"
rm -rf jsiv_app/

printf "Done.\n"
