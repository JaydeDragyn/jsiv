@echo off
setlocal enabledelayedexpansion

echo Cleaning...
echo     old jsiv.jar
if exist jsiv.jar del /f /q jsiv.jar
echo     jar staging directory
if exist jar\ rmdir /s /q jar\
echo     jsiv application directory
if exist jsiv\ rmdir /s /q jsiv\

echo Compiling...
javac -d jar\ -cp "src\;libs\*" src\jsiv\*.java src\usermanual\*.java
if %ERRORLEVEL% neq 0 (
    echo Error: Compilation failed!
    exit /b 1
)

echo Making executable JAR...
jar -cfm jsiv.jar manifest.txt -C . *.MD *.txt *.bat src\ -C jar\ . -C . assets\ -C . libs\ -C . docs\
if %ERRORLEVEL% neq 0 (
    echo Error: Could not create JAR!
    exit /b 1
)

echo Packaging...
if not exist jsiv_app\ mkdir jsiv_app
copy jsiv.jar jsiv_app\
jpackage.exe --type app-image --name jsiv --input jsiv_app --main-jar jsiv.jar --add-modules java.desktop,java.base
if %ERRORLEVEL% neq 0 (
    echo Error: Could not package executable!
    exit /b 1
)

echo Cleaning up staging files...
if exist jsiv_app\ rmdir /s /q jsiv_app\

echo Done.
