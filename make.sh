#!/bin/bash

echo Making JSIV in Windows under GIT BASH
echo
echo Cleaning...
printf "    jar staging directory\n"
rm -rf jar/
printf "    app staging directory\n"
rm -rf app_staging/
printf "    old application directory\n"
rm -rf jsiv/

echo Compiling...
javac -d jar/ -cp "src/:libs/*" src/jsiv/*.java src/usermanual/*.java
if [[ $? -ne 0 ]]; then
    echo Compilation failed!
    exit 1
fi

echo Staging files for executable JAR...
cp -rf assets/ jar/

echo Making executable JAR...
jar -cfm jsiv.jar manifest.txt -C jar/ .
if [[ $? != 0 ]]; then
    echo Could not create JAR!
    exit 1
fi

echo Staging files for packaging...
mkdir app_staging/
mkdir app_staging/dev/

mv jsiv.jar app_staging/

cp -rf libs/ app_staging/
cp -rf assets/ app_staging/
cp -rf mime-types/ app_staging/

cp *.MD app_staging/dev/
cp make* app_staging/dev/
cp -rf src/ app_staging/dev/
cp manifest.txt app_staging/dev/

echo Packaging...
jpackage --type app-image --name jsiv --input app_staging --main-jar jsiv.jar --add-modules java.desktop,java.base --icon assets/jsiv.ico --app-version 1.2.0
if [[ $? != 0 ]]; then
    echo Could not package executable!
    exit 1
fi

echo Cleaning up staging files...
rm -rf jar/
rm -rf app_staging/

echo Done.
