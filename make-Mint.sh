#!/bin/bash

echo Making JSIV in Linux Mint
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
    printf "Compilation failed!\n"
    exit 1
fi

echo Making executable JAR...
jar -cfm jsiv.jar manifest.txt -C jar/ .
if [[ $? != 0 ]]; then
    printf "Could not create JAR!\n"
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
jpackage --type deb \
         --name jsiv \
         --linux-package-name jsiv-app \
         --input app_staging \
         --main-jar jsiv.jar \
         --add-modules java.desktop,java.base \
         --linux-shortcut \
         --file-associations mime-types/png.properties \
         --file-associations mime-types/jpg.properties \
         --file-associations mime-types/jpeg.properties \
         --file-associations mime-types/gif.properties \
         --file-associations mime-types/bmp.properties \
         --file-associations mime-types/tif.properties \
         --file-associations mime-types/tiff.properties \
         --file-associations mime-types/wbmp.properties \
         --file-associations mime-types/webp.properties \
         --dest dist/ \
         --app-version 1.2.0 \
         --vendor JaydeDragyn \
         --description "Simple Image Viewer" \
         --icon assets/jsiv.png
if [[ $? != 0 ]]; then
    printf "Could not package executable!\n"
    exit 1
fi

printf "Cleaning up staging files...\n"
rm -rf jar/
rm -rf app_staging/

printf "Done.\n"
