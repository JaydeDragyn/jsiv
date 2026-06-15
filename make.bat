@echo off
setlocal enabledelayedexpansion

echo Making JSIV in Windows
echo.
echo Cleaning...
echo     jar staging directory
if exist jar\ rmdir /s /q jar\
echo     app staging directory
if exist app_staging\ rmdir /s /q app_staging\
echo     old application directory
if exist jsiv\ rmdir /s /q jsiv\

echo Compiling...
javac -d jar\ -cp "src\;libs\*" src\jsiv\*.java src\usermanual\*.java
if %ERRORLEVEL% neq 0 (
    echo Error: Compilation failed!
    exit /b 1
)

echo Making executable JAR...
:: Create the executable JAR in the app_staging directory
jar -cfm jsiv.jar manifest.txt -C jar\ .
if %ERRORLEVEL% neq 0 (
    echo Error: Could not create JAR!
    exit /b 1
)

echo Staging files for packaging...
:: Copy the executable JAR into the app_staging directory
if not exist app_staging\ mkdir app_staging\
move jsiv.jar app_staging\

:: Copy the external dependencies and assets into the app_staging directory
if exist libs\ xcopy /q /e /i /y libs\ app_staging\libs\
if exist assets\ xcopy /q /e /i /y assets\ app_staging\assets\
if exist mime-types\ xcopy /q /e /i /y mime-types\ app_staging\mime-types\

:: Copy the project files into a dev\ folder in the app_staging directory
if not exist app_staging\dev\ mkdir app_staging\dev\
if exist src\ xcopy /q /e /i /y src\ app_staging\dev\src\
copy manifest.txt app_staging\dev\
copy make* app_staging\dev\
copy *.MD app_staging\dev\

echo Packaging...
jpackage --type app-image --name jsiv --input app_staging --main-jar jsiv.jar --add-modules java.desktop,java.base --icon assets\jsiv.ico
if %ERRORLEVEL% neq 0 (
    echo Error: Could not package executable!
    exit /b 1
)

echo Cleaning up staging files...
if exist jar\ rmdir /s /q jar\
if exist app_staging\ rmdir /s /q app_staging\

echo Done.
